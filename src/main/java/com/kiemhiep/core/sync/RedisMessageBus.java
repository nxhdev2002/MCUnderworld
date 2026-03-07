package com.kiemhiep.core.sync;

import com.kiemhiep.api.sync.InvalidationEvent;
import com.kiemhiep.api.sync.MessageBus;
import com.kiemhiep.core.config.RedisConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Redis Pub/Sub implementation of MessageBus. Channel: kiemhiep:invalidate, payload: {"domain":"...","id":"..."}
 */
public class RedisMessageBus implements MessageBus {

    private static final String CHANNEL = "kiemhiep:invalidate";
    private static final Gson GSON = new Gson();
    private static final Type MAP_TYPE = new TypeToken<Map<String, String>>() {}.getType();

    private final RedisClient client;
    private final StatefulRedisPubSubConnection<String, String> pubSubConnection;
    private final RedisPubSubCommands<String, String> pubSub;
    private final io.lettuce.core.api.sync.RedisCommands<String, String> sync;

    public RedisMessageBus(RedisConfig config) {
        RedisURI uri = RedisURI.builder()
            .withHost(config.host())
            .withPort(config.port())
            .withPassword(config.password() != null && !config.password().isEmpty() ? config.password().toCharArray() : null)
            .build();
        this.client = RedisClient.create(uri);
        this.pubSubConnection = client.connectPubSub();
        this.pubSub = pubSubConnection.sync();
        this.sync = client.connect().sync();
    }

    @Override
    public void publishInvalidate(String domain, String id) {
        String payload = GSON.toJson(Map.of("domain", domain, "id", id != null ? id : ""));
        sync.publish(CHANNEL, payload);
    }

    @Override
    public void subscribeInvalidate(java.util.function.Consumer<InvalidationEvent> handler) {
        pubSubConnection.addListener(new io.lettuce.core.pubsub.RedisPubSubListener<String, String>() {
            @Override
            public void message(String channel, String message) {
                if (CHANNEL.equals(channel) && message != null) {
                    try {
                        Map<String, String> map = GSON.fromJson(message, MAP_TYPE);
                        if (map != null) {
                            String domain = map.getOrDefault("domain", "");
                            String id = map.getOrDefault("id", "");
                            handler.accept(new InvalidationEvent(domain, id));
                        }
                    } catch (Exception ignored) {
                        // ignore parse errors
                    }
                }
            }

            @Override
            public void message(String pattern, String channel, String message) { /* not used */ }

            @Override
            public void subscribed(String channel, long count) { /* not used */ }

            @Override
            public void psubscribed(String pattern, long count) { /* not used */ }

            @Override
            public void unsubscribed(String channel, long count) { /* not used */ }

            @Override
            public void punsubscribed(String pattern, long count) { /* not used */ }
        });
        // subscribe() blocks; run in daemon thread
        Thread t = new Thread(() -> pubSub.subscribe(CHANNEL), "kiemhiep-redis-subscriber");
        t.setDaemon(true);
        t.start();
    }

    public void close() {
        if (pubSubConnection != null) {
            pubSubConnection.close();
        }
        if (client != null) {
            client.shutdown();
        }
    }
}
