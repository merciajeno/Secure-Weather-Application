package com.mercia.weather.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.mercia.weather.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class JwtFilterTest {

	@Mock
	private JwtService jwtService;

	@Mock
	private UserDetailsService userDetailsService;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private FilterChain filterChain;

	@Mock
	private UserDetails userDetails;

	@InjectMocks
	private JwtFilter filter;

	@Test
	void shouldContinueWhenNoCookiesArePresent() throws Exception {

		when(request.getCookies()).thenReturn(null);
		filter.doFilterInternal(request, response, filterChain);
		verify(filterChain).doFilter(request, response);
		verifyNoInteractions(jwtService);
		verifyNoInteractions(userDetailsService);
	}

	@Test
	void shouldContinueWhenTokenCookieIsNotPresent() throws Exception {

		Cookie sessionCookie = new Cookie("session", "abc");
		when(request.getCookies()).thenReturn(new Cookie[] { sessionCookie });
		filter.doFilterInternal(request, response, filterChain);
		verify(filterChain).doFilter(request, response);
		verifyNoInteractions(jwtService);
		verifyNoInteractions(userDetailsService);
	}

	@Test
	void shouldAuthenticateUserWhenTokenCookieIsPresent() throws Exception {

		Cookie tokenCookie = new Cookie("token", "jwt-token");
		when(request.getCookies()).thenReturn(new Cookie[] { tokenCookie });
		when(jwtService.extractUsername("jwt-token")).thenReturn("john");
		when(userDetailsService.loadUserByUsername("john")).thenReturn(userDetails);
		when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());// can be ADMIN,USER
		filter.doFilterInternal(request, response, filterChain);
		verify(jwtService).extractUsername("jwt-token");
		verify(userDetailsService).loadUserByUsername("john");
		verify(filterChain).doFilter(request, response);
		assertNotNull(SecurityContextHolder.getContext().getAuthentication());
		assertEquals(userDetails, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
	}

}
