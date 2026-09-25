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

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthService {

	private final UserRepository userRepo;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AccessAuditRepository accessAuditRepo;

	AuthService(UserRepository userRepo, PasswordEncoder passwordEncoder, JwtService jwtService,
			AccessAuditRepository accessAuditRepo) {
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.accessAuditRepo = accessAuditRepo;
	}

    @Transactional
	public ResponseEntity<String> register(RegisterRequest request) {
		System.out.println(request.getEmail());
		Optional<User> byEmail = userRepo.findByEmail(request.getEmail());
		Optional<User> byUsername = userRepo.findByUsername(request.getUsername());
		if (byEmail.isPresent() || byUsername.isPresent()) {
			return ResponseEntity.badRequest().body("User already exists");
		}
		User user = new User(request.getUsername(), request.getEmail(), passwordEncoder.encode(request.getPassword()),
				Role.USER, LocalDateTime.now());
		log.debug("User registered:"+user);
		userRepo.save(user);
		Long id = userRepo.findByEmail(user.getEmail()).get().getId();
		AccessAudit entity = new AccessAudit(id, request.getUsername(), request.getEmail(), "/auth/register",
				Status.SUCCESS, LocalDateTime.now());
		entity.setActionDetails("Registered ");
		accessAuditRepo.save(entity);
		return ResponseEntity.ok().body("User Registered successfully");
	}

	@Transactional
	public ResponseEntity<String> login(LoginRequest request) {

		String username = request.getUsername();
		String password = request.getPassword();

		User existingUser = userRepo.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User not found"));

		boolean passwordMatch = passwordEncoder.matches(password, existingUser.getPassword());

		if (!passwordMatch) {

			AccessAudit audit = new AccessAudit(existingUser.getId(), username, existingUser.getEmail(), "/auth/login",
					Status.FAILED, LocalDateTime.now());

			audit.setActionDetails("Login failed: invalid username or password");
			accessAuditRepo.save(audit);

			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
		}

		String token = jwtService.generateToken(username);
        log.info("Token is generated");
		AccessAudit audit = new AccessAudit(existingUser.getId(), username, existingUser.getEmail(), "/auth/login",
				Status.SUCCESS, LocalDateTime.now());

		audit.setActionDetails("User logged in successfully");
		accessAuditRepo.save(audit);

		return ResponseEntity.ok().body(token);
	}

}
