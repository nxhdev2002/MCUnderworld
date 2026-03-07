package com.kiemhiep.cultivation;

import com.kiemhiep.api.repository.PlayerRepository;
import com.kiemhiep.api.service.CultivationService;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public final class CultivationJoinListener {

    private CultivationJoinListener() {}

    public static void register(CultivationService cultivationService, PlayerRepository playerRepository) {
        ServerPlayConnectionEvents.JOIN.register((ServerGamePacketListenerImpl handler,
                                                  net.fabricmc.fabric.api.networking.v1.PacketSender sender,
                                                  net.minecraft.server.MinecraftServer server) -> {
            ServerPlayer serverPlayer = handler.getPlayer();
            if (serverPlayer == null) return;
            String uuid = serverPlayer.getUUID().toString();
            playerRepository.getByUuid(uuid).ifPresent(player -> cultivationService.getOrCreate(player.id()));
        });
    }
}
