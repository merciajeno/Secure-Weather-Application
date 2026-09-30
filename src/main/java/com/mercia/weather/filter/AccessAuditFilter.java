package com.mercia.weather.filter;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.mercia.weather.entities.AccessAudit;
import com.mercia.weather.entities.Status;
import com.mercia.weather.repository.AccessAuditRepository;
import com.mercia.weather.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class AccessAuditFilter extends OncePerRequestFilter {

	private final AccessAuditRepository accessAuditRepository;

	private final UserRepository userRepo;

	public boolean publicApis(String requestURI) {
		return requestURI.contains("/api/v3/docs") || requestURI.contains("/swagger") || requestURI.contains("api-docs")
				|| requestURI.contains("/favicon.ico") || requestURI.contains("/.well-known")
				|| requestURI.contains("html");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		try {
			filterChain.doFilter(request, response);

		} finally {
			String requestURI = request.getRequestURI();
			log.debug(requestURI);
			// Don't audit these endpoints
			if (publicApis(requestURI)) {
				log.debug("Is open endpoint");
				return;
			}

			AccessAudit audit = new AccessAudit();
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null && authentication.isAuthenticated()) {
				String name = authentication.getName();
				log.debug(name);
				userRepo.findByUsername(name).ifPresent(user -> {
					audit.setUserId(user.getId());
					audit.setUserName(user.getUsername());
					audit.setUserEmail(user.getEmail());
				});
			}
			audit.setEndpoint(requestURI);
			audit.setTimeStamp(LocalDateTime.now());
			int status = response.getStatus();
			log.debug(String.format("Status is:%d", status));
			if (status >= 200 && status < 400) {
				audit.setStatus(Status.SUCCESS);
			} else {
				audit.setStatus(Status.FAILED);
			}
			accessAuditRepository.save(audit);
		}
	}
}