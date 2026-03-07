package com.kiemhiep.core.monitor;

import com.kiemhiep.api.model.ServerMetrics;
import com.kiemhiep.api.repository.ServerMetricsRepository;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Records TPS/tick metrics to kiemhiep_server_metrics at a throttled interval (e.g. every 10s).
 */
public class ServerMetricsRecorder {

    private final ServerMetricsRepository repository;
    private final String serverId;
    private final long intervalMs;
    private final AtomicLong lastRecordTime = new AtomicLong(0);

    public ServerMetricsRecorder(ServerMetricsRepository repository, String serverId, long intervalMs) {
        this.repository = repository;
        this.serverId = serverId != null && !serverId.isBlank() ? serverId : "default";
        this.intervalMs = intervalMs > 0 ? intervalMs : 10_000;
    }

    /**
     * Call from TPSMonitor callback. Persists only when interval has elapsed.
     *
     * @param tps        TPS value
     * @param tickTimeMs tick time in ms
     */
    public void onMetrics(double tps, double tickTimeMs) {
        long now = System.currentTimeMillis();
        long last = lastRecordTime.get();
        if (now - last >= intervalMs && lastRecordTime.compareAndSet(last, now)) {
            try {
                repository.save(new ServerMetrics(0, serverId, Instant.now(), tps, (long) tickTimeMs));
            } catch (Exception ignored) {
                lastRecordTime.set(last); // allow retry next tick
            }
        }
    }
}
