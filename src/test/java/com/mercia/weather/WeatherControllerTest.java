package com.mercia.weather;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.mercia.weather.config.SecurityConfig;
import com.mercia.weather.controller.WeatherController;
import com.mercia.weather.service.JwtService;
import com.mercia.weather.service.WeatherService;

@WebMvcTest(WeatherController.class)
@Import(SecurityConfig.class)
public class WeatherControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private WeatherService weatherService;

	@MockitoBean
	private JwtService jwtService;
	
	@Test
	void getInfo_whenNotAuthenticated_shouldReturnUnauthorized() throws Exception {

	    mockMvc.perform(
	            get("/weather/getInfo")
	                .contentType(MediaType.APPLICATION_JSON)
	                .content("""
	                    {
	                      "city": "Mountain View",
	                      "state": "California",
	                      "country": "US"
	                    }
	                    """))
	        .andExpect(status().isForbidden());
	}

}
