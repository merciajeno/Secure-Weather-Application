package com.mercia.weather.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mercia.weather.dto.LoginRequest;
import com.mercia.weather.dto.RegisterRequest;
import com.mercia.weather.entities.AccessAudit;
import com.mercia.weather.entities.Role;
import com.mercia.weather.entities.Status;
import com.mercia.weather.entities.User;
import com.mercia.weather.exception.UserNotFoundException;
import com.mercia.weather.repository.AccessAuditRepository;
import com.mercia.weather.repository.UserRepository;

@Service
public class AuthService {

	private final UserRepository userRepo;
	private final  PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AccessAuditRepository accessAuditRepo;

	AuthService(UserRepository userRepo, PasswordEncoder passwordEncoder, JwtService jwtService, AccessAuditRepository accessAuditRepo) {
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.accessAuditRepo = accessAuditRepo;
	}
	
	public ResponseEntity<String> register(RegisterRequest request)
	{
		System.out.println(request.getEmail());
		Optional<User> byEmail = userRepo.findByEmail(request.getEmail());
		Optional<User> byUsername = userRepo.findByUsername(request.getUsername());
		if(byEmail.isPresent() || byUsername.isPresent()) {
			return ResponseEntity.badRequest().body("User already exists");
		}
		User user = new User(request.getUsername(),
				request.getEmail(),
				passwordEncoder.encode(request.getPassword()),
				Role.USER,
				LocalDateTime.now());
		System.out.println(user);
		userRepo.save(user);
		Long id  = userRepo.findByEmail(user.getEmail()).get().getId();
		accessAuditRepo.save(new AccessAudit(id, request.getUsername(), request.getEmail(),"/auth/register", Status.SUCCESS, LocalDateTime.now()));
		return ResponseEntity.ok().body("User Registered successfully");
	}
	
	public  ResponseEntity<String> login(LoginRequest request)
	{
		String username = request.getUsername();
		String password = request.getPassword();
		User existingUser = userRepo.findByUsername(username)
				.orElseThrow(()-> new UserNotFoundException("User Not found"));
		
        System.out.println(existingUser);
		// check the plain password with the existing one
		boolean passwordMatch = passwordEncoder.matches(password, existingUser.getPassword());
		if (!passwordMatch || !username.equals(existingUser.getUsername())) {
			accessAuditRepo.save(new AccessAudit(-1L, request.getUsername(), null,"/auth/register", Status.FAILED, LocalDateTime.now()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
		}
		//get the token 
		String token = jwtService.generateToken(username);
		System.out.println(token);
		accessAuditRepo.save(new AccessAudit(existingUser.getId(), request.getUsername(), existingUser.getEmail(),"/auth/login", Status.SUCCESS, LocalDateTime.now()));
		return ResponseEntity.ok().body(token);
	}
	
}
