package com.example.itinerarly_BE.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    private static final Logger logger = LoggerFactory.getLogger(CorsConfig.class);

    @Value("${app.frontend.url:https://itinerarly-fe.vercel.app}")
    private String frontendUrl;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                logger.info("Configuring CORS for frontend URL: {}", frontendUrl);

                registry.addMapping("/api/**")
                        .allowedOrigins(
                            "http://localhost:3000",
                            "https://itinerarly-fe.vercel.app",
                            frontendUrl
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .exposedHeaders("Set-Cookie", "Authorization");

                registry.addMapping("/oauth2/**")
                        .allowedOrigins(
                            "http://localhost:3000",
                            "https://itinerarly-fe.vercel.app",
                            frontendUrl
                        )
                        .allowedMethods("GET", "POST", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .exposedHeaders("Set-Cookie", "Authorization");


                registry.addMapping("/login/**")
                        .allowedOrigins(
                            "http://localhost:3000",
                            "https://itinerarly-fe.vercel.app",
                            frontendUrl
                        )
                        .allowedMethods("GET", "POST", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .exposedHeaders("Set-Cookie", "Authorization");

                registry.addMapping("/swagger-ui/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "OPTIONS")
                        .allowedHeaders("*");

                registry.addMapping("/v3/api-docs/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "OPTIONS")
                        .allowedHeaders("*");

                logger.info("CORS configuration completed");
            }
        };
    }
}
