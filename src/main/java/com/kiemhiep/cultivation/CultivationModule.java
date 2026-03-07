package com.kiemhiep.cultivation;

import com.kiemhiep.api.module.KiemHiepModule;
import com.kiemhiep.api.module.ModuleContext;
import com.kiemhiep.api.repository.PlayerRepository;
import com.kiemhiep.api.service.CultivationService;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import java.util.List;

/**
 * Cultivation module: lifecycle onLoad/onEnable/onDisable; registers join listener and commands.
 */
public class CultivationModule implements KiemHiepModule {

    private final CultivationService cultivationService;
    private final PlayerRepository playerRepository;
    private boolean enabled;

    public CultivationModule(CultivationService cultivationService, PlayerRepository playerRepository) {
        this.cultivationService = cultivationService;
        this.playerRepository = playerRepository;
        this.enabled = false;
    }

    @Override
    public String getId() {
        return "cultivation";
    }

    @Override
    public String getName() {
        return "Cultivation";
    }

    @Override
    public List<String> getDependencies() {
        return List.of();
    }

    @Override
    public void onLoad(ModuleContext ctx) {
        // Bindings provided via constructor; nothing to register in context.
    }

    @Override
    public void onEnable(ModuleContext ctx) {
        CultivationJoinListener.register(cultivationService, playerRepository);
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            CultivationCommands.register(dispatcher, cultivationService, playerRepository));
    }

    @Override
    public void onDisable() {
        // Fabric does not support unregistering command or JOIN listener; they stay until server shutdown.
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public CultivationService getService() {
        return cultivationService;
    }
}
