package com.kiemhiep.core.limit;

import com.kiemhiep.core.config.LimitsConfigLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class EntityLimitEnforcerTest {

    @TempDir
    Path tempDir;

    private LimitsConfigLoader limitsLoader;
    private EntityLimitEnforcer enforcer;

    @BeforeEach
    void setUp() {
        limitsLoader = new LimitsConfigLoader(tempDir.resolve("config/kiemhiep"));
        limitsLoader.loadLimits();
        enforcer = new EntityLimitEnforcer(limitsLoader);
    }

    @Test
    void canSpawn_returnsTrueWhenUnderLimit() {
        assertTrue(enforcer.canSpawn("mob"));
        enforcer.recordSpawn("mob");
        enforcer.recordSpawn("mob");
        assertTrue(enforcer.canSpawn("mob"));
    }

    @Test
    void canSpawn_returnsFalseAtLimit() {
        for (int i = 0; i < 1000; i++) {
            enforcer.recordSpawn("mob");
        }
        assertFalse(enforcer.canSpawn("mob"));
        assertTrue(enforcer.canSpawn("npc"));
    }

    @Test
    void recordRemoval_decrementsCount() {
        enforcer.recordSpawn("mob");
        enforcer.recordSpawn("mob");
        assertEquals(2, enforcer.getCurrentCount("mob"));
        enforcer.recordRemoval("mob");
        assertEquals(1, enforcer.getCurrentCount("mob"));
        assertTrue(enforcer.canSpawn("mob"));
    }

    @Test
    void recordRemoval_doesNotGoBelowZero() {
        enforcer.recordRemoval("mob");
        assertEquals(0, enforcer.getCurrentCount("mob"));
        assertTrue(enforcer.canSpawn("mob"));
    }

    @Test
    void getCurrentCount_returnsZeroForUnknownType() {
        assertEquals(0, enforcer.getCurrentCount("unknown"));
    }

    @Test
    void tryRecordSpawn_returnsTrueAndIncrementsWhenUnderLimit() {
        assertTrue(enforcer.tryRecordSpawn("mob"));
        assertEquals(1, enforcer.getCurrentCount("mob"));
        assertTrue(enforcer.tryRecordSpawn("mob"));
        assertTrue(enforcer.tryRecordSpawn("mob"));
        assertEquals(3, enforcer.getCurrentCount("mob"));
    }

    @Test
    void tryRecordSpawn_returnsFalseWhenAtLimit() {
        for (int i = 0; i < 1000; i++) {
            assertTrue(enforcer.tryRecordSpawn("mob"));
        }
        assertEquals(1000, enforcer.getCurrentCount("mob"));
        assertFalse(enforcer.tryRecordSpawn("mob"));
        assertEquals(1000, enforcer.getCurrentCount("mob"));
    }

    @Test
    void tryRecordSpawn_returnsFalseForNull() {
        assertFalse(enforcer.tryRecordSpawn(null));
        assertEquals(0, enforcer.getCurrentCount("mob"));
    }
}
