package com.elyte.security;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.elyte.domain.User;

public class CustomLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{

    @Autowired
    private  LoginAttemptService loginAttemptService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException, ServletException {
        UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
        User user = userDetails.getUser();
        if (user.getFailedAttempt() > 0) {
            
            loginAttemptService.resetFailedAttempts(user.getUsername());
        }
         
        super.onAuthenticationSuccess(request, response, authentication);
    }
     
    
}
