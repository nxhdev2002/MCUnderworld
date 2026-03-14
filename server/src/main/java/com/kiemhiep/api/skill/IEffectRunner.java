package com.kiemhiep.api.skill;

import com.kiemhiep.api.platform.Location;

/**
 * Abstraction for playing skill effects (particles, S2C). Skills use this via SkillContext;
 * platform (e.g. Fabric) provides the real implementation.
 */
public interface IEffectRunner {

    /**
     * Play a named effect at the skill origin (e.g. "tornado", "tsunami", "meteor").
     * Server may send S2C for clients to spawn particles.
     */
    void playEffect(SkillContext ctx, String effectType);

    /**
     * Spawn particles at a location (server may send S2C for client to render).
     */
    void spawnParticleAt(Location location, String particleType, int count);
}
