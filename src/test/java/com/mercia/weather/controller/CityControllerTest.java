package com.mercia.weather.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.mercia.weather.config.ApplicationConfig;
import com.mercia.weather.entities.City;
import com.mercia.weather.exception.ResourceAlreadyExists;
import com.mercia.weather.filter.AccessAuditFilter;
import com.mercia.weather.service.CityService;
import com.mercia.weather.service.JwtService;

@WebMvcTest(controllers = CityController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AccessAuditFilter.class))
@Import(ApplicationConfig.class)
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

	@Test
	void getCities_whenAuthenticated_shouldBeAccepted() throws Exception {

		when(cityService.getAllCities()).thenReturn(ResponseEntity.ok(List.of(new City())));
		mockMvc.perform(get("/city/all").with(user("admin").roles("ADMIN"))).andExpect(status().isOk());
	}

	@Test
	void getCities_whenServiceThrowsException_shouldTriggerExceptionHandler() throws Exception {

		when(cityService.getAllCities()).thenThrow(new DataAccessException("Something went wrong") {
		});
		mockMvc.perform(get("/city/all").with(user("admin").roles("ADMIN")))
				// Change this status to match what your GlobalExceptionHandler returns!
				.andExpect(status().isInternalServerError());
	}

	// only admin can add the city
	@Test
	void addCity_whenAdmin_shouldBeAccepted() throws Exception {

		when(cityService.addCity(any(City.class))).thenReturn(ResponseEntity.ok("City added"));
		mockMvc.perform(post("/city").with(user("admin").roles("ADMIN")).with(csrf())
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

		mockMvc.perform(post("/city").with(user("john").roles("USER")).with(csrf())
				.contentType(MediaType.APPLICATION_JSON).content("""
						{
						  "cityName": "Mountain View",
						  "state": "California",
						  "country": "US"
						}
						""")).andExpect(status().is(403));
	}

	@Test
	void addCityByAdmin_whenExists_shouldBeRejected() throws Exception {
		when(cityService.addCity(any(City.class))).thenThrow(new ResourceAlreadyExists("City already exists"));
		mockMvc.perform(post("/city").with(user("admin").roles("ADMIN")).with(csrf())
				.contentType(MediaType.APPLICATION_JSON).content("""

						{
						  "cityName": "Mountain View",
						  "state": "California",
						  "country": "US"
						}
						                        """)).andExpect(status().isBadRequest());
	}

	@Test
	void updateCity_whenUser_shouldBeRejected() throws Exception {
		mockMvc.perform(put("/city/5").with(user("john").roles("USER")).with(csrf())
				.contentType(MediaType.APPLICATION_JSON).content("""
						{
						  "cityName": "Mountain View",
						  "state": "California",
						  "country": "US"
						}
						""")).andExpect(status().is(403));
	}

	@Test
	void updateCity_whenAdmin_shouldBeAccepted() throws Exception {
		when(cityService.updateCity(any(City.class), eq(5L))).thenReturn(ResponseEntity.ok("City updated"));
		mockMvc.perform(put("/city/5").with(user("admin").roles("ADMIN")).with(csrf())
				.contentType(MediaType.APPLICATION_JSON).content("""
						{
						  "cityName": "Mountain View",
						  "state": "California",
						  "country": "US"
						}
						""")).andExpect(status().isOk());
	}

	@Test
	void deleteCity_whenAdmin_shouldBeAccepted() throws Exception {
		when(cityService.deleteCity(5L)).thenReturn(ResponseEntity.ok("City deleted"));
		mockMvc.perform(delete("/city/5").with(user("admin").roles("ADMIN")).with(csrf())).andExpect(status().isOk());
	}

	@Test
	void deleteCity_whenUser_shouldBeRejected() throws Exception {
		mockMvc.perform(delete("/city/5").with(user("john").roles("USER")).with(csrf()))
				.andExpect(status().isForbidden());
	}

}