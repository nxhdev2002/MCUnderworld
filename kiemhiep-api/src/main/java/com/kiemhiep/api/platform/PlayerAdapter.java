package com.kiemhiep.api.platform;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Adapter interface for player operations.
 * Provides platform-agnostic access to player functions.
 */
public interface PlayerAdapter {

    /**
     * Get the player's UUID.
     */
    UUID getUuid();

    /**
     * Get the player's name.
     */
    String getName();

    /**
     * Send a message to the player.
     */
    void sendMessage(String message);

    /**
     * Send a message with placeholders to the player.
     */
    void sendMessage(String message, String... placeholders);

    /**
     * Send an action bar message to the player.
     */
    void sendActionBar(String message);

    /**
     * Get the player's current location.
     */
    Location getLocation();

    /**
     * Teleport the player to a location.
     */
    void teleport(Location location);

    /**
     * Teleport the player asynchronously.
     */
    CompletableFuture<Boolean> teleportAsync(Location location);

    /**
     * Get the player's current health.
     */
    int getHealth();

    /**
     * Set the player's health.
     */
    void setHealth(int health);

    /**
     * Get the player's maximum health.
     */
    int getMaxHealth();

    /**
     * Check if the player is online.
     */
    boolean isOnline();

    /**
     * Check if the player is an operator.
     */
    boolean isOp();

    /**
     * Get the player's current world.
     */
    World getWorld();

    /**
     * Give an item to the player.
     */
    void giveItem(String material, int amount);

    /**
     * Remove an item from the player.
     */
    void removeItem(String material, int amount);

    /**
     * Check if the player has an item.
     */
    boolean hasItem(String material, int amount);

    /**
     * Add a potion effect to the player.
     */
    void addEffect(String effectName, int duration, int amplifier);

    /**
     * Remove a potion effect from the player.
     */
    void removeEffect(String effectName);

    /**
     * Check if the player has a potion effect.
     */
    boolean hasEffect(String effectName);

    /**
     * Get the duration of a potion effect.
     */
    int getPotionEffectDuration(String effectName);

    /**
     * Set the player's game mode.
     */
    void setGameMode(String gameMode);

    /**
     * Get the player's game mode.
     */
    String getGameMode();

    /**
     * Kick the player from the server.
     */
    void kick(String reason);

    /**
     * Ban the player from the server.
     */
    void ban(String reason);

    /**
     * Play a sound for the player.
     */
    void playSound(String soundName);

    /**
     * Send a title to the player.
     */
    void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut);

    /**
     * Get the player's experience points.
     */
    int getExperience();

    /**
     * Set the player's experience points.
     */
    void setExperience(int experience);

    /**
     * Get the player's level.
     */
    int getLevel();

    /**
     * Set the player's level.
     */
    void setLevel(int level);

    /**
     * Get the player's food level.
     */
    int getFoodLevel();

    /**
     * Set the player's food level.
     */
    void setFoodLevel(int foodLevel);

    /**
     * Get the raw player object (platform-specific).
     */
    Object getRawPlayer();

    /**
     * Check if the player has a permission.
     */
    boolean hasPermission(String permission);

    /**
     * Get the weapon item in the player's main hand.
     */
    Object getWeaponInMainHand();
}
