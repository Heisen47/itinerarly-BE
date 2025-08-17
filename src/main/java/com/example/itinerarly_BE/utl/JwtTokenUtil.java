package com.example.itinerarly_BE.utl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil {

@Value("${JWT_SECRET}")
    private  String jwtSecret;

    private final int jwtExpirationMs = 86400000;

    public String generateToken(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        Map<String, Object> claims = new HashMap<>();

        if (principal instanceof OAuth2User oauth2User) {
            claims.put("name", oauth2User.getAttribute("name"));
            claims.put("email", oauth2User.getAttribute("email"));
            claims.put("login", oauth2User.getAttribute("login"));
            claims.put("id", oauth2User.getAttribute("id"));

            // Fix avatar URL handling for different providers
            String avatarUrl = oauth2User.getAttribute("avatar_url"); // GitHub
            if (avatarUrl == null) {
                avatarUrl = oauth2User.getAttribute("picture"); // Google
            }

            claims.put("avatar", avatarUrl);
        }

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public Map<String, Object> getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}