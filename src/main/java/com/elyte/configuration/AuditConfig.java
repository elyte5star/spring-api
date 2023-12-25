package com.elyte.configuration;
import com.elyte.utils.AuditAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@EnableTransactionManagement
public class AuditConfig {
    
    @Bean
    AuditorAware<String> auditorProvider() {
        return new AuditAwareImpl();
    }

}
