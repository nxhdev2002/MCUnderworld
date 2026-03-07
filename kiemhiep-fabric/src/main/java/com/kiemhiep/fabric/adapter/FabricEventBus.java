package com.kiemhiep.fabric.adapter;

import com.kiemhiep.api.event.Event;
import com.kiemhiep.api.event.EventDispatcher;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Fabric event bus implementation.
 * Wraps Fabric events and provides platform-agnostic event handling.
 */
public class FabricEventBus extends EventDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(FabricEventBus.class);

    private final Queue<Event> pendingEvents = new ConcurrentLinkedQueue<>();

    /**
     * Register Fabric event listeners.
     */
    public void registerFabricEvents(MinecraftServer server) {
        // Register server tick event for processing pending events
        ServerTickEvents.END_SERVER_TICK.register((MinecraftServer s) -> {
            // Process pending events
            Event event;
            while ((event = pendingEvents.poll()) != null) {
                fire(event);
            }
        });

        logger.info("Fabric event bus registered");
    }

    /**
     * Fire an event (queued for main thread processing).
     */
    public void fireAsync(Event event) {
        pendingEvents.offer(event);
    }

    /**
     * Fire an event immediately.
     * Use this only when already on the main thread.
     */
    @Override
    public <T extends Event> void fire(T event) {
        super.fire(event);
    }

    /**
     * Clear all pending events.
     */
    public void clearPending() {
        pendingEvents.clear();
    }

    /**
     * Get the number of pending events.
     */
    public int getPendingCount() {
        return pendingEvents.size();
    }
}
