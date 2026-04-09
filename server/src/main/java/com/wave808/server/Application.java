package com.wave808.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		
		
		
	}
	
	// @Bean
	// CommandLineRunner initDatabase(UserService userService) {
    // 	return args -> {
    //     userService.registerUser("admin", "admin@808wave.com", "admin");
    //     System.out.println("Admin creado");
    // 	};
	// }


}
