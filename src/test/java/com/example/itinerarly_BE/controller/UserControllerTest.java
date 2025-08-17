package com.example.itinerarly_BE.controller;

import com.example.itinerarly_BE.model.User;
import com.example.itinerarly_BE.repository.UserRepository;
import com.example.itinerarly_BE.service.TokenRefreshService;
import com.example.itinerarly_BE.service.TokenService;
import com.example.itinerarly_BE.utl.JwtTokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
class UserControllerTest {

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

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setUsername("testuser");
        testUser.setProvider("google");
        testUser.setOauthId("12345");
        testUser.setDailyTokens(5);
        testUser.setLastTokenRefresh(LocalDate.now());
        testUser.setLoginTime(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldReturnUserProfileWhenAuthenticated() throws Exception {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(get("/api/v1/user/profile")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.provider").value("google"));
    }

    @Test
    void shouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/user/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    void shouldReturnTokenStatusWhenAuthenticated() throws Exception {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(get("/api/v1/user/tokens")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dailyTokens").value(5))
                .andExpect(jsonPath("$.lastTokenRefresh").exists());
    }

    @Test
    @WithMockUser(username = "nonexistent@example.com")
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/user/profile")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }
}
