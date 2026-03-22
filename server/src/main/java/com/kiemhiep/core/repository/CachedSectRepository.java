package com.kiemhiep.core.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.kiemhiep.api.cache.DistributedCache;
import com.kiemhiep.api.model.Sect;
import com.kiemhiep.api.model.SectMember;
import com.kiemhiep.api.model.SectRelation;
import com.kiemhiep.api.repository.SectRepository;
import com.kiemhiep.api.sync.MessageBus;
import com.kiemhiep.core.cache.CacheKeys;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Cache-aside wrapper for SectRepository.
 */
public class CachedSectRepository implements SectRepository {

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

    private final SectRepository delegate;
    private final DistributedCache cache;
    private final MessageBus messageBus;

    public CachedSectRepository(SectRepository delegate, DistributedCache cache, MessageBus messageBus) {
        this.delegate = delegate;
        this.cache = cache;
        this.messageBus = messageBus;
    }

    // --- Sect operations ---

    @Override
    public Optional<Sect> getById(long id) {
        String key = CacheKeys.sectById(id);
        String cached = cache.get(key);
        if (cached != null) {
            Sect parsed = fromJson(cached, Sect.class);
            if (parsed != null) return Optional.of(parsed);
            cache.delete(key);
        }
        Optional<Sect> s = delegate.getById(id);
        s.ifPresent(sect -> cache.set(key, toJson(sect), CACHE_TTL_SECONDS));
        return s;
    }

    @Override
    public Optional<Sect> getByName(String name) {
        // For simplicity, delegate directly - name lookups are infrequent
        return delegate.getByName(name);
    }

    @Override
    public Sect save(Sect sect) {
        Sect saved = delegate.save(sect);
        cache.set(CacheKeys.sectById(saved.id()), toJson(saved), CACHE_TTL_SECONDS);
        // Also update player's sects cache
        cache.delete(CacheKeys.sectsByPlayer(saved.leaderId()));
        messageBus.publishInvalidate("sect", "id:" + saved.id());
        messageBus.publishInvalidate("sect-player", String.valueOf(saved.leaderId()));
        return saved;
    }

    @Override
    public void deleteById(long id) {
        delegate.getById(id).ifPresent(s -> {
            cache.delete(CacheKeys.sectById(s.id()));
            cache.delete(CacheKeys.sectsByPlayer(s.leaderId()));
            messageBus.publishInvalidate("sect", "id:" + s.id());
            messageBus.publishInvalidate("sect-player", String.valueOf(s.leaderId()));
        });
        delegate.deleteById(id);
    }

    @Override
    public List<Sect> findAll() {
        return delegate.findAll();
    }

    // --- Member operations ---

    @Override
    public List<SectMember> getMembers(long sectId) {
        String key = CacheKeys.sectMembers(sectId);
        String cached = cache.get(key);
        if (cached != null) {
            List<SectMember> parsed = fromJsonList(cached, SectMember.class);
            if (parsed != null) return parsed;
            cache.delete(key);
        }
        List<SectMember> members = delegate.getMembers(sectId);
        cache.set(key, toJsonList(members), CACHE_TTL_SECONDS);
        return members;
    }

    @Override
    public Optional<SectMember> getMember(long sectId, long playerId) {
        // Delegate directly - this is a specific lookup
        return delegate.getMember(sectId, playerId);
    }

    @Override
    public List<SectMember> getMembersByPlayer(long playerId) {
        String key = CacheKeys.sectsByPlayer(playerId);
        String cached = cache.get(key);
        if (cached != null) {
            List<SectMember> parsed = fromJsonList(cached, SectMember.class);
            if (parsed != null) return parsed;
            cache.delete(key);
        }
        List<SectMember> members = delegate.getMembersByPlayer(playerId);
        cache.set(key, toJsonList(members), CACHE_TTL_SECONDS);
        return members;
    }

    @Override
    public void deleteMember(long sectId, long playerId) {
        delegate.deleteMember(sectId, playerId);
        cache.delete(CacheKeys.sectMembers(sectId));
        cache.delete(CacheKeys.sectsByPlayer(playerId));
        messageBus.publishInvalidate("sect-members", String.valueOf(sectId));
        messageBus.publishInvalidate("sect-player", String.valueOf(playerId));
    }

    @Override
    public SectMember joinMember(long sectId, long playerId, SectMember.Rank rank) {
        SectMember member = delegate.joinMember(sectId, playerId, rank);
        cache.delete(CacheKeys.sectMembers(sectId));
        cache.delete(CacheKeys.sectsByPlayer(playerId));
        messageBus.publishInvalidate("sect-members", String.valueOf(sectId));
        messageBus.publishInvalidate("sect-player", String.valueOf(playerId));
        return member;
    }

    @Override
    public void updateMember(long sectId, long playerId, SectMember.Rank rank, int contribution) {
        delegate.updateMember(sectId, playerId, rank, contribution);
        cache.delete(CacheKeys.sectMembers(sectId));
        cache.delete(CacheKeys.sectsByPlayer(playerId));
        messageBus.publishInvalidate("sect-members", String.valueOf(sectId));
        messageBus.publishInvalidate("sect-player", String.valueOf(playerId));
    }

    @Override
    public List<SectMember> getMembersByRank(long sectId, SectMember.Rank rank) {
        return delegate.getMembersByRank(sectId, rank);
    }

    // --- Relation operations ---

    @Override
    public List<SectRelation> getRelations(long sectId) {
        String key = CacheKeys.sectRelations(sectId);
        String cached = cache.get(key);
        if (cached != null) {
            List<SectRelation> parsed = fromJsonList(cached, SectRelation.class);
            if (parsed != null) return parsed;
            cache.delete(key);
        }
        List<SectRelation> relations = delegate.getRelations(sectId);
        cache.set(key, toJsonList(relations), CACHE_TTL_SECONDS);
        return relations;
    }

    @Override
    public Optional<SectRelation> getRelation(long sectId, long relatedSectId) {
        // Delegate directly - this is a specific lookup
        return delegate.getRelation(sectId, relatedSectId);
    }

    @Override
    public void deleteRelation(long sectId, long relatedSectId) {
        delegate.deleteRelation(sectId, relatedSectId);
        cache.delete(CacheKeys.sectRelations(sectId));
        cache.delete(CacheKeys.sectRelations(relatedSectId));
        messageBus.publishInvalidate("sect-relations", String.valueOf(sectId));
        messageBus.publishInvalidate("sect-relations", String.valueOf(relatedSectId));
    }

    @Override
    public List<SectRelation> getRelationsByType(long sectId, SectRelation.Type type) {
        return delegate.getRelationsByType(sectId, type);
    }

    // --- Serialization helpers ---

    private static <T> String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    private static <T> List<T> fromJsonList(String json, Class<T> clazz) {
        try {
            return GSON.fromJson(json, new com.google.gson.reflect.TypeToken<List<T>>() {}.getType());
        } catch (Exception e) {
            return null;
        }
    }

    private static <T> String toJsonList(List<T> list) {
        return GSON.toJson(list);
    }

    private static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return GSON.fromJson(json, clazz);
        } catch (Exception e) {
            return null;
        }
    }
}
