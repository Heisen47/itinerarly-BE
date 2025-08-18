package com.example.itinerarly_BE.config;

import com.example.itinerarly_BE.model.User;
import com.example.itinerarly_BE.repository.UserRepository;
import com.example.itinerarly_BE.utl.JwtTokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;

    @Value("${app.frontend.url}")
    private String frontendUrl;

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
                        .ignoringRequestMatchers("/api/v1/logout", "/api/**", "/oauth2/**", "/login/**")
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/", "/favicon.ico", "/swagger-ui/**", "/v3/api-docs/**",
                            "/oauth2/authorization/**", "/api/v1/start", "/test", "/login/**").permitAll();
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
                logger.info("=== OAuth2 Success Handler Started ===");
                logger.info("Request URL: {}", request.getRequestURL());
                logger.info("Request headers: {}", request.getHeaderNames());
                logger.info("Frontend URL configured: {}", frontendUrl);

                OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                logger.info("OAuth2User attributes: {}", oauth2User.getAttributes());

                String oauthId = null;
                String provider = null;
                String email = null;
                String name = null;
                String username = null;
                String avatarUrl = null;

                // Google OAuth
                if (oauth2User.getAttribute("iss") != null && oauth2User.getAttribute("iss").toString().contains("google")) {
                    logger.info("Processing Google OAuth login");
                    oauthId = oauth2User.getAttribute("sub").toString();
                    provider = "google";
                    email = oauth2User.getAttribute("email");
                    name = oauth2User.getAttribute("name");
                    username = oauth2User.getAttribute("email");
                    avatarUrl = oauth2User.getAttribute("picture");
                    logger.info("Google user - ID: {}, Email: {}, Name: {}", oauthId, email, name);
                }
                // GitHub OAuth
                else if (oauth2User.getAttribute("login") != null) {
                    logger.info("Processing GitHub OAuth login");
                    oauthId = oauth2User.getAttribute("id").toString();
                    provider = "github";
                    email = oauth2User.getAttribute("email");
                    name = oauth2User.getAttribute("name");
                    username = oauth2User.getAttribute("login");
                    avatarUrl = oauth2User.getAttribute("avatar_url");

                    // Handle case where GitHub email might be null if private
                    if (email == null) {
                        email = username + "@github.local";
                        logger.info("GitHub email was null, using fallback: {}", email);
                    }
                    logger.info("GitHub user - ID: {}, Email: {}, Name: {}, Username: {}", oauthId, email, name, username);
                }
                else {
                    logger.error("Unsupported OAuth provider. Available attributes: {}", oauth2User.getAttributes());
                    throw new RuntimeException("Unsupported OAuth provider");
                }

                // Validate required fields
                if (oauthId == null || provider == null || email == null) {
                    logger.error("Missing required OAuth fields - oauthId: {}, provider: {}, email: {}", oauthId, provider, email);
                    throw new RuntimeException("Missing required OAuth fields");
                }

                logger.info("Looking up user in database with oauthId: {}", oauthId);
                User user = userRepository.findByOauthId(oauthId)
                        .orElse(new User());

                boolean isNewUser = user.getId() == null;
                logger.info("User found in DB: {}, Is new user: {}", !isNewUser, isNewUser);

                user.setOauthId(oauthId);
                user.setEmail(email);
                user.setName(name);
                user.setUsername(username);
                user.setAvatarUrl(avatarUrl);
                user.setProvider(provider);

                if (isNewUser) {
                    user.setDailyTokens(tokenConfig.getDailyTokenLimit());
                    user.setLastTokenRefresh(LocalDate.now());
                    user.setLoginTime(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")));
                    logger.info("Setting up new user with {} daily tokens", tokenConfig.getDailyTokenLimit());
                }

                User savedUser = userRepository.save(user);
                logger.info("User saved successfully with ID: {}", savedUser.getId());

                String jwt = jwtTokenUtil.generateToken(authentication);
                logger.info("JWT token generated successfully. Length: {}", jwt.length());
                logger.info("JWT token (first 50 chars): {}...", jwt.substring(0, Math.min(50, jwt.length())));

                // Create multiple cookie formats for better cross-domain compatibility
                Cookie cookie = new Cookie("auth-token", jwt);
                cookie.setHttpOnly(false);
                cookie.setPath("/");
                cookie.setMaxAge(86400); // 24 hours
                cookie.setSecure(true);
                cookie.setAttribute("SameSite", "None");

                logger.info("Cross-domain cookie configured - Name: {}, Path: {}, MaxAge: {}, HttpOnly: {}, Secure: true, SameSite: None",
                    cookie.getName(), cookie.getPath(), cookie.getMaxAge(), cookie.isHttpOnly());

                response.addCookie(cookie);

                // Add multiple Set-Cookie headers for better compatibility
                String[] cookieHeaders = {
                    String.format("auth-token=%s; Path=/; Max-Age=86400; Secure; SameSite=None", jwt),
                    String.format("auth-token=%s; Path=/; Max-Age=86400; Secure; SameSite=None; Domain=.vercel.app", jwt),
                    String.format("authToken=%s; Path=/; Max-Age=86400; Secure; SameSite=None", jwt)
                };

                for (String cookieHeader : cookieHeaders) {
                    response.addHeader("Set-Cookie", cookieHeader);
                    logger.info("Added Set-Cookie header: {}", cookieHeader);
                }

                // Add CORS headers explicitly for this response
                response.setHeader("Access-Control-Allow-Origin", frontendUrl);
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader("Access-Control-Expose-Headers", "Set-Cookie, Authorization");

                // Add the JWT token as a custom header as well (backup method)
                response.setHeader("X-Auth-Token", jwt);
                response.setHeader("Authorization", "Bearer " + jwt);

                logger.info("Cross-domain headers and cookies added to response");
                logger.info("Frontend URL for CORS: {}", frontendUrl);

                // Ensure frontend URL has trailing slash removed if present, then add /start
                String redirectUrl = frontendUrl.endsWith("/") ? frontendUrl.substring(0, frontendUrl.length() - 1) : frontendUrl;
                redirectUrl += "/start";

                logger.info("Redirecting to: {}", redirectUrl);
                response.sendRedirect(redirectUrl);
                logger.info("=== OAuth2 Success Handler Completed Successfully ===");

            } catch (Exception ex) {
                logger.error("OAuth2 authentication failed with exception: ", ex);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write("{\"error\": \"OAuth2 authentication failed: " + ex.getMessage() + "\"}");
            }
        };
    }

    private AuthenticationFailureHandler oAuth2FailureHandler() {
        return (request, response, exception) -> {
            logger.error("OAuth2 authentication failed: ", exception);
            logger.info("Redirecting to frontend with error parameter");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            String redirectUrl = frontendUrl.endsWith("/") ? frontendUrl.substring(0, frontendUrl.length() - 1) : frontendUrl;
            redirectUrl += "/auth?error=oauth_failed";
            logger.info("Failure redirect URL: {}", redirectUrl);
            response.sendRedirect(redirectUrl);
        };
    }
}