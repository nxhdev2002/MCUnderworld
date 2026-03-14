package com.kiemhiep.core.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.kiemhiep.api.model.Skill;
import com.kiemhiep.api.model.SkillDefinition;
import com.kiemhiep.api.repository.PlayerRepository;
import com.kiemhiep.api.service.SkillService;
import com.kiemhiep.platform.SkillItemRegistrationHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class SkillCommands {

    private SkillCommands() {}

    public static void register(Supplier<SkillService> skillServiceSupplier, Supplier<PlayerRepository> playerRepositorySupplier) {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            register(dispatcher, skillServiceSupplier, playerRepositorySupplier);
        });
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                 Supplier<SkillService> skillServiceSupplier,
                                 Supplier<PlayerRepository> playerRepositorySupplier) {
        var skill = Commands.literal("skill")
            .then(Commands.literal("list")
                .executes(ctx -> executeList(ctx.getSource(), skillServiceSupplier, playerRepositorySupplier)))
            .then(Commands.literal("all")
                .requires(source -> CommandPermissionHelper.hasPermissionLevel(source, 2))
                .executes(ctx -> executeAll(ctx.getSource(), skillServiceSupplier)))
            .then(Commands.literal("info")
                .then(Commands.argument("skillId", StringArgumentType.string())
                    .executes(ctx -> executeInfo(ctx.getSource(), skillServiceSupplier, StringArgumentType.getString(ctx, "skillId")))))
            .then(Commands.literal("give")
                .requires(s -> s.getEntity() instanceof ServerPlayer)
                .then(Commands.literal("all")
                    .executes(ctx -> executeGiveAll(ctx.getSource(), skillServiceSupplier, playerRepositorySupplier)))
                .then(Commands.argument("player", net.minecraft.commands.arguments.EntityArgument.player())
                    .then(Commands.argument("skillId", StringArgumentType.string())
                        .then(Commands.argument("count", IntegerArgumentType.integer(1, 64))
                            .executes(ctx -> executeGive(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), skillServiceSupplier, StringArgumentType.getString(ctx, "skillId"), IntegerArgumentType.getInteger(ctx, "count"))))
                        .executes(ctx -> executeGive(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), skillServiceSupplier, StringArgumentType.getString(ctx, "skillId"), 1)))));
        dispatcher.register(skill);
    }

    private static int executeList(CommandSourceStack source, Supplier<SkillService> skillServiceSupplier, Supplier<PlayerRepository> playerRepositorySupplier) {
        SkillService skillService = skillServiceSupplier.get();
        PlayerRepository playerRepository = playerRepositorySupplier.get();
        if (skillService == null || playerRepository == null) {
            source.sendFailure(Component.literal("Skill module is disabled."));
            return 0;
        }
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
        List<String> parts = new ArrayList<>(skills.size());
        for (Skill s : skills) {
            parts.add(s.skillId() + " (Lv." + s.level() + ")");
        }
        source.sendSuccess(() -> Component.literal("Skills: " + String.join(", ", parts)), false);
        return skills.size();
    }

    private static int executeAll(CommandSourceStack source, Supplier<SkillService> skillServiceSupplier) {
        SkillService skillService = skillServiceSupplier.get();
        if (skillService == null) {
            source.sendFailure(Component.literal("Skill module is disabled."));
            return 0;
        }
        List<SkillDefinition> all = skillService.getAllSkillDefinitions();
        if (all.isEmpty()) {
            source.sendSuccess(() -> Component.literal("No skills defined on the system."), false);
            return 0;
        }
        List<String> parts = new ArrayList<>(all.size());
        for (SkillDefinition d : all) {
            parts.add(d.skillId() + " (" + d.name() + ", " + d.itemId() + ")");
        }
        source.sendSuccess(() -> Component.literal("Skills on system: " + String.join(", ", parts)), false);
        return all.size();
    }

    private static int executeInfo(CommandSourceStack source, Supplier<SkillService> skillServiceSupplier, String skillId) {
        SkillService skillService = skillServiceSupplier.get();
        if (skillService == null) {
            source.sendFailure(Component.literal("Skill module is disabled."));
            return 0;
        }
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

    private static int executeGiveAll(CommandSourceStack source, Supplier<SkillService> skillServiceSupplier, Supplier<PlayerRepository> playerRepositorySupplier) {
        SkillService skillService = skillServiceSupplier.get();
        PlayerRepository playerRepository = playerRepositorySupplier.get();
        if (skillService == null || playerRepository == null) {
            source.sendFailure(Component.literal("Skill module is disabled."));
            return 0;
        }
        if (!(source.getEntity() instanceof ServerPlayer serverPlayer)) {
            source.sendFailure(Component.literal("Only players can use this command."));
            return 0;
        }
        Optional<com.kiemhiep.api.model.Player> player = playerRepository.getByUuid(serverPlayer.getUUID().toString());
        if (player.isEmpty()) {
            source.sendFailure(Component.literal("Player not found in database."));
            return 0;
        }
        List<SkillDefinition> all = skillService.getAllSkillDefinitions();
        if (all.isEmpty()) {
            source.sendSuccess(() -> Component.literal("No skills defined. Nothing to give."), false);
            return 0;
        }
        int given = 0;
        for (SkillDefinition def : all) {
            String itemId = def.itemId();
            Optional<Item> itemOpt = SkillItemRegistrationHelper.getItemById(itemId);
            if (itemOpt.isEmpty()) continue;
            ItemStack stack = new ItemStack(itemOpt.get(), 1);
            if (!serverPlayer.getInventory().add(stack)) {
                serverPlayer.drop(stack, false);
            }
            given++;
        }
        return given;
    }

    private static int executeGive(CommandSourceStack source, ServerPlayer target, Supplier<SkillService> skillServiceSupplier, String skillId, int count) {
        SkillService skillService = skillServiceSupplier.get();
        if (skillService == null) {
            source.sendFailure(Component.literal("Skill module is disabled."));
            return 0;
        }
        Optional<SkillDefinition> def = skillService.getSkillDefinition(skillId);
        if (def.isEmpty()) {
            source.sendFailure(Component.literal("Unknown skill: " + skillId));
            return 0;
        }
        String itemId = def.get().itemId();
        Optional<Item> itemOpt = SkillItemRegistrationHelper.getItemById(itemId);
        if (itemOpt.isEmpty()) {
            source.sendFailure(Component.literal("Skill item not registered: " + itemId));
            return 0;
        }
        Item item = itemOpt.get();
        ItemStack stack = new ItemStack(item, count);
        if (!target.getInventory().add(stack)) {
            target.drop(stack, false);
            source.sendFailure(Component.literal("Target inventory is full. Dropped " + count + " x " + itemId + " on the ground."));
            return 0;
        }
        source.sendSuccess(() -> Component.literal("Gave " + count + " x " + itemId + " to " + target.getName().getString() + "."), false);
        return 1;
    }
}
