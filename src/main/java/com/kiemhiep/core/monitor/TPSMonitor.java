package com.kiemhiep.core.monitor;

import java.util.function.Consumer;

/**
 * Giữ giá trị TPS / tick time gần nhất (optional, cho server_metrics).
 * Implementation (hoặc platform) gọi update mỗi tick; module đọc getLastTps / getLastTickTimeMs.
 */
public class TPSMonitor {

    private volatile double lastTps = 20.0;
    private volatile long lastTickTimeMs = 50L;
    private volatile Consumer<double[]> metricsCallback;
    private final double[] metricsBuffer = new double[2];

    /**
     * Cập nhật từ tick vừa xong.
     *
     * @param tickTimeMs thời gian tick (ms); TPS = 1000 / tickTimeMs
     */
    public void update(long tickTimeMs) {
        this.lastTickTimeMs = Math.max(1L, tickTimeMs);
        this.lastTps = 1000.0 / this.lastTickTimeMs;
        Consumer<double[]> cb = metricsCallback;
        if (cb != null) {
            metricsBuffer[0] = lastTps;
            metricsBuffer[1] = lastTickTimeMs;
            cb.accept(metricsBuffer);
        }
    }

    public double getLastTps() {
        return lastTps;
    }

    public long getLastTickTimeMs() {
        return lastTickTimeMs;
    }

    /** Optional: callback mỗi khi update (tps, tickTimeMs). */
    public void setMetricsCallback(Consumer<double[]> callback) {
        this.metricsCallback = callback;
    }
}
