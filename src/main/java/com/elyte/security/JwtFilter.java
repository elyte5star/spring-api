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
import com.elyte.service.CredentialsService;
import java.io.IOException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.FilterChain;
import jakarta.validation.constraints.NotNull;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private CredentialsService jwtCredentialsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        String audience = null;

        String token = jwtTokenUtil.extractTokenFromRequest(request);

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            try {

                // username = jwtTokenUtil.getUserNameFromToken(jwtToken);
                // Audience is equivalent to the userid string
                jwtTokenUtil.validateToken(token);
                audience = jwtTokenUtil.getAudienceFromToken(token);
                User user = userRepository.findByUserid(audience);
                UserPrincipal userPrincipal = jwtCredentialsService.loadUserByUsername(user.getUsername());
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userPrincipal, null, userPrincipal.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);

            } catch (MalformedJwtException e) {
                log.error("[+] Invalid JWT token");
            } catch (UnsupportedJwtException ex) {
                log.error("[+] Unsupported JWT token");
            } catch (IllegalArgumentException e) {
                log.error("[+] JWT claims string is empty");
            } catch (ExpiredJwtException e) {
                log.error("[+] JWT Token has expired");
            } catch (SignatureException e) {
                log.error("there is an error with the signature of you token ");
            }

        } else {
            log.warn("[+] UNPROTECTED ROUTE");
        }

        filterChain.doFilter(request, response);

    }

}
