package com.kiemhiep.api.model;

import java.time.Instant;

public record Wallet(
    long id,
    long playerId,
    long balance,
    String currency,
    Instant createdAt,
    Instant updatedAt
) {}
