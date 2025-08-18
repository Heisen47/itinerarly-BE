package com.example.itinerarly_BE.controller;

import com.example.itinerarly_BE.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tokens")
@CrossOrigin(origins = "${app.frontend.url}", allowCredentials = "true")
public class TokenController {

    private static final Logger logger = LoggerFactory.getLogger(TokenController.class);

    @Autowired
    private TokenService tokenService;

    @GetMapping("/remaining")
    public ResponseEntity<?> getRemainingTokens(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("jwt_token") == null) {
                logger.warn("No active session found for remaining tokens request");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Authentication required", "authenticated", false));
            }

            Long userId = (Long) session.getAttribute("user_id");
            String userEmail = (String) session.getAttribute("user_email");

            if (userId == null) {
                logger.error("User ID not found in session");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Session data incomplete"));
            }

            // For token service, we need to get the oauth ID from the user
            // Since we're using session-based auth, we'll use the user_id as identifier
            String oauthId = userId.toString();

            int remainingTokens = tokenService.getRemainingTokens(oauthId);

            logger.info("Retrieved remaining tokens for user {}: {}", userEmail, remainingTokens);
            return ResponseEntity.ok(Map.of(
                "remainingTokens", remainingTokens,
                "userId", userId,
                "userEmail", userEmail
            ));

        } catch (Exception e) {
            logger.error("Error getting remaining tokens: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get remaining tokens: " + e.getMessage()));
        }
    }

    @PostMapping("/consume")
    public ResponseEntity<?> consumeToken(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("jwt_token") == null) {
                logger.warn("No active session found for consume token request");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Authentication required", "authenticated", false));
            }

            Long userId = (Long) session.getAttribute("user_id");
            String userEmail = (String) session.getAttribute("user_email");

            if (userId == null) {
                logger.error("User ID not found in session for token consumption");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Session data incomplete"));
            }

            // Use user_id as identifier for token service
            String oauthId = userId.toString();

            logger.info("Attempting to consume token for user: {} (ID: {})", userEmail, userId);

            boolean success = tokenService.consumeToken(oauthId);
            Map<String, Object> response = new HashMap<>();

            if (success) {
                int remainingTokens = tokenService.getRemainingTokens(oauthId);
                response.put("success", true);
                response.put("remainingTokens", remainingTokens);
                response.put("message", "Token consumed successfully");

                logger.info("Token consumed successfully for user: {} (ID: {}). Remaining tokens: {}",
                    userEmail, userId, remainingTokens);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "No tokens remaining for today");
                response.put("remainingTokens", 0);

                logger.warn("Token consumption failed for user: {} (ID: {}) - no tokens remaining",
                    userEmail, userId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        } catch (Exception e) {
            logger.error("Error in token consumption: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Server encountered an error", "message", e.getMessage()));
        }
    }
}