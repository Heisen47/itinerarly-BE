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
class TokenRefreshServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenConfig tokenConfig;

    @InjectMocks
    private TokenRefreshService tokenRefreshService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setDailyTokens(5);
        testUser.setLastTokenRefresh(LocalDate.now().minusDays(1));
    }

    @Test
    void shouldRefreshTokensWhenLastRefreshWasYesterday() {
        // Given
        when(tokenConfig.getDailyTokenLimit()).thenReturn(10);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        tokenRefreshService.refreshDailyTokensIfNeeded(1L);

        // Then
        verify(userRepository).save(argThat(user -> 
            user.getDailyTokens() == 10 && 
            user.getLastTokenRefresh().equals(LocalDate.now())
        ));
    }

    @Test
    void shouldNotRefreshTokensWhenAlreadyRefreshedToday() {
        // Given
        testUser.setLastTokenRefresh(LocalDate.now());
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        tokenRefreshService.refreshDailyTokensIfNeeded(1L);

        // Then
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> 
            tokenRefreshService.refreshDailyTokensIfNeeded(1L)
        );
    }
}
