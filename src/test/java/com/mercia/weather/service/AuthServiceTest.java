package com.mercia.weather.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mercia.weather.dto.LoginRequest;
import com.mercia.weather.dto.RegisterRequest;
import com.mercia.weather.entities.Role;
import com.mercia.weather.entities.User;
import com.mercia.weather.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserRepository userRepo;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtService jwtService;

	@InjectMocks
	private AuthService authService;

	@Test
	void login_shouldLoginSuccessfully() {

		LoginRequest request = new LoginRequest("john", "password123");
		User user = new User("john", "john@gmail.com", "encodedPassword", Role.USER, LocalDateTime.now());
		user.setId(1L);
		when(userRepo.findByUsername("john")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
		when(jwtService.generateToken("john")).thenReturn("fake-jwt-token");
		ResponseEntity<String> response = authService.login(request);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Login successful", response.getBody());
		assertTrue(response.getHeaders().getFirst(HttpHeaders.SET_COOKIE).contains("token=fake-jwt-token"));
		verify(jwtService).generateToken("john");
	}

	@Test
	void login_shouldFailWhenPasswordIsWrong() {

		LoginRequest request = new LoginRequest("john", "wrongPassword");
		User user = new User("john", "john@gmail.com", "encodedPassword", Role.USER, LocalDateTime.now());
		user.setId(1L);
		when(userRepo.findByUsername("john")).thenReturn(Optional.of(user));
		when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);
		ResponseEntity<String> response = authService.login(request);
		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		assertEquals("Invalid password", response.getBody());
		verify(jwtService, never()).generateToken("john");
	}

	@Test
	void register_newUser_success() {

		RegisterRequest request = new RegisterRequest("john", "merc@co.in", "password");
		User savedUser = new User("john", "merc@co.in", "encodedPassword", Role.USER, LocalDateTime.now());
		savedUser.setId(1L);
		when(userRepo.findByUsername("john")).thenReturn(Optional.empty());
		when(userRepo.findByEmail("merc@co.in")).thenReturn(Optional.empty());
		when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
		when(userRepo.save(any(User.class))).thenReturn(savedUser);
		ResponseEntity<String> response = authService.register(request);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("User Registered successfully", response.getBody());
		verify(passwordEncoder).encode("password");
		verify(userRepo).save(any(User.class));
	}

	@Test
	void register_userExists_shouldFail() {

		RegisterRequest request = new RegisterRequest("john", "merc@co.in", "password");
		User existingUser = new User("john", "merc@co.in", "encodedPassword", Role.USER, LocalDateTime.now());
		existingUser.setId(1L);
		when(userRepo.findByUsername("john")).thenReturn(Optional.of(existingUser));
		when(userRepo.findByEmail("merc@co.in")).thenReturn(Optional.of(existingUser));
		ResponseEntity<String> response = authService.register(request);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertEquals("User already exists", response.getBody());
		verify(userRepo, never()).save(any(User.class));
		verify(passwordEncoder, never()).encode(any(String.class));
	}
}
