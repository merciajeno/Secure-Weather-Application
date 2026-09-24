package com.mercia.weather.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtService {

	private final SecretKey secretKey = Keys
			.hmacShaKeyFor("this-is-a-demo-secret-key-that-is-long-enough-123456".getBytes(StandardCharsets.UTF_8));

	public String generateToken(String username) {

		long now = System.currentTimeMillis();

		return Jwts.builder().subject(username).issuedAt(new Date(now)).expiration(new Date(now + 1000 * 60 * 15))// 15
																													// minutes
				.signWith(secretKey).compact();
	}

	public String extractUsername(String token) {
		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
	}
}
