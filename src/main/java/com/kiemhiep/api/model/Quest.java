package com.kiemhiep.api.model;

import java.time.Instant;

public record Quest(
    long id,
    long playerId,
    String questId,
    int progress,
    Instant createdAt,
    Instant updatedAt
) {}
