package com.mercia.weather.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.mercia.weather.filter.JwtFilter;
import com.mercia.weather.repository.AccessAuditRepository;
import com.mercia.weather.repository.ChangeAuditRepository;

@WebMvcTest(AuditController.class)
public class AuditControllerTest {

	@MockitoBean
	private AccessAuditRepository accessAuditRepo;

	@MockitoBean
	private ChangeAuditRepository changeAuditRepo;

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private JwtFilter jwtFilter;

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void getAccessAudit_ifAdmin_success() throws Exception {
		mockMvc.perform(get("/audit/access")).andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "admin", roles = { "ADMIN" })
	void getChangeAudit_ifAdmin_success() throws Exception {
		mockMvc.perform(get("/audit/access")).andExpect(status().isOk());
	}

}
