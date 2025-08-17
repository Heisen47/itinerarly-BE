package com.example.itinerarly_BE.controller;

import com.example.itinerarly_BE.model.User;
import com.example.itinerarly_BE.repository.UserRepository;
import com.example.itinerarly_BE.service.TokenRefreshService;
import com.example.itinerarly_BE.service.TokenService;
import com.example.itinerarly_BE.utl.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TokenController.class)
@ActiveProfiles("test")
class TokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private TokenRefreshService tokenRefreshService;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setDailyTokens(5);
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldUseTokenSuccessfully() throws Exception {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(tokenService.useToken(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/v1/tokens/use")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Token used successfully"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldReturnBadRequestWhenNoTokensAvailable() throws Exception {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(tokenService.useToken(1L)).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/v1/tokens/use")
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("No tokens available"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldCheckTokenAvailabilitySuccessfully() throws Exception {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(tokenService.hasTokensAvailable(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/v1/tokens/available")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void shouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        mockMvc.perform(post("/api/v1/tokens/use"))
                .andExpect(status().isUnauthorized());
    }
}
