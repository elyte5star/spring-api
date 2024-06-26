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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.elyte.domain.SecProperties;
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
    private SecProperties secProperties;

    @Autowired
    private LoggingFilter loggingFilter;

    
    @Bean
    SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        log.debug("Bearer SecurityConfig initialized.");
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(configurationSource()))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(secProperties.getAllowedPublicApis().stream().toArray(String[]::new)).permitAll()
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
    CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(secProperties.isAllowCredentials());
        configuration.setAllowedHeaders(secProperties.getAllowedHeaders());
        configuration.setAllowedMethods(secProperties.getAllowedMethods());
        configuration.setAllowedOrigins(secProperties.getAllowedOrigins());
        configuration.setExposedHeaders(secProperties.getExposedHeaders());
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
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