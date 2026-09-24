package com.mercia.weather.service;

import java.time.LocalDateTime;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.mercia.weather.dto.WeatherRequestDto;
import com.mercia.weather.dto.WeatherResponseDto;
import com.mercia.weather.entities.AccessAudit;
import com.mercia.weather.entities.Status;
import com.mercia.weather.entities.User;
import com.mercia.weather.repository.AccessAuditRepository;
import com.mercia.weather.repository.UserRepository;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class WeatherService {

	@Value("${api.key}")
	private String apiKey;

	private final RestClient restClient;
	
	private final CacheService cacheService;
	
	private final ObjectMapper objectMapper;
	
	private final AccessAuditRepository accessAuditRepo;
	
	private final UserRepository userRepo;

	public WeatherService(RestClient restClient, CacheService cacheService, ObjectMapper objectMapper, AccessAuditRepository accessAuditRepo, UserRepository userRepo) {
		this.restClient = restClient;
		this.cacheService = cacheService;
		this.objectMapper = objectMapper;
		this.accessAuditRepo = accessAuditRepo;
		this.userRepo = userRepo;
	}

	public WeatherResponseDto info(WeatherRequestDto weatherRequestDto) {
		String city = weatherRequestDto.getCity();
		String state = weatherRequestDto.getState();
		String country = weatherRequestDto.getCountry();
		WeatherResponseDto ifPresent = cacheService.getIfPresent(weatherRequestDto);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String name = authentication.getName();
		User user = userRepo.findByUsername(name).get();
		accessAuditRepo.save(new AccessAudit(user.getId(),name,user.getEmail(),"/weather/getInfo",Status.SUCCESS,LocalDateTime.now()));
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
