package com.kiemhiep.api.model;

import java.time.Instant;

public record Cultivation(
    long id,
    long playerId,
    int level,
    long exp,
    Instant createdAt,
    Instant updatedAt
) {}
