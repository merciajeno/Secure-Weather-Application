package com.mercia.weather.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherRequestDto {

	@NotBlank @NotNull
	private String city;
	
	@NotBlank @NotNull
	private String state;
	
	@NotBlank @NotNull
	private String country;
	
}
