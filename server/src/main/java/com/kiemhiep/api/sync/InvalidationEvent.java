package com.kiemhiep.api.sync;

/**
 * Event when a cache entry should be invalidated (domain + id).
 */
public record InvalidationEvent(String domain, String id) {}
