package com.webapp.bankingportal.type;

/**
 * Enumeration describing the serialization format expected for a cached value.
 *
 * <p>Used by {@link CacheKeyType} to indicate how values stored under a given key
 * type should be serialized and deserialized.</p>
 */
public enum CacheValueType {
    /** Plain string value, stored and retrieved without additional serialization. */
    STRING,
    /** JSON-encoded value, serialized and deserialized using Jackson. */
    JSON,
    ;
}
