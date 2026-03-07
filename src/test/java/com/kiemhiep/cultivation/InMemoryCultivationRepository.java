package com.kiemhiep.cultivation;

import com.kiemhiep.api.model.Cultivation;
import com.kiemhiep.api.repository.CultivationRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/** In-memory CultivationRepository for unit tests. */
public class InMemoryCultivationRepository implements CultivationRepository {

    private final AtomicLong nextId = new AtomicLong(1);
    private final List<Cultivation> store = new ArrayList<>();

    @Override
    public Optional<Cultivation> getById(long id) {
        return store.stream().filter(c -> c.id() == id).findFirst();
    }

    @Override
    public Optional<Cultivation> getByPlayerId(long playerId) {
        return store.stream().filter(c -> c.playerId() == playerId).findFirst();
    }

    @Override
    public Cultivation save(Cultivation cultivation) {
        Instant now = Instant.now();
        if (cultivation.id() > 0) {
            for (int i = 0; i < store.size(); i++) {
                if (store.get(i).id() == cultivation.id()) {
                    Cultivation updated = new Cultivation(
                        cultivation.id(), cultivation.playerId(), cultivation.level(), cultivation.exp(),
                        cultivation.createdAt(), now);
                    store.set(i, updated);
                    return updated;
                }
            }
        }
        long id = nextId.getAndIncrement();
        Cultivation created = new Cultivation(id, cultivation.playerId(), cultivation.level(), cultivation.exp(), now, now);
        store.add(created);
        return created;
    }

    @Override
    public void deleteById(long id) {
        store.removeIf(c -> c.id() == id);
    }

    @Override
    public List<Cultivation> findAll() {
        return new ArrayList<>(store);
    }
}
