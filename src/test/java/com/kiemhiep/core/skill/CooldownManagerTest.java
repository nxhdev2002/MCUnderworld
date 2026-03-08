package com.kiemhiep.core.skill;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CooldownManagerTest {

    private CooldownManager manager;

    @BeforeEach
    void setUp() {
        manager = new CooldownManager();
    }

    @Test
    void isOnCooldown_returnsFalseWhenNotSet() {
        UUID player = UUID.randomUUID();
        assertFalse(manager.isOnCooldown(player, "fireball"));
    }

    @Test
    void setCooldown_and_isOnCooldown() {
        UUID player = UUID.randomUUID();
        long end = System.currentTimeMillis() + 5000;
        manager.setCooldown(player, "fireball", end);
        assertTrue(manager.isOnCooldown(player, "fireball"));
        assertEquals(end, manager.getCooldownEndTimeMillis(player, "fireball"));
    }

    @Test
    void tick_removesExpiredEntries() {
        UUID player = UUID.randomUUID();
        manager.setCooldown(player, "fireball", System.currentTimeMillis() - 100);
        manager.tick(System.currentTimeMillis());
        assertFalse(manager.isOnCooldown(player, "fireball"));
    }

    @Test
    void clear_removesAll() {
        UUID player = UUID.randomUUID();
        manager.setCooldown(player, "fireball", System.currentTimeMillis() + 5000);
        manager.clear();
        assertFalse(manager.isOnCooldown(player, "fireball"));
    }
}
