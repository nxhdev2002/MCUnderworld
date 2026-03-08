package com.kiemhiep.cultivation;

import com.kiemhiep.api.model.Player;
import com.kiemhiep.api.service.CultivationService;
import com.kiemhiep.api.service.PlayerService;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public final class CultivationJoinListener {

    private CultivationJoinListener() {}

    public static void register(CultivationService cultivationService, PlayerService playerService) {
        ServerPlayConnectionEvents.JOIN.register((ServerGamePacketListenerImpl handler,
                                                  net.fabricmc.fabric.api.networking.v1.PacketSender sender,
                                                  net.minecraft.server.MinecraftServer server) -> {
            ServerPlayer serverPlayer = handler.getPlayer();
            if (serverPlayer == null) return;
            String uuid = serverPlayer.getUUID().toString();
            String name = serverPlayer.getName().getString();
            Player player = playerService.getOrCreate(uuid, name);
            cultivationService.getOrCreate(player.id());
        });
    }
}
