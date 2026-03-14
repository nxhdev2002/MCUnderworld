package com.kiemhiep.cultivation;

import com.kiemhiep.api.module.KiemHiepModule;
import com.kiemhiep.api.module.ModuleContext;
import com.kiemhiep.api.service.CultivationService;
import com.kiemhiep.api.service.PlayerService;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import java.util.List;

/**
 * Cultivation module: lifecycle onLoad/onEnable/onDisable; registers join listener and commands.
 */
public class CultivationModule implements KiemHiepModule {

    private final CultivationService cultivationService;
    private final PlayerService playerService;
    private boolean enabled;

    public CultivationModule(CultivationService cultivationService, PlayerService playerService) {
        this.cultivationService = cultivationService;
        this.playerService = playerService;
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
        CultivationJoinListener.register(cultivationService, playerService);
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            CultivationCommands.register(dispatcher, cultivationService, playerService));
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

    public PlayerService getPlayerService() {
        return playerService;
    }
}
