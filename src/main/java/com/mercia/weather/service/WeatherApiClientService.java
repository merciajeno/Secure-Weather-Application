package com.mercia.weather.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.mercia.weather.dto.OpenWeatherResponse;
import com.mercia.weather.dto.WeatherResponseDto;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class WeatherApiClientService {

	private String apiKey;

	private final RestClient restClient;

	public WeatherResponseDto getWeatherDetailsFromApi(String query) {

		OpenWeatherResponse openWeatherResponse = restClient.get()
				.uri(uriBuilder -> uriBuilder.path("/data/2.5/weather").queryParam("q", query)
						.queryParam("units", "metric").queryParam("appid", apiKey).build())
				.retrieve().body(OpenWeatherResponse.class);

		WeatherResponseDto weatherResponseDto = new WeatherResponseDto();
		weatherResponseDto.setTemperature(openWeatherResponse.getMain().getTemp());
		weatherResponseDto.setPressure(openWeatherResponse.getMain().getPressure());
		weatherResponseDto.setHumidity(openWeatherResponse.getMain().getHumidity());
		weatherResponseDto.setWindSpeed(openWeatherResponse.getWind().getSpeed());
		return weatherResponseDto;
	}
}
