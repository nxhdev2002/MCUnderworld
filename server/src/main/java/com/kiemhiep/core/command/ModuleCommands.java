package com.kiemhiep.core.command;

import com.kiemhiep.api.module.ModuleRegistry;
import com.kiemhiep.core.module.ModuleLoader;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public final class ModuleCommands {

    private ModuleCommands() {}

    public static void register(Supplier<ModuleRegistry> registrySupplier, Supplier<ModuleLoader> loaderSupplier) {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            register(dispatcher, registrySupplier.get(), loaderSupplier.get());
        });
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher, ModuleRegistry registry, ModuleLoader loader) {
        var module = Commands.literal("module")
            .then(Commands.literal("list")
                .executes(ctx -> executeList(ctx.getSource(), registry)))
            .then(Commands.literal("reload")
                .executes(ctx -> executeReload(ctx.getSource(), registry, loader)))
            .then(Commands.literal("enable")
                .then(Commands.argument("id", StringArgumentType.string())
                    .suggests((ctx, builder) -> {
                        registry.getAll().forEach(m -> builder.suggest(m.getId()));
                        return builder.buildFuture();
                    })
                    .executes(ctx -> executeEnable(ctx.getSource(), registry, loader, StringArgumentType.getString(ctx, "id")))))
            .then(Commands.literal("disable")
                .then(Commands.argument("id", StringArgumentType.string())
                    .suggests((ctx, builder) -> {
                        registry.getAll().forEach(m -> builder.suggest(m.getId()));
                        return builder.buildFuture();
                    })
                    .executes(ctx -> executeDisable(ctx.getSource(), registry, loader, StringArgumentType.getString(ctx, "id")))));
        dispatcher.register(Commands.literal("kiemhiep").then(module));
    }

    private static int executeList(CommandSourceStack source, ModuleRegistry registry) {
        var list = registry.getAll().stream()
            .map(m -> m.getId() + " (" + (registry.isEnabled(m.getId()) ? "on" : "off") + ")")
            .toList();
        source.sendSuccess(() -> Component.literal("Modules: " + String.join(", ", list)), false);
        return list.size();
    }

    private static int executeReload(CommandSourceStack source, ModuleRegistry registry, ModuleLoader loader) {
        loader.loadAll();
        loader.applyConfig();
        source.sendSuccess(() -> Component.literal("Module config reloaded."), true);
        return 1;
    }

    private static int executeEnable(CommandSourceStack source, ModuleRegistry registry, ModuleLoader loader, String id) {
        if (registry.get(id).isEmpty()) {
            source.sendFailure(Component.literal("Unknown module: " + id));
            return 0;
        }
        loader.enableModule(id);
        source.sendSuccess(() -> Component.literal("Module enabled: " + id), true);
        return 1;
    }

    private static int executeDisable(CommandSourceStack source, ModuleRegistry registry, ModuleLoader loader, String id) {
        if (registry.get(id).isEmpty()) {
            source.sendFailure(Component.literal("Unknown module: " + id));
            return 0;
        }
        loader.disableModule(id);
        source.sendSuccess(() -> Component.literal("Module disabled: " + id), true);
        return 1;
    }
}
