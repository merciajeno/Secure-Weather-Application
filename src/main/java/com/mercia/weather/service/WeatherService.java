package com.mercia.weather.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.mercia.weather.dto.WeatherRequestDto;
import com.mercia.weather.dto.WeatherResponseDto;
import com.mercia.weather.entities.City;
import com.mercia.weather.exception.UnavailableCity;
import com.mercia.weather.repository.CityRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class WeatherService {

	private final CacheService cacheService;

	private final CityRepository cityRepo;

	private final WeatherApiClientService weatherApiClientService;

	@Transactional()
	public WeatherResponseDto getWeatherDetails(WeatherRequestDto weatherRequestDto) {
		String city = weatherRequestDto.getCity();
		String state = weatherRequestDto.getState();
		String country = weatherRequestDto.getCountry();
		log.info(String.format("Requested:%s ,%s, %s", city, state, country));

		Optional<City> byNameStateCountry = cityRepo.findByNameStateCountry(city, state, country);
		if (byNameStateCountry.isEmpty()) {

			byNameStateCountry.orElseThrow(() -> new UnavailableCity("City you have requested is unavailable"));
		}
		WeatherResponseDto ifPresent = cacheService.getWeatherDetailsIfPresent(weatherRequestDto);

		if (ifPresent != null) {

			return ifPresent;
		}
		String query = String.format("%s, %s, %s", city, state, country);

		try {

			WeatherResponseDto weatherResponseDto = weatherApiClientService.getWeatherDetailsFromApi(query);
			cacheService.addToCache(weatherRequestDto, weatherResponseDto);
			return weatherResponseDto;

		} catch (Exception e)// if the requested city present in the db but not in the api
		{

			throw new UnavailableCity("Weather information is currently unavailable");
			// handled in global exception but need to audit so the exception is handled
			// here.

		}
	}
}
