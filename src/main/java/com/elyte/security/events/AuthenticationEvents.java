package com.elyte.security.events;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
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
import com.elyte.security.LoginAttemptService;
import com.elyte.security.UserPrincipal;
import com.elyte.service.ActiveUsersService;
import com.elyte.service.DeviceService;
import com.elyte.utils.UtilityFunctions;
import com.elyte.security.location.StrangeLocationChecker;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.LoggerFactory;
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
public class AuthenticationEvents extends UtilityFunctions{
    private static final Logger log = LoggerFactory.getLogger(AuthenticationEvents.class);
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private Environment env;

    @Autowired
    private DeviceService deviceService;

   
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActiveUsersService activeUsers;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    private StrangeLocationChecker strangeLocationChecker;

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        final UserPrincipal userDetails = (UserPrincipal) event.getAuthentication().getPrincipal();
        activeUsers.registerLoggedUser(userDetails.getUsername());
        User user = userDetails.getUser();
        if (user.getFailedAttempt() > 0) {
            loginAttemptService.resetUserFailedAttempts(user);
        } else {
            loginAttemptService.resetFailedAttemptsCache();
        }
        loginNotification(userDetails, request);
        strangeLocationChecker.checkDifferentLocation(userDetails);
       
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
                loginAttemptService.increaseUnknownUserFailedAttemptsByIP(this.getClientIP());
            }

        } else if (events instanceof AuthenticationFailureLockedEvent) {

            throw new LockedException("Your account has been locked due to 10 failed attempts."
                    + " It will be unlocked after 24 hours.");
        }

        else if (events instanceof AuthenticationFailureDisabledEvent) {

            throw new DisabledException("Your account is disabled. Verify your account with OTP");

        }

        // ...Other failure events....
    }

    

    private void loginNotification(UserPrincipal userDetails, HttpServletRequest request) {
        try {
            if (isGeoIpLibEnabled()) {
                deviceService.verifyDevice(userDetails.getUser(), request);
            }
        } catch (Exception e) {
            log.error("[x] An error occurred while verifying device or location", e);
            throw new RuntimeException(e);
        }

    }

    private boolean isGeoIpLibEnabled() {
        return Boolean.parseBoolean(env.getProperty("geo.ip.lib.enabled"));
    }

}