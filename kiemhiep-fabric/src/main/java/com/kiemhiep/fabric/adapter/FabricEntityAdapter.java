package com.kiemhiep.fabric.adapter;

import com.kiemhiep.api.platform.EntityAdapter;
import com.kiemhiep.api.platform.Location;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;

import java.util.UUID;

/**
 * Fabric adapter for Entity.
 */
public class FabricEntityAdapter implements EntityAdapter {
    private final LivingEntity entity;

    public FabricEntityAdapter(LivingEntity entity) {
        this.entity = entity;
    }

    public LivingEntity getHandle() {
        return entity;
    }

    @Override
    public UUID getUuid() {
        return entity.getUuid();
    }

    @Override
    public Location getLocation() {
        return new Location(
                new com.kiemhiep.api.platform.World(entity.getWorld().getRegistryKey().getValue().toString()),
                entity.getX(),
                entity.getY(),
                entity.getZ(),
                entity.getYaw(),
                entity.getPitch()
        );
    }

    @Override
    public double getHealth() {
        return entity.getHealth();
    }

    @Override
    public void setHealth(double health) {
        entity.setHealth((float) health);
    }

    @Override
    public double getMaxHealth() {
        return entity.getMaxHealth();
    }

    @Override
    public void damage(double amount) {
        entity.damage(entity.getDamageSources().generic(), (float) amount);
    }

    @Override
    public void heal(double amount) {
        entity.heal((float) amount);
    }

    @Override
    public boolean isAlive() {
        return entity.isAlive();
    }

    @Override
    public boolean isDead() {
        return entity.isDead();
    }

    @Override
    public void addEffect(String effectName, int duration, int amplifier) {
        // TODO: Implement status effect addition
    }

    @Override
    public void removeEffect(String effectName) {
        // TODO: Implement status effect removal
    }

    @Override
    public boolean hasEffect(String effectName) {
        // TODO: Implement status effect check
        return false;
    }

    @Override
    public String getType() {
        return Registries.ENTITY_TYPE.getId(entity.getType()).toString();
    }

    @Override
    public void setCustomName(String name) {
        entity.setCustomName(net.minecraft.text.Text.literal(name));
    }

    @Override
    public String getCustomName() {
        if (entity.getCustomName() != null) {
            return entity.getCustomName().getString();
        }
        return null;
    }

    @Override
    public boolean isCustomNameVisible() {
        return entity.isCustomNameVisible();
    }

    @Override
    public void setCustomNameVisible(boolean visible) {
        entity.setCustomNameVisible(visible);
    }

    @Override
    public void setInvulnerable(boolean invulnerable) {
        entity.setInvulnerable(invulnerable);
    }

    @Override
    public void setMaxHealth(double health) {
        entity.getAttribute(entity.getMaxHealthAttribute()).setBaseValue(health);
    }

    @Override
    public void setNbt(String key, String value) {
        NbtCompound nbt = entity.writeNbt(new NbtCompound());
        nbt.putString(key, value);
        entity.readNbt(nbt);
    }

    @Override
    public String getNbt(String key) {
        NbtCompound nbt = entity.writeNbt(new NbtCompound());
        if (nbt.contains(key)) {
            return nbt.getString(key);
        }
        return null;
    }

    @Override
    public void remove() {
        entity.kill();
    }

    @Override
    public double[] getVelocity() {
        var vel = entity.getVelocity();
        return new double[]{vel.x, vel.y, vel.z};
    }

    @Override
    public void setVelocity(double[] velocity) {
        entity.setVelocity(velocity[0], velocity[1], velocity[2]);
    }

    @Override
    public void teleport(Location location) {
        entity.teleport(location.getX(), location.getY(), location.getZ());
    }

    @Override
    public String getItemInMainHand() {
        // TODO: Implement
        return "air";
    }

    @Override
    public void setItemInMainHand(String material) {
        // TODO: Implement
    }

    @Override
    public Object getRawEntity() {
        return entity;
    }
}
