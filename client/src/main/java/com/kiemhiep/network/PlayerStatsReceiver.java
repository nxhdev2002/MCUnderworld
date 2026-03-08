package com.kiemhiep.network;

import com.kiemhiep.ClientPlayerStats;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

/**
 * Registers PlayerStatsPayload and updates ClientPlayerStats when received.
 */
public final class PlayerStatsReceiver {

    private PlayerStatsReceiver() {}

    public static void register() {
        PayloadTypeRegistry.playS2C().register(PlayerStatsPayload.TYPE, PlayerStatsPayload.STREAM_CODEC);
        ClientPlayNetworking.registerGlobalReceiver(PlayerStatsPayload.TYPE, (payload, context) ->
            context.client().execute(() -> ClientPlayerStats.set(payload)));
    }
}
