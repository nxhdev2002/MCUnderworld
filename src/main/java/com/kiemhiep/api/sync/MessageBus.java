package com.kiemhiep.api.sync;

import java.util.function.Consumer;

/**
 * Cross-instance sync: publish and subscribe to cache invalidation events (e.g. Redis Pub/Sub).
 */
public interface MessageBus {

    /**
     * Publish invalidation for domain:id so other instances can invalidate their cache.
     */
    void publishInvalidate(String domain, String id);

    /**
     * Subscribe to invalidation events. Handler may be called from another thread.
     */
    void subscribeInvalidate(Consumer<InvalidationEvent> handler);
}
