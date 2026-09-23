package com.mercia.weather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WeatherApplication {

   
	public static void main(String[] args) {
		SpringApplication.run(WeatherApplication.class, args);
	}

//	@Override
//	public void run(String... args) throws Exception {
//		// TODO Auto-generated method stub
//	System.out.println("Hello worlld");
//		User admin =new User("Trapmaker","123#",passwordEncoder.encode("1234"),Role.ADMIN,LocalDateTime.now());
//		userRepo.save(admin);
//		
//	}

}
