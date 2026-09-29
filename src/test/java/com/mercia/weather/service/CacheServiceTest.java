package com.mercia.weather.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.github.benmanes.caffeine.cache.Cache;
import com.mercia.weather.dto.WeatherRequestDto;
import com.mercia.weather.dto.WeatherResponseDto;

@ExtendWith(MockitoExtension.class)
public class CacheServiceTest {

	@Mock
	private  Cache<WeatherRequestDto, WeatherResponseDto> cache;
	
	@InjectMocks
	private CacheService cacheService;
	
	@Test
	void success()
	{
		WeatherRequestDto request = new WeatherRequestDto();
		WeatherResponseDto response = new WeatherResponseDto();
		request.setCity("ABC");
		
		WeatherResponseDto weatherDetailsIfPresent = cacheService.getWeatherDetailsIfPresent(request);
		assertNull(weatherDetailsIfPresent);
		when(cache.getIfPresent(request)).thenReturn(response);
		cacheService.addToCache(request, response);
		
		weatherDetailsIfPresent = cacheService.getWeatherDetailsIfPresent(request);
		assertNotNull(weatherDetailsIfPresent);
		
	}
}
