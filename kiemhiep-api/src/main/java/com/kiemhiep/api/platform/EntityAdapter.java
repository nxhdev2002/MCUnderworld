package com.kiemhiep.api.platform;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Adapter interface for entity operations.
 */
public interface EntityAdapter {

    /**
     * Get the entity's UUID.
     */
    UUID getUuid();

    /**
     * Get the entity's location.
     */
    Location getLocation();

    /**
     * Get the entity's current health.
     */
    double getHealth();

    /**
     * Set the entity's health.
     */
    void setHealth(double health);

    /**
     * Get the entity's maximum health.
     */
    double getMaxHealth();

    /**
     * Deal damage to the entity.
     */
    void damage(double amount);

    /**
     * Heal the entity.
     */
    void heal(double amount);

    /**
     * Check if the entity is alive.
     */
    boolean isAlive();

    /**
     * Check if the entity is valid.
     */
    default boolean isValid() {
        return isAlive();
    }

    /**
     * Check if the entity is dead.
     */
    boolean isDead();

    /**
     * Add a potion effect to the entity.
     */
    void addEffect(String effectName, int duration, int amplifier);

    /**
     * Remove a potion effect from the entity.
     */
    void removeEffect(String effectName);

    /**
     * Check if the entity has a potion effect.
     */
    boolean hasEffect(String effectName);

    /**
     * Get the entity type.
     */
    String getType();

    /**
     * Set the entity's custom name.
     */
    void setCustomName(String name);

    /**
     * Get the entity's custom name.
     */
    default String getCustomName() {
        return null;
    }

    /**
     * Check if the custom name is visible.
     */
    boolean isCustomNameVisible();

    /**
     * Set if the custom name is visible.
     */
    void setCustomNameVisible(boolean visible);

    /**
     * Set if the entity is invulnerable.
     */
    void setInvulnerable(boolean invulnerable);

    /**
     * Set the entity's maximum health.
     */
    void setMaxHealth(double health);

    /**
     * Set NBT data on the entity.
     */
    void setNbt(String key, String value);

    /**
     * Get NBT data from the entity.
     */
    String getNbt(String key);

    /**
     * Check if the entity has NBT data.
     */
    default boolean hasNbt(String key) {
        return getNbt(key) != null;
    }

    /**
     * Remove the entity.
     */
    void remove();

    /**
     * Get the entity's velocity.
     */
    double[] getVelocity();

    /**
     * Set the entity's velocity.
     */
    void setVelocity(double[] velocity);

    /**
     * Teleport the entity to a location.
     */
    void teleport(Location location);

    /**
     * Get the item in the entity's main hand.
     */
    String getItemInMainHand();

    /**
     * Set the item in the entity's main hand.
     */
    void setItemInMainHand(String material);

    /**
     * Get the raw entity object (platform-specific).
     */
    Object getRawEntity();
}
