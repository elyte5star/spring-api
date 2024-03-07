package com.elyte.configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.elyte.security.JwtAuthEntryPoint;
import com.elyte.security.JwtFilter;
import com.elyte.security.LoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class JwtAuthSecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthSecurityConfig.class);

 
    @Autowired
    private JwtFilter jwtRequestFilter;
    
    @Autowired
    private JwtAuthEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private LoggingFilter loggingFilter;

    private static final String[] AUTH_WHITELIST = {
            "/",
            "/index",
            "/api/users/signup/**",
            "/api/users/customer/service",
            "/api/users/enableNewLocation",
            "/api/users/password/change-password",
            "/api/users/reset/password",
            "/api/users/logout",
            "/api/users/reset/confirm-token",
            "/api/reviews/create-review",
            "/api/auth/token",
            "/api/auth/signout",
            "/api/auth/form-login",
            "/api/auth/get-token",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/api/products/**",
            "/docs/**",
             "/actuator/**",
             "/favicon.ico"

    };

    

    @Bean
    SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        log.debug("Bearer SecurityConfig initialized.");
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(
                         ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

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
        return new BCryptPasswordEncoder(16);
    }

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/resources/**",
                "/static/**",
                "/css/**", "/js/**",
                "/images/**",
                "/resources/static/**",
                "/fonts/**"
               );
    }

}