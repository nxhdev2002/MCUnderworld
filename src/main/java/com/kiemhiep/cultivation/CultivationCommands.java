package com.kiemhiep.cultivation;

import com.kiemhiep.api.model.Player;
import com.kiemhiep.api.repository.PlayerRepository;
import com.kiemhiep.api.service.CultivationService;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public final class CultivationCommands {

    private CultivationCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CultivationService cultivationService,
                                PlayerRepository playerRepository) {
        var cultivation = Commands.literal("cultivation")
            .then(Commands.literal("info")
                .executes(ctx -> executeInfo(ctx.getSource(), cultivationService, playerRepository, null))
                .then(Commands.argument("player", net.minecraft.commands.arguments.EntityArgument.player())
                    .executes(ctx -> executeInfo(ctx.getSource(), cultivationService, playerRepository,
                        net.minecraft.commands.arguments.EntityArgument.getPlayer(ctx, "player")))))
            .then(Commands.literal("addExp")
                .then(Commands.argument("player", net.minecraft.commands.arguments.EntityArgument.player())
                    .then(Commands.argument("amount", LongArgumentType.longArg(0))
                        .executes(ctx -> executeAddExp(ctx.getSource(), cultivationService, playerRepository,
                            net.minecraft.commands.arguments.EntityArgument.getPlayer(ctx, "player"),
                            LongArgumentType.getLong(ctx, "amount"))))))
            .then(Commands.literal("breakthrough")
                .executes(ctx -> executeBreakthrough(ctx.getSource(), cultivationService, playerRepository)));
        dispatcher.register(Commands.literal("kiemhiep").then(cultivation));
    }

    private static int executeInfo(CommandSourceStack source, CultivationService cultivationService,
                                   PlayerRepository playerRepository, ServerPlayer targetPlayer) {
        ServerPlayer player = targetPlayer != null ? targetPlayer : source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("No player specified and source is not a player."));
            return 0;
        }
        Optional<Player> optPlayer = playerRepository.getByUuid(player.getUUID().toString());
        if (optPlayer.isEmpty()) {
            source.sendFailure(Component.literal("Player not found in database."));
            return 0;
        }
        long playerId = optPlayer.get().id();
        var optCult = cultivationService.get(playerId);
        if (optCult.isEmpty()) {
            source.sendFailure(Component.literal("No cultivation data."));
            return 0;
        }
        var c = optCult.get();
        int realm = cultivationService.getRealm(c.level());
        int subLevel = cultivationService.getSubLevel(c.level());
        long required = cultivationService.getExpRequired(realm);
        source.sendSuccess(() -> Component.literal(
            "Cultivation: Realm %d, Sub-level %d, Exp %d / %d (Level %d)"
                .formatted(realm, subLevel, c.exp(), required, c.level())), false);
        return 1;
    }

    private static int executeAddExp(CommandSourceStack source, CultivationService cultivationService,
                                    PlayerRepository playerRepository, ServerPlayer targetPlayer, long amount) {
        Optional<Player> optPlayer = playerRepository.getByUuid(targetPlayer.getUUID().toString());
        if (optPlayer.isEmpty()) {
            source.sendFailure(Component.literal("Player not found in database."));
            return 0;
        }
        cultivationService.addExp(optPlayer.get().id(), amount);
        source.sendSuccess(() -> Component.literal("Added %d exp to %s.".formatted(amount, targetPlayer.getName().getString())), true);
        return 1;
    }

    private static int executeBreakthrough(CommandSourceStack source, CultivationService cultivationService,
                                          PlayerRepository playerRepository) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("Only players can use this command."));
            return 0;
        }
        Optional<Player> optPlayer = playerRepository.getByUuid(player.getUUID().toString());
        if (optPlayer.isEmpty()) {
            source.sendFailure(Component.literal("Player not found in database."));
            return 0;
        }
        boolean ok = cultivationService.breakthrough(optPlayer.get().id());
        if (ok) {
            source.sendSuccess(() -> Component.literal("Breakthrough successful!"), true);
            return 1;
        } else {
            source.sendFailure(Component.literal("Cannot breakthrough: must be at max sub-level (8) in current realm."));
            return 0;
        }
    }
}
