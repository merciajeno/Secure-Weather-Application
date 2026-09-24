package com.mercia.weather.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mercia.weather.entities.City;

@Repository
public interface CityRepository extends JpaRepository<City, Long>{

	@Query("SELECT c FROM City c WHERE c.cityName=?1 AND c.state=?2 AND c.country=?3")
	Optional<City> findByNameStateCountry(String city,String state,String country);
}
