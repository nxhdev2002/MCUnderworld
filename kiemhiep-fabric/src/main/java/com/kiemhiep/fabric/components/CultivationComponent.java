package com.kiemhiep.fabric.components;

import com.kiemhiep.api.model.Cultivation;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

/**
 * Cultivation component for players using Cardinal Components API.
 */
public class CultivationComponent implements ComponentV3, AutoSyncedComponent {
    private int realmLevel = 0;
    private int subLevel = 0;
    private int exp = 0;

    public CultivationComponent() {
    }

    public CultivationComponent(int realmLevel, int subLevel, int exp) {
        this.realmLevel = realmLevel;
        this.subLevel = subLevel;
        this.exp = exp;
    }

    public int getRealmLevel() {
        return realmLevel;
    }

    public void setRealmLevel(int realmLevel) {
        this.realmLevel = realmLevel;
    }

    public int getSubLevel() {
        return subLevel;
    }

    public void setSubLevel(int subLevel) {
        this.subLevel = subLevel;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public void addExp(int amount) {
        this.exp += amount;
    }

    public void breakthrough(int newRealmLevel) {
        this.realmLevel = newRealmLevel;
        this.subLevel = 0;
        this.exp = 0;
    }

    public Cultivation toCultivation(UUID playerId) {
        return new Cultivation(playerId, realmLevel, subLevel, exp);
    }

    public void fromCultivation(Cultivation cultivation) {
        this.realmLevel = cultivation.getRealmLevel();
        this.subLevel = cultivation.getSubLevel();
        this.exp = cultivation.getExp();
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.realmLevel = tag.getInt("RealmLevel");
        this.subLevel = tag.getInt("SubLevel");
        this.exp = tag.getInt("Exp");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("RealmLevel", realmLevel);
        tag.putInt("SubLevel", subLevel);
        tag.putInt("Exp", exp);
    }
}
