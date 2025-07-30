package com.example.itinerarly_BE.service;

import com.example.itinerarly_BE.config.TokenConfig;
import com.example.itinerarly_BE.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class TokenRefreshService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenConfig tokenConfig;

    @Scheduled(cron = "0 0 0 * * *")
    public void refreshAllUserTokens() {
        userRepository.findAll().forEach(user -> {
            user.setDailyTokens(tokenConfig.getDailyTokenLimit());
            user.setLastTokenRefresh(LocalDate.now());
            userRepository.save(user);
        });
        System.out.println("Daily tokens refreshed for all users" + tokenConfig.getDailyTokenLimit());
    }
}