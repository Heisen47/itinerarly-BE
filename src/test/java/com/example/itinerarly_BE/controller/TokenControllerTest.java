package com.example.itinerarly_BE.controller;

import com.example.itinerarly_BE.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TokenController.class)
@ActiveProfiles("test")
class TokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TokenService tokenService;

    @Test
    void shouldGetRemainingTokensSuccessfully() throws Exception {
        // Given
        when(tokenService.getRemainingTokens(anyString())).thenReturn(5);

        // When & Then
        mockMvc.perform(get("/api/v1/tokens/remaining")
                .with(oauth2Login().attributes(attrs -> {
                    attrs.put("sub", "test-oauth-id");
                    attrs.put("email", "test@example.com");
                })))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"remainingTokens\": 5}"));
    }

    @Test
    void shouldConsumeTokenSuccessfully() throws Exception {
        // Given
        when(tokenService.consumeToken(anyString())).thenReturn(true);
        when(tokenService.getRemainingTokens(anyString())).thenReturn(4);

        // When & Then
        mockMvc.perform(post("/api/v1/tokens/consume")
                .with(csrf())
                .with(oauth2Login().attributes(attrs -> {
                    attrs.put("sub", "test-oauth-id");
                    attrs.put("email", "test@example.com");
                })))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.remainingTokens").value(4));
    }

    @Test
    void shouldReturnForbiddenWhenNoTokensAvailable() throws Exception {
        // Given
        when(tokenService.consumeToken(anyString())).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/v1/tokens/consume")
                .with(csrf())
                .with(oauth2Login().attributes(attrs -> {
                    attrs.put("sub", "test-oauth-id");
                    attrs.put("email", "test@example.com");
                })))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("No tokens remaining for today"));
    }

    @Test
    void shouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
        mockMvc.perform(post("/api/v1/tokens/consume")
                .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnUnauthorizedForRemainingTokensWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/tokens/remaining"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldHandleMissingOAuthIdGracefully() throws Exception {
        // Test with OAuth2User that doesn't have 'sub' or 'id' attribute
        mockMvc.perform(get("/api/v1/tokens/remaining")
                .with(oauth2Login().attributes(attrs -> {
                    attrs.put("email", "test@example.com");
                    // No 'sub' or 'id' attribute
                })))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"error\": \"Unable to extract OAuth ID\"}"));
    }
}
