package com.mercia.weather.service;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.mercia.weather.dto.WeatherRequestDto;
import com.mercia.weather.dto.WeatherResponseDto;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class WeatherService {

	@Value("${api.key}")
	private String apiKey;

	private final RestClient restClient;
	
	private final CacheService cacheService;
	
	private final ObjectMapper objectMapper;

	public WeatherService(RestClient restClient, CacheService cacheService, ObjectMapper objectMapper) {
		this.restClient = restClient;
		this.cacheService = cacheService;
		this.objectMapper = objectMapper;
	}

	public WeatherResponseDto info(WeatherRequestDto weatherRequestDto) {
		String city = weatherRequestDto.getCity();
		String state = weatherRequestDto.getState();
		String country = weatherRequestDto.getCountry();
		WeatherResponseDto ifPresent = cacheService.getIfPresent(weatherRequestDto);
		if (ifPresent!=null)
		{
			return ifPresent;
		}
		String query = String.format("%s, %s, %s", city, state, country);
		@Nullable
		String body = restClient.get().uri(uriBuilder -> uriBuilder.path("/data/2.5/weather")
				// Combine city, state, and country code with commas
				.queryParam("q", query).queryParam("units", "metric").queryParam("appid", apiKey).build()).retrieve()
				.body(String.class);
		
		JsonNode json = objectMapper.readTree(body);
		WeatherResponseDto weatherResponseDto = new WeatherResponseDto();
		weatherResponseDto.setPressure(json.get("main").get("pressure").asFloat());
		weatherResponseDto.setHumidity(json.get("main").get("humidity").asFloat());
		weatherResponseDto.setTemperature(json.get("main").get("temp").asFloat());
		weatherResponseDto.setWindSpeed(json.get("wind").get("speed").asFloat());
		cacheService.addToCache(weatherRequestDto, weatherResponseDto);
		return weatherResponseDto;
	}
}
