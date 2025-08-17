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
import java.util.Arrays;
import java.util.List;

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

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        testUser1 = new User();
        testUser1.setId(1L);
        testUser1.setEmail("test1@example.com");
        testUser1.setOauthId("oauth-1");
        testUser1.setDailyTokens(5);
        testUser1.setLastTokenRefresh(LocalDate.now().minusDays(1));

        testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setEmail("test2@example.com");
        testUser2.setOauthId("oauth-2");
        testUser2.setDailyTokens(3);
        testUser2.setLastTokenRefresh(LocalDate.now().minusDays(1));

        when(tokenConfig.getDailyTokenLimit()).thenReturn(10);
    }

    @Test
    void shouldRefreshAllUsersTokens() {
        // Given
        List<User> users = Arrays.asList(testUser1, testUser2);
        when(userRepository.findAll()).thenReturn(users);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        tokenRefreshService.refreshAllUserTokens();

        // Then
        verify(userRepository).save(argThat(user -> 
            user.getDailyTokens() == 10 && 
            user.getLastTokenRefresh().equals(LocalDate.now()) &&
            user.getId().equals(1L)
        ));
        verify(userRepository).save(argThat(user ->
            user.getDailyTokens() == 10 &&
            user.getLastTokenRefresh().equals(LocalDate.now()) &&
            user.getId().equals(2L)
        ));
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void shouldHandleEmptyUserList() {
        // Given
        when(userRepository.findAll()).thenReturn(Arrays.asList());

        // When
        tokenRefreshService.refreshAllUserTokens();

        // Then
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldRefreshEvenWhenUserHasMaxTokens() {
        // Given
        testUser1.setDailyTokens(10); // Already at max
        List<User> users = Arrays.asList(testUser1);
        when(userRepository.findAll()).thenReturn(users);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        tokenRefreshService.refreshAllUserTokens();

        // Then
        verify(userRepository).save(argThat(user ->
            user.getDailyTokens() == 10 &&
            user.getLastTokenRefresh().equals(LocalDate.now())
        ));
    }
}
