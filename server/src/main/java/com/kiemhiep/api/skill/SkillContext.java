package com.kiemhiep.api.skill;

import com.kiemhiep.api.model.SkillDefinition;
import com.kiemhiep.api.platform.EntityAdapter;
import com.kiemhiep.api.platform.Location;
import com.kiemhiep.api.platform.PlayerAdapter;
import net.minecraft.world.damagesource.DamageSource;

import java.util.List;
import java.util.UUID;

/**
 * Context passed to skill execution and effect interfaces.
 * Provides caster, definition, target location, and resolved targets in radius.
 */
public interface SkillContext {

    /** Caster player UUID. */
    UUID getCasterId();

    /** Caster adapter (may be null if not resolved). */
    PlayerAdapter getCaster();

    /** Skill definition from DB. */
    SkillDefinition getDefinition();

    /** Origin location (e.g. caster position or target block). */
    Location getOrigin();

    /** Entities in radius (from WorldAdapter.getEntitiesInRadius), may be empty. */
    List<EntityAdapter> getTargetsInRadius();

    /** Current server tick when execution started. */
    long getServerTick();

    /**
     * Effect runner for playing particles/effects (e.g. tornado, tsunami, meteor).
     * May be null in headless or test environments.
     */
    IEffectRunner getEffectRunner();

    /**
     * Gets the caster's entity from the skill data (if available).
     * Falls back to resolving from caster adapter if needed.
     */
    default Object getCasterEntity() {
        return null;
    }

    /**
     * Gets the target entity from the skill data (if available).
     */
    default Object getTargetEntity() {
        return null;
    }

    /**
     * Gets the location where the skill should be cast.
     * Defaults to origin if not specified.
     */
    default Location getSkillLocation() {
        return getOrigin();
    }

    /**
     * Gets the caster player adapter for the skill.
     */
    default PlayerAdapter getSkillCaster() {
        return getCaster();
    }

    /**
     * Gets the skill level from the definition.
     */
    default int getSkillLevel() {
        return 1;
    }

    /**
     * Creates a DamageSource with the specified type.
     * Used for applying damage with proper source identification.
     */
    default DamageSource getDamageSource(String type) {
        return null;
    }

    /**
     * Gets entities within area around the skill location.
     */
    default List<Object> getEntitiesWithinArea(Object areaBox) {
        return List.of();
    }
}
