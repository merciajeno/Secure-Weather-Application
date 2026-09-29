package com.mercia.weather.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
public class WeatherApiClientServiceTest {

	@Mock
	private RestClient restClient;
	
	@InjectMocks
	private WeatherApiClientService weatherApiClientService;
	
	
	@Test
	void success()
	{
		
	}

}
