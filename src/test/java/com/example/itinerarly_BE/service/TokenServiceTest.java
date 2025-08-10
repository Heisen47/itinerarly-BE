package com.example.itinerarly_BE.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    @InjectMocks
    private TokenService tokenService;

    @Test
    void testTokenServiceNotNull() {
        assertThat(tokenService).isNotNull();
    }
}

