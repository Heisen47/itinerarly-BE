package com.example.itinerarly_BE.config;

import com.example.itinerarly_BE.utl.JwtTokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/", "/favicon.ico", "/swagger-ui", "/oauth2/authorization/**" ).permitAll();
                    auth.anyRequest().authenticated();
                })
                .logout(logout -> logout.disable())
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2SuccessHandler())
                        .failureHandler(oAuth2FailureHandler())
                );
        return http.build();
    }

    private AuthenticationSuccessHandler oAuth2SuccessHandler() {
        return (request, response, authentication) -> {
            try {
                String jwt = jwtTokenUtil.generateToken(authentication);
                Cookie cookie = new Cookie("auth-token", jwt);
               //cookie.setHttpOnly(true);  //For prod
                cookie.setHttpOnly(false);  //For Dev
                cookie.setPath("/");
                cookie.setMaxAge(86400);
                response.addCookie(cookie);
                response.sendRedirect("http://localhost:3000/start");
            } catch (Exception ex) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write("{\"error\": \"OAuth2 authentication failed: " + ex.getMessage() + "\"}");
            }
        };
    }

    private AuthenticationFailureHandler oAuth2FailureHandler() {
        return (request, response, exception) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\": \"OAuth2 authentication error: " + exception.getMessage() + "\"}");
        };
    }
}