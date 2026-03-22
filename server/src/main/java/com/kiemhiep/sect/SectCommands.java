package com.kiemhiep.sect;

import com.kiemhiep.api.model.Sect;
import com.kiemhiep.api.model.SectMember;
import com.kiemhiep.api.model.SectRelation;
import com.kiemhiep.api.service.SectService;
import com.kiemhiep.core.command.CommandPermissionHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Optional;

public final class SectCommands {

    private SectCommands() {}

    public static void register() {
        net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            var sectBase = Commands.literal("sect")
                // /kiemhiep sect create <name>
                .then(Commands.literal("create")
                    .then(Commands.argument("name", StringArgumentType.string())
                        .executes(ctx -> executeCreate(ctx.getSource(),
                            StringArgumentType.getString(ctx, "name")))))
                // /kiemhiep sect join <sectId>
                .then(Commands.literal("join")
                    .then(Commands.argument("sectId", IntegerArgumentType.integer(1))
                        .executes(ctx -> executeJoin(ctx.getSource(),
                            IntegerArgumentType.getInteger(ctx, "sectId")))))
                // /kiemhiep sect leave <sectId>
                .then(Commands.literal("leave")
                    .then(Commands.argument("sectId", IntegerArgumentType.integer(1))
                        .executes(ctx -> executeLeave(ctx.getSource(),
                            IntegerArgumentType.getInteger(ctx, "sectId")))))
                // /kiemhiep sect list
                .then(Commands.literal("list")
                    .executes(ctx -> executeList(ctx.getSource(), null)))
                // /kiemhiep sect info [sectId]
                .then(Commands.literal("info")
                    .executes(ctx -> executeInfo(ctx.getSource(), null))
                    .then(Commands.argument("sectId", IntegerArgumentType.integer(1))
                        .requires(source -> CommandPermissionHelper.hasPermissionLevel(source, 2))
                        .executes(ctx -> executeInfo(ctx.getSource(),
                            (long) IntegerArgumentType.getInteger(ctx, "sectId")))))
                // /kiemhiep sect relation <sectId> <ALLIED|HOSTILE|NEUTRAL> <targetSectId>
                .then(Commands.literal("relation")
                    .then(Commands.argument("sectId", IntegerArgumentType.integer(1))
                        .then(Commands.argument("type", StringArgumentType.string())
                            .then(Commands.argument("targetSectId", IntegerArgumentType.integer(1))
                                .requires(source -> CommandPermissionHelper.hasPermissionLevel(source, 2))
                                .executes(ctx -> executeRelation(ctx.getSource(),
                                    (long) IntegerArgumentType.getInteger(ctx, "sectId"),
                                    StringArgumentType.getString(ctx, "type"),
                                    (long) IntegerArgumentType.getInteger(ctx, "targetSectId")))))))
                // /kiemhiep sect leader <sectId> <playerName> (admin only)
                .then(Commands.literal("leader")
                    .then(Commands.argument("sectId", IntegerArgumentType.integer(1))
                        .then(Commands.argument("playerName", StringArgumentType.string())
                            .requires(source -> CommandPermissionHelper.hasPermissionLevel(source, 2))
                            .executes(ctx -> executeLeader(ctx.getSource(),
                                (long) IntegerArgumentType.getInteger(ctx, "sectId"),
                                StringArgumentType.getString(ctx, "playerName"))))));

            dispatcher.register(Commands.literal("kiemhiep").then(sectBase));
        });
    }

    private static int executeCreate(CommandSourceStack source, String name) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("Only players can create a sect."));
            return 0;
        }

        Optional<Sect> optSect = getSectService().createSect(player.getUUID().toString().hashCode(), name);
        if (optSect.isPresent()) {
            Sect sect = optSect.get();
            source.sendSuccess(() -> Component.literal("Sect created successfully: %s (ID: %d)".formatted(sect.name(), sect.id())), true);
            return 1;
        } else {
            source.sendFailure(Component.literal("Failed to create sect. You may already be leading another sect."));
            return 0;
        }
    }

    private static int executeJoin(CommandSourceStack source, long sectId) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("Only players can join a sect."));
            return 0;
        }

        Optional<SectMember> optMember = getSectService().joinSect(player.getUUID().toString().hashCode(), sectId);
        if (optMember.isPresent()) {
            source.sendSuccess(() -> Component.literal("Joined sect ID %d successfully.".formatted(sectId)), true);
            return 1;
        } else {
            source.sendFailure(Component.literal("Failed to join sect. You may already be a member or leading another sect."));
            return 0;
        }
    }

    private static int executeLeave(CommandSourceStack source, long sectId) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("Only players can leave a sect."));
            return 0;
        }

        boolean success = getSectService().leaveSect(player.getUUID().toString().hashCode(), sectId);
        if (success) {
            source.sendSuccess(() -> Component.literal("Left sect ID %d successfully.".formatted(sectId)), true);
            return 1;
        } else {
            source.sendFailure(Component.literal("Failed to leave sect. You may not be a member."));
            return 0;
        }
    }

    private static int executeList(CommandSourceStack source, Long targetSectId) {
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            source.sendFailure(Component.literal("Only players can use this command."));
            return 0;
        }

        List<Sect> playerSects = getSectService().getSectsForPlayer(player.getUUID().toString().hashCode());
        source.sendSuccess(() -> Component.literal("Sects you are a member of:"), false);

        for (Sect sect : playerSects) {
            Optional<SectMember> memberOpt = getSectService().getMember(sect.id(), player.getUUID().toString().hashCode());
            String rank = memberOpt.map(m -> m.rank().name()).orElse("UNKNOWN");
            source.sendSuccess(() -> Component.literal("  - %s (ID: %d, Rank: %s)".formatted(sect.name(), sect.id(), rank)), false);
        }

        if (playerSects.isEmpty()) {
            source.sendSuccess(() -> Component.literal("  (None)"), false);
        }

        return playerSects.size();
    }

    private static int executeInfo(CommandSourceStack source, Long targetSectId) {
        long sectId = targetSectId != null ? targetSectId : 0;

        if (sectId == 0) {
            // Get info for a sect the player is in
            ServerPlayer player = source.getPlayer();
            if (player == null) {
                source.sendFailure(Component.literal("Specify a sect ID or use from player context."));
                return 0;
            }

            List<Sect> playerSects = getSectService().getSectsForPlayer(player.getUUID().toString().hashCode());
            if (playerSects.isEmpty()) {
                source.sendFailure(Component.literal("You are not a member of any sect."));
                return 0;
            }
            sectId = playerSects.get(0).id(); // Show info for first sect
        }

        Optional<Sect> optSect = getSectService().getSectById(sectId);
        if (optSect.isEmpty()) {
            source.sendFailure(Component.literal("Sect not found: " + sectId));
            return 0;
        }

        Sect sect = optSect.get();
        source.sendSuccess(() -> Component.literal("Sect Info: %s (ID: %d)".formatted(sect.name(), sect.id())), false);
        source.sendSuccess(() -> Component.literal("  Leader: " + sect.leaderId()), false);
        source.sendSuccess(() -> Component.literal("  Level: %d".formatted(sect.level())), false);
        source.sendSuccess(() -> Component.literal("  EXP: %d".formatted(sect.exp())), false);

        List<SectMember> members = getSectService().getMembers(sectId);
        source.sendSuccess(() -> Component.literal("  Members: %d".formatted(members.size())), false);

        return 1;
    }

    private static int executeRelation(CommandSourceStack source, long sectId, String typeStr, long targetSectId) {
        SectRelation.Type type;
        try {
            type = SectRelation.Type.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            source.sendFailure(Component.literal("Invalid relation type: %s. Valid types: ALLIED, HOSTILE, NEUTRAL.".formatted(typeStr)));
            return 0;
        }

        getSectService().setRelation(sectId, targetSectId, type);
        source.sendSuccess(() -> Component.literal("Set relation between sect %d and %d to %s".formatted(sectId, targetSectId, type)), true);
        return 1;
    }

    private static int executeLeader(CommandSourceStack source, long sectId, String playerName) {
        // In a real implementation, this would look up player by name
        // For now, just a placeholder
        source.sendSuccess(() -> Component.literal("Transfer leadership command received (player lookup by name would happen here)"), false);
        return 1;
    }

    private static SectService getSectService() {
        return SectModule.getInstance()
            .map(SectModule::getService)
            .orElseThrow(() -> new IllegalStateException("Sect module is not enabled"));
    }
}
