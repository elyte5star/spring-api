package com.elyte.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.elyte.service.JwtCredentialsService;
import com.elyte.utils.JwtTokenUtil;
import com.elyte.utils.EncryptionUtil;
import java.io.IOException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.FilterChain;


@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private JwtCredentialsService jwtCredentialsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String tokenFromRequest = request.getHeader("Authorization");

        String userName = null;
        String encryptedJwtToken = null;
        String jwtToken = null;
        log.debug("Inside JwtRequestFilter--OncePerRequestFilter");
        // JWT Token is in the form "Bearer token". Remove Bearer word and get only the
        // Token
        if (tokenFromRequest != null && tokenFromRequest.startsWith("Bearer ")) {
            encryptedJwtToken = tokenFromRequest.substring(7);
            jwtToken = EncryptionUtil.decrypt(encryptedJwtToken);
            try {
                userName = jwtTokenUtil.getUserNameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                log.error("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                log.error("JWT Token has expired");
            }

        } else {
            log.warn("JWT Token does not begin with Bearer String");
        }

        // Once we get the token validate it and extract username(principal/subject)
        // from it.
        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.jwtCredentialsService.loadUserByUsername(userName);

            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // After setting the Authentication in the context, we specify
                // that the current user is authenticated. So it passes the
                // Spring Security Configurations successfully.

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

            }

        }

        filterChain.doFilter(request, response);

    }

}
