package com.mercia.weather.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mercia.weather.dto.WeatherRequestDto;
import com.mercia.weather.dto.WeatherResponseDto;
import com.mercia.weather.service.WeatherService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/weather")
@Tag(name="weather information of the cities")
public class WeatherController {

	private final WeatherService weatherService;

	public WeatherController(WeatherService weatherService) {
		this.weatherService = weatherService;
	}

	@PostMapping("/getInfo")
	public ResponseEntity<WeatherResponseDto> getInfo(@RequestBody WeatherRequestDto weatherRequestDto) {

		WeatherResponseDto response = weatherService.getWeatherDetails(weatherRequestDto);
		return ResponseEntity.ok().body(response);
	}
}
