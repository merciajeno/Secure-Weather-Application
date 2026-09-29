package com.mercia.weather.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class JwtServiceTest {

	@Autowired
	private JwtService jwtService;

	@Test
	void shouldGenerateAndExtractUsername() {

		String username = "john";
		String token = jwtService.generateToken(username);
		assertNotNull(token);
		assertEquals(username, jwtService.extractUsername(token));
	}

//	@Test
//	void shouldReject_TamperedJwt() {
//		String username = "john";
//		String token = jwtService.extractUsername(username).replace('e', '2');
//		assertThrows(Exception.class, () -> {
//			jwtService.extractUsername(token);
//		});
//	}
}