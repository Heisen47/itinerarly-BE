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
import java.time.ZoneId;
import java.time.ZonedDateTime;

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

                String oauthId = null;
                String provider = null;
                String email = null;
                String name = null;
                String username = null;
                String avatarUrl = null;

                // Handle Google OAuth2
                if (oauth2User.getAttribute("iss") != null && oauth2User.getAttribute("iss").toString().contains("google")) {
                    oauthId = oauth2User.getAttribute("sub").toString();
                    provider = "google";
                    email = oauth2User.getAttribute("email");
                    name = oauth2User.getAttribute("name");
                    username = null;
                    avatarUrl = oauth2User.getAttribute("picture");
                }
                // Handle GitHub OAuth2
                else if (oauth2User.getAttribute("login") != null && oauth2User.getAttribute("avatar_url") != null) {
                    oauthId = oauth2User.getAttribute("id").toString();
                    provider = "github";
                    email = oauth2User.getAttribute("email");
                    name = oauth2User.getAttribute("name");
                    username = oauth2User.getAttribute("login");
                    avatarUrl = oauth2User.getAttribute("avatar_url");
                }

                User user = userRepository.findByOauthId(oauthId)
                        .orElse(new User());

                user.setOauthId(oauthId);
                user.setEmail(email);
                user.setName(name);
                user.setUsername(username);
                user.setAvatarUrl(avatarUrl);
                user.setProvider(provider);

                if (user.getId() == null) {
                    user.setDailyTokens(tokenConfig.getDailyTokenLimit());
                    user.setLastTokenRefresh(LocalDate.now());
                    user.setLoginTime(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")));
                }

                userRepository.save(user);

                String jwt = jwtTokenUtil.generateToken(authentication);
                Cookie cookie = new Cookie("auth-token", jwt);
                cookie.setHttpOnly(false);
                cookie.setPath("/");
                cookie.setMaxAge(86400);
                response.addCookie(cookie);

                // Use environment variable for frontend URL
                String frontendUrl = System.getenv("FRONTEND_URL");
                if (frontendUrl == null || frontendUrl.isEmpty()) {
                    frontendUrl = "http://localhost:3000"; // fallback for development
                }
                response.sendRedirect(frontendUrl + "/start");

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