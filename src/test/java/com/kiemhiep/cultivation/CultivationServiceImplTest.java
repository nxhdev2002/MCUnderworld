package com.kiemhiep.cultivation;

import com.kiemhiep.api.event.CultivationBreakthroughEvent;
import com.kiemhiep.api.event.CultivationSubLevelUpEvent;
import com.kiemhiep.api.event.EventDispatcher;
import com.kiemhiep.api.model.Cultivation;
import com.kiemhiep.api.model.CultivationRealm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class CultivationServiceImplTest {

    private InMemoryCultivationRepository repository;
    private List<Object> firedEvents;
    private EventDispatcher eventDispatcher;
    private CultivationServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCultivationRepository();
        firedEvents = new ArrayList<>();
        eventDispatcher = new EventDispatcher() {
            @Override
            public void fire(Object event) {
                firedEvents.add(event);
            }
            @Override
            public <T> void register(Class<T> eventType, Consumer<T> handler) {}
            @Override
            public <T> void unregister(Class<T> eventType, Consumer<T> handler) {}
        };
        service = new CultivationServiceImpl(repository, eventDispatcher);
    }

    @Test
    void getOrCreate_createsDefault() {
        Cultivation c = service.getOrCreate(100L);
        assertEquals(100L, c.playerId());
        assertEquals(1, c.level());
        assertEquals(0L, c.exp());
    }

    @Test
    void getOrCreate_returnsExisting() {
        service.getOrCreate(100L);
        Cultivation c2 = service.getOrCreate(100L);
        assertEquals(1, repository.findAll().size());
        assertEquals(1, c2.level());
    }

    @Test
    void get_returnsEmptyWhenMissing() {
        assertTrue(service.get(999L).isEmpty());
    }

    @Test
    void get_returnsAfterGetOrCreate() {
        service.getOrCreate(100L);
        assertTrue(service.get(100L).isPresent());
        assertEquals(1, service.get(100L).orElseThrow().level());
    }

    @Test
    void getExpRequired_perRealm() {
        assertEquals(100L, service.getExpRequired(1));
        assertEquals(200L, service.getExpRequired(2));
        assertEquals(1000L, service.getExpRequired(10));
    }

    @Test
    void addExp_noLevelUp_whenExpBelowRequired() {
        service.getOrCreate(100L);
        service.addExp(100L, 50L);
        Cultivation c = service.get(100L).orElseThrow();
        assertEquals(1, c.level());
        assertEquals(50L, c.exp());
        assertTrue(firedEvents.isEmpty());
    }

    @Test
    void addExp_singleSubLevelUp() {
        service.getOrCreate(100L);
        service.addExp(100L, 100L); // realm 1 requires 100
        Cultivation c = service.get(100L).orElseThrow();
        assertEquals(2, c.level());
        assertEquals(0L, c.exp());
        assertEquals(1, firedEvents.size());
        assertTrue(firedEvents.get(0) instanceof CultivationSubLevelUpEvent);
        assertEquals(1, ((CultivationSubLevelUpEvent) firedEvents.get(0)).oldLevel());
        assertEquals(2, ((CultivationSubLevelUpEvent) firedEvents.get(0)).newLevel());
    }

    @Test
    void addExp_multipleSubLevels() {
        service.getOrCreate(100L);
        service.addExp(100L, 300L); // 3 * 100 for realm 1
        Cultivation c = service.get(100L).orElseThrow();
        assertEquals(4, c.level());
        assertEquals(0L, c.exp());
        assertTrue(firedEvents.size() >= 3);
    }

    @Test
    void addExp_breakthroughAtSub8() {
        // Set to realm 1, sub-level 8 (level 9)
        service.setSubLevel(100L, 1, 8);
        firedEvents.clear();
        service.addExp(100L, 100L); // level up to level 10 = realm 2 sub 0
        Cultivation c = service.get(100L).orElseThrow();
        assertEquals(10, c.level());
        boolean hasBreakthrough = firedEvents.stream().anyMatch(e -> e instanceof CultivationBreakthroughEvent);
        assertTrue(hasBreakthrough);
    }

    @Test
    void setSubLevel_valid() {
        service.getOrCreate(100L);
        service.setSubLevel(100L, 2, 3);
        Cultivation c = service.get(100L).orElseThrow();
        assertEquals(CultivationRealm.toLevel(2, 3), c.level());
        assertEquals(0L, c.exp());
    }

    @Test
    void setSubLevel_invalidRealm_throws() {
        service.getOrCreate(100L);
        assertThrows(IllegalArgumentException.class, () -> service.setSubLevel(100L, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> service.setSubLevel(100L, 11, 0));
    }

    @Test
    void setSubLevel_invalidSubLevel_throws() {
        service.getOrCreate(100L);
        assertThrows(IllegalArgumentException.class, () -> service.setSubLevel(100L, 1, -1));
        assertThrows(IllegalArgumentException.class, () -> service.setSubLevel(100L, 1, 9));
    }

    @Test
    void breakthrough_successWhenSub8() {
        service.setSubLevel(100L, 1, 8);
        firedEvents.clear();
        boolean ok = service.breakthrough(100L);
        assertTrue(ok);
        Cultivation c = service.get(100L).orElseThrow();
        assertEquals(10, c.level()); // realm 2 sub 0
        assertEquals(1, firedEvents.size());
        assertTrue(firedEvents.get(0) instanceof CultivationBreakthroughEvent);
        assertEquals(1, ((CultivationBreakthroughEvent) firedEvents.get(0)).oldRealm());
        assertEquals(2, ((CultivationBreakthroughEvent) firedEvents.get(0)).newRealm());
    }

    @Test
    void breakthrough_failWhenNotSub8() {
        service.getOrCreate(100L); // level 1, sub 0
        boolean ok = service.breakthrough(100L);
        assertFalse(ok);
        assertEquals(1, service.get(100L).orElseThrow().level());
    }

    @Test
    void breakthrough_failAtMaxRealm() {
        service.setSubLevel(100L, 10, 8); // max realm max sub
        boolean ok = service.breakthrough(100L);
        assertFalse(ok);
        assertEquals(90, service.get(100L).orElseThrow().level());
    }

    @Test
    void getRealm_getSubLevel() {
        assertEquals(1, service.getRealm(1));
        assertEquals(0, service.getSubLevel(1));
        assertEquals(1, service.getRealm(9));
        assertEquals(8, service.getSubLevel(9));
        assertEquals(2, service.getRealm(10));
        assertEquals(0, service.getSubLevel(10));
        assertEquals(10, service.getRealm(90));
        assertEquals(8, service.getSubLevel(90));
    }
}
