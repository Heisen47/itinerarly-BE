package com.example.itinerarly_BE.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Value("${app.frontend.url:https://itinerarly-fe.vercel.app}")
    private String frontendUrl;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins(
                            "http://localhost:3000",
                            "https://itinerarly-fe.vercel.app",
                            frontendUrl
                        )
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .exposedHeaders("Set-Cookie");

                registry.addMapping("/oauth2/**")
                        .allowedOrigins(
                            "http://localhost:3000",
                            "https://itinerarly-fe.vercel.app",
                            frontendUrl
                        )
                        .allowedMethods("GET", "POST", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);

                registry.addMapping("/swagger-ui/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "OPTIONS")
                        .allowedHeaders("*");

                registry.addMapping("/v3/api-docs/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
}
