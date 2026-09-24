package com.mercia.weather.exception;

public class ResourceNotFoundException extends RuntimeException{

	public ResourceNotFoundException(String message)
	{
		super(message);
	}
}
