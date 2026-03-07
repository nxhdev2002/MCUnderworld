package com.kiemhiep.core.player;

import com.kiemhiep.api.model.Player;
import com.kiemhiep.api.repository.PlayerRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/** In-memory PlayerRepository for unit tests. */
public class InMemoryPlayerRepository implements PlayerRepository {

    private final AtomicLong nextId = new AtomicLong(1);
    private final List<Player> store = new ArrayList<>();

    @Override
    public Optional<Player> getById(long id) {
        return store.stream().filter(p -> p.id() == id).findFirst();
    }

    @Override
    public Optional<Player> getByUuid(String uuid) {
        return store.stream().filter(p -> p.uuid().equals(uuid)).findFirst();
    }

    @Override
    public Player save(Player player) {
        Instant now = Instant.now();
        if (player.id() > 0) {
            for (int i = 0; i < store.size(); i++) {
                if (store.get(i).id() == player.id()) {
                    Player updated = new Player(
                        player.id(), player.uuid(), player.name(),
                        player.createdAt(), now);
                    store.set(i, updated);
                    return updated;
                }
            }
        }
        long id = nextId.getAndIncrement();
        Player created = new Player(id, player.uuid(), player.name(), now, now);
        store.add(created);
        return created;
    }

    @Override
    public void deleteById(long id) {
        store.removeIf(p -> p.id() == id);
    }
}
