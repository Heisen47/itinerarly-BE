package com.example.itinerarly_BE.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "${app.frontend.url}", allowCredentials = "true")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getAuthStatus(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            HttpSession session = request.getSession(false);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            logger.info("Checking auth status - Session exists: {}, Authentication: {}",
                session != null, authentication != null && authentication.isAuthenticated());

            if (session != null) {
                logger.info("Session ID: {}", session.getId());
                logger.info("Session attributes: jwt_token={}, user_id={}, user_email={}",
                    session.getAttribute("jwt_token") != null,
                    session.getAttribute("user_id"),
                    session.getAttribute("user_email"));
            }

            if (session != null && session.getAttribute("jwt_token") != null) {
                response.put("authenticated", true);
                response.put("sessionId", session.getId());
                response.put("user", Map.of(
                    "id", session.getAttribute("user_id"),
                    "email", session.getAttribute("user_email"),
                    "name", session.getAttribute("user_name")
                ));
                logger.info("User is authenticated via session");
                return ResponseEntity.ok(response);
            } else {
                response.put("authenticated", false);
                response.put("message", "No active session found");
                logger.info("User is not authenticated - no session or JWT");
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            logger.error("Error checking auth status: ", e);
            response.put("authenticated", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> result = new HashMap<>();

        try {
            logger.info("Processing logout request");

            // Invalidate session
            HttpSession session = request.getSession(false);
            if (session != null) {
                logger.info("Invalidating session: {}", session.getId());
                session.invalidate();
            }

            // Clear authentication context
            SecurityContextHolder.clearContext();

            // Clear all authentication-related cookies
            String[] cookiesToClear = {"auth-token", "isLoggedIn", "userInfo", "JSESSIONID"};

            for (String cookieName : cookiesToClear) {
                Cookie cookie = new Cookie(cookieName, "");
                cookie.setPath("/");
                cookie.setMaxAge(0); // Expire immediately
                cookie.setSecure(true);
                cookie.setAttribute("SameSite", "None");
                if (!"isLoggedIn".equals(cookieName) && !"userInfo".equals(cookieName)) {
                    cookie.setHttpOnly(true);
                }
                response.addCookie(cookie);
                logger.info("Cleared cookie: {}", cookieName);
            }

            // Add CORS headers
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            response.setHeader("Access-Control-Allow-Credentials", "true");

            result.put("message", "Logged out successfully");
            logger.info("Logout completed successfully");
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            logger.error("Error during logout: ", e);
            result.put("error", "Logout failed: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }
}
