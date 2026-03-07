package com.kiemhiep.api.model;

import java.time.Instant;

public record Sect(
    long id,
    String name,
    Instant createdAt,
    Instant updatedAt
) {}
