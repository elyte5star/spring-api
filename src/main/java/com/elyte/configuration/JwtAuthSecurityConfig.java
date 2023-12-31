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
import com.elyte.security.JwtFilter;
import com.elyte.security.LoggingFilter;
import com.elyte.security.JwtAuthEntryPoint;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import java.io.IOException;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class JwtAuthSecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthSecurityConfig.class);

    @Autowired
    private JwtAuthEntryPoint jwtAuthenticationEntryPoint;


    @Autowired
    private JwtFilter jwtRequestFilter;

    @Autowired
    private LoggingFilter loggingFilter;

    private static final String[] AUTH_WHITELIST = {
            "/",
            "/index",
            "/users/signup/**",
            "/users/enableNewLocation",
            "/users/reset/password",
            "/users/logout",
            "/users/reset/confirm-token",
            "/reviews/create-review",
            "/auth/token",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/products/**",
            "/docs/**",
             "/actuator/**"

    };

    

    @Bean
    SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        log.debug("Bearer SecurityConfig initialized.");
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(
                        ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint).accessDeniedPage("/403.html"))
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Add a filter to log the request-response of every request
        http.addFilterBefore(loggingFilter, UsernamePasswordAuthenticationFilter.class);
        // Add a filter to validate the tokens with every request
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }

    @Bean(name = "GeoIPCountry")
    DatabaseReader databaseReader() throws IOException, GeoIp2Exception {
        final File resource = new File(this.getClass()
                .getClassLoader()
                .getResource("maxmind/GeoLite2-Country.mmdb")
                .getFile());
        return new DatabaseReader.Builder(resource).build();
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
                "/fonts/**",
                "/favicon.ico");
    }

}