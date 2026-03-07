package com.kiemhiep.core.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.kiemhiep.api.cache.DistributedCache;
import com.kiemhiep.api.model.Player;
import com.kiemhiep.api.repository.PlayerRepository;
import com.kiemhiep.api.sync.MessageBus;
import com.kiemhiep.core.cache.CacheKeys;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

/**
 * Cache-aside wrapper for PlayerRepository. Get: cache first, miss then DB and set cache.
 * Save: DB then set cache and publish invalidate.
 */
public class CachedPlayerRepository implements PlayerRepository {

    private static final Gson GSON = new GsonBuilder()
        .registerTypeAdapter(Instant.class, new TypeAdapter<Instant>() {
            @Override
            public void write(JsonWriter out, Instant value) throws IOException {
                out.value(value != null ? value.toString() : null);
            }
            @Override
            public Instant read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    return null;
                }
                return Instant.parse(in.nextString());
            }
        })
        .create();

    private static final long CACHE_TTL_SECONDS = 300;

    private final PlayerRepository delegate;
    private final DistributedCache cache;
    private final MessageBus messageBus;

    public CachedPlayerRepository(PlayerRepository delegate, DistributedCache cache, MessageBus messageBus) {
        this.delegate = delegate;
        this.cache = cache;
        this.messageBus = messageBus;
    }

    @Override
    public Optional<Player> getById(long id) {
        String key = CacheKeys.playerById(id);
        String cached = cache.get(key);
        if (cached != null) {
            return Optional.ofNullable(fromJson(cached));
        }
        Optional<Player> p = delegate.getById(id);
        p.ifPresent(player -> cache.set(key, toJson(player), CACHE_TTL_SECONDS));
        return p;
    }

    @Override
    public Optional<Player> getByUuid(String uuid) {
        String key = CacheKeys.playerByUuid(uuid);
        String cached = cache.get(key);
        if (cached != null) {
            return Optional.ofNullable(fromJson(cached));
        }
        Optional<Player> p = delegate.getByUuid(uuid);
        p.ifPresent(player -> cache.set(key, toJson(player), CACHE_TTL_SECONDS));
        return p;
    }

    @Override
    public Player save(Player player) {
        Player saved = delegate.save(player);
        String uuidKey = CacheKeys.playerByUuid(saved.uuid());
        String idKey = CacheKeys.playerById(saved.id());
        cache.set(uuidKey, toJson(saved), CACHE_TTL_SECONDS);
        cache.set(idKey, toJson(saved), CACHE_TTL_SECONDS);
        messageBus.publishInvalidate("player", saved.uuid());
        messageBus.publishInvalidate("player", "id:" + saved.id());
        return saved;
    }

    @Override
    public void deleteById(long id) {
        delegate.getById(id).ifPresent(p -> {
            cache.delete(CacheKeys.playerByUuid(p.uuid()));
            cache.delete(CacheKeys.playerById(p.id()));
            messageBus.publishInvalidate("player", p.uuid());
            messageBus.publishInvalidate("player", "id:" + p.id());
        });
        delegate.deleteById(id);
    }

    private static String toJson(Player p) {
        return GSON.toJson(p);
    }

    private static Player fromJson(String json) {
        try {
            return GSON.fromJson(json, Player.class);
        } catch (Exception e) {
            return null;
        }
    }
}
