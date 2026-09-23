package com.mercia.weather.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
                .anyRequest().authenticated()
                
                
            );

        return http.build();
    }
	
	@Bean
	PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
}
