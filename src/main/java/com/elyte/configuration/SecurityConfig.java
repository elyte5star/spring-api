package com.elyte.configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private LoggingFilter loggingFilter;

    private static final String[] AUTH_WHITELIST = {
            "/",
            "/users/signup",
            "/auth/token",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/products/**",
            "/docs/**",

           
    };

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("SecurityConfig initialized.");
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                .requestMatchers(AUTH_WHITELIST).permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint));

        // Add a filter to log the request-response of every request
        http.addFilterBefore(loggingFilter, UsernamePasswordAuthenticationFilter.class);
        // Add a filter to validate the tokens with every request
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        log.debug("AuthenticationManager invoked.");
        return authenticationConfiguration.getAuthenticationManager();
    }


    
    @Bean
    PasswordEncoder passwordEncoder() {
        log.debug("PasswordEncoder invoked.");
        return new BCryptPasswordEncoder();
    }

}