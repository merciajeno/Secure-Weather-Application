package com.mercia.weather.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

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
	public ResponseEntity<String> userNotFound(UserNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	@ExceptionHandler(UnavailableCity.class)
	public ResponseEntity<String> cityNotIncluded(UnavailableCity ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}

	@ExceptionHandler(ResourceAccessException.class)
	public ResponseEntity<String> handleResourceAccess(ResourceAccessException ex) {

		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("External service is unavailable");
	}

	@ExceptionHandler(HttpServerErrorException.class)
	public ResponseEntity<String> handleExternalAPIFailure(HttpServerErrorException ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Service is down");
	}

	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<String> handleDBException(DataAccessException e) {
		return ResponseEntity.internalServerError().body("Database is facing problem");
	}
}
