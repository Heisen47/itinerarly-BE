package com.example.itinerarly_BE.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    private static final Logger logger = LoggerFactory.getLogger(CorsConfig.class);

    @Value("${app.frontend.url:https://itinerarly-fe.vercel.app}")
    private String frontendUrl;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        logger.info("Configuring CORS for cross-domain cookie support with frontend URL: {}", frontendUrl);

        CorsConfiguration configuration = new CorsConfiguration();

        // Allow specific origins
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:3000",
            "http://localhost:*",
            "https://itinerarly-fe.vercel.app",
            "https://*.vercel.app",
            frontendUrl
        ));

        // Allow all standard HTTP methods plus OPTIONS for preflight
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));

        // Allow all headers including authorization and cookie headers
        configuration.setAllowedHeaders(List.of("*"));

        // Essential for cross-domain cookies to work
        configuration.setAllowCredentials(true);

        // Expose headers that frontend needs to access
        configuration.setExposedHeaders(Arrays.asList(
            "Set-Cookie",
            "Authorization",
            "X-Auth-Token",
            "Access-Control-Allow-Credentials",
            "Access-Control-Allow-Origin"
        ));

        // Set preflight request cache time
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        logger.info("CORS configuration completed - AllowCredentials: true, Origins: {}, MaxAge: {}",
            configuration.getAllowedOriginPatterns(), configuration.getMaxAge());

        return source;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                logger.info("Adding CORS mappings for cross-domain cookie support with frontend URL: {}", frontendUrl);

                registry.addMapping("/**")
                        .allowedOriginPatterns(
                            "http://localhost:3000",
                            "http://localhost:*",
                            "https://itinerarly-fe.vercel.app",
                            "https://*.vercel.app",
                            frontendUrl
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .exposedHeaders("Set-Cookie", "Authorization", "X-Auth-Token", "Access-Control-Allow-Credentials")
                        .maxAge(3600);

                logger.info("CORS mappings configured for cross-domain cookie support");
            }
        };
    }
}
