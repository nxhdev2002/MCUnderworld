package com.kiemhiep.api.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record Sect(
    long id,
    String name,
    long leaderId,
    int level,
    long exp,
    Instant createdAt,
    Instant updatedAt
) {}
