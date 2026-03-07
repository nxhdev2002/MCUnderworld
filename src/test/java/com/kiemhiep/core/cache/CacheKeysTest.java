package com.kiemhiep.core.cache;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CacheKeysTest {

    @Test
    void key_format() {
        assertEquals("kiemhiep:player:uuid-123", CacheKeys.playerByUuid("uuid-123"));
        assertEquals("kiemhiep:player:id:42", CacheKeys.playerById(42));
        assertEquals("kiemhiep:cultivation:1", CacheKeys.cultivationByPlayer(1));
        assertEquals("kiemhiep:cultivation:id:2", CacheKeys.cultivationById(2));
    }
}
