package com.elyte.utils;

import org.springframework.data.domain.AuditorAware;
//import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditAwareImpl implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
//        ApplicationUser principal = (ApplicationUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        return Optional.of(principal.getId());
        return Optional.of(0L);
    }
}
