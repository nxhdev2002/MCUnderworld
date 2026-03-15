package com.kiemhiep.economy;

import com.kiemhiep.api.module.KiemHiepModule;
import com.kiemhiep.api.module.ModuleContext;
import com.kiemhiep.api.repository.TransactionRepository;
import com.kiemhiep.api.repository.WalletRepository;
import com.kiemhiep.api.service.EconomyService;
import com.kiemhiep.core.database.JdbcTransactionRepository;
import com.kiemhiep.core.database.JdbcWalletRepository;
import com.kiemhiep.core.repository.CachedWalletRepository;
import com.kiemhiep.core.service.EconomyServiceImpl;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Economy module: multi-currency system with signed balance (no 64 stack limit).
 * Depends on: Cultivation module (requires player data).
 */
public class EconomyModule implements KiemHiepModule {

    private static final AtomicReference<EconomyModule> INSTANCE = new AtomicReference<>();

    private final EconomyService economyService;

    private boolean enabled;

    public EconomyModule(EconomyService economyService) {
        this.economyService = economyService;
        INSTANCE.set(this);
    }

    @Override
    public String getId() {
        return "economy";
    }

    @Override
    public String getName() {
        return "Economy";
    }

    @Override
    public List<String> getDependencies() {
        return List.of("cultivation");
    }

    @Override
    public void onLoad(ModuleContext ctx) {
        // Service already injected via constructor - nothing to do here
    }

    @Override
    public void onEnable(ModuleContext ctx) {
        enabled = true;

        // Register commands and listeners
        EconomyCommands.register();
        EconomyListener.register();
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

    public EconomyService getService() {
        return economyService;
    }

    public static Optional<EconomyModule> getInstance() {
        return Optional.ofNullable(INSTANCE.get());
    }
}
