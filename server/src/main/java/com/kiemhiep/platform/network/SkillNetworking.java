package com.kiemhiep.platform.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

/**
 * Skill S2C networking (Fabric 1.21). Registers SkillEffectPayload; server sends via FabricEffectManager.
 */
public final class SkillNetworking {

    private SkillNetworking() {}

    /**
     * Call from mod initializer (server and client) to register payload types.
     */
    public static void register() {
        PayloadTypeRegistry.playS2C().register(SkillEffectPayload.TYPE, SkillEffectPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(PlayerStatsPayload.TYPE, PlayerStatsPayload.STREAM_CODEC);
    }
}
