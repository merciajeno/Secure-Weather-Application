package com.mercia.weather.service;

import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.mercia.weather.dto.WeatherRequestDto;
import com.mercia.weather.dto.WeatherResponseDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CacheService {

	private final Cache<WeatherRequestDto, WeatherResponseDto> cache;

	public CacheService(Cache<WeatherRequestDto, WeatherResponseDto> cache) {
		this.cache = cache;
	}

	public void addToCache(WeatherRequestDto weatherRequestDto, WeatherResponseDto weatherResponseDto) {
		cache.put(weatherRequestDto, weatherResponseDto);
		log.info("cached");
	}

	public WeatherResponseDto getIfPresent(WeatherRequestDto key) {
		log.info("returned from the cache");
		return cache.getIfPresent(key);
	}
}
