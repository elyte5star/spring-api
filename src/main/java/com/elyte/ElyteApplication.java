package com.elyte;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCaching
public class ElyteApplication {
	public static void main(String[] args) {
		SpringApplication.run(ElyteApplication.class, args);
	}

}
