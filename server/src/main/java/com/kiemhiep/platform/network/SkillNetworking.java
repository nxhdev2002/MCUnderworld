package com.kiemhiep.platform.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

/**
 * Skill S2C networking (Fabric 1.21). Registers SkillEffectPayload and PlayerStatsPayload on the server.
 * Server sends via FabricEffectManager and player-stats sync. Client registers S2C payloads in
 * SkillEffectReceiver (SkillEffectPayload) and PlayerStatsReceiver (PlayerStatsPayload).
 */
public final class SkillNetworking {

    private SkillNetworking() {}

    /**
     * Call from server mod initializer (SkillModule). Registers S2C payload types for server send.
     * Client registers the same payload types in SkillEffectReceiver and PlayerStatsReceiver.
     */
    public static void register() {
        PayloadTypeRegistry.playS2C().register(SkillEffectPayload.TYPE, SkillEffectPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(PlayerStatsPayload.TYPE, PlayerStatsPayload.STREAM_CODEC);
    }
}
