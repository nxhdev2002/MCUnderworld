package com.kiemhiep.core.cache;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NoOpDistributedCacheTest {

    @Test
    void get_alwaysReturnsNull() {
        NoOpDistributedCache cache = new NoOpDistributedCache();
        assertNull(cache.get("any"));
    }

    @Test
    void set_and_delete_doNotThrow() {
        NoOpDistributedCache cache = new NoOpDistributedCache();
        cache.set("k", "v", 60);
        cache.delete("k");
    }
}
