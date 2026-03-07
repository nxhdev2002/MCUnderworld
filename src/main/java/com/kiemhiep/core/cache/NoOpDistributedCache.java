package com.kiemhiep.core.cache;

import com.kiemhiep.api.cache.DistributedCache;

/**
 * No-op implementation when Redis is not configured. Single-node mode.
 */
public class NoOpDistributedCache implements DistributedCache {

    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public void set(String key, String value, long ttlSeconds) {
        // no-op
    }

    @Override
    public void delete(String key) {
        // no-op
    }
}
