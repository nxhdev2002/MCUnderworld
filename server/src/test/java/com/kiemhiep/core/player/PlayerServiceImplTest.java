package com.kiemhiep.core.player;

import com.kiemhiep.api.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PlayerServiceImplTest {

    private InMemoryPlayerRepository repository;
    private PlayerServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryPlayerRepository();
        service = new PlayerServiceImpl(repository);
    }

    @Test
    void get_returnsEmptyWhenMissing() {
        assertTrue(service.get("unknown-uuid").isEmpty());
    }

    @Test
    void get_returnsPlayerWhenExists() {
        String uuid = "a1b2c3d4-0000-0000-0000-000000000001";
        Player created = service.getOrCreate(uuid, "Name");
        assertEquals(Optional.of(created), service.get(uuid));
    }

    @Test
    void getOrCreate_firstTime_createsPlayer() {
        String uuid = "a1b2c3d4-0000-0000-0000-000000000001";
        String name = "TestPlayer";
        Player player = service.getOrCreate(uuid, name);
        assertTrue(player.id() > 0);
        assertEquals(uuid, player.uuid());
        assertEquals(name, player.name());
        assertTrue(service.get(uuid).isPresent());
        assertTrue(service.get("other-uuid").isEmpty());
    }

    @Test
    void getOrCreate_secondTime_sameUuid_returnsExisting() {
        String uuid = "a1b2c3d4-0000-0000-0000-000000000002";
        Player first = service.getOrCreate(uuid, "Name");
        Player second = service.getOrCreate(uuid, "Name");
        assertEquals(first.id(), second.id());
        assertEquals(first.uuid(), second.uuid());
        assertTrue(service.get("other-uuid").isEmpty());
    }

    @Test
    void getOrCreate_differentUuids_createsTwoPlayers() {
        String uuid1 = "a1b2c3d4-0000-0000-0000-000000000003";
        String uuid2 = "a1b2c3d4-0000-0000-0000-000000000004";
        Player p1 = service.getOrCreate(uuid1, "A");
        Player p2 = service.getOrCreate(uuid2, "B");
        assertNotEquals(p1.id(), p2.id());
        assertEquals(uuid1, p1.uuid());
        assertEquals(uuid2, p2.uuid());
        assertEquals("A", p1.name());
        assertEquals("B", p2.name());
    }

    @Test
    void getOrCreate_existingPlayer_differentName_updatesName() {
        String uuid = "a1b2c3d4-0000-0000-0000-000000000005";
        service.getOrCreate(uuid, "Old");
        Player updated = service.getOrCreate(uuid, "New");
        assertEquals("New", updated.name());
        assertEquals(uuid, updated.uuid());
        assertEquals("New", service.get(uuid).orElseThrow().name());
    }
}
