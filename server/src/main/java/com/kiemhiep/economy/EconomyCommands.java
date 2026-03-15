package com.kiemhiep.economy;

import com.kiemhiep.api.model.Player;
import com.kiemhiep.api.service.EconomyService;
import com.kiemhiep.cultivation.CultivationModule;
import com.kiemhiep.core.command.CommandPermissionHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public final class EconomyCommands {

    private EconomyCommands() {}

    public static void register() {
        // Register via CommandRegistrationCallback
        net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            var economy = Commands.literal("economy")
                .then(Commands.literal("info")
                    .executes(ctx -> executeInfo(ctx.getSource(), null))
                    .then(Commands.argument("player", net.minecraft.commands.arguments.EntityArgument.player())
                        .requires(source -> CommandPermissionHelper.hasPermissionLevel(source, 2))
                        .executes(ctx -> executeInfo(ctx.getSource(),
                            net.minecraft.commands.arguments.EntityArgument.getPlayer(ctx, "player")))))
                .then(Commands.literal("pay")
                    .then(Commands.argument("player", net.minecraft.commands.arguments.EntityArgument.player())
                        .then(Commands.argument("amount", LongArgumentType.longArg(1))
                            .then(Commands.argument("currency", StringArgumentType.string())
                                .executes(ctx -> executePay(ctx.getSource(),
                                    net.minecraft.commands.arguments.EntityArgument.getPlayer(ctx, "player"),
                                    LongArgumentType.getLong(ctx, "amount"),
                                    StringArgumentType.getString(ctx, "currency")))))))
                .then(Commands.literal("add")
                    .requires(source -> CommandPermissionHelper.hasPermissionLevel(source, 2))
                    .then(Commands.argument("player", net.minecraft.commands.arguments.EntityArgument.player())
                        .then(Commands.argument("amount", LongArgumentType.longArg(1))
                            .then(Commands.argument("currency", StringArgumentType.string())
                                .executes(ctx -> executeAdd(ctx.getSource(),
                                    net.minecraft.commands.arguments.EntityArgument.getPlayer(ctx, "player"),
                                    LongArgumentType.getLong(ctx, "amount"),
                                    StringArgumentType.getString(ctx, "currency")))))))
                .then(Commands.literal("remove")
                    .requires(source -> CommandPermissionHelper.hasPermissionLevel(source, 2))
                    .then(Commands.argument("player", net.minecraft.commands.arguments.EntityArgument.player())
                        .then(Commands.argument("amount", LongArgumentType.longArg(1))
                            .then(Commands.argument("currency", StringArgumentType.string())
                                .executes(ctx -> executeRemove(ctx.getSource(),
                                    net.minecraft.commands.arguments.EntityArgument.getPlayer(ctx, "player"),
                                    LongArgumentType.getLong(ctx, "amount"),
                                    StringArgumentType.getString(ctx, "currency")))))));
            dispatcher.register(Commands.literal("kiemhiep").then(economy));
        });
    }

    private static int executeInfo(CommandSourceStack source, ServerPlayer targetPlayer) {
        ServerPlayer player = targetPlayer != null ? targetPlayer : source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("No player specified and source is not a player."));
            return 0;
        }
        Optional<Player> optPlayer = lookupPlayerByUuid(player.getUUID().toString());
        if (optPlayer.isEmpty()) {
            source.sendFailure(Component.literal("Player not found in database."));
            return 0;
        }
        long playerId = optPlayer.get().id();
        EconomyService economyService = EconomyModule.getInstance()
            .map(EconomyModule::getService).orElse(null);
        if (economyService == null) {
            source.sendFailure(Component.literal("Economy module is disabled."));
            return 0;
        }
        source.sendSuccess(() -> Component.literal("Wallets for %s:".formatted(player.getName().getString())), false);
        for (String currency : economyService.getDefaultCurrencies()) {
            long balance = economyService.getBalance(playerId, currency);
            source.sendSuccess(() -> Component.literal("  %s: %d".formatted(currency, balance)), false);
        }
        return 1;
    }

    private static int executePay(CommandSourceStack source, ServerPlayer target, long amount, String currency) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("Only players can use this command."));
            return 0;
        }
        Optional<Player> optFrom = lookupPlayerByUuid(player.getUUID().toString());
        Optional<Player> optTo = lookupPlayerByUuid(target.getUUID().toString());
        if (optFrom.isEmpty()) {
            source.sendFailure(Component.literal("Sender player not found in database."));
            return 0;
        }
        if (optTo.isEmpty()) {
            source.sendFailure(Component.literal("Target player not found in database."));
            return 0;
        }
        EconomyService economyService = EconomyModule.getInstance()
            .map(EconomyModule::getService).orElse(null);
        if (economyService == null) {
            source.sendFailure(Component.literal("Economy module is disabled."));
            return 0;
        }
        boolean success = economyService.transfer(optFrom.get().id(), optTo.get().id(), currency, amount);
        if (success) {
            source.sendSuccess(() -> Component.literal("Paid %d %s to %s.".formatted(amount, currency, target.getName().getString())), true);
            player.sendSystemMessage(Component.literal("Sent %d %s to %s.".formatted(amount, currency, target.getName().getString())));
            target.sendSystemMessage(Component.literal("Received %d %s from %s.".formatted(amount, currency, player.getName().getString())));
            return 1;
        } else {
            source.sendFailure(Component.literal("Transfer failed: insufficient balance or invalid currency."));
            return 0;
        }
    }

    private static int executeAdd(CommandSourceStack source, ServerPlayer target, long amount, String currency) {
        Optional<Player> optPlayer = lookupPlayerByUuid(target.getUUID().toString());
        if (optPlayer.isEmpty()) {
            source.sendFailure(Component.literal("Player not found in database."));
            return 0;
        }
        EconomyService economyService = EconomyModule.getInstance()
            .map(EconomyModule::getService).orElse(null);
        if (economyService == null) {
            source.sendFailure(Component.literal("Economy module is disabled."));
            return 0;
        }
        economyService.add(optPlayer.get().id(), currency, amount);
        source.sendSuccess(() -> Component.literal("Added %d %s to %s.".formatted(amount, currency, target.getName().getString())), true);
        return 1;
    }

    private static int executeRemove(CommandSourceStack source, ServerPlayer target, long amount, String currency) {
        Optional<Player> optPlayer = lookupPlayerByUuid(target.getUUID().toString());
        if (optPlayer.isEmpty()) {
            source.sendFailure(Component.literal("Player not found in database."));
            return 0;
        }
        EconomyService economyService = EconomyModule.getInstance()
            .map(EconomyModule::getService).orElse(null);
        if (economyService == null) {
            source.sendFailure(Component.literal("Economy module is disabled."));
            return 0;
        }
        try {
            economyService.subtract(optPlayer.get().id(), currency, amount);
            source.sendSuccess(() -> Component.literal("Removed %d %s from %s.".formatted(amount, currency, target.getName().getString())), true);
            return 1;
        } catch (IllegalArgumentException e) {
            source.sendFailure(Component.literal("Remove failed: " + e.getMessage()));
            return 0;
        }
    }

    private static Optional<Player> lookupPlayerByUuid(String uuid) {
        // Get cultivation module to access PlayerService
        com.kiemhiep.api.module.ModuleRegistry registry = com.kiemhiep.KiemhiepBootstrap.getRegistry();
        Optional<com.kiemhiep.api.module.KiemHiepModule> cultivationOpt = registry.get("cultivation");
        if (cultivationOpt.isEmpty() || !(cultivationOpt.get() instanceof CultivationModule cm)) {
            return Optional.empty();
        }
        return cm.getPlayerService().get(uuid);
    }
}
