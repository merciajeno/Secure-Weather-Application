package com.mercia.weather.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mercia.weather.dto.LoginRequest;
import com.mercia.weather.dto.RegisterRequest;
import com.mercia.weather.entities.AccessAudit;
import com.mercia.weather.entities.Role;
import com.mercia.weather.entities.User;
import com.mercia.weather.repository.AccessAuditRepository;
import com.mercia.weather.repository.UserRepository;

class AuthServiceTest {

	@Mock
	private UserRepository userRepo;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtService jwtService;

	@Mock
	private AccessAuditRepository accessAuditRepo;

	private AuthService authService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		authService = new AuthService(userRepo, passwordEncoder, jwtService, accessAuditRepo);
	}

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
		assertEquals("fake-jwt-token", response.getBody());

		verify(jwtService).generateToken("john");
		verify(accessAuditRepo).save(any(AccessAudit.class));
	}

	@Test
	void login_shouldFailWhenPasswordIsWrong() {

		// Arrange
		LoginRequest request = new LoginRequest("john", "wrongPassword");

		User user = new User("john", "john@gmail.com", "encodedPassword", Role.USER, LocalDateTime.now());

		user.setId(1L);

		when(userRepo.findByUsername("john")).thenReturn(Optional.of(user));

		ResponseEntity<String> response = authService.login(request);

		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

		assertEquals("Invalid password", response.getBody());

		verify(accessAuditRepo).save(any(AccessAudit.class));

		verify(jwtService, never()).generateToken(toString());
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

		verify(userRepo).save(any(User.class));
		verify(accessAuditRepo).save(any(AccessAudit.class));
	}

	@Test
	void register_userExists_shouldFail() {
		RegisterRequest request = new RegisterRequest("john", "merc@co.in", "password");

		User savedUser = new User("john", "merc@co.in", "encodedPassword", Role.USER, LocalDateTime.now());
		savedUser.setId(1L);

		when(userRepo.findByUsername("john")).thenReturn(Optional.of(savedUser));

		when(userRepo.findByEmail("merc@co.in")).thenReturn(Optional.of(savedUser));

		ResponseEntity<String> response = authService.register(request);

		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
}