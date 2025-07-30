package com.example.itinerarly_BE.config;

import com.example.itinerarly_BE.model.User;
import com.example.itinerarly_BE.repository.UserRepository;
import com.example.itinerarly_BE.utl.JwtTokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import java.time.LocalDate;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;

    @Autowired
    public SecurityConfig(JwtTokenUtil jwtTokenUtil, UserRepository userRepository) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
    }

    @Autowired
    private TokenConfig tokenConfig;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configure(http))
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/v1/logout", "/api/**")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/", "/favicon.ico", "/swagger-ui/**", "/v3/api-docs/**", "/oauth2/authorization/**", "/api/v1/start").permitAll();
                    auth.requestMatchers("/api/**").authenticated();
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
                OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

                String oauthId = oauth2User.getAttribute("sub") != null ?
                        oauth2User.getAttribute("sub").toString() :
                        oauth2User.getAttribute("id").toString();

                User user = userRepository.findByOauthId(oauthId)
                        .orElse(new User());

                // Set user data
                user.setOauthId(oauthId);
                user.setEmail(oauth2User.getAttribute("email"));
                user.setName(oauth2User.getAttribute("name"));
                user.setUsername(oauth2User.getAttribute("login"));
                user.setAvatarUrl(oauth2User.getAttribute("avatar_url"));
                user.setProvider(oauth2User.getAttribute("iss") != null ? "google" : "github");

                // Initialize tokens for new users
                if (user.getId() == null) {
                    user.setDailyTokens(tokenConfig.getDailyTokenLimit());
                    user.setLastTokenRefresh(LocalDate.now());
                }

                userRepository.save(user);

                String jwt = jwtTokenUtil.generateToken(authentication);
                Cookie cookie = new Cookie("auth-token", jwt);
                cookie.setHttpOnly(false);
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