package com.kiemhiep.api.event;

import java.util.function.Consumer;

/**
 * Dispatcher sự kiện: fire event và đăng ký/hủy đăng ký handler theo type.
 * Module dùng API này thay vì đăng ký trực tiếp Fabric events.
 */
public interface EventDispatcher {

    /**
     * Gửi event tới mọi handler đã đăng ký cho type của event (và supertype).
     */
    void fire(Object event);

    /**
     * Đăng ký handler cho loại event.
     *
     * @param eventType type của event (class)
     * @param handler   callback khi fire(event) với event thuộc eventType
     */
    <T> void register(Class<T> eventType, Consumer<T> handler);

    /**
     * Hủy đăng ký handler.
     *
     * @param eventType type của event
     * @param handler   handler đã đăng ký trước đó (phải cùng instance để remove)
     */
    <T> void unregister(Class<T> eventType, Consumer<T> handler);
}
