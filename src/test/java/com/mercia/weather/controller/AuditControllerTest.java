package com.mercia.weather.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.mercia.weather.config.SecurityConfig;
import com.mercia.weather.repository.AccessAuditRepository;
import com.mercia.weather.repository.ChangeAuditRepository;
import com.mercia.weather.service.JwtService;

@WebMvcTest(AuditController.class)
@Import(SecurityConfig.class)
public class AuditControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@MockitoBean
	private AccessAuditRepository accessAuditRepo;

	@MockitoBean
	private ChangeAuditRepository changeAuditRepo;
	
	@MockitoBean
	private JwtService jwtService;

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void getAccessAudit_ifAdmin_success() throws Exception {
		mockMvc.perform(get("/audit/access")).andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void getChangeAudit_ifAdmin_success() throws Exception {
		mockMvc.perform(get("/audit/changing")).andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username="user",roles= {"USER"})
	void getAccessAudit_ifUnauthorised_failed() throws Exception {
		mockMvc.perform(get("/audit/access")).andExpect(status().isForbidden());
	}
	
	@Test
	@WithMockUser(username="user",roles= {"USER"})
	void getChangeAudit_ifUnauthorised_failed() throws Exception {
		mockMvc.perform(get("/audit/changing")).andExpect(status().isForbidden());
	}
	
	
}
