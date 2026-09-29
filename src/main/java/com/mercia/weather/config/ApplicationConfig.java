package com.mercia.weather.config;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestClient;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mercia.weather.dto.WeatherRequestDto;
import com.mercia.weather.dto.WeatherResponseDto;
import com.mercia.weather.filter.JwtFilter;

@Configuration
public class ApplicationConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
		http.csrf(csrf -> csrf.disable()).cors(cors -> cors.configure(http))
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				.authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.POST, "/city/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/city/**").hasRole("ADMIN")
						.requestMatchers("/swagger-ui/**", "/v3/api-docs/**","/swagger-ui.html").permitAll()
						.requestMatchers(HttpMethod.PUT, "/city/**").hasRole("ADMIN")
						.requestMatchers("/auth/register", "/auth/login", "/auth/me").permitAll()
						.requestMatchers("/weather/**").hasAnyRole("ADMIN", "USER").requestMatchers("/audit/**")
						.hasRole("ADMIN")

						// frontend stuffs
						.requestMatchers("/", "/index.html", "/login.html", "/register.html").permitAll()
						.requestMatchers("/admin.html").hasRole("ADMIN").requestMatchers("/css/**", "/favicon.ico")
						.permitAll().anyRequest().authenticated()

				);

		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	HttpClient httpClient() {
		return HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
	}

	@Bean
	ClientHttpRequestFactory getClientHttpRequestFactory() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setReadTimeout(6000);
		factory.setConnectTimeout(6000);
		return factory;
	}

	@Bean
	RestClient restClient(HttpClient httpClient, ClientHttpRequestFactory clientRequestFactory) {

		return RestClient.builder().requestFactory(clientRequestFactory).baseUrl("http://api.openweathermap.org")
				.build();

	}

	@Bean
	Cache<WeatherRequestDto, WeatherResponseDto> cache() {
		return Caffeine.newBuilder().maximumSize(200).expireAfterWrite(10, TimeUnit.MINUTES).build();
	}

}
