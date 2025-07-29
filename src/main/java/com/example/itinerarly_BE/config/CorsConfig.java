package com.example.itinerarly_BE.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**") //
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .exposedHeaders("Set-Cookie");

                registry.addMapping("/swagger-ui/**")
                        .allowedOrigins("http://localhost:8080")
                        .allowedMethods("GET", "OPTIONS")
                        .allowedHeaders("*");

                registry.addMapping("/v3/api-docs/**")
                        .allowedOrigins("http://localhost:8080")
                        .allowedMethods("GET", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
}
