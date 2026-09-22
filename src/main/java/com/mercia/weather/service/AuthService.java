package com.mercia.weather.service;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mercia.weather.dto.LoginRequest;
import com.mercia.weather.dto.RegisterRequest;
import com.mercia.weather.entities.Role;
import com.mercia.weather.entities.User;
import com.mercia.weather.repository.UserRepository;

@Service
public class AuthService {

	private final UserRepository userRepo;

	AuthService(UserRepository userRepo) {
		this.userRepo = userRepo;
	}
	
	public ResponseEntity<String> register(RegisterRequest request)
	{
		if(userRepo.findByEmail(request.getEmail()))
			throw new RuntimeException("User Already exists");
		User user = new User(request.getUsername(),request.getEmail(),request.getPassword(),Role.USER,LocalDateTime.now());
		userRepo.save(user);
		return ResponseEntity.ok().body("User Registered successfully");
	}
	
	public  ResponseEntity<String> login(LoginRequest request)
	{
		return null;
	}
	
}
