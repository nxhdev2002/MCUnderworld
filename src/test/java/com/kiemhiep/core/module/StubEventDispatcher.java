package com.kiemhiep.core.module;

import com.kiemhiep.api.event.EventDispatcher;

import java.util.function.Consumer;

/** Stub EventDispatcher for unit tests. */
public class StubEventDispatcher implements EventDispatcher {

    @Override
    public void fire(Object event) {
    }

    @Override
    public <T> void register(Class<T> eventType, Consumer<T> handler) {
    }

    @Override
    public <T> void unregister(Class<T> eventType, Consumer<T> handler) {
    }
}
