package com.mercia.weather.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/city")
public class CityController {

	@GetMapping("/getCity")
	public  String city()
	{
		return "Mercia is a good girl";
	}
}
