package com.mercia.weather.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherResponseDto {

	private float temperature;
	private float humidity;
	private float windSpeed;
	private float pressure;
}
