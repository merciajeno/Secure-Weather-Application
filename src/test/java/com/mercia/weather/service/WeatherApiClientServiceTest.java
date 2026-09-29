package com.mercia.weather.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import com.mercia.weather.dto.Main;
import com.mercia.weather.dto.OpenWeatherResponse;
import com.mercia.weather.dto.WeatherResponseDto;
import com.mercia.weather.dto.Wind;

@ExtendWith(MockitoExtension.class)
public class WeatherApiClientServiceTest {

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private RestClient restClient;

	@InjectMocks
	private WeatherApiClientService weatherApiClientService;


	@Test
	void getWeatherDetailsFromApi_success() {

		// 1. Create fake API response
		OpenWeatherResponse response = new OpenWeatherResponse();

		Main main = new Main();
		main.setTemp(25.5f);
		main.setPressure(1012);
		main.setHumidity(80);

		Wind wind = new Wind();
		wind.setSpeed(4.5f);

		response.setMain(main);
		response.setWind(wind);

		// 2. Tell RestClient to return our fake response
		when(restClient.get().uri(ArgumentMatchers.<Function<UriBuilder, URI>>any()).retrieve()
				.body(OpenWeatherResponse.class)).thenReturn(response);

		// 3. Call your actual method
		WeatherResponseDto result = weatherApiClientService.getWeatherDetailsFromApi("Bangalore,Karnataka,IN");

		// 4. Check the mapping
		assertEquals(25.5, result.getTemperature());
		assertEquals(1012, result.getPressure());
		assertEquals(80, result.getHumidity());
		assertEquals(4.5, result.getWindSpeed());
	}

}
