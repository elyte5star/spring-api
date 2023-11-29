package com.elyte.configuration;

import org.springframework.context.annotation.Profile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import com.elyte.security.BasicAuthEntryPoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Profile(Profiles.BASIC_AUTH)
@Configuration
@EnableWebSecurity
public class BasicAuthSecurityConfig {

    @Autowired
    private BasicAuthEntryPoint basicAuthEntryPoint;

    private static final Logger log = LoggerFactory.getLogger(BasicAuthSecurityConfig.class);

    private static final String[] AUTH_WHITELIST = {
            "/",
            "/users/signup",
            "/reviews/create-review",
            "/auth/token",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/products/**",
            "/docs/**",

    };

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic(httpSecurityHttpBasicConfigurer -> {
            httpSecurityHttpBasicConfigurer.authenticationEntryPoint(basicAuthEntryPoint);
        })
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();

    }

    @Bean
    PasswordEncoder passwordEncoder() {
        log.debug("PasswordEncoder invoked.");
        return new BCryptPasswordEncoder();
    }

}
