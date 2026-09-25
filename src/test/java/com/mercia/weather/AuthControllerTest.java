package com.mercia.weather;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.mercia.weather.config.SecurityConfig;
import com.mercia.weather.controller.AuthController;
import com.mercia.weather.service.AuthService;
import com.mercia.weather.service.JwtService;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
public class AuthControllerTest {

	@Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
	private JwtService jwtService;
    
   

    @Test
    void register_whenNotAuthenticated_shouldBeAllowed() throws Exception {

        mockMvc.perform(
                post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "username": "john",
                          "email":"merc@co.in",
                          "password": "12345"
                        }
                        """))
            .andExpect(status().isOk());
    }
    
    @Test
    void register_InvalidEmail_shouldBeRejected() throws Exception {

        mockMvc.perform(
                post("/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""
                        {
                          "username": "john",
                          "email":"merc",
                          "password": "12345"
                        }
                        """))
            .andExpect(status().is(400));
    }
    
    
}

