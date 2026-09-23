package com.mercia.weather.controller;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/weather")
public class WeatherController {

	@Value("${api.key}")
	private String apiKey;
	
	private final RestClient restClient;
	
	public WeatherController(RestClient restClient)
	{
		this.restClient = restClient;
	}
	
	@GetMapping("/getInfo")
	public @Nullable String getInfo()
	{
		return restClient.get()
				.uri(uriBuilder -> uriBuilder
                .path("/data/2.5/weather")
                .queryParam("q", "Bengaluru")
                .queryParam("units", "metric")
                .queryParam("appid", apiKey)
                .build())
        .retrieve()
        .body(String.class);
	}
}
