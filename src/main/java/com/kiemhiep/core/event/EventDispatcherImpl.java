package com.kiemhiep.core.event;

import com.kiemhiep.api.event.EventDispatcher;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Implementation của EventDispatcher: lưu handler theo Class, khi fire gọi mọi handler
 * đã đăng ký cho exact type của event (không dispatch theo supertype để đơn giản và dễ đoán).
 */
public class EventDispatcherImpl implements EventDispatcher {

    private final Map<Class<?>, List<Consumer<?>>> handlersByType = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public void fire(Object event) {
        if (event == null) {
            return;
        }
        Class<?> type = event.getClass();
        List<Consumer<?>> list = handlersByType.get(type);
        if (list == null || list.isEmpty()) {
            return;
        }
        for (Consumer<?> h : list) {
            ((Consumer<Object>) h).accept(event);
        }
    }

    @Override
    public <T> void register(Class<T> eventType, Consumer<T> handler) {
        if (eventType == null || handler == null) {
            return;
        }
        handlersByType.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(handler);
    }

    @Override
    public <T> void unregister(Class<T> eventType, Consumer<T> handler) {
        if (eventType == null || handler == null) {
            return;
        }
        List<Consumer<?>> list = handlersByType.get(eventType);
        if (list != null) {
            list.remove(handler);
        }
    }
}
