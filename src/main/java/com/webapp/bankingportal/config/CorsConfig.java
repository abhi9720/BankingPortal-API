package com.webapp.bankingportal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * CORS (Cross-Origin Resource Sharing) configuration for the banking portal.
 *
 * <p>Allows all origins and all HTTP methods for every endpoint, enabling the
 * front-end (hosted on a different origin) to communicate with the API without
 * browser-imposed CORS restrictions.</p>
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Registers a global CORS mapping that permits all origins and HTTP methods
     * for all endpoint patterns.
     *
     * @param registry the CORS registry to configure
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*");
    }
}
