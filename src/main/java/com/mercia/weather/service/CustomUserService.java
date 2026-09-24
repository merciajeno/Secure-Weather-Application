package com.mercia.weather.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mercia.weather.entities.User;
import com.mercia.weather.exception.UserNotFoundException;
import com.mercia.weather.repository.UserRepository;

@Service
public class CustomUserService implements UserDetailsService {

	private final UserRepository userRepository;

	CustomUserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User name:" + username + " not found"));
		return org.springframework.security.core.userdetails.User.builder().username(username)
				.password(user.getPassword()).roles(user.getRole().toString()).build();

	}

}
