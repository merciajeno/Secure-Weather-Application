package com.mercia.weather.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	@ExceptionHandler(ResourceAlreadyExists.class)
	public ResponseEntity<String> handleExistingResource(ResourceAlreadyExists ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<String> userNotFound(UserNotFoundException ex)
	{
		return ResponseEntity.badRequest().body(ex.getMessage());
	}
	
	@ExceptionHandler(UnavailableCity.class)
	public ResponseEntity<String> cityNotIncluded(UnavailableCity ex)
	{
		return ResponseEntity.badRequest().body(ex.getMessage());
	}
}
