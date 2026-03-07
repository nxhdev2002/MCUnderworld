package com.kiemhiep.api.repository;

import com.kiemhiep.api.model.ServerMetrics;

public interface ServerMetricsRepository {

    ServerMetrics save(ServerMetrics metrics);
}
