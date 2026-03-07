package com.kiemhiep.core.sync;

import com.kiemhiep.api.sync.InvalidationEvent;
import com.kiemhiep.api.sync.MessageBus;

import java.util.function.Consumer;

/**
 * No-op MessageBus when Redis is not configured. Single-node mode.
 */
public class NoOpMessageBus implements MessageBus {

    @Override
    public void publishInvalidate(String domain, String id) {
        // no-op
    }

    @Override
    public void subscribeInvalidate(Consumer<InvalidationEvent> handler) {
        // no-op
    }
}
