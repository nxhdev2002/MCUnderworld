package com.kiemhiep.api.service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for combat system.
 * Handles damage calculation and combat sessions.
 */
public interface CombatService {

    /**
     * Calculate damage dealt by an attacker to a defender.
     *
     * @param attackerId The attacker's UUID
     * @param defenderId The defender's UUID
     * @param baseDamage The base damage of the attack
     * @return Calculated damage value
     */
    double calculateDamage(UUID attackerId, UUID defenderId, double baseDamage);

    /**
     * Calculate damage with skill multiplier.
     *
     * @param attackerId      The attacker's UUID
     * @param defenderId      The defender's UUID
     * @param baseDamage      The base damage
     * @param skillMultiplier The skill damage multiplier
     * @return Calculated damage value
     */
    double calculateSkillDamage(UUID attackerId, UUID defenderId, double baseDamage, double skillMultiplier);

    /**
     * Apply damage to an entity.
     *
     * @param targetId The target's UUID
     * @param damage   The damage amount
     * @param sourceId The damage source UUID (attacker)
     * @return CompletableFuture with actual damage dealt
     */
    CompletableFuture<Double> applyDamage(UUID targetId, double damage, UUID sourceId);

    /**
     * Heal an entity.
     *
     * @param targetId The target's UUID
     * @param amount   The heal amount
     * @return CompletableFuture with actual amount healed
     */
    CompletableFuture<Double> heal(UUID targetId, double amount);

    /**
     * Check if an entity is in combat.
     *
     * @param entityId The entity's UUID
     * @return true if in combat
     */
    boolean isInCombat(UUID entityId);

    /**
     * Get the remaining combat timeout for an entity.
     *
     * @param entityId The entity's UUID
     * @return Remaining ticks in combat
     */
    int getCombatTimeout(UUID entityId);

    /**
     * Start combat for an entity.
     *
     * @param entityId The entity's UUID
     */
    void startCombat(UUID entityId);

    /**
     * End combat for an entity.
     *
     * @param entityId The entity's UUID
     */
    void endCombat(UUID entityId);
}
