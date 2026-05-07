package com.webapp.bankingportal.config;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.val;

/**
 * Spring cache configuration using the Caffeine in-process cache.
 *
 * <p>The {@code otpAttempts} cache stores per-account OTP generation attempt counts
 * with a 15-minute expiry and a maximum of 100 entries. It is used by
 * {@link com.webapp.bankingportal.service.OtpServiceImpl} to enforce the OTP
 * rate limit without requiring a database round-trip.</p>
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Creates and configures the application's primary {@link CacheManager}.
     *
     * @return a {@link CaffeineCacheManager} with the {@code otpAttempts} cache pre-registered
     */
    @Bean
    public CacheManager cacheManager() {
        val cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(List.of("otpAttempts")); // Define the cache names
        cacheManager.setCaffeine(caffeineConfig());
        return cacheManager;
    }

    /**
     * Provides the Caffeine builder used to configure individual caches.
     *
     * @return a {@link Caffeine} builder configured with a 15-minute write expiry,
     *         a maximum size of 100 entries, and statistics recording enabled
     */
    public Caffeine<Object, Object> caffeineConfig() {
        return Caffeine.newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES) // Cache entries expire after 15 minutes
                .maximumSize(100) // Maximum of 100 entries in the cache
                .recordStats(); // For monitoring cache statistics (optional)
    }

}
