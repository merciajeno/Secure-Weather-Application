package com.mercia.weather.controller;

import org.jspecify.annotations.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public @Nullable String getInfo()
	{
		return weatherService.info();
	}
}
