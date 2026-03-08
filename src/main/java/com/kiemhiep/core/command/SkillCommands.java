package com.kiemhiep.core.command;

import com.kiemhiep.api.model.Skill;
import com.kiemhiep.api.model.SkillDefinition;
import com.kiemhiep.api.repository.PlayerRepository;
import com.kiemhiep.api.service.SkillService;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public final class SkillCommands {

    private SkillCommands() {}

    public static void register(Supplier<SkillService> skillServiceSupplier, Supplier<PlayerRepository> playerRepositorySupplier) {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            register(dispatcher, skillServiceSupplier.get(), playerRepositorySupplier.get());
        });
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher, SkillService skillService, PlayerRepository playerRepository) {
        var skill = Commands.literal("skill")
            .then(Commands.literal("list")
                .executes(ctx -> executeList(ctx.getSource(), skillService, playerRepository)))
            .then(Commands.literal("info")
                .then(Commands.argument("skillId", StringArgumentType.string())
                    .executes(ctx -> executeInfo(ctx.getSource(), skillService, StringArgumentType.getString(ctx, "skillId")))))
            .then(Commands.literal("give")
                .requires(s -> s.getEntity() instanceof ServerPlayer)
                .then(Commands.argument("player", net.minecraft.commands.arguments.EntityArgument.player())
                    .then(Commands.argument("skillId", StringArgumentType.string())
                        .then(Commands.argument("count", IntegerArgumentType.integer(1, 64))
                            .executes(ctx -> executeGive(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), skillService, StringArgumentType.getString(ctx, "skillId"), IntegerArgumentType.getInteger(ctx, "count"))))
                        .executes(ctx -> executeGive(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), skillService, StringArgumentType.getString(ctx, "skillId"), 1)))));
        dispatcher.register(skill);
    }

    private static int executeList(CommandSourceStack source, SkillService skillService, PlayerRepository playerRepository) {
        if (!(source.getEntity() instanceof ServerPlayer serverPlayer)) {
            source.sendFailure(Component.literal("Only players can list skills."));
            return 0;
        }
        Optional<com.kiemhiep.api.model.Player> player = playerRepository.getByUuid(serverPlayer.getUUID().toString());
        if (player.isEmpty()) {
            source.sendFailure(Component.literal("Player not found in database."));
            return 0;
        }
        List<Skill> skills = skillService.getPlayerSkills(player.get().id());
        if (skills.isEmpty()) {
            source.sendSuccess(() -> Component.literal("You have no skills learned."), false);
            return 0;
        }
        String line = skills.stream().map(s -> s.skillId() + " (Lv." + s.level() + ")").reduce((a, b) -> a + ", " + b).orElse("");
        source.sendSuccess(() -> Component.literal("Skills: " + line), false);
        return skills.size();
    }

    private static int executeInfo(CommandSourceStack source, SkillService skillService, String skillId) {
        Optional<SkillDefinition> def = skillService.getSkillDefinition(skillId);
        if (def.isEmpty()) {
            source.sendFailure(Component.literal("Unknown skill: " + skillId));
            return 0;
        }
        SkillDefinition d = def.get();
        source.sendSuccess(() -> Component.literal(String.format(
            "Skill %s: %s | mana=%d cooldown=%dt maxRadius=%.1f type=%s cast=%dt consumable=%s",
            d.skillId(), d.name(), d.manaCost(), d.cooldownTicks(), d.maxRadius(), d.skillType(), d.castTimeTicks(), d.consumable())), false);
        return 1;
    }

    private static int executeGive(CommandSourceStack source, ServerPlayer target, SkillService skillService, String skillId, int count) {
        Optional<SkillDefinition> def = skillService.getSkillDefinition(skillId);
        if (def.isEmpty()) {
            source.sendFailure(Component.literal("Unknown skill: " + skillId));
            return 0;
        }
        // Give item: resolve item from definition and add to target inventory (done in SkillModule with item registry)
        source.sendSuccess(() -> Component.literal("Use /skill give via SkillModule item registry (give " + def.get().itemId() + " x" + count + ")"), false);
        return 1;
    }
}
