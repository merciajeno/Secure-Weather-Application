package com.mercia.weather.service;

import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.mercia.weather.dto.WeatherRequestDto;
import com.mercia.weather.dto.WeatherResponseDto;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class CacheService {

	private final Cache<WeatherRequestDto, WeatherResponseDto> cache;

	public void addToCache(WeatherRequestDto weatherRequestDto, WeatherResponseDto weatherResponseDto) {
		cache.put(weatherRequestDto, weatherResponseDto);
		log.info("cached");
	}

	public WeatherResponseDto getWeatherDetailsIfPresent(WeatherRequestDto key) {
		log.info("returned from the cache");
		return cache.getIfPresent(key);
	}
}
