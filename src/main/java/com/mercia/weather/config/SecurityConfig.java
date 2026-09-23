package com.mercia.weather.config;

import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestClient;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mercia.weather.filter.JwtFilter;

@Configuration
public class SecurityConfig {

	
	@Bean SecurityFilterChain securityFilterChain(HttpSecurity http,JwtFilter jwtFilter) throws Exception {
        http
        .csrf(csrf -> csrf
                .disable())
        .addFilterBefore(jwtFilter,UsernamePasswordAuthenticationFilter.class)
            
            .authorizeHttpRequests(auth -> auth
            
                .requestMatchers("/auth/register","/auth/login").permitAll()
                .requestMatchers("/city/**").hasRole("ADMIN")
                .requestMatchers("/weather/**").permitAll()
                .requestMatchers("/","/index.html").permitAll()
                .requestMatchers("/css/**").permitAll()
                .anyRequest().authenticated()
                
                
            );

        return http.build();
    }
	
	@Bean
	PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	RestClient restClient()
	{
		return RestClient.builder()
				.baseUrl("http://api.openweathermap.org")
				.build();
	}
	
	@Bean
	Cache<String, String> cache()
	{
		return Caffeine.newBuilder()
	            .maximumSize(200)
	            .expireAfterWrite(10, TimeUnit.MINUTES)
	            .build();
	}
}
