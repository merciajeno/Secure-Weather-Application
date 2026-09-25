package com.mercia.weather;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.mercia.weather.config.SecurityConfig;
import com.mercia.weather.controller.CityController;
import com.mercia.weather.service.CityService;
import com.mercia.weather.service.JwtService;

@WebMvcTest(CityController.class)
@Import(SecurityConfig.class)
class CityControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CityService cityService;

	@MockitoBean
	private JwtService jwtService;

	@Test
	void getCities_whenNotAuthenticated_shouldReturnUnauthorized() throws Exception {

		mockMvc.perform(get("/city/all")).andExpect(status().is(403));
	}
	
    // only admin can add the city
	@Test
	void addCity_whenAdmin_shouldBeAccepted() throws Exception {

		mockMvc.perform(
				post("/city").with(user("admin").roles("ADMIN"))
				 .with(csrf())
				.contentType(MediaType.APPLICATION_JSON).content("""

						{
						  "cityName": "Mountain View",
						  "state": "California",
						  "country": "US"
						}
						                        """)).andExpect(status().isOk());
	}
	
	// user role should be rejected here
	@Test
	void addCity_whenUser_shouldBeRejected() throws Exception {

	    mockMvc.perform(
	            post("/city")
	                .with(user("john").roles("USER"))
	                .with(csrf())
	                .contentType(MediaType.APPLICATION_JSON)
	                .content("""
	                    {
	                      "cityName": "Mountain View",
	                      "state": "California",
	                      "country": "US"
	                    }
	                    """))
	    .andExpect(status().is(403));
	}
	
	
}