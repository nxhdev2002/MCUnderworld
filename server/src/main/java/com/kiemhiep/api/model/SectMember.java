package com.kiemhiep.api.model;

import java.time.Instant;

public record SectMember(
    long id,
    long sectId,
    long playerId,
    Rank rank,
    int contribution,
    Instant joinedAt,
    Instant updatedAt
) {
    public enum Rank {
        LEADER,
        ELDER,
        MEMBER,
        NOVICE
    }
}
