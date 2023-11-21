package com.elyte;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;



@SpringBootApplication
public class ServiceApplication {

	public static void main(String[] args) {

		SpringApplication.run(ServiceApplication.class, args);

	}

	@RestController
	public static class IndexController {
		@GetMapping("/")
		public String index() {
			return "Hello World, Spring Boot!";
		}

	}


}
