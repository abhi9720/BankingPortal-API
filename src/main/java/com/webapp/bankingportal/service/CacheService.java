package com.webapp.bankingportal.service;

import com.webapp.bankingportal.type.CacheKeyType;
import java.util.Optional;

/**
 * Service contract for interacting with the Redis-backed cache store.
 *
 * <p>All cache operations are keyed using a {@link CacheKeyType} enum value,
 * which encapsulates the key pattern, default TTL, and value type. Variable
 * key segments are supplied via the {@code keyArguments} varargs parameter.</p>
 */
public interface CacheService {

    /**
     * Returns {@code true} if a value exists in the cache for the computed key.
     *
     * @param cacheKeyType the key type defining the key pattern
     * @param keyArguments variable arguments substituted into the key pattern
     * @return {@code true} if the key is present, {@code false} otherwise
     */
    boolean exists(CacheKeyType cacheKeyType, String... keyArguments);

    /**
     * Retrieves a string value from the cache.
     *
     * @param cacheKeyType the key type defining the key pattern
     * @param keyArguments variable arguments substituted into the key pattern
     * @return an {@link Optional} containing the string value, or empty if not found
     */
    Optional<String> get(CacheKeyType cacheKeyType, String... keyArguments);

    /**
     * Retrieves a typed value from the cache, casting it to the requested class.
     *
     * @param <T>          the expected return type
     * @param cacheKeyType the key type defining the key pattern
     * @param clazz        the class to cast the cached value to
     * @param keyArguments variable arguments substituted into the key pattern
     * @return an {@link Optional} containing the typed value, or empty if not found or type mismatch
     */
    <T> Optional<T> get(CacheKeyType cacheKeyType, Class<T> clazz, String... keyArguments);

    /**
     * Stores a value in the cache using the default TTL defined by {@code cacheKeyType}.
     *
     * @param cacheKeyType the key type defining the key pattern and TTL
     * @param value        the value to cache
     * @param keyArguments variable arguments substituted into the key pattern
     */
    void put(CacheKeyType cacheKeyType, Object value, String... keyArguments);

    /**
     * Stores a value in the cache with an explicit TTL override.
     *
     * @param cacheKeyType the key type defining the key pattern
     * @param value        the value to cache
     * @param ttlSeconds   the number of seconds before the entry expires
     * @param keyArguments variable arguments substituted into the key pattern
     */
    void put(CacheKeyType cacheKeyType, Object value, long ttlSeconds, String... keyArguments);

    /**
     * Removes the cache entry for the computed key.
     *
     * @param cacheKeyType the key type defining the key pattern
     * @param keyArguments variable arguments substituted into the key pattern
     */
    void delete(CacheKeyType cacheKeyType, String... keyArguments);
}
