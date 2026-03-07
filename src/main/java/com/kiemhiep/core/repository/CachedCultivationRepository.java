package com.kiemhiep.core.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.kiemhiep.api.cache.DistributedCache;
import com.kiemhiep.api.model.Cultivation;
import com.kiemhiep.api.repository.CultivationRepository;
import com.kiemhiep.api.sync.MessageBus;
import com.kiemhiep.core.cache.CacheKeys;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Cache-aside wrapper for CultivationRepository.
 */
public class CachedCultivationRepository implements CultivationRepository {

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

    private final CultivationRepository delegate;
    private final DistributedCache cache;
    private final MessageBus messageBus;

    public CachedCultivationRepository(CultivationRepository delegate, DistributedCache cache, MessageBus messageBus) {
        this.delegate = delegate;
        this.cache = cache;
        this.messageBus = messageBus;
    }

    @Override
    public Optional<Cultivation> getById(long id) {
        String key = CacheKeys.cultivationById(id);
        String cached = cache.get(key);
        if (cached != null) {
            return Optional.ofNullable(fromJson(cached));
        }
        Optional<Cultivation> c = delegate.getById(id);
        c.ifPresent(cult -> cache.set(key, toJson(cult), CACHE_TTL_SECONDS));
        return c;
    }

    @Override
    public Optional<Cultivation> getByPlayerId(long playerId) {
        String key = CacheKeys.cultivationByPlayer(playerId);
        String cached = cache.get(key);
        if (cached != null) {
            return Optional.ofNullable(fromJson(cached));
        }
        Optional<Cultivation> c = delegate.getByPlayerId(playerId);
        c.ifPresent(cult -> cache.set(key, toJson(cult), CACHE_TTL_SECONDS));
        return c;
    }

    @Override
    public Cultivation save(Cultivation cultivation) {
        Cultivation saved = delegate.save(cultivation);
        cache.set(CacheKeys.cultivationById(saved.id()), toJson(saved), CACHE_TTL_SECONDS);
        cache.set(CacheKeys.cultivationByPlayer(saved.playerId()), toJson(saved), CACHE_TTL_SECONDS);
        messageBus.publishInvalidate("cultivation", String.valueOf(saved.playerId()));
        messageBus.publishInvalidate("cultivation", "id:" + saved.id());
        return saved;
    }

    @Override
    public void deleteById(long id) {
        delegate.getById(id).ifPresent(c -> {
            cache.delete(CacheKeys.cultivationById(c.id()));
            cache.delete(CacheKeys.cultivationByPlayer(c.playerId()));
            messageBus.publishInvalidate("cultivation", String.valueOf(c.playerId()));
            messageBus.publishInvalidate("cultivation", "id:" + c.id());
        });
        delegate.deleteById(id);
    }

    @Override
    public List<Cultivation> findAll() {
        return delegate.findAll();
    }

    private static String toJson(Cultivation c) {
        return GSON.toJson(c);
    }

    private static Cultivation fromJson(String json) {
        try {
            return GSON.fromJson(json, Cultivation.class);
        } catch (Exception e) {
            return null;
        }
    }
}
