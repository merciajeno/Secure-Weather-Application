package com.mercia.weather.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
import com.mercia.weather.exception.ResourceAlreadyExists;
import com.mercia.weather.exception.ResourceNotFoundException;
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
	
	@PutMapping("/{id}")
	public ResponseEntity<String> updateCity(@RequestBody City city,@PathVariable Long id)
	{
		City existingCity = cityRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("City with id:"+id+" is not found"));
		
		existingCity.setCityName(city.getCityName());
		existingCity.setState(city.getState());
		existingCity.setCountry(city.getCountry());
		existingCity.setCreatedAt(LocalDateTime.now());
		cityRepo.save(existingCity);
		return ResponseEntity.status(HttpStatus.ACCEPTED).build();
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteCity(@PathVariable Long id)
	{
		City existingCity = cityRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("City with id:"+id+" is not found"));
		
		cityRepo.delete(existingCity);
		return ResponseEntity.status(HttpStatus.ACCEPTED).build();
	}
}
