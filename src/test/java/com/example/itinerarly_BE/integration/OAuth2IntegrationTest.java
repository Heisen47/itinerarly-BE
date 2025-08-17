package com.example.itinerarly_BE.integration;

import com.example.itinerarly_BE.model.User;
import com.example.itinerarly_BE.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class OAuth2IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "integration-test@example.com")
    void shouldHandleCompleteOAuth2FlowIntegration() throws Exception {
        // Given - Create a user as if they came through OAuth2
        User user = new User();
        user.setEmail("integration-test@example.com");
        user.setName("Integration Test User");
        user.setUsername("integration-test");
        user.setProvider("google");
        user.setOauthId("oauth-12345");
        user.setAvatarUrl("https://example.com/avatar.jpg");
        user.setDailyTokens(6);
        user.setLastTokenRefresh(LocalDate.now());
        user.setLoginTime(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")));

        userRepository.save(user);

        // When & Then - Test full user profile access
        mockMvc.perform(get("/api/v1/user/profile")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("integration-test@example.com"))
                .andExpect(jsonPath("$.provider").value("google"))
                .andExpect(jsonPath("$.avatarUrl").value("https://example.com/avatar.jpg"));

        // Test token usage
        mockMvc.perform(post("/api/v1/tokens/use")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Verify token was decremented
        mockMvc.perform(get("/api/v1/user/tokens")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dailyTokens").value(5));
    }

    @Test
    void shouldAllowAccessToPublicEndpoints() throws Exception {
        mockMvc.perform(get("/api/v1/start"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/test"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRedirectToOAuth2ProvidersCorrectly() throws Exception {
        mockMvc.perform(get("/oauth2/authorization/google"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/oauth2/authorization/github"))
                .andExpect(status().is3xxRedirection());
    }
}
