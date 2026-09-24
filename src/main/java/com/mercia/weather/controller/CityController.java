package com.mercia.weather.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mercia.weather.entities.City;
import com.mercia.weather.service.CityService;

@RestController
@RequestMapping("/city")
public class CityController {

	private final CityService cityService;

	CityController(CityService cityService) {
		this.cityService = cityService;
	}

	@GetMapping("/all")
	public ResponseEntity<List<City>> getCities() {
		return cityService.getAllCities();
	}

	@PostMapping
	public ResponseEntity<String> addCity(@RequestBody City city) {
		return cityService.addCity(city);
	}

	@PutMapping("/{id}")
	public ResponseEntity<String> updateCity(@RequestBody City city, @PathVariable Long id) {
		return cityService.updateCity(city, id);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteCity(@PathVariable Long id) {
		return cityService.deleteCity(id);
	}
}
