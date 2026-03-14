package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.platform.Location;
import com.kiemhiep.api.platform.PlayerAdapter;
import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.IThunder;
import com.kiemhiep.core.skill.BaseSkill;
import com.kiemhiep.platform.FabricPlayerAdapter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Kim_Cang_Lôi - Vajra Lightning skill that delivers divine punishment through lightning.
 * Massive area damage with biblical lightning effect.
 * Unique behavior: Divine golden lightning with protective aura particles.
 */
public class VajraLightningSkill extends BaseSkill implements IThunder {

    public static final VajraLightningSkill INSTANCE = new VajraLightningSkill();

    private static final int STUN_DURATION = 50; // 2.5 seconds
    private static final int WEAKNESS_DURATION = 150; // 7.5 seconds
    private static final int DIVINE_AURA_DURATION = 200; // 10 seconds
    private static final double LIGHTNING_RADIUS = 8.0;
    private static final double LIGHTNING_HEIGHT = 30.0;
    private static final double DAMAGE_MULTIPLIER = 1.8;

    private VajraLightningSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        Location origin = ctx.getOrigin();
        PlayerAdapter player = ctx.getCaster();

        if (origin != null && player != null) {
            // Sound effect: Deep divine thunder - get ServerLevel from player adapter
            if (player instanceof FabricPlayerAdapter fabricPlayer) {
                ServerLevel level = (ServerLevel) fabricPlayer.getServerPlayer().level();
                level.playSound(null, origin.x(), origin.y(), origin.z(),
                        SoundEvents.AMBIENT_CAVE, SoundSource.PLAYERS, 2.5f, 0.3f);
            }

            // Spawn summoning particles before strike
            LivingEntity caster = ctx.getCasterEntity() instanceof LivingEntity le ? le : null;
            spawnPreStrikeParticles(origin, caster);
        }
    }

    /**
     * Spawn particles before the divine lightning strike.
     */
    private void spawnPreStrikeParticles(Location origin, LivingEntity caster) {
        if (caster == null || !(caster.level() instanceof ServerLevel level)) return;
        RandomSource random = level.getRandom();

        // Golden particles forming a divine circle
        for (int i = 0; i < 30; i++) {
            double angle = (i / 30.0) * Math.PI * 2;
            double radius = 3.0 + random.nextDouble() * 2.0;

            // Gold divine particles
            level.sendParticles(ParticleTypes.SMOKE,
                    origin.x() + Math.cos(angle) * radius,
                    origin.y() + 1.5 + random.nextDouble() * 1.0,
                    origin.z() + Math.sin(angle) * radius,
                    4,
                    0, 0, 0,
                    0.2);

            // white spark particles
            level.sendParticles(ParticleTypes.END_ROD,
                    origin.x() + Math.cos(angle) * radius,
                    origin.y() + 2.0 + random.nextDouble(),
                    origin.z() + Math.sin(angle) * radius,
                    2,
                    0, 0, 0,
                    0.3);
        }

        // Electric spark particles in the center - use END_ROD instead of SCUCK_CHARGE
        for (int i = 0; i < 15; i++) {
            level.sendParticles(ParticleTypes.END_ROD,
                    origin.x() + (random.nextDouble() - 0.5) * 2.0,
                    origin.y() + 2.0 + random.nextDouble() * 2.0,
                    origin.z() + (random.nextDouble() - 0.5) * 2.0,
                    3,
                    0, 0, 0,
                    0.25);

            // Flash effect - use END_ROD
            level.sendParticles(ParticleTypes.END_ROD,
                    origin.x() + (random.nextDouble() - 0.5) * 1.0,
                    origin.y() + 3.0 + random.nextDouble(),
                    origin.z() + (random.nextDouble() - 0.5) * 1.0,
                    2,
                    0, 0, 0,
                    0.15);
        }
    }

    /**
     * Spawn golden divine lightning bolt from sky to ground.
     */
    private void spawnDivineLightningBolt(ServerLevel level, Location origin, RandomSource random) {
        int bolts = 5 + (int) (LIGHTNING_RADIUS / 2.0);

        for (int i = 0; i < bolts; i++) {
            // Random position within radius
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = random.nextDouble() * LIGHTNING_RADIUS;
            double x = origin.x() + Math.cos(angle) * distance;
            double z = origin.z() + Math.sin(angle) * distance;

            // Spawn lightning particles falling from sky
            int segments = 15;
            for (int j = 0; j < segments; j++) {
                double progress = (double) j / segments;
                double y = origin.y() + LIGHTNING_HEIGHT * (1.0 - progress);
                double jitter = (random.nextDouble() - 0.5) * (2.0 + progress * 3.0);

                // Golden electric particles
                level.sendParticles(ParticleTypes.SMOKE,
                        x + jitter, y, z + jitter,
                        3,
                        0, -0.3, 0,
                        0.25);

                // White end rod particles
                level.sendParticles(ParticleTypes.END_ROD,
                        x + jitter, y, z + jitter,
                        2,
                        0, 0, 0,
                        0.3);

                // indicator particles
                if (j % 3 == 0) {
                    level.sendParticles(ParticleTypes.END_ROD,
                            x + jitter, y, z + jitter,
                            1,
                            0, 0, 0,
                            0.1);
                }
            }
        }
    }

    /**
     * Spawn divine protective aura particles around the strike area.
     */
    private void spawnDivineAura(ServerLevel level, Location origin, RandomSource random) {
        int rings = 8;
        for (int ring = 0; ring < rings; ring++) {
            double radius = 2.0 + ring * 0.8;
            double y = origin.y() + 1.5 + Math.sin(ring * 0.4) * 1.0;

            for (int i = 0; i < 20; i++) {
                double angle = (i / 20.0) * Math.PI * 2 + (ring * 0.3);
                double x = origin.x() + Math.cos(angle) * radius;
                double z = origin.z() + Math.sin(angle) * radius;

                // Gold divine particles for aura
                level.sendParticles(ParticleTypes.SMOKE,
                        x, y, z,
                        3,
                        0, 0, 0,
                        0.2);

                // White spark particles
                level.sendParticles(ParticleTypes.END_ROD,
                        x, y, z,
                        2,
                        0, 0, 0,
                        0.3);
            }
        }

        // Rising divine particles
        for (int i = 0; i < 25; i++) {
            double x = origin.x() + (random.nextDouble() - 0.5) * 5.0;
            double z = origin.z() + (random.nextDouble() - 0.5) * 5.0;
            double startY = origin.y();

            level.sendParticles(ParticleTypes.SMOKE,
                    x, startY, z,
                    2,
                    0, 0.2, 0,
                    0.15);
        }
    }

    /**
     * Spawn target hit particles with divine glow.
     */
    private void spawnTargetDivineParticles(LivingEntity target, RandomSource random) {
        if (!(target.level() instanceof ServerLevel level)) return;

        // Golden electric particles from target
        for (int i = 0; i < 20; i++) {
            double x = target.getX() + (random.nextDouble() - 0.5) * 1.5;
            double y = target.getY() + target.getBbHeight() / 2.0 + random.nextDouble() * target.getBbHeight();
            double z = target.getZ() + (random.nextDouble() - 0.5) * 1.5;

            level.sendParticles(ParticleTypes.SMOKE,
                    x, y, z,
                    4,
                    0, 0, 0,
                    0.25);

            // White end rod particles
            level.sendParticles(ParticleTypes.END_ROD,
                    x, y, z,
                    2,
                    0, 0, 0,
                    0.3);
        }

        // Divine flash
        level.sendParticles(ParticleTypes.END_ROD,
                target.getX(), target.getY() + target.getBbHeight(), target.getZ(),
                3,
                0, 0, 0,
                0.25);
    }

    @Override
    public void applyThunder(SkillContext ctx) {
        PlayerAdapter casterAdapter = ctx.getCaster();
        if (!(casterAdapter instanceof FabricPlayerAdapter fabricCaster)) {
            return;
        }
        ServerLevel level = (ServerLevel) fabricCaster.getServerPlayer().level();
        Location origin = ctx.getOrigin();
        RandomSource random = level.getRandom();

        if (origin == null) return;

        Object casterObj = ctx.getCasterEntity();
        if (!(casterObj instanceof LivingEntity caster)) return;

        int casterLevel = ctx.getSkillLevel();

        // Calculate area based on skill level
        double finalRadius = LIGHTNING_RADIUS + (casterLevel * 0.5);
        double finalHeight = LIGHTNING_HEIGHT + (casterLevel * 3.0);

        // Spawn divine lightning bolts from sky
        spawnDivineLightningBolt(level, origin, random);

        // Spawn divine aura effect
        spawnDivineAura(level, origin, random);

        // Area damage using getEntitiesWithinArea
        AABB area = new AABB(
                origin.x() - finalRadius, origin.y() - 2, origin.z() - finalRadius,
                origin.x() + finalRadius, origin.y() + finalHeight, origin.z() + finalRadius
        );

        List<LivingEntity> entities = ctx.getEntitiesWithinArea(area).stream()
                .filter(LivingEntity.class::isInstance)
                .map(LivingEntity.class::cast)
                .collect(java.util.stream.Collectors.toList());

        for (LivingEntity target : entities) {
            if (target == null || target == caster) continue;

            // Calculate distance from origin
            double distance = target.distanceToSqr(origin.x(), origin.y(), origin.z());
            if (distance > finalRadius * finalRadius) continue;

            // Calculate damage based on distance (closer = more damage)
            double distanceFactor = 1.0 - (distance / (finalRadius * finalRadius));
            double baseDamage = 18.0 + (casterLevel * 4.0);
            double divineDamage = baseDamage * distanceFactor * DAMAGE_MULTIPLIER;

            // Apply damage - use direct damage source
            target.hurt(target.damageSources().indirectMagic(caster, caster), (float) divineDamage);

            // Sound on hit - thunder crack with divine resonance
            if (target.level() instanceof ServerLevel targetLevel) {
                targetLevel.playSound(null, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.AMBIENT_CAVE, SoundSource.PLAYERS, 1.8f, 0.5f + ThreadLocalRandom.current().nextFloat() * 0.5f);
            }

            // Apply brief stun
            target.setNoGravity(true);
            target.setDeltaMovement(target.getDeltaMovement().multiply(0.2, 1.0, 0.2));

            // Schedule recovery using runTaskLater pattern
            target.level().getServer().execute(() -> {
                if (target.isAlive()) {
                    target.setNoGravity(false);
                    // Apply weakness and slowness with MobEffectInstance wrapper
                    if (target instanceof Player player) {
                        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, WEAKNESS_DURATION, 1, false, false));
                        player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, WEAKNESS_DURATION, 1, false, false));
                    }
                }
            });

            // Spawn divine particles on target
            spawnTargetDivineParticles(target, target.level().getRandom());
        }

        // Divine protection effect for caster
        Player player = fabricCaster.getPlayer();
        if (player != null && player.isAlive()) {
            player.level().getServer().execute(() -> {
                if (player.isAlive()) {
                    // Divine protection buff with MobEffectInstance wrapper - use available effects
                    player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, DIVINE_AURA_DURATION, 2, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, DIVINE_AURA_DURATION, 1, false, false));
                }
            });
        }

        // Final divine thunder
        level.playSound(null, origin.x(), origin.y(), origin.z(),
                SoundEvents.AMBIENT_CAVE, SoundSource.PLAYERS, 2.0f, 0.2f);
    }
}
