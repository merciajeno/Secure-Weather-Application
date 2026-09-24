package com.mercia.weather.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mercia.weather.entities.City;
import com.mercia.weather.exception.ResourceAlreadyExists;
import com.mercia.weather.repository.CityRepository;

@RestController
@RequestMapping("/city")
public class CityController {

	private final CityRepository cityRepo;

	CityController(CityRepository cityRepo) {
		this.cityRepo = cityRepo;
	}
	
	@GetMapping("/getCity")
	public  String city()
	{
		return "Mercia is a good girl";
	}
	
	@PostMapping
	public ResponseEntity<String> addCity(@RequestBody City city)
	{
		Optional<City> byNameStateCountry = cityRepo.findByNameStateCountry(city.getCityName(), city.getState(), city.getCountry());
		if(byNameStateCountry.isPresent())
			throw new ResourceAlreadyExists("City is already present");
		cityRepo.save(city);
		return ResponseEntity.ok().body("City is saved");
	}
}
