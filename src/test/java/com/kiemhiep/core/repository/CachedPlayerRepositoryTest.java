package com.kiemhiep.core.repository;

import com.kiemhiep.api.cache.DistributedCache;
import com.kiemhiep.api.model.Player;
import com.kiemhiep.api.repository.PlayerRepository;
import com.kiemhiep.api.sync.InvalidationEvent;
import com.kiemhiep.api.sync.MessageBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CachedPlayerRepositoryTest {

    private StubPlayerRepository delegate;
    private StubCache cache;
    private StubMessageBus messageBus;
    private CachedPlayerRepository cached;

    @BeforeEach
    void setUp() {
        delegate = new StubPlayerRepository();
        cache = new StubCache();
        messageBus = new StubMessageBus();
        cached = new CachedPlayerRepository(delegate, cache, messageBus);
    }

    @Test
    void getByUuid_cacheMiss_hitsDelegateThenSetsCache() {
        Player p = new Player(1, "uuid-1", "Alice", Instant.EPOCH, Instant.EPOCH);
        delegate.add(p);

        Optional<Player> result = cached.getByUuid("uuid-1");

        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().name());
        assertEquals(1, delegate.getByUuidCount);
        assertEquals(1, cache.getCount);
        assertEquals(1, cache.setCount);
    }

    @Test
    void getByUuid_cacheHit_doesNotHitDelegate() {
        cache.put("kiemhiep:player:uuid-1", "{\"id\":1,\"uuid\":\"uuid-1\",\"name\":\"Cached\",\"createdAt\":\"1970-01-01T00:00:00Z\",\"updatedAt\":\"1970-01-01T00:00:00Z\"}");

        Optional<Player> result = cached.getByUuid("uuid-1");

        assertTrue(result.isPresent());
        assertEquals("Cached", result.get().name());
        assertEquals(0, delegate.getByUuidCount);
    }

    @Test
    void save_delegateThenCacheThenPublishInvalidate() {
        Player p = new Player(0, "uuid-2", "Bob", Instant.EPOCH, Instant.EPOCH);
        delegate.saveResult = new Player(1, "uuid-2", "Bob", Instant.EPOCH, Instant.now());

        Player saved = cached.save(p);

        assertEquals(1, delegate.saveCount);
        assertTrue(cache.setCount >= 1);
        assertTrue(messageBus.publishCount >= 1);
    }

    private static class StubPlayerRepository implements PlayerRepository {
        final List<Player> players = new ArrayList<>();
        int getByIdCount, getByUuidCount, saveCount;

        void add(Player p) {
            players.add(p);
        }

        @Override
        public Optional<Player> getById(long id) {
            getByIdCount++;
            return players.stream().filter(p -> p.id() == id).findFirst();
        }

        @Override
        public Optional<Player> getByUuid(String uuid) {
            getByUuidCount++;
            return players.stream().filter(p -> p.uuid().equals(uuid)).findFirst();
        }

        Player saveResult;

        @Override
        public Player save(Player player) {
            saveCount++;
            return saveResult != null ? saveResult : player;
        }

        @Override
        public void deleteById(long id) {}
    }

    private static class StubCache implements DistributedCache {
        final java.util.Map<String, String> map = new java.util.HashMap<>();
        int getCount, setCount;

        void put(String key, String value) {
            map.put(key, value);
        }

        @Override
        public String get(String key) {
            getCount++;
            return map.get(key);
        }

        @Override
        public void set(String key, String value, long ttlSeconds) {
            setCount++;
            map.put(key, value);
        }

        @Override
        public void delete(String key) {
            map.remove(key);
        }
    }

    private static class StubMessageBus implements MessageBus {
        int publishCount;

        @Override
        public void publishInvalidate(String domain, String id) {
            publishCount++;
        }

        @Override
        public void subscribeInvalidate(java.util.function.Consumer<InvalidationEvent> handler) {}
    }
}
