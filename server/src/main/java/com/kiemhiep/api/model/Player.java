package com.kiemhiep.api.model;

import java.time.Instant;

public record Player(
    long id,
    String uuid,
    String name,
    Instant createdAt,
    Instant updatedAt
) {}
