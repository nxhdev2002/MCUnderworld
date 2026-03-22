package com.kiemhiep.sect;

import com.kiemhiep.api.event.EventDispatcher;
import com.kiemhiep.api.module.KiemHiepModule;
import com.kiemhiep.api.module.ModuleContext;
import com.kiemhiep.api.service.PlayerService;
import com.kiemhiep.api.service.SectService;
import com.kiemhiep.core.database.JdbcSectRepository;
import com.kiemhiep.core.repository.CachedSectRepository;
import com.kiemhiep.core.service.SectServiceImpl;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Sect module: manage tông môn (guild) creation, membership, and relations.
 * <p>
 * Dependencies: Cultivation (requires player data).
 */
public class SectModule implements KiemHiepModule {

    private static final AtomicReference<SectModule> INSTANCE = new AtomicReference<>();

    private final SectService sectService;
    private final PlayerService playerService;

    private boolean enabled;

    public SectModule(SectService sectService, PlayerService playerService) {
        this.sectService = sectService;
        this.playerService = playerService;
        INSTANCE.set(this);
    }

    @Override
    public String getId() {
        return "sect";
    }

    @Override
    public String getName() {
        return "Sect";
    }

    @Override
    public List<String> getDependencies() {
        // Requires Cultivation module for player data
        return List.of("cultivation");
    }

    @Override
    public void onLoad(ModuleContext ctx) {
        // Service already injected via constructor - nothing to do here
    }

    @Override
    public void onEnable(ModuleContext ctx) {
        enabled = true;

        // Register commands
        SectCommands.register();
    }

    @Override
    public void onDisable() {
        enabled = false;
        INSTANCE.set(null);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public SectService getService() {
        return sectService;
    }

    public PlayerService getPlayerService() {
        return playerService;
    }

    public static Optional<SectModule> getInstance() {
        return Optional.ofNullable(INSTANCE.get());
    }

    // Factory method for creating SectModule with JDBC repo
    public static SectModule createWithJdbc(DataSource dataSource, PlayerService playerService, EventDispatcher eventDispatcher) {
        JdbcSectRepository jdbcRepo = new JdbcSectRepository(dataSource);
        // In production, wrap with cache
        SectService sectService = new SectServiceImpl(jdbcRepo, playerService, eventDispatcher);
        return new SectModule(sectService, playerService);
    }
}
