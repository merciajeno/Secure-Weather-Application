package com.mercia.weather.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class JwtService {

	private final SecretKey secretKey;

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
