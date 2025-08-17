package com.example.itinerarly_BE.service;

import com.example.itinerarly_BE.config.TokenConfig;
import com.example.itinerarly_BE.model.User;
import com.example.itinerarly_BE.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class TokenServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenConfig tokenConfig;

    @InjectMocks
    private TokenService tokenService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setOauthId("test-oauth-id");
        testUser.setDailyTokens(5);
        testUser.setLastTokenRefresh(LocalDate.now());

        when(tokenConfig.getDailyTokenLimit()).thenReturn(10);
    }

    @Test
    void shouldReturnRemainingTokensWhenUserExists() {
        // Given
        when(userRepository.findByOauthId("test-oauth-id")).thenReturn(Optional.of(testUser));

        // When
        int remainingTokens = tokenService.getRemainingTokens("test-oauth-id");

        // Then
        assertEquals(5, remainingTokens);
    }

    @Test
    void shouldReturnZeroWhenUserNotFound() {
        // Given
        when(userRepository.findByOauthId("nonexistent-id")).thenReturn(Optional.empty());

        // When
        int remainingTokens = tokenService.getRemainingTokens("nonexistent-id");

        // Then
        assertEquals(0, remainingTokens);
    }

    @Test
    void shouldConsumeTokenSuccessfully() {
        // Given
        when(userRepository.findByOauthId("test-oauth-id")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        boolean result = tokenService.consumeToken("test-oauth-id");

        // Then
        assertTrue(result);
        verify(userRepository).save(argThat(user -> user.getDailyTokens() == 4));
    }

    @Test
    void shouldNotConsumeTokenWhenNoTokensAvailable() {
        // Given
        testUser.setDailyTokens(0);
        when(userRepository.findByOauthId("test-oauth-id")).thenReturn(Optional.of(testUser));

        // When
        boolean result = tokenService.consumeToken("test-oauth-id");

        // Then
        assertFalse(result);
    }

    @Test
    void shouldRefreshTokensWhenNewDay() {
        // Given
        testUser.setLastTokenRefresh(LocalDate.now().minusDays(1));
        testUser.setDailyTokens(2);
        when(userRepository.findByOauthId("test-oauth-id")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        int remainingTokens = tokenService.getRemainingTokens("test-oauth-id");

        // Then
        assertEquals(10, remainingTokens);
        verify(userRepository).save(argThat(user ->
            user.getDailyTokens() == 10 && user.getLastTokenRefresh().equals(LocalDate.now())
        ));
    }

    @Test
    void shouldReturnFalseWhenUserNotFoundForConsume() {
        // Given
        when(userRepository.findByOauthId("nonexistent-id")).thenReturn(Optional.empty());

        // When
        boolean result = tokenService.consumeToken("nonexistent-id");

        // Then
        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }
}
