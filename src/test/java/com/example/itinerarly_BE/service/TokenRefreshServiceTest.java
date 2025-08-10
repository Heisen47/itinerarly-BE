package com.example.itinerarly_BE.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TokenRefreshServiceTest {
    @InjectMocks
    private TokenRefreshService tokenRefreshService;

    @Test
    void testTokenRefreshServiceNotNull() {
        assertThat(tokenRefreshService).isNotNull();
    }
}

