package com.kiemhiep.api.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Simple event dispatcher implementation.
 * Allows registering consumers for specific event types and firing events.
 */
public class EventDispatcher {
    private final Map<Class<? extends Event>, List<EventConsumer<?>>> consumers = new ConcurrentHashMap<>();

    /**
     * Register a consumer for a specific event type.
     */
    public <T extends Event> void register(Class<T> eventType, EventConsumer<T> consumer) {
        consumers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(consumer);
    }

    /**
     * Unregister a consumer for a specific event type.
     */
    public <T extends Event> void unregister(Class<T> eventType, EventConsumer<T> consumer) {
        List<EventConsumer<?>> list = consumers.get(eventType);
        if (list != null) {
            list.remove(consumer);
        }
    }

    /**
     * Fire an event to all registered consumers.
     */
    @SuppressWarnings("unchecked")
    public <T extends Event> void fire(T event) {
        List<EventConsumer<?>> list = consumers.get(event.getClass());
        if (list != null) {
            for (EventConsumer<?> consumer : list) {
                try {
                    ((EventConsumer<T>) consumer).accept(event);
                } catch (Exception e) {
                    // Log error but continue processing other consumers
                    System.err.println("Error processing event " + event.getClass().getSimpleName() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Clear all registered consumers.
     */
    public void clear() {
        consumers.clear();
    }

    /**
     * Functional interface for event consumers.
     */
    @FunctionalInterface
    public interface EventConsumer<T extends Event> {
        void accept(T event);
    }
}
