package com.kiemhiep.api.model;

import java.time.Instant;

public record SectRelation(
    long id,
    long sectId,
    long relatedSectId,
    Type type,
    Instant createdAt
) {
    public enum Type {
        ALLIED,
        HOSTILE,
        NEUTRAL
    }
}
