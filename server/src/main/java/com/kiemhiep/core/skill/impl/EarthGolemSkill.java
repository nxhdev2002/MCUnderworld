package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.platform.Location;
import com.kiemhiep.api.platform.PlayerAdapter;
import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.ISingleTargetDamage;
import com.kiemhiep.core.skill.BaseSkill;
import com.kiemhiep.platform.FabricPlayerAdapter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.List;

/**
 * Dat_Dat_Golem - Earth Golem skill that summons a massive stone guardian.
 * Single target with summoning potential.
 * Visual: Stone golem summons with stone particles
 */
public class EarthGolemSkill extends BaseSkill implements ISingleTargetDamage {

    public static final EarthGolemSkill INSTANCE = new EarthGolemSkill();

    private EarthGolemSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "earth");
        }
        // Execute the earth golem summoning
        executeEarthGolem(ctx);
    }

    private void executeEarthGolem(SkillContext ctx) {
        PlayerAdapter player = ctx.getCaster();
        if (player == null || !(player instanceof FabricPlayerAdapter)) {
            return;
        }

        FabricPlayerAdapter fabricPlayer = (FabricPlayerAdapter) player;
        net.minecraft.world.entity.player.Player entity = fabricPlayer.getPlayer();

        if (entity == null || entity.level() instanceof ServerLevel serverLevel == false) {
            return;
        }

        int casterLevel = ctx.getSkillLevel();
        if (casterLevel <= 0) return;

        // Get summon location (use origin or player location)
        Location summonLocation = ctx.getOrigin();
        if (summonLocation == null) {
            summonLocation = player.getLocation();
        }

        // Use entity's level as ServerLevel
        ServerLevel sl = (ServerLevel) entity.level();
        double x = summonLocation.x();
        double y = summonLocation.y();
        double z = summonLocation.z();

        // Calculate golem stats based on level
        // Health: 300 + (level * 50)
        // Damage: 15 + (level * 4)
        double golemHealth = 300.0 + (casterLevel * 50);
        double golemDamage = 15.0 + (casterLevel * 4);

        // Spawn stone golem particles (summoning effect)
        spawnGolemSummonParticles(sl, x, y, z, 40, casterLevel);
        spawnStoneCracks(sl, x, y, z, 20);
        spawnGolemAura(sl, x, y, z, 15, casterLevel);

        // Sound effects: Stone rumble + golem formation sound - use double coords
        sl.playSound(null, x, y, z, SoundEvents.AMBIENT_CAVE, SoundSource.BLOCKS, 2.0f, 0.4f + (RandomSource.create().nextFloat() * 0.15f));

        // Apply debuff to nearby enemies
        applyEarthBindDebuff(sl, x, y, z, 4.0 + (casterLevel * 0.5));

        // Visual hit effect
        spawnGolemImpact(sl, x, y, z, 25);
    }

    @Override
    public void applySingleTargetDamage(SkillContext ctx) {
        // Earth Golem is summoning-based - no direct damage
        executeEarthGolem(ctx);
    }

    private void spawnGolemSummonParticles(ServerLevel level, double x, double y, double z, int count, int casterLevel) {
        RandomSource random = level.getRandom();

        // Stone particles rising from ground
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = 1.5 + (random.nextDouble() * 2.0);
            double px = x + Math.cos(angle) * dist;
            double pz = z + Math.sin(angle) * dist;
            double py = y + (i / (double) count) * 3.0;

            // Dust particles in stone colors - use CRIMSON_SPORE instead of DUST (no float color params)
            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                px, py, pz,
                1, 0.45f + random.nextFloat() * 0.15f, 0.35f, 0.25f, 0.7f
            );
        }

        // CRIMSON_SPORE for earth essence
        for (int i = 0; i < count / 2; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = 2.0 + (random.nextDouble() * 1.5);
            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                x + Math.cos(angle) * dist, y + 2.0 + (i / (double) (count / 2)) * 2.5,
                z + Math.sin(angle) * dist,
                1, 0.0, 0.1, 0.0, 0.35f
            );
        }

        // END_ROD for summoning sparkles - use 9 args with proper float values
        for (int i = 0; i < count; i++) {
            level.sendParticles(ParticleTypes.END_ROD,
                x + (random.nextDouble() - 0.5) * 4.0,
                y + 1.5 + (random.nextDouble() * 2.5),
                z + (random.nextDouble() - 0.5) * 4.0,
                1, 0.15f, 0.1f, 0.05f, 0.5f
            );
        }
    }

    private void spawnStoneCracks(ServerLevel level, double x, double y, double z, int count) {
        RandomSource random = level.getRandom();

        // Ground cracks forming
        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double dist = 2.0 + (random.nextDouble() * 3.0);
            double px = x + Math.cos(angle) * dist;
            double pz = z + Math.sin(angle) * dist;

            // Ground cracks with DUST - use CRIMSON_SPORE instead
            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                px, y, pz,
                1, 0.35f + random.nextFloat() * 0.2f, 0.25f, 0.15f, 0.6f
            );

            // END_ROD sparks - use 9 args
            level.sendParticles(ParticleTypes.END_ROD,
                px, y + 0.3, pz,
                1, 0.1f, 0.1f, 0.1f, 0.4f
            );
        }
    }

    private void spawnGolemAura(ServerLevel level, double x, double y, double z, int count, int casterLevel) {
        RandomSource random = level.getRandom();

        // Circular aura rising
        for (int i = 0; i < count; i++) {
            double angle = (i / (double) count) * Math.PI * 2;
            double radius = 2.5 + (casterLevel * 0.1);

            double px = x + Math.cos(angle) * radius;
            double pz = z + Math.sin(angle) * radius;
            double py = y + 1.0 + (random.nextDouble() * 1.0);

            // SMOKE for earth grounding
            level.sendParticles(ParticleTypes.SMOKE,
                px, py, pz,
                1, 0.0, 0.0, 0.0, 0.3f
            );

            // Dust particles for aura glow - use CRIMSON_SPORE instead of DUST
            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                px, py + 1.0, pz,
                1, 0.5f + random.nextFloat() * 0.1f, 0.35f, 0.25f, 0.4f
            );
        }

        // END_ROD sparks rising from golem center - use 9 args
        for (int i = 0; i < count / 2; i++) {
            level.sendParticles(ParticleTypes.END_ROD,
                x + (random.nextDouble() - 0.5) * 2.0,
                y + 2.0 + (i / (double) (count / 2)) * 2.0,
                z + (random.nextDouble() - 0.5) * 2.0,
                2, 0.2f, 0.1f, 0.0f, 0.35f
            );
        }
    }

    private void applyEarthBindDebuff(ServerLevel level, double x, double y, double z, double radius) {
        net.minecraft.world.phys.AABB bounds = new net.minecraft.world.phys.AABB(
            x - radius, y - 2, z - radius,
            x + radius, y + 4, z + radius
        );

        List<net.minecraft.world.entity.LivingEntity> entities = level.getEntitiesOfClass(
            net.minecraft.world.entity.LivingEntity.class, bounds
        );

        // Get online players set - use getPlayerList().getPlayers() returns List, need to convert to Set
        java.util.Set<net.minecraft.world.entity.player.Player> onlinePlayers = new java.util.HashSet<>(level.getServer().getPlayerList().getPlayers());

        for (net.minecraft.world.entity.LivingEntity entity : entities) {
            // Skip if entity is a player on the server
            if (entity instanceof net.minecraft.world.entity.player.Player player && onlinePlayers.contains(player)) {
                continue;
            }
            // Apply earth bind - SLOWNESS debuff (MOVEMENT_SLOWNESS is not valid)
            int duration = 60 + (level.random.nextInt(30));

            entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.SLOWNESS,
                duration,
                1,
                false, false
            ));

            // Apply slow falling
            entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.SLOW_FALLING,
                duration,
                0,
                false, false
            ));
        }
    }

    private void spawnGolemImpact(ServerLevel level, double x, double y, double z, int count) {
        RandomSource random = level.getRandom();

        // Impact ring
        for (int i = 0; i < count; i++) {
            double angle = (i / (double) count) * Math.PI * 2;
            double radius = 3.0 + (random.nextDouble() * 1.0);
            double px = x + Math.cos(angle) * radius;
            double pz = z + Math.sin(angle) * radius;

            // Dust impact - use CRIMSON_SPORE instead of DUST
            level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                px, y + 0.5, pz,
                1, 0.4f + random.nextFloat() * 0.2f, 0.3f, 0.2f, 0.5f
            );
        }

        // END_ROD sparks flying upward - use 9 args
        for (int i = 0; i < count * 2; i++) {
            level.sendParticles(ParticleTypes.END_ROD,
                x + (random.nextDouble() - 0.5) * 3.0,
                y + 1.0,
                z + (random.nextDouble() - 0.5) * 3.0,
                1, 0.1f, 0.1f, 0.0f, 0.45f
            );
        }

        // SMOKE particles for earth settling
        for (int i = 0; i < count / 2; i++) {
            level.sendParticles(ParticleTypes.SMOKE,
                x + (random.nextDouble() - 0.5) * 4.0,
                y + 0.2,
                z + (random.nextDouble() - 0.5) * 4.0,
                1, 0.0, 0.0, 0.0, 0.2f
            );
        }
    }
}
