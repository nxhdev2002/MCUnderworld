package com.kiemhiep.core.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.kiemhiep.api.cache.DistributedCache;
import com.kiemhiep.api.model.Wallet;
import com.kiemhiep.api.repository.WalletRepository;
import com.kiemhiep.api.sync.MessageBus;
import com.kiemhiep.core.cache.CacheKeys;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Cache-aside wrapper for WalletRepository.
 */
public class CachedWalletRepository implements WalletRepository {

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

    private final WalletRepository delegate;
    private final DistributedCache cache;
    private final MessageBus messageBus;

    public CachedWalletRepository(WalletRepository delegate, DistributedCache cache, MessageBus messageBus) {
        this.delegate = delegate;
        this.cache = cache;
        this.messageBus = messageBus;
    }

    @Override
    public Optional<Wallet> getById(long id) {
        String key = CacheKeys.walletById(id);
        String cached = cache.get(key);
        if (cached != null) {
            Wallet parsed = fromJson(cached);
            if (parsed != null) return Optional.of(parsed);
            cache.delete(key);
        }
        Optional<Wallet> w = delegate.getById(id);
        w.ifPresent(wallet -> cache.set(key, toJson(wallet), CACHE_TTL_SECONDS));
        return w;
    }

    @Override
    public List<Wallet> getByPlayerId(long playerId) {
        String key = CacheKeys.walletsByPlayer(playerId);
        String cached = cache.get(key);
        if (cached != null) {
            List<Wallet> parsed = fromJsonList(cached);
            if (parsed != null) return parsed;
            cache.delete(key);
        }
        List<Wallet> wallets = delegate.getByPlayerId(playerId);
        cache.set(key, toJsonList(wallets), CACHE_TTL_SECONDS);
        return wallets;
    }

    @Override
    public Optional<Wallet> getByPlayerIdAndCurrency(long playerId, String currency) {
        String key = CacheKeys.walletByPlayerAndCurrency(playerId, currency);
        String cached = cache.get(key);
        if (cached != null) {
            Wallet parsed = fromJson(cached);
            if (parsed != null) return Optional.of(parsed);
            cache.delete(key);
        }
        Optional<Wallet> w = delegate.getByPlayerIdAndCurrency(playerId, currency);
        w.ifPresent(wallet -> cache.set(key, toJson(wallet), CACHE_TTL_SECONDS));
        return w;
    }

    @Override
    public Wallet save(Wallet wallet) {
        Wallet saved = delegate.save(wallet);
        String byIdKey = CacheKeys.walletById(saved.id());
        String byPlayerKey = CacheKeys.walletsByPlayer(saved.playerId());
        String byPlayerCurrencyKey = CacheKeys.walletByPlayerAndCurrency(saved.playerId(), saved.currency());
        cache.set(byIdKey, toJson(saved), CACHE_TTL_SECONDS);
        cache.set(byPlayerKey, toJsonList(delegate.getByPlayerId(saved.playerId())), CACHE_TTL_SECONDS);
        cache.set(byPlayerCurrencyKey, toJson(saved), CACHE_TTL_SECONDS);
        messageBus.publishInvalidate("wallet", String.valueOf(saved.playerId()));
        messageBus.publishInvalidate("wallet", "id:" + saved.id());
        return saved;
    }

    @Override
    public void deleteById(long id) {
        delegate.getById(id).ifPresent(w -> {
            cache.delete(CacheKeys.walletById(w.id()));
            cache.delete(CacheKeys.walletByPlayerAndCurrency(w.playerId(), w.currency()));
            cache.delete(CacheKeys.walletsByPlayer(w.playerId()));
            messageBus.publishInvalidate("wallet", String.valueOf(w.playerId()));
            messageBus.publishInvalidate("wallet", "id:" + w.id());
        });
        delegate.deleteById(id);
    }

    private static String toJson(Wallet w) {
        return GSON.toJson(w);
    }

    private static Wallet fromJson(String json) {
        try {
            return GSON.fromJson(json, Wallet.class);
        } catch (Exception e) {
            return null;
        }
    }

    private static String toJsonList(List<Wallet> wallets) {
        return GSON.toJson(wallets);
    }

    private static List<Wallet> fromJsonList(String json) {
        try {
            return GSON.fromJson(json, java.util.List.class);
        } catch (Exception e) {
            return null;
        }
    }
}
