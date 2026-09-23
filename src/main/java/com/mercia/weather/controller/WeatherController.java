package com.mercia.weather.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mercia.weather.dto.WeatherRequestDto;
import com.mercia.weather.dto.WeatherResponseDto;
import com.mercia.weather.service.WeatherService;

@RestController
@RequestMapping("/weather")
public class WeatherController {

	private final WeatherService weatherService;
	
	public WeatherController(WeatherService weatherService)
	{
		this.weatherService = weatherService;
	}
	
	@GetMapping("/getInfo")
	public ResponseEntity<WeatherResponseDto> getInfo(@RequestBody WeatherRequestDto weatherRequestDto)
	{
		
	   WeatherResponseDto response = weatherService.info(weatherRequestDto);
	   return ResponseEntity.ok().body(response);
	}
}
