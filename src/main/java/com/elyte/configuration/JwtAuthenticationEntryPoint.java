package com.elyte.configuration;

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
        // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You're not
        // authorized to perform this transaction.");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.ALL_VALUE);
        // You can also write JSON object below to send proper response as you send from
        // REST resources.
        response.getWriter().write("You're not authorized to perform this transaction.");
    }

}
