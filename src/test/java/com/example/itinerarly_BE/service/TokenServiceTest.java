package com.example.itinerarly_BE.service;

import com.example.itinerarly_BE.model.User;
import com.example.itinerarly_BE.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class TokenServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TokenService tokenService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setDailyTokens(5);
    }

    @Test
    void shouldReturnTrueWhenUserHasTokens() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        boolean hasTokens = tokenService.hasTokensAvailable(1L);

        // Then
        assertTrue(hasTokens);
    }

    @Test
    void shouldReturnFalseWhenUserHasNoTokens() {
        // Given
        testUser.setDailyTokens(0);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        boolean hasTokens = tokenService.hasTokensAvailable(1L);

        // Then
        assertFalse(hasTokens);
    }

    @Test
    void shouldDecrementTokensWhenUsed() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        boolean result = tokenService.useToken(1L);

        // Then
        assertTrue(result);
        verify(userRepository).save(argThat(user -> user.getDailyTokens() == 4));
    }

    @Test
    void shouldNotDecrementWhenNoTokensAvailable() {
        // Given
        testUser.setDailyTokens(0);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        boolean result = tokenService.useToken(1L);

        // Then
        assertFalse(result);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> 
            tokenService.hasTokensAvailable(1L)
        );
    }
}
