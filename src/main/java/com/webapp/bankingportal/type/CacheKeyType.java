package com.webapp.bankingportal.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enumeration of all cache key types used by the banking portal.
 *
 * <p>Each constant encapsulates the key pattern (a {@link String#format} template),
 * the default TTL in seconds, and the expected value type. This centralises cache
 * key construction and prevents typos.</p>
 */
@Getter
@RequiredArgsConstructor
public enum CacheKeyType {

    /**
     * Key type for idempotency tokens used to deduplicate state-changing API requests.
     * Pattern: {@code IDPT:<accountNumber>:<endpoint>:<requestHash>}
     * TTL: 86400 seconds (24 hours).
     */
    IDEMPOTENCY("IDPT:%s:%s:%s", 86400, CacheValueType.JSON),
    ;

    /** {@link String#format} pattern used to build the fully qualified cache key. */
    private final String prefix;

    /** Default time-to-live in seconds for entries stored under this key type. */
    private final long ttlSeconds;

    /** Expected serialization format for the cached value. */
    private final CacheValueType cacheValueType;

    /**
     * Returns the raw key pattern without any argument substitution.
     * Useful when no variable segments are required.
     *
     * @return the unformatted key pattern string
     */
    public String generateKey() {
        return prefix;
    }

    /**
     * Formats the key pattern with the supplied arguments.
     *
     * @param arguments the variable segments to substitute into the key pattern
     * @return the fully qualified cache key string
     */
    public String generateKey(String... arguments) {
        return String.format(prefix, (Object[]) arguments);
    }
}
