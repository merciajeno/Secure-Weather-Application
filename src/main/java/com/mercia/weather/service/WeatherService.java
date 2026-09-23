package com.mercia.weather.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class WeatherService {

	@Value("${api.key}")
	private String apiKey;
	
	private final RestClient restClient;
	
	public WeatherService(RestClient restClient)
	{
		this.restClient = restClient;
	}
	
	public String info()
	{
		return restClient.get()
				.uri(uriBuilder -> uriBuilder
                .path("/data/2.5/weather")
                .queryParam("q", "Bangalore")
                .queryParam("units", "metric")
                .queryParam("appid", apiKey)
                .build())
        .retrieve()
        .body(String.class);
	}
}
