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
                        .body(Map.of(
                            "error", "Authentication required",
                            "message", "Please log in to check your token balance",
                            "authenticated", false
                        ));
            }

            Long userId = (Long) session.getAttribute("user_id");
            String userEmail = (String) session.getAttribute("user_email");
            String oauthId = (String) session.getAttribute("oauth_id");

            if (userId == null || oauthId == null) {
                logger.error("User ID or OAuth ID not found in session");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of(
                            "error", "Session data incomplete",
                            "message", "Authentication session is corrupted. Please log in again.",
                            "authenticated", false
                        ));
            }

            logger.info("Getting remaining tokens for user: {} (OAuth ID: {})", userEmail, oauthId);
            int remainingTokens = tokenService.getRemainingTokens(oauthId);

            logger.info("Retrieved remaining tokens for user {} (OAuth ID: {}): {}", userEmail, oauthId, remainingTokens);
            return ResponseEntity.ok(Map.of(
                "remainingTokens", remainingTokens,
                "userId", userId,
                "userEmail", userEmail,
                "lastChecked", java.time.Instant.now().toString(),
                "success", true
            ));

        } catch (Exception e) {
            logger.error("Error getting remaining tokens: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Internal server error",
                        "message", "Unable to retrieve token balance. Please try again later.",
                        "errorCode", "INTERNAL_ERROR",
                        "success", false
                    ));
        }
    }

    @PostMapping("/consume")
    public ResponseEntity<?> consumeToken(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("jwt_token") == null) {
                logger.warn("No active session found for consume token request");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                            "error", "Authentication required",
                            "message", "Please log in to consume tokens",
                            "authenticated", false
                        ));
            }

            Long userId = (Long) session.getAttribute("user_id");
            String userEmail = (String) session.getAttribute("user_email");
            String oauthId = (String) session.getAttribute("oauth_id");

            if (userId == null || oauthId == null) {
                logger.error("User ID or OAuth ID not found in session for token consumption");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of(
                            "error", "Session data incomplete",
                            "message", "Authentication session is corrupted. Please log in again.",
                            "authenticated", false
                        ));
            }

            logger.info("Token consumption request for user: {} (OAuth ID: {})", userEmail, oauthId);

            // First check remaining tokens before attempting consumption
            int remainingBeforeConsumption = tokenService.getRemainingTokens(oauthId);
            logger.info("User {} has {} tokens before consumption attempt", userEmail, remainingBeforeConsumption);

            boolean success = tokenService.consumeToken(oauthId);
            Map<String, Object> response = new HashMap<>();

            if (success) {
                int remainingTokens = tokenService.getRemainingTokens(oauthId);
                response.put("success", true);
                response.put("remainingTokens", remainingTokens);
                response.put("message", "Token consumed successfully");
                response.put("consumedAt", java.time.Instant.now().toString());

                logger.info("Token consumed successfully for user: {} (OAuth ID: {}). Remaining tokens: {}",
                    userEmail, oauthId, remainingTokens);
                return ResponseEntity.ok(response);
            } else {
                // Get current token count for detailed error message
                int currentTokens = tokenService.getRemainingTokens(oauthId);

                response.put("success", false);
                response.put("remainingTokens", currentTokens);

                if (currentTokens == 0) {
                    response.put("error", "No tokens remaining");
                    response.put("message", "You have used all your daily tokens. Tokens reset daily at midnight.");
                    response.put("errorCode", "DAILY_LIMIT_EXCEEDED");
                } else {
                    response.put("error", "Token consumption failed");
                    response.put("message", "Unable to consume token due to system error. Please try again.");
                    response.put("errorCode", "CONSUMPTION_FAILED");
                }

                logger.warn("Token consumption failed for user: {} (OAuth ID: {}) - Current tokens: {}",
                    userEmail, oauthId, currentTokens);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        } catch (Exception e) {
            logger.error("Error in token consumption for session: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Internal server error",
                        "message", "An unexpected error occurred while processing your request. Please try again later.",
                        "errorCode", "INTERNAL_ERROR",
                        "success", false
                    ));
        }
    }
}