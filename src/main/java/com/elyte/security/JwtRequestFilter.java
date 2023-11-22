package com.elyte.security;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.elyte.domain.User;
import com.elyte.repository.UserRepository;
import com.elyte.service.JwtCredentialsService;
import com.elyte.utils.EncryptionUtil;
import java.io.IOException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.FilterChain;
import jakarta.validation.constraints.NotNull;


@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private JwtCredentialsService jwtCredentialsService;

   
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        String audience = null;
        String encryptedJwtToken = null;
        String jwtToken = null;

        log.debug("Inside JwtRequestFilter--OncePerRequestFilter");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            encryptedJwtToken = authHeader.substring(7);

            jwtToken = EncryptionUtil.decrypt(encryptedJwtToken);

            try {
                // username = jwtTokenUtil.getUserNameFromToken(jwtToken);
                audience = jwtTokenUtil.getAudienceFromToken(jwtToken);

            } catch (IllegalArgumentException e) {

                log.error("Unable to get JWT Token");

            } catch (ExpiredJwtException e) {
                log.error("JWT Token has expired");
            }

        } else {
            log.warn("UNPROTECTED ROUTE");
        }

        // Audience is equivalent to the userid string
        // A work around incase the user modifies its details

        if (audience != null && SecurityContextHolder.getContext().getAuthentication() == null) {

             User user = userRepository.findByUserid(audience);


            JwtUserPrincipal userDetails = this.jwtCredentialsService.loadUserByUsername(user.getUsername());

            // if token is valid configure Spring Security to manually set
            // authentication

            if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);

            }

        }

        filterChain.doFilter(request, response);

    }

}
