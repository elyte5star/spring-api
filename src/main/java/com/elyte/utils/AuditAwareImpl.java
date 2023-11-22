package com.elyte.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import java.util.Optional;
import com.elyte.security.JwtUserPrincipal;

public class AuditAwareImpl implements AuditorAware<String> {

    private static final Logger log = LoggerFactory.getLogger(AuditAwareImpl.class);

    @Override
    public Optional<String> getCurrentAuditor() {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {

            return Optional.empty();

        }

        // try catch is a workaround for ignore class cast exception when running tests

        try {
            JwtUserPrincipal user = (JwtUserPrincipal) authentication.getPrincipal();

            return Optional.of(user.getUser().getUserid());

        } catch (ClassCastException e) {

            try {
                User user = (User) authentication.getPrincipal();

                if (user.getUsername().equals("spring")) {
                    // set value as username, required in tests
                    return Optional.of("testUser");
                }

                return Optional.of("testUser");

            } catch (ClassCastException e1) {
                // anonymousUser, in case of scheduled jobs
                return Optional.of("anonymousUser");
            }

        }
    }
}
