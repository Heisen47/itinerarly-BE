package com.example.itinerarly_BE.controller;

import com.example.itinerarly_BE.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@AuthenticationPrincipal OAuth2User oauth2User) {
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
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            return userRepository.findByOauthId(oauthId)
                    .map(user -> ResponseEntity.ok().body(user))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}