package com.elyte.security;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain)
            throws ServletException, IOException, JwtException {

        String username = null;

        String token = jwtTokenUtil.extractTokenFromRequest(request);

        //String token  = jwtTokenUtil.parseJwt(request);

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            try {
                username = jwtTokenUtil.getUserNameFromToken(token);
                UserPrincipal userPrincipal = jwtCredentialsService.loadUserByUsername(username);
                jwtTokenUtil.validateToken(token, userPrincipal);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userPrincipal, null, userPrincipal.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } catch (MalformedJwtException e) {
                log.error("[x] Invalid JWT token :" + e.getLocalizedMessage());
            } catch (ExpiredJwtException e) {
                log.error("[x] JWT Token has expired: " + e.getLocalizedMessage());
            } catch (JwtException ex) {
                log.error("[x] Token error: " + ex.getLocalizedMessage());
            }

        } else {
            log.debug("[+] NO JWT HEADER");
        }

        filterChain.doFilter(request, response);

    }

}
