package com.example.itinerarly_BE.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionConfig {

    private static final Logger logger = LoggerFactory.getLogger(SessionConfig.class);

    @Bean
    public CookieSameSiteSupplier cookieSameSiteSupplier() {
        logger.info("Configuring cookie SameSite attribute for cross-domain support");
        return CookieSameSiteSupplier.ofNone();
    }
}
