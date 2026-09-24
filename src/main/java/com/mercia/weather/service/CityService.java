package com.mercia.weather.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.mercia.weather.entities.Action;
import com.mercia.weather.entities.ChangeAudit;
import com.mercia.weather.entities.City;
import com.mercia.weather.entities.ResourceType;
import com.mercia.weather.exception.ResourceAlreadyExists;
import com.mercia.weather.exception.ResourceNotFoundException;
import com.mercia.weather.repository.ChangeAuditRepository;
import com.mercia.weather.repository.CityRepository;

import tools.jackson.databind.ObjectMapper;

@Service
public class CityService {
	private final CityRepository cityRepo;

	private final ChangeAuditRepository changeAuditRepo;

	private final ObjectMapper objectMapper;

	CityService(CityRepository cityRepo, ChangeAuditRepository changeAuditRepo, ObjectMapper objectMapper) {
		this.cityRepo = cityRepo;
		this.changeAuditRepo = changeAuditRepo;
		this.objectMapper = objectMapper;

	}

	public ResponseEntity<String> addCity(City city) {

		Optional<City> byNameStateCountry = cityRepo.findByNameStateCountry(city.getCityName(), city.getState(),
				city.getCountry());
		if (byNameStateCountry.isPresent())
			throw new ResourceAlreadyExists("City is already present");

		cityRepo.save(city);
		ChangeAudit entity = new ChangeAudit();
		entity.setUpdatedAt(LocalDateTime.now());
		entity.setResourceType(ResourceType.CITY);
		entity.setAction(Action.CITY_ADDED);
		changeAuditRepo.save(entity);
		return ResponseEntity.ok().body("City is saved");
	}

	public ResponseEntity<String> updateCity(City city, Long id) {
		City existingCity = cityRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("City with id:" + id + " is not found"));

		existingCity.setCityName(city.getCityName());
		existingCity.setState(city.getState());
		existingCity.setCountry(city.getCountry());
		existingCity.setCreatedAt(LocalDateTime.now());
		existingCity.setId(city.getId());
		cityRepo.save(existingCity);
		ChangeAudit entity = new ChangeAudit();
		entity.setAction(Action.CITY_UPDATED);
		entity.setOldValue(objectMapper.writeValueAsString(existingCity));
		entity.setNewValue(objectMapper.writeValueAsString(city));
		entity.setResourceType(ResourceType.CITY);
		entity.setUpdatedAt(LocalDateTime.now());
		changeAuditRepo.save(entity);
		return ResponseEntity.status(HttpStatus.ACCEPTED).build();
	}

	public ResponseEntity<String> deleteCity(Long id) {
		City existingCity = cityRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("City with id:" + id + " is not found"));

		cityRepo.delete(existingCity);
		ChangeAudit change = new ChangeAudit();
		change.setAction(Action.CITY_DELETED);
		change.setResourceType(ResourceType.CITY);
		change.setUpdatedAt(LocalDateTime.now());
		changeAuditRepo.save(change);
		return ResponseEntity.status(HttpStatus.ACCEPTED).build();
	}

	public ResponseEntity<List<City>> getAllCities() {
		return ResponseEntity.ok().body(cityRepo.findAll());
	}
}
