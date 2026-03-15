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

/**
 * Economy module: multi-currency system with signed balance (no 64 stack limit).
 * Depends on: Cultivation module (requires player data).
 */
public class EconomyModule implements KiemHiepModule {

    private static volatile EconomyService economyServiceHolder;

    private boolean enabled;

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
        Optional<DataSource> dsOpt = com.kiemhiep.KiemhiepBootstrap.getDataSource();
        if (dsOpt.isEmpty()) {
            com.kiemhiep.Kiemhiep.LOGGER.warn("Economy module: no DataSource, economy features disabled.");
            return;
        }
        DataSource ds = dsOpt.get();

        // Create repositories
        WalletRepository walletRepository = new JdbcWalletRepository(ds);
        TransactionRepository transactionRepository = new JdbcTransactionRepository(ds);

        // Create service
        EconomyServiceImpl economyServiceImpl = new EconomyServiceImpl(walletRepository, transactionRepository, ctx.getEventDispatcher());
        economyServiceHolder = economyServiceImpl;
    }

    @Override
    public void onEnable(ModuleContext ctx) {
        enabled = true;
        if (economyServiceHolder == null) return;

        // Register commands and listeners
        EconomyCommands.register();
        EconomyListener.register();
    }

    @Override
    public void onDisable() {
        enabled = false;
        economyServiceHolder = null;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public static Optional<EconomyService> getEconomyService() {
        return Optional.ofNullable(economyServiceHolder);
    }
}
