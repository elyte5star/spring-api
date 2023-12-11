package com.elyte.security;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEvents {

    
    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        final UserPrincipal userDetails = (UserPrincipal) event.getAuthentication().getPrincipal();
        System.out.println("LOGIN name: " + userDetails.getUsername());
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent failures) {
        
        System.out.println("It failed: " + failures.getAuthentication().getPrincipal());
        if (failures instanceof AuthenticationFailureBadCredentialsEvent){
            System.out.println("bad cred: " );
        }
       



        // ...
    }
}