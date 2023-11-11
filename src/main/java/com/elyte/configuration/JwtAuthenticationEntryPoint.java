package com.elyte.configuration;

//JwtAuthenticationEntryPoint extends Spring’s AuthenticationEntryPoint class and overrides its method commence. 
//It rejects every unauthenticated request and sends error code 401.


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import java.io.Serializable;
import java.io.IOException;
import org.springframework.http.MediaType;


@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {
    private static final long serialVersionUID = -7858869558953243875L;
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        log.error("Unauthorized error: {}", authException.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.ALL_VALUE);
        // You can also write JSON object below to send proper response as you send from
        // REST resources.
        response.getWriter().write("You're not authorized to perform this operation.");
    }

}
