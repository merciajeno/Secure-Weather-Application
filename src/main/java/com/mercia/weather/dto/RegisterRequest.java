package com.mercia.weather.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

	@NotBlank @NotNull
	private String username;
	
	@Email(
		    regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",// email pattern matching
		    message = "Email format must be like user@example.com"
		)
	private String email;
	
	@NotBlank @NotNull
	private String password;
}
