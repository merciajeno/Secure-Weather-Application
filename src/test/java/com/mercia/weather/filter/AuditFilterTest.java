package com.mercia.weather.filter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.mercia.weather.entities.AccessAudit;
import com.mercia.weather.entities.User;
import com.mercia.weather.repository.AccessAuditRepository;
import com.mercia.weather.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class AuditFilterTest {

	@InjectMocks
	private AccessAuditFilter auditFilter;

	@Mock
	private UserRepository userRepo;

	@Mock
	private AccessAuditRepository accessAuditRepository;

	@Mock
	private FilterChain filterChain;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Test
	void shouldCreateSuccessfulAuditForAuthenticatedUser() throws ServletException, IOException {

		String username = "admin";
		String requestUri = "/auth/useful";

		User user = new User();
		user.setId(1L);
		user.setUsername(username);
		user.setEmail("admin@gmail.com");

		UserDetails userDetails = new org.springframework.security.core.userdetails.User(username, "password",
				Collections.emptyList());

		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);

		when(request.getRequestURI()).thenReturn(requestUri);
		when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
		when(response.getStatus()).thenReturn(200); // auditing success value
		auditFilter.doFilterInternal(request, response, filterChain);

		verify(filterChain).doFilter(request, response);
		verify(accessAuditRepository).save(any(AccessAudit.class));
	}

	@Test
	void shouldCreateFailedAuditWhenResponseStatusIs400() throws ServletException, IOException {

		String username = "admin";
		User user = new User();
		user.setId(1L);
		user.setUsername(username);
		user.setEmail("admin@gmail.com");

		UserDetails userDetails = new org.springframework.security.core.userdetails.User(username, "password",
				Collections.emptyList());
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);
		when(request.getRequestURI()).thenReturn("/api/secrets");
		when(response.getStatus()).thenReturn(403); 
		when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
		auditFilter.doFilterInternal(request, response, filterChain);
		verify(accessAuditRepository).save(any(AccessAudit.class));

	}
	@Test
	void shouldNotCreateAudit_OpenEndpoints() throws ServletException, IOException
	{
		when(request.getRequestURI()).thenReturn("/swagger");
		auditFilter.doFilterInternal(request, response, filterChain);
	}

}
