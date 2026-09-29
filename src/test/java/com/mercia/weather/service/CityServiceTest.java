package com.mercia.weather.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import com.mercia.weather.entities.ChangeAudit;
import com.mercia.weather.entities.City;
import com.mercia.weather.exception.ResourceAlreadyExists;
import com.mercia.weather.exception.ResourceNotFoundException;
import com.mercia.weather.repository.AccessAuditRepository;
import com.mercia.weather.repository.ChangeAuditRepository;
import com.mercia.weather.repository.CityRepository;

import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {

	@Mock
	private CityRepository cityRepo;

	@Mock
	private ChangeAuditRepository changeAuditRepo;

	@Mock
	private AccessAuditRepository accessAuditRepo;

	@Mock
	private ObjectMapper objectMapper;

	@InjectMocks
	private CityService cityService;

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void getAllCities_shouldReturnAllCities() {

		// sample cities as no db here
		City city1 = new City();
		city1.setId(1L);
		city1.setCityName("Bangalore");

		City city2 = new City();
		city2.setId(2L);
		city2.setCityName("Mumbai");

		List<City> cities = List.of(city1, city2);

		when(cityRepo.findAll()).thenReturn(cities);

		ResponseEntity<List<City>> response = cityService.getAllCities();

		// response status code
		assertEquals(200, response.getStatusCode().value());

		// response body checking
		assertEquals(cities, response.getBody());
	}

	@Test
	void getCities_DbFailure_Failed() {
		when(cityRepo.findAll()).thenThrow(new DataAccessException("Database is facing problem") {
		});
		assertThrows(DataAccessException.class, () -> {
			cityService.getAllCities();
		});
	}

	@Test
	void addCity_ifConfigured_Failed() {

		City city = new City("Bengaluru", "Karnataka", "IN");
		when(cityRepo.findByNameStateCountry("Bengaluru", "Karnataka", "IN")).thenReturn(Optional.of(city));

		assertThrows(ResourceAlreadyExists.class, () -> {
			cityService.addCity(city);
		});

	}

	@Test
	void addCity_ifNotConfigured_Success() {

		City city = new City("Bengaluru", "Karnataka", "IN");

		when(cityRepo.findByNameStateCountry("Bengaluru", "Karnataka", "IN")).thenReturn(Optional.empty());
		ResponseEntity<String> response = cityService.addCity(city);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());

	}

	@Test
	void updateCity_whenExist_success() {
		City city = new City("Bengaluru", "Karnataka", "IN");
		city.setId(1L);

		when(cityRepo.findById(1L)).thenReturn(Optional.of(city));
		ResponseEntity<String> updateCityResponse = cityService.updateCity(city, 1L);

		assertEquals(HttpStatus.CREATED, updateCityResponse.getStatusCode());
		verify(changeAuditRepo).save(any(ChangeAudit.class));
	}

	@Test
	void updateCity_whenNotExist_failed() {
		City city = new City("Bengaluru", "Karnataka", "IN");
		when(cityRepo.findById(2L)).thenReturn(Optional.empty());
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			cityService.updateCity(city, 2L);
		});

		assertEquals("City with id:2 is not found", exception.getMessage());
	}

	@Test
	void deleteCity_whenExist_success() {
		City city = new City("Bengaluru", "Karnataka", "IN");
		city.setId(1L);
		when(cityRepo.findById(1L)).thenReturn(Optional.of(city));
		ResponseEntity<String> deleteCity = cityService.deleteCity(1L);
		assertEquals(HttpStatus.ACCEPTED, deleteCity.getStatusCode());

	}

	@Test
	void deleteCity_whenNotExist_failed() {
		when(cityRepo.findById(1L)).thenReturn(Optional.empty());
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
			cityService.deleteCity(1L);
		});

		assertEquals("City with id:1 is not found", exception.getMessage());
	}
}