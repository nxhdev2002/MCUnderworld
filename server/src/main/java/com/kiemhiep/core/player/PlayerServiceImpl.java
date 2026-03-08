package com.kiemhiep.core.player;

import com.kiemhiep.api.model.Player;
import com.kiemhiep.api.repository.PlayerRepository;
import com.kiemhiep.api.service.PlayerService;

import java.time.Instant;
import java.util.Optional;

public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository repository;

    public PlayerServiceImpl(PlayerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Player> get(String uuid) {
        return repository.getByUuid(uuid);
    }

    @Override
    public Player getOrCreate(String uuid, String name) {
        Optional<Player> existing = repository.getByUuid(uuid);
        if (existing.isPresent()) {
            Player p = existing.get();
            if (!p.name().equals(name)) {
                Player updated = new Player(p.id(), uuid, name, p.createdAt(), Instant.now());
                return repository.save(updated);
            }
            return p;
        }
        Instant now = Instant.now();
        Player created = new Player(0, uuid, name, now, now);
        return repository.save(created);
    }
}
