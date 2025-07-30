package com.example.itinerarly_BE.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenConfig {

    @Value("${app.daily-token-limit:6}")
    private int dailyTokenLimit;

    public int getDailyTokenLimit() {
        return dailyTokenLimit;
    }
}