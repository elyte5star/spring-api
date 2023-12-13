package com.elyte.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationFailureDisabledEvent;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import com.elyte.domain.User;
import com.elyte.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

/*
We inform the LoginAttemptService of the IP address from where the unsuccessful attempt originated. 
 Here, we get the IP address from the HttpServletRequest bean,
  which also gives us the originating address in the X-Forwarded-For header for requests that are forwarded by e.g. a proxy server.
*  AuthenticationFailureExpiredEvent
 * AuthenticationFailureProviderNotFoundEvent
 * AuthenticationFailureDisabledEvent
 * AuthenticationFailureLockedEvent
 * AuthenticationFailureServiceExceptionEvent
 * AuthenticationFailureCredentialsExpiredEvent
 * 
*/

@Component
public class AuthenticationEvents {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        final UserPrincipal userDetails = (UserPrincipal) event.getAuthentication().getPrincipal();
        System.out.println(userDetails.getAuthorities().toString());
        User user = userDetails.getUser();
        if (user.getFailedAttempt() > 0) {
            loginAttemptService.resetUserFailedAttempts(user);
        } else {
            loginAttemptService.resetFailedAttemptsCache();
        }
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent events) {

        Object username = events.getAuthentication().getPrincipal();

        if (events instanceof AuthenticationFailureBadCredentialsEvent) {
            User user = userRepository.findByUsername(username.toString());
            if (user != null) {
                if (user.isEnabled() && user.isAccountNonLocked()) {
                    if (user.getFailedAttempt() < LoginAttemptService.MAX_ATTEMPT - 1) {
                        loginAttemptService.increaseUserFailedAttempts(user);
                    } else {
                        loginAttemptService.lockUserAccount(user);

                    }
                } else if (!user.isAccountNonLocked()) {
                    if (loginAttemptService.unlockWhenTimeExpired(user)) {
                        throw new LockedException("Your account has been unlocked. Please try to login again.");
                    }
                }

            } else {
                loginAttemptService.increaseUnknownUserFailedAttemptsByIP(getClientIP());
            }

        } else if (events instanceof AuthenticationFailureLockedEvent) {

            throw new LockedException("Your account has been locked due to 5 failed attempts."
                    + " It will be unlocked after 24 hours.");
        }

        else if (events instanceof AuthenticationFailureDisabledEvent) {

            throw new DisabledException("Your account is disabled. Verify your account with OTP");

        }

        // ...Other failure events....
    }

    private String getClientIP() {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr())) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];

    }
}