package com.kiemhiep.core.skill;

import com.kiemhiep.api.platform.Location;
import com.kiemhiep.api.skill.IEffectRunner;
import com.kiemhiep.api.skill.SkillContext;

import java.util.UUID;

/**
 * Helper for skill effects: particles, fake entity, S2C packets (Rule 3).
 * Implementation can send packets to client for particle/sound; prefer particle over real entity.
 */
public class EffectManager implements IEffectRunner {

    /** Spawn particle at location (server may send S2C for client to render). */
    public void spawnParticleAt(Location location, String particleType, int count) {
        // No-op in core; platform/Fabric impl sends packet to clients in range
    }

    /** Notify client of skill effect for cooldown/cast bar (S2C). */
    public void sendSkillEffectToClient(UUID playerId, String skillId, String effectType, Location location) {
        // No-op; SkillModule networking will send payload
    }

    /** Notify client of skill cooldown (S2C). */
    public void sendSkillCooldownToClient(UUID playerId, String skillId, long cooldownEndTimeMillis) {
        // No-op; platform/Fabric impl sends payload
    }

    /** Called from skill effect interfaces (e.g. IThunder default) to play effect. */
    public void playEffect(SkillContext ctx, String effectType) {
        sendSkillEffectToClient(ctx.getCasterId(), ctx.getDefinition().skillId(), effectType, ctx.getOrigin());
    }
}
