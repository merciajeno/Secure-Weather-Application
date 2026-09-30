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

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
@AllArgsConstructor
public class CityService {
	private final CityRepository cityRepo;
	private final ChangeAuditRepository changeAuditRepo;
	private final ObjectMapper objectMapper;

	@Transactional(dontRollbackOn = ResourceAlreadyExists.class)
	public ResponseEntity<String> addCity(City city) {
		String cityName = city.getCityName();
		String state = city.getState();
		String country = city.getCountry();
		System.out.println(cityName);
		System.out.println(state);
		System.out.println(country);

		Optional<City> byNameStateCountry = cityRepo.findByNameStateCountry(cityName, state, country);
		if (byNameStateCountry.isPresent()) {

			log.warn("You are adding a city that is already configured");
			throw new ResourceAlreadyExists("City is already present");
		}
		cityRepo.save(city);
		log.info("City is added");
		return ResponseEntity.status(201).body("City is saved");
	}

	@Transactional
	public ResponseEntity<String> updateCity(City city, Long id) {
		City existingCity = cityRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("City with id:" + id + " is not found"));
		ChangeAudit entity = new ChangeAudit();

		String cityName = city.getCityName();
		String state = city.getState();
		String country = city.getCountry();

		existingCity.setCityName(cityName);
		existingCity.setState(state);
		existingCity.setCountry(country);
		existingCity.setCreatedAt(LocalDateTime.now());
		city.setId(existingCity.getId());
		cityRepo.save(existingCity);

		entity.setAction(Action.CITY_UPDATED);
		entity.setOldValue(objectMapper.writeValueAsString(existingCity));
		entity.setNewValue(objectMapper.writeValueAsString(city));
		entity.setResourceType(ResourceType.CITY);
		entity.setUpdatedAt(LocalDateTime.now());
		changeAuditRepo.save(entity);
		log.info("city is updated");
		return ResponseEntity.status(201).build();
	}

	@Transactional
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
