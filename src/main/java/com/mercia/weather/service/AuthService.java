package com.mercia.weather.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mercia.weather.dto.LoginRequest;
import com.mercia.weather.dto.RegisterRequest;
import com.mercia.weather.entities.Role;
import com.mercia.weather.entities.User;
import com.mercia.weather.exception.UserNotFoundException;
import com.mercia.weather.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class AuthService {

	private final UserRepository userRepo;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	@Transactional
	public ResponseEntity<String> register(RegisterRequest request) {
		Optional<User> byEmail = userRepo.findByEmail(request.getEmail());
		Optional<User> byUsername = userRepo.findByUsername(request.getUsername());
		if (byEmail.isPresent() || byUsername.isPresent()) {
			return ResponseEntity.badRequest().body("User already exists");
		}
		User user = new User(request.getUsername(), request.getEmail(), passwordEncoder.encode(request.getPassword()),
				Role.USER, LocalDateTime.now());
		log.debug("User registered:" + user);
		userRepo.save(user);

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
            log.warn("Invalid password. Please try again");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
		}

		String token = jwtService.generateToken(username);
		log.info("Token is generated");
		ResponseCookie cookie = ResponseCookie.from("token", token).httpOnly(true).secure(false) // true when using
																									// HTTPS
				.path("/").sameSite("Strict").maxAge(Duration.ofMinutes(15)).build();
		log.info("Login is successful ");
		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body("Login successful");
	}

}
