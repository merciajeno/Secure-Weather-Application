package com.mercia.weather.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.mercia.weather.config.ApplicationConfig;
import com.mercia.weather.exception.UserNotFoundException;
import com.mercia.weather.filter.AccessAuditFilter;
import com.mercia.weather.service.AuthService;
import com.mercia.weather.service.JwtService;

@WebMvcTest(controllers = AuthController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AccessAuditFilter.class))
@Import(ApplicationConfig.class)
public class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AuthService authService;

	@MockitoBean
	private JwtService jwtService;

	@Test
	void register_whenNotAuthenticated_shouldBeAllowed() throws Exception {

		when(authService.register(any())).thenReturn(ResponseEntity.ok("User registered"));
		mockMvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON).content("""
				{
				    "username": "john",
				    "email": "merc@co.in",
				    "password": "12345"
				}
				""")).andExpect(status().isOk());
	}
	
	

	@Test
	void login_whenCorrectCredentials_success() throws Exception {

		when(authService.login(any())).thenReturn(ResponseEntity.ok("Login successful"));
		mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
				{
				    "username": "john",
				    "password": "12345"
				}
				""")).andExpect(status().isOk());
	}

	@Test
	void login_whenNotRegistered_failed() throws Exception {
		when(authService.login(any())).thenThrow(new UserNotFoundException("User not found"));
		mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content("""
				{
				    "username": "john",
				    "password": "12345"
				}
				""")).andExpect(status().isNotFound());
	}
}
