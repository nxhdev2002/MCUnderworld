package com.kiemhiep.core.monitor;

import com.kiemhiep.api.model.ServerMetrics;
import com.kiemhiep.api.repository.ServerMetricsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServerMetricsRecorderTest {

    private List<ServerMetrics> recorded;
    private ServerMetricsRepository repo;
    private ServerMetricsRecorder recorder;

    @BeforeEach
    void setUp() {
        recorded = new ArrayList<>();
        repo = metrics -> {
            recorded.add(metrics);
            return metrics;
        };
        recorder = new ServerMetricsRecorder(repo, "test-server", 10_000);
    }

    @Test
    void onMetrics_recordsFirstCall() {
        recorder.onMetrics(19.5, 51.2);

        assertEquals(1, recorded.size());
        assertEquals("test-server", recorded.get(0).serverId());
        assertEquals(19.5, recorded.get(0).tps());
        assertEquals(51L, recorded.get(0).tickTimeMs());
    }

    @Test
    void onMetrics_throttlesWithinInterval() {
        recorder.onMetrics(20.0, 50.0);
        recorder.onMetrics(20.0, 50.0);
        recorder.onMetrics(20.0, 50.0);

        assertEquals(1, recorded.size());
    }
}
