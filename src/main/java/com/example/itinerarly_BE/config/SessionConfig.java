package com.example.itinerarly_BE.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class SessionConfig {

    private static final Logger logger = LoggerFactory.getLogger(SessionConfig.class);

    @Bean
    public CookieSerializer cookieSerializer() {
        logger.info("Configuring cookie serializer for cross-domain support");

        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("JSESSIONID");
        serializer.setCookiePath("/");
        serializer.setDomainNamePattern("^.+?\\.(\\w+\\.[a-z]+)$");
        serializer.setHttpOnly(false);
        serializer.setSecure(true);
        serializer.setSameSite("None");
        serializer.setUseSecureCookie(true);
        serializer.setCookieMaxAge(86400); // 24 hours

        logger.info("Cookie serializer configured for cross-domain cookies with SameSite=None and Secure=true");
        return serializer;
    }

    @Bean
    public CookieSameSiteSupplier cookieSameSiteSupplier() {
        return CookieSameSiteSupplier.ofNone();
    }
}
