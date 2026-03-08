package com.kiemhiep.api.model;

import java.time.Instant;

public record Skill(
    long id,
    long playerId,
    String skillId,
    int level,
    Instant createdAt,
    Instant updatedAt
) {}
