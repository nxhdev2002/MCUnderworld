package com.kiemhiep.economy;

import com.kiemhiep.api.module.ModuleRegistry;
import com.kiemhiep.api.service.EconomyService;
import com.kiemhiep.cultivation.CultivationModule;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

import java.util.Optional;

/**
 * Listener for economy events on player join.
 * Ensures default wallets exist for new players.
 */
public final class EconomyListener {

    private EconomyListener() {}

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((ServerGamePacketListenerImpl handler,
                                                  net.fabricmc.fabric.api.networking.v1.PacketSender sender,
                                                  net.minecraft.server.MinecraftServer server) -> {
            ServerPlayer player = handler.getPlayer();
            if (player == null) return;
            ensureDefaultWallets(player.getUUID());
        });
    }

    private static void ensureDefaultWallets(java.util.UUID playerUuid) {
        Optional<EconomyService> economyServiceOpt = EconomyModule.getEconomyService();
        if (economyServiceOpt.isEmpty()) return;

        EconomyService economyService = economyServiceOpt.get();

        // Create default wallets for new players (lazy - created on first access)
        for (String currency : economyService.getDefaultCurrencies()) {
            economyService.getBalance(getPlayerId(playerUuid), currency);
        }
    }

    // Helper to get player ID from UUID
    private static long getPlayerId(java.util.UUID uuid) {
        Optional<ModuleRegistry> registryOpt = getModuleRegistry();
        if (registryOpt.isEmpty()) return 0;

        ModuleRegistry registry = registryOpt.get();
        Optional<com.kiemhiep.api.module.KiemHiepModule> cultivationOpt = registry.get("cultivation");
        if (cultivationOpt.isEmpty() || !(cultivationOpt.get() instanceof CultivationModule cm)) {
            return 0;
        }

        com.kiemhiep.api.service.PlayerService playerService = cm.getPlayerService();
        Optional<com.kiemhiep.api.model.Player> playerOpt = playerService.get(uuid.toString());
        return playerOpt.map(p -> p.id()).orElse(0L);
    }

    private static Optional<ModuleRegistry> getModuleRegistry() {
        try {
            return Optional.of(com.kiemhiep.KiemhiepBootstrap.getRegistry());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
