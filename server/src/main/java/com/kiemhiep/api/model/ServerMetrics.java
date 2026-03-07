package com.kiemhiep.api.model;

import java.time.Instant;

public record ServerMetrics(
    long id,
    String serverId,
    Instant ts,
    double tps,
    long tickTimeMs
) {}
