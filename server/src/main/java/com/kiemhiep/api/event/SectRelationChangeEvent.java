package com.kiemhiep.api.event;

import java.time.Instant;

/**
 * Event fired when a sect's relation to another sect changes.
 */
public record SectRelationChangeEvent(
    long sectId,
    long relatedSectId,
    SectRelationChangeEvent.Type oldType,
    SectRelationChangeEvent.Type newType
) {
    public enum Type {
        ALLIED,
        HOSTILE,
        NEUTRAL
    }
}
