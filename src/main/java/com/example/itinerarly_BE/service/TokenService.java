package com.example.itinerarly_BE.service;

import com.example.itinerarly_BE.config.TokenConfig;
import com.example.itinerarly_BE.model.User;
import com.example.itinerarly_BE.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenConfig tokenConfig;


    public boolean consumeToken(String oauthId) {
        try {
            logger.info("Attempting to consume token for OAuth ID: {}", oauthId);

            User user = userRepository.findByOauthId(oauthId).orElse(null);
            if (user == null) {
                logger.error("User not found with OAuth ID: {}", oauthId);
                return false;
            }

            logger.info("Found user: {} (ID: {}) with {} tokens, last refresh: {}",
                user.getEmail(), user.getId(), user.getDailyTokens(), user.getLastTokenRefresh());

            LocalDate today = LocalDate.now();
            LocalDate lastRefresh = user.getLastTokenRefresh();

            // Check if tokens need to be refreshed (null check added)
            if (lastRefresh == null || !lastRefresh.equals(today)) {
                logger.info("Refreshing daily tokens for user {} from {} to {} tokens",
                    user.getEmail(), user.getDailyTokens(), tokenConfig.getDailyTokenLimit());

                user.setDailyTokens(tokenConfig.getDailyTokenLimit());
                user.setLastTokenRefresh(today);
                userRepository.save(user);

                logger.info("Tokens refreshed successfully for user {}", user.getEmail());
            }

            // Check if user has tokens available
            if (user.getDailyTokens() > 0) {
                int previousTokens = user.getDailyTokens();
                user.setDailyTokens(user.getDailyTokens() - 1);
                userRepository.save(user);

                logger.info("Token consumed successfully for user {}. Tokens: {} -> {}",
                    user.getEmail(), previousTokens, user.getDailyTokens());
                return true;
            } else {
                logger.warn("Token consumption failed for user {} - no tokens remaining (current: {})",
                    user.getEmail(), user.getDailyTokens());
                return false;
            }
        } catch (Exception e) {
            logger.error("Unexpected error during token consumption for OAuth ID {}: ", oauthId, e);
            return false;
        }
    }

    public int getRemainingTokens(String oauthId) {
        try {
            logger.debug("Getting remaining tokens for OAuth ID: {}", oauthId);

            User user = userRepository.findByOauthId(oauthId).orElse(null);
            if (user == null) {
                logger.error("User not found with OAuth ID: {} when getting remaining tokens", oauthId);
                return 0;
            }

            LocalDate today = LocalDate.now();
            LocalDate lastRefresh = user.getLastTokenRefresh();

            // Check if tokens need to be refreshed (null check added)
            if (lastRefresh == null || !lastRefresh.equals(today)) {
                logger.info("Auto-refreshing daily tokens for user {} from {} to {} tokens",
                    user.getEmail(), user.getDailyTokens(), tokenConfig.getDailyTokenLimit());

                user.setDailyTokens(tokenConfig.getDailyTokenLimit());
                user.setLastTokenRefresh(today);
                userRepository.save(user);

                logger.info("Auto-refresh completed for user {}", user.getEmail());
            }

            logger.debug("Returning {} remaining tokens for user {}", user.getDailyTokens(), user.getEmail());
            return user.getDailyTokens();

        } catch (Exception e) {
            logger.error("Unexpected error getting remaining tokens for OAuth ID {}: ", oauthId, e);
            return 0;
        }
    }

    public User getUserByOauthId(String oauthId) {
        return userRepository.findByOauthId(oauthId).orElse(null);
    }
}