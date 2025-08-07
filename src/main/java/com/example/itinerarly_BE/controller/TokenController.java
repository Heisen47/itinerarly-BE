package com.example.itinerarly_BE.controller;

import com.example.itinerarly_BE.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tokens")
public class TokenController {

    private static final Logger logger = LoggerFactory.getLogger(TokenController.class);

    @Autowired
    private TokenService tokenService;

    @GetMapping("/remaining")
    public ResponseEntity<?> getRemainingTokens(@AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String oauthId = null;

            Object subAttribute = oauth2User.getAttribute("sub");
            if (subAttribute != null) {
                oauthId = subAttribute.toString();
            } else {
                Object idAttribute = oauth2User.getAttribute("id");
                if (idAttribute != null) {
                    oauthId = idAttribute.toString();
                }
            }

            if (oauthId == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("{\"error\": \"Unable to extract OAuth ID\"}");
            }

            int remainingTokens = tokenService.getRemainingTokens(oauthId);
            return ResponseEntity.ok().body("{\"remainingTokens\": " + remainingTokens + "}");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Failed to get remaining tokens: " + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/consume")
    public ResponseEntity<?> consumeToken(@AuthenticationPrincipal OAuth2User oauth2User) {
        try {
            if (oauth2User == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String oauthId = null;
            Object subAttribute = oauth2User.getAttribute("sub");
            if (subAttribute != null) {
                oauthId = subAttribute.toString();
            } else {
                Object idAttribute = oauth2User.getAttribute("id");
                if (idAttribute != null) {
                    oauthId = idAttribute.toString();
                }
            }

            if (oauthId == null) {
                logger.error("Failed to extract OAuth ID from authenticated user");
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Authentication problem: Unable to identify user");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }

            boolean success = tokenService.consumeToken(oauthId);
            Map<String, Object> response = new HashMap<>();

            if (success) {
                response.put("success", true);
                response.put("remainingTokens", tokenService.getRemainingTokens(oauthId));
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "No tokens remaining for today");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        } catch (Exception e) {
            logger.error("Error in token consumption", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Server encountered an error");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}