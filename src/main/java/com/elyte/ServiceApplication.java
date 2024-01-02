package com.elyte;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;


@SpringBootApplication
public class ServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceApplication.class, args);
	}

	// AuthenticationEventPublisher for loggedIn users
	@Bean
	AuthenticationEventPublisher authenticationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		return new DefaultAuthenticationEventPublisher(applicationEventPublisher);
	}

}
