package com.example.itinerarly_BE.controller;

import com.example.itinerarly_BE.utl.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class user {

    @GetMapping("/api/v1/user/profile")
    public ResponseEntity<?> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> userInfo = new HashMap<>();

        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User oauth2User) {
            userInfo.put("name", oauth2User.getAttribute("name"));
            userInfo.put("email", oauth2User.getAttribute("email"));
            userInfo.put("login", oauth2User.getAttribute("login")); // GitHub username
            userInfo.put("id", oauth2User.getAttribute("id"));
            userInfo.put("avatar", oauth2User.getAttribute("avatar_url"));
        }

        return ResponseEntity.ok(userInfo);
    }
}