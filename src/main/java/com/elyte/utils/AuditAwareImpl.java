package com.elyte.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;
import org.springframework.security.core.Authentication;


public class AuditAwareImpl implements AuditorAware<String> {

    private static final Logger log = LoggerFactory.getLogger(AuditAwareImpl.class);

    @Override
    public Optional<String> getCurrentAuditor() {

        Authentication loggedInUser =SecurityContextHolder.getContext().getAuthentication();
        String username = loggedInUser.getName();
        log.info(username);
       
     
        return Optional.of("");
    }
}
