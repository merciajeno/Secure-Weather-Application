package com.mercia.weather.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.mercia.weather.dto.WeatherRequestDto;
import com.mercia.weather.dto.WeatherResponseDto;
import com.mercia.weather.entities.AccessAudit;
import com.mercia.weather.entities.City;
import com.mercia.weather.entities.Status;
import com.mercia.weather.entities.User;
import com.mercia.weather.exception.UnavailableCity;
import com.mercia.weather.repository.AccessAuditRepository;
import com.mercia.weather.repository.CityRepository;
import com.mercia.weather.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class WeatherService {

	private final CacheService cacheService;

	private final AccessAuditRepository accessAuditRepo;

	private final UserRepository userRepo;

	private final CityRepository cityRepo;

	private final WeatherApiClientService weatherApiClientService;

	@Transactional(dontRollbackOn = UnavailableCity.class)
	public WeatherResponseDto getWeatherDetails(WeatherRequestDto weatherRequestDto) {
		String city = weatherRequestDto.getCity();
		String state = weatherRequestDto.getState();
		String country = weatherRequestDto.getCountry();
         log.info(String.format("Requested:%s ,%s, %s", city,state,country));
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String name = authentication.getName();
		User user = userRepo.findByUsername(name).get();
		AccessAudit entity = new AccessAudit(user.getId(), name, user.getEmail(), "/weather/getInfo", null,
				LocalDateTime.now());
		Optional<City> byNameStateCountry = cityRepo.findByNameStateCountry(city, state, country);
		if (byNameStateCountry.isEmpty()) {
			entity.setActionDetails("user tried to fetch unavailable city");
			entity.setStatus(Status.FAILED);
			accessAuditRepo.save(entity);

			byNameStateCountry.orElseThrow(() -> new UnavailableCity("City you have requested is unavailable"));
		}
		WeatherResponseDto ifPresent = cacheService.getWeatherDetailsIfPresent(weatherRequestDto);

		entity.setActionDetails(String.format("User fetched details for %s,%s,%s", city, state, country));
		entity.setStatus(Status.SUCCESS);
		accessAuditRepo.save(entity);
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
			entity.setActionDetails(
					String.format("Weather API failed for %s,%s,%s. Please check if changed in the external API.", city,
							state, country));

			entity.setStatus(Status.FAILED);
			accessAuditRepo.save(entity);
			throw new UnavailableCity("Weather information is currently unavailable");
			// handled in global exception but need to audit so the exception is handled
			// here.

		}
	}
}
