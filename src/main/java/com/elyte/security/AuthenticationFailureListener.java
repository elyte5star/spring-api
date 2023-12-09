package com.elyte.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;


 //We inform the LoginAttemptService of the IP address from where the unsuccessful attempt originated. 
 //Here, we get the IP address from the HttpServletRequest bean,
 // which also gives us the originating address in the X-Forwarded-For header for requests that are forwarded by e.g. a proxy server.

@Component
public class AuthenticationFailureListener implements 
  ApplicationListener<AuthenticationFailureBadCredentialsEvent>{

    @Autowired
    private HttpServletRequest request;


    @Autowired
    private LoginAttemptService loginAttemptService;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.getRemoteAddr())) {
            loginAttemptService.loginFailed(request.getRemoteAddr());
        } else {
            loginAttemptService.loginFailed(xfHeader.split(",")[0]);
        }
    }
    
}
