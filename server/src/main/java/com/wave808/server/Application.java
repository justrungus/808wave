package com.wave808.server;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.wave808.server.services.UserService;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		
		
		
	}
	
	@Bean
	//@SuppressWarnings("unused") 
	CommandLineRunner initDatabase(UserService userService) {
    	return args -> {
        userService.registerUser("admin", "admin@808wave.com", "admin");
        System.out.println("Admin creado");
    	};
	}


}
