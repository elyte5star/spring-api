package com.elyte.utils;

import org.springframework.data.domain.AuditorAware;
import com.elyte.domain.User;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;

public class AuditAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
       //User principal = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Optional.of("hello");
    }
}
