package com.kiemhiep.platform;

import com.kiemhiep.Kiemhiep;
import com.kiemhiep.api.platform.Location;
import com.kiemhiep.core.skill.EffectManager;
import com.kiemhiep.platform.network.SkillEffectPayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

/**
 * Fabric implementation of EffectManager: sends SkillEffectPayload S2C so clients can spawn particles.
 */
public class FabricEffectManager extends EffectManager {

    private static final double EFFECT_BROADCAST_RADIUS = 64.0;

    private final FabricPlatformProvider platformProvider;

    public FabricEffectManager(FabricPlatformProvider platformProvider) {
        this.platformProvider = platformProvider;
    }

    @Override
    public void sendSkillEffectToClient(UUID playerId, String skillId, String effectType, Location location) {
        if (location == null) return;
        platformProvider.getPlayer(playerId).ifPresent(adapter -> {
            if (adapter instanceof FabricPlayerAdapter fabricAdapter) {
                ServerPlayer caster = fabricAdapter.getServerPlayer();
                if (caster == null) return;
                ServerLevel level = (ServerLevel) caster.level();
                Vec3 pos = new Vec3(location.x(), location.y(), location.z());
                SkillEffectPayload payload = new SkillEffectPayload(
                    skillId,
                    effectType,
                    location.worldId(),
                    location.x(),
                    location.y(),
                    location.z()
                );
                var playersInRange = PlayerLookup.around(level, pos, EFFECT_BROADCAST_RADIUS);
                for (ServerPlayer recipient : playersInRange) {
                    ServerPlayNetworking.send(recipient, payload);
                }
                Kiemhiep.LOGGER.info("Skill effect sent S2C: skillId={} effectType={} at ({}, {}, {}) playersInRange={}", skillId, effectType, location.x(), location.y(), location.z(), playersInRange.size());
            } else {
                Kiemhiep.LOGGER.warn("Skill effect not sent: caster adapter is not FabricPlayerAdapter");
            }
        });
    }
}
