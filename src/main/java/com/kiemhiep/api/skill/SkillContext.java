package com.kiemhiep.api.skill;

import com.kiemhiep.api.model.SkillDefinition;
import com.kiemhiep.api.platform.Location;
import com.kiemhiep.api.platform.PlayerAdapter;

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
    List<?> getTargetsInRadius();

    /** Current server tick when execution started. */
    long getServerTick();
}
