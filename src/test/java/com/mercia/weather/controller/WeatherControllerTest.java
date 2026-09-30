package com.mercia.weather.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.mercia.weather.config.ApplicationConfig;
import com.mercia.weather.dto.WeatherResponseDto;
import com.mercia.weather.exception.UnavailableCity;
import com.mercia.weather.filter.AccessAuditFilter;
import com.mercia.weather.service.JwtService;
import com.mercia.weather.service.WeatherService;

@WebMvcTest(controllers = WeatherController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AccessAuditFilter.class))
@Import(ApplicationConfig.class)
public class WeatherControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private WeatherService weatherService;

	@MockitoBean
	private JwtService jwtService;

	@Test
	void getInfo_whenNotAuthenticated_shouldReturnUnauthorized() throws Exception {

		mockMvc.perform(post("/weather/getInfo").contentType(MediaType.APPLICATION_JSON).content("""
				{
				  "city": "Mountain View",
				  "state": "California",
				  "country": "US"
				}
				""")).andExpect(status().isForbidden());
	}

	@Test
	void getInfo_whenAuthenticated_shouldBeAccepted() throws Exception {
		when(weatherService.getWeatherDetails(any())).thenReturn(new WeatherResponseDto());
		mockMvc.perform(post("/weather/getInfo").with(user("admin").roles("ADMIN")).with(csrf())
				.contentType(MediaType.APPLICATION_JSON).content("""

						{
						  "city": "Mountain View",
						  "state": "California",
						  "country": "US"
						}
						                        """)).andExpect(status().isOk());

	}

	@Test
	void getInfo_whenCityUnavailable_shouldBeRejected() throws Exception {
		when(weatherService.getWeatherDetails(any())).thenThrow(new UnavailableCity("city is not found"));
		mockMvc.perform(post("/weather/getInfo").with(user("admin").roles("ADMIN")).with(csrf())
				.contentType(MediaType.APPLICATION_JSON).content("""

						{
						  "city": "Mountain View",
						  "state": "California",
						  "country": "US"
						}
						                        """)).andExpect(status().isBadRequest());
	}
}
