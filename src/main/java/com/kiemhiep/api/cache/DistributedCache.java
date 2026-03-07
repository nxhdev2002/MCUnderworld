package com.kiemhiep.api.cache;

/**
 * Distributed cache (e.g. Redis). Key naming: kiemhiep:&lt;domain&gt;:&lt;id&gt;
 */
public interface DistributedCache {

    /**
     * Get value by key. Returns null if missing.
     */
    String get(String key);

    /**
     * Set value with TTL in seconds. Use 0 or negative for no expiry.
     */
    void set(String key, String value, long ttlSeconds);

    /**
     * Delete key.
     */
    void delete(String key);
}
