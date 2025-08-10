package com.example.itinerarly_BE.service;

import com.example.itinerarly_BE.config.TokenConfig;
import com.example.itinerarly_BE.model.User;
import com.example.itinerarly_BE.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class TokenService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenConfig tokenConfig;


    public boolean consumeToken(String oauthId) {
        User user = userRepository.findByOauthId(oauthId).orElse(null);
        if (user == null) {
            return false;
        }

        if (!user.getLastTokenRefresh().equals(LocalDate.now())) {
            user.setDailyTokens(tokenConfig.getDailyTokenLimit());
            user.setLastTokenRefresh(LocalDate.now());
        }

        if (user.getDailyTokens() > 0) {
            user.setDailyTokens(user.getDailyTokens() - 1);
            userRepository.save(user);
            return true;
        }

        return false;
    }

    public int getRemainingTokens(String oauthId) {
        User user = userRepository.findByOauthId(oauthId).orElse(null);
        if (user == null) {
            return 0;
        }

        LocalDate today = LocalDate.now();
        LocalDate lastRefresh = user.getLastTokenRefresh();


        if (lastRefresh == null || !lastRefresh.equals(today)) {
            System.out.println("Updating tokens to: " + tokenConfig.getDailyTokenLimit());
            user.setDailyTokens(tokenConfig.getDailyTokenLimit());
            user.setLastTokenRefresh(today);
            userRepository.save(user);
        }

        return user.getDailyTokens();
    }
}