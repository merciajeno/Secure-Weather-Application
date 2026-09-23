package com.mercia.weather.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="city")
@Getter
@Setter
@NoArgsConstructor
public class City {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank @NotNull
	private String cityName;
	
	private String country;
	
	private  String state;
	
	private LocalDateTime createdAt;
	
	public City(String cityName,String country, String state,LocalDateTime createdAt)
	{
		this.cityName = cityName;
		this.country = country;
		this.createdAt = createdAt;
		this.state = state;
	}
}
