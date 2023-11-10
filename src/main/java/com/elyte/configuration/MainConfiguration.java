package com.elyte.configuration;
import com.elyte.utils.AuditAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class MainConfiguration {
    @Bean
    AuditorAware<String> auditorAware() {
        return new AuditAwareImpl();
    }

}
