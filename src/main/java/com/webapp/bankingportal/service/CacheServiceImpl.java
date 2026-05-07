package com.webapp.bankingportal.service;

import com.webapp.bankingportal.type.CacheKeyType;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Redis-backed implementation of {@link CacheService}.
 *
 * <p>Uses a {@link RedisTemplate} configured with JSON serialization for values
 * and string serialization for keys. All operations gracefully handle Redis
 * unavailability by logging errors and returning empty optionals where possible.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

    /** Redis template used for all cache read/write operations. */
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean exists(CacheKeyType cacheKeyType, String... keyArguments) {
        return get(cacheKeyType, keyArguments).isPresent();
    }

    @Override
    public Optional<String> get(CacheKeyType cacheKeyType, String... keyArguments) {
        try {
            String key = acquireKey(cacheKeyType, keyArguments);
            Object value = redisTemplate.opsForValue().get(key);
            return Optional.ofNullable(value).map(Object::toString);
        } catch (Exception e) {
            log.error("Error retrieving value from cache: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public <T> Optional<T> get(CacheKeyType cacheKeyType, Class<T> clazz, String... keyArguments) {
        try {
            String key = acquireKey(cacheKeyType, keyArguments);
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return Optional.empty();
            }

            if (clazz.isInstance(value)) {
                return Optional.of(clazz.cast(value));
            }

            log.warn("Value type mismatch. Expected: {}, Actual: {}", clazz.getSimpleName(), value.getClass().getSimpleName());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error retrieving value from cache: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public void put(CacheKeyType cacheKeyType, Object value, String... keyArguments) {
        put(cacheKeyType, value, cacheKeyType.getTtlSeconds(), keyArguments);
    }

    @Override
    public void put(CacheKeyType cacheKeyType, Object value, long ttlSeconds, String... keyArguments) {
        String key = acquireKey(cacheKeyType, keyArguments);

        try {
            redisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
            log.debug("Cache stored: key={}", key);
        } catch (Exception e) {
            log.error("Error storing value in cache: {}", e.getMessage());
            throw new RuntimeException("Failed to store value in cache", e);
        }
    }

    @Override
    public void delete(CacheKeyType cacheKeyType, String... keyArguments) {
        String key = acquireKey(cacheKeyType, keyArguments);

        try {
            redisTemplate.delete(key);
            log.debug("Cache deleted: key={}", key);
        } catch (Exception e) {
            log.error("Error deleting key from cache: {}", e.getMessage());
        }
    }

    /**
     * Computes the final cache key by formatting the {@code cacheKeyType}'s pattern with
     * any provided arguments, or returns the bare pattern when no arguments are given.
     *
     * @param cacheKeyType the key type whose pattern is used
     * @param keyArguments variable arguments substituted into the key pattern
     * @return the resolved cache key string
     */
    private String acquireKey(CacheKeyType cacheKeyType, String... keyArguments) {
        String key = cacheKeyType.generateKey();
        if (keyArguments.length != 0) {
            key = cacheKeyType.generateKey(keyArguments);
        }

        return key;
    }
}