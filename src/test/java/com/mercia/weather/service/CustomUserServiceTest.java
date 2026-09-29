package com.mercia.weather.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.mercia.weather.entities.Role;
import com.mercia.weather.entities.User;
import com.mercia.weather.exception.UserNotFoundException;
import com.mercia.weather.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class CustomUserServiceTest {

	@Mock
	private UserRepository userRepo;

	@InjectMocks
	private CustomUserService customUserService;

	@Test
	void success_whenUserPresentInDatabase() {
		String username = "admin";

		User user = new User();
		user.setUsername(username);
		user.setPassword("1233");
		user.setRole(Role.ADMIN);

		when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
		UserDetails userByUsername = customUserService.loadUserByUsername(username);
		assertNotNull(userByUsername);
	}

	@Test
	void failed_whenUserNotPresentInDatabase() {
		String username = "admin";

		User user = new User();
		user.setUsername(username);
		user.setPassword("1233");
		user.setRole(Role.ADMIN);

		when(userRepo.findByUsername(username)).thenReturn(Optional.empty());
		assertThrows(UserNotFoundException.class, () -> {
			customUserService.loadUserByUsername(username);
		});
	}
}
