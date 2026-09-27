package com.mercia.weather.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestClient;

import com.mercia.weather.dto.WeatherRequestDto;
import com.mercia.weather.dto.WeatherResponseDto;
import com.mercia.weather.entities.AccessAudit;
import com.mercia.weather.entities.City;
import com.mercia.weather.entities.User;
import com.mercia.weather.exception.UnavailableCity;
import com.mercia.weather.repository.AccessAuditRepository;
import com.mercia.weather.repository.CityRepository;
import com.mercia.weather.repository.UserRepository;

import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

	@Mock
	private RestClient restClient;

	@Mock
	private CacheService cacheService;

	@Mock
	private ObjectMapper objectMapper;

	@Mock
	private AccessAuditRepository accessAuditRepo;

	@Mock
	private UserRepository userRepo;

	@Mock
	private CityRepository cityRepo;

	@InjectMocks
	private WeatherService weatherService;

	@AfterEach
	void clearSecurityContext() {
	    SecurityContextHolder.clearContext();
	}
	@Test
	void whenCityDoesNotExist_failed() {

		WeatherRequestDto request = new WeatherRequestDto();

		request.setCity("UnknownCity");
		request.setState("UnknownState");
		request.setCountry("UnknownCountry");

		// Mock logged-in user
		Authentication authentication = mock(Authentication.class);
		when(authentication.getName()).thenReturn("testuser");

		SecurityContext securityContext = mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);

		SecurityContextHolder.setContext(securityContext);

		// Mock user repository
		User user = new User();
		user.setId(1L);
		user.setUsername("testuser");
		user.setEmail("test@gmail.com");

		when(userRepo.findByUsername("testuser")).thenReturn(Optional.of(user));

		// Mock city repository: city does NOT exist
		when(cityRepo.findByNameStateCountry("UnknownCity", "UnknownState", "UnknownCountry"))
				.thenReturn(Optional.empty());

		// Execute + verify exception
		assertThrows(UnavailableCity.class, () -> weatherService.getWeatherDetails(request));

		// Verify failed audit was saved
		verify(accessAuditRepo).save(any(AccessAudit.class));
	}
	
	@Test
	void shouldReturnWeatherFromCache() {

	    WeatherRequestDto request = new WeatherRequestDto();
	    request.setCity("Bengaluru");
	    request.setState("Karnataka");
	    request.setCountry("IN");

	    User user = new User();
	    user.setId(1L);
	    user.setUsername("john");
	    user.setEmail("john@gmail.com");

	    WeatherResponseDto cachedWeather = new WeatherResponseDto();
	    cachedWeather.setTemperature(25.0f);

	    when(userRepo.findByUsername("john"))
	            .thenReturn(Optional.of(user));

	    when(cityRepo.findByNameStateCountry(
	            "Bengaluru", "Karnataka", "IN"
	    )).thenReturn(Optional.of(new City()));

	    when(cacheService.getIfPresent(request))
	            .thenReturn(cachedWeather);

	    // SecurityContext setup
	    Authentication authentication = mock(Authentication.class);
	    when(authentication.getName()).thenReturn("john");

	    SecurityContext securityContext = mock(SecurityContext.class);
	    when(securityContext.getAuthentication()).thenReturn(authentication);

	    SecurityContextHolder.setContext(securityContext);

	    WeatherResponseDto result =
	            weatherService.getWeatherDetails(request);

	    assertEquals(25.0f, result.getTemperature());

	    verify(cacheService).getIfPresent(request);
	}
}