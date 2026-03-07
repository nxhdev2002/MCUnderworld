package com.kiemhiep.fabric.components;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;

import java.util.HashMap;
import java.util.Map;

/**
 * Wallet component for players using Cardinal Components API.
 */
public class WalletComponent implements ComponentV3, AutoSyncedComponent {
    private final Map<String, Integer> balances = new HashMap<>();

    public WalletComponent() {
        // Initialize default currencies
        balances.put("SPIRIT_STONE", 0);
        balances.put("SILVER", 0);
        balances.put("GOLD", 0);
    }

    public int getBalance(String currencyType) {
        return balances.getOrDefault(currencyType, 0);
    }

    public void setBalance(String currencyType, int amount) {
        balances.put(currencyType, amount);
    }

    public void addBalance(String currencyType, int amount) {
        balances.merge(currencyType, amount, Integer::sum);
    }

    public boolean removeBalance(String currencyType, int amount) {
        int current = getBalance(currencyType);
        if (current >= amount) {
            setBalance(currencyType, current - amount);
            return true;
        }
        return false;
    }

    public boolean hasBalance(String currencyType, int amount) {
        return getBalance(currencyType) >= amount;
    }

    public Map<String, Integer> getAllBalances() {
        return new HashMap<>(balances);
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        balances.clear();
        for (String key : tag.getKeys()) {
            balances.put(key, tag.getInt(key));
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        for (Map.Entry<String, Integer> entry : balances.entrySet()) {
            tag.putInt(entry.getKey(), entry.getValue());
        }
    }
}
