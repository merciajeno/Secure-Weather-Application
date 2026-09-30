package com.mercia.weather.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestClient;

import com.mercia.weather.dto.WeatherRequestDto;
import com.mercia.weather.dto.WeatherResponseDto;
import com.mercia.weather.entities.City;
import com.mercia.weather.exception.UnavailableCity;
import com.mercia.weather.repository.AccessAuditRepository;
import com.mercia.weather.repository.CityRepository;

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
	private CityRepository cityRepo;

	@Mock
	private WeatherApiClientService weatherApiClientService;

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

		// Mock city repository: city does NOT exist
		when(cityRepo.findByNameStateCountry("UnknownCity", "UnknownState", "UnknownCountry"))
				.thenReturn(Optional.empty());

		// Execute and verify exception
		assertThrows(UnavailableCity.class, () -> weatherService.getWeatherDetails(request));

	}

	@Test
	void shouldReturnWeatherFromCache() {

		WeatherRequestDto request = new WeatherRequestDto();
		request.setCity("Bengaluru");
		request.setState("Karnataka");
		request.setCountry("IN");

		WeatherResponseDto cachedWeather = new WeatherResponseDto();
		cachedWeather.setTemperature(25.0f);
		cachedWeather.setHumidity(1000f);
		cachedWeather.setPressure(1000f);
		cachedWeather.setWindSpeed(30f);

		when(cityRepo.findByNameStateCountry("Bengaluru", "Karnataka", "IN")).thenReturn(Optional.of(new City()));
		when(cacheService.getWeatherDetailsIfPresent(request)).thenReturn(cachedWeather);

		WeatherResponseDto result = weatherService.getWeatherDetails(request);

		assertEquals(25.0f, result.getTemperature());
		assertEquals(1000f, result.getHumidity());
		assertEquals(1000f, result.getPressure());
		assertEquals(30f, result.getWindSpeed());

		verify(cacheService).getWeatherDetailsIfPresent(request);
	}

	@Test
	void shouldReturnWeather_FromApi_IfNotInCache() {
		WeatherRequestDto request = new WeatherRequestDto();
		request.setCity("Bengaluru");
		request.setState("Karnataka");
		request.setCountry("IN");

		WeatherResponseDto response = new WeatherResponseDto();
		response.setTemperature(25.0f);
		response.setHumidity(1000f);
		response.setPressure(1000f);
		response.setWindSpeed(30f);

		String query = "Bengaluru, Karnataka, IN";
		when(cityRepo.findByNameStateCountry("Bengaluru", "Karnataka", "IN")).thenReturn(Optional.of(new City()));
		when(cacheService.getWeatherDetailsIfPresent(request)).thenReturn(null);
		when(weatherApiClientService.getWeatherDetailsFromApi(query)).thenReturn(response);

		assertEquals(weatherService.getWeatherDetails(request), response);

	}

	@Test
	void shouldFail_fromExternalApi_ifWrongCityChoosen() {
		WeatherRequestDto request = new WeatherRequestDto();
		String city = "Bengaluru";
		request.setCity(city);
		String state = "Karnataka";
		request.setState(state);
		String country = "IN";
		request.setCountry(country);
		City existingCity = new City(city, state, country);
		String query = String.format("%s, %s, %s", city, state, country);
		when(cityRepo.findByNameStateCountry(city, state, country)).thenReturn(Optional.of(existingCity));
		when(cacheService.getWeatherDetailsIfPresent(request)).thenReturn(null);
		when(weatherApiClientService.getWeatherDetailsFromApi(query)).thenThrow(UnavailableCity.class);

		assertThrows(UnavailableCity.class, () -> {
			weatherService.getWeatherDetails(request);
		});
	}

}