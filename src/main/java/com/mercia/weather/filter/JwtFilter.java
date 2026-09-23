package com.mercia.weather.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mercia.weather.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter{

	private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
  

    public JwtFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
		
    }
    
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String authHeader = request.getHeader("Authorization");
		log.info(
				 "JWT FILTER -> " +
			        	    request.getMethod() + " " +
			        	    request.getRequestURI());
		
		if(authHeader==null || !authHeader.startsWith("Bearer"))
		{
			filterChain.doFilter(request, response);
			return;
		}
		
		// to remove Bearer
		String token =authHeader.substring(7);
		
		String username = jwtService.extractUsername(token);
		
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		
		 Authentication authentication =
	                new UsernamePasswordAuthenticationToken(
	                        userDetails,
	                        null,
	                        userDetails.getAuthorities()
	                );

		
		SecurityContext context = SecurityContextHolder.getContext();
		context.setAuthentication(authentication);
		
	    filterChain.doFilter(request, response);
	}

}
