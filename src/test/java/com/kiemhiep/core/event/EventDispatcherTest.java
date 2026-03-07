package com.kiemhiep.core.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class EventDispatcherTest {

    private EventDispatcherImpl dispatcher;

    @BeforeEach
    void setUp() {
        dispatcher = new EventDispatcherImpl();
    }

    @Test
    void fire_callsRegisteredHandler() {
        AtomicInteger count = new AtomicInteger(0);
        dispatcher.register(String.class, s -> count.incrementAndGet());
        dispatcher.fire("hello");
        assertEquals(1, count.get());
    }

    @Test
    void fire_callsAllHandlersForType() {
        AtomicInteger a = new AtomicInteger(0);
        AtomicInteger b = new AtomicInteger(0);
        dispatcher.register(String.class, s -> a.incrementAndGet());
        dispatcher.register(String.class, s -> b.incrementAndGet());
        dispatcher.fire("x");
        assertEquals(1, a.get());
        assertEquals(1, b.get());
    }

    @Test
    void fire_ignoresNullEvent() {
        AtomicInteger count = new AtomicInteger(0);
        dispatcher.register(String.class, s -> count.incrementAndGet());
        dispatcher.fire(null);
        assertEquals(0, count.get());
    }

    @Test
    void unregister_removesHandler() {
        AtomicInteger count = new AtomicInteger(0);
        java.util.function.Consumer<String> handler = s -> count.incrementAndGet();
        dispatcher.register(String.class, handler);
        dispatcher.fire("a");
        assertEquals(1, count.get());
        dispatcher.unregister(String.class, handler);
        dispatcher.fire("b");
        assertEquals(1, count.get());
    }

    @Test
    void fire_onlyDispatchesToExactType() {
        AtomicInteger stringCount = new AtomicInteger(0);
        AtomicInteger objectCount = new AtomicInteger(0);
        dispatcher.register(String.class, s -> stringCount.incrementAndGet());
        dispatcher.register(Object.class, o -> objectCount.incrementAndGet());
        dispatcher.fire("hello");
        assertEquals(1, stringCount.get());
        assertEquals(0, objectCount.get());
    }
}
