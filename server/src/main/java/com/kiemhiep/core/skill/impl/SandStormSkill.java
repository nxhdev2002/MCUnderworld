package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.platform.Location;
import com.kiemhiep.api.platform.PlayerAdapter;
import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.IAreaDamage;
import com.kiemhiep.core.skill.BaseSkill;
import com.kiemhiep.platform.FabricPlayerAdapter;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Sa_Mạc_Bão - Sand Storm skill that blinds and damages with swirling sand.
 * Area damage with wind/earth mix.
 * Unique behavior: Creates a blinding sand vortex that stuns and damages
 * enemies caught in the swirling vortex.
 */
public class SandStormSkill extends BaseSkill implements IAreaDamage {

    public static final SandStormSkill INSTANCE = new SandStormSkill();

    private static final int STUN_DURATION = 100; // 5 seconds
    private static final int BLINDNESS_DURATION = 100;
    private static final double AREA_DAMAGE_BASE = 6.0;
    private static final double AREA_RADIUS = 7.0;
    private static final int KNOCKBACK_FORCE = 4;
    private static final RandomSource RANDOM = RandomSource.create();

    private SandStormSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        Location location = ctx.getOrigin();
        PlayerAdapter player = ctx.getCaster();

        if (location != null && player != null) {
            // Sound effect: Howling sand storm with SoundSource
            if (player instanceof FabricPlayerAdapter fabricPlayer) {
                ServerLevel level = (ServerLevel) fabricPlayer.getServerPlayer().level();
                level.playSound(null, location.x(), location.y(), location.z(), SoundEvents.AMBIENT_CAVE, SoundSource.BLOCKS, 1.0f, 0.3f + ThreadLocalRandom.current().nextFloat() * 0.3f);
            }

            // Spawn sand vortex particles
            spawnSandVortex(location, ctx.getSkillLevel());
        }
    }

    /**
     * Spawn swirling sand vortex particles
     */
    private void spawnSandVortex(Location center, int skillLevel) {
        // Use context or effect runner to get ServerLevel
        // Particles will be spawned in applyAreaDamage which has proper ServerLevel access
    }

    @Override
    public void applyAreaDamage(SkillContext ctx) {
        Location center = ctx.getOrigin();
        PlayerAdapter playerAdapter = ctx.getCaster();

        if (center == null || playerAdapter == null) return;

        // Get ServerLevel from context
        if (!(playerAdapter instanceof FabricPlayerAdapter fabricCaster)) return;
        ServerLevel level = (ServerLevel) fabricCaster.getServerPlayer().level();

        // Configure sound via EffectRunner instead of direct call
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "earth");
        }

        // Define area
        AABB area = new AABB(
                center.x() - AREA_RADIUS, center.y() - 2.0, center.z() - AREA_RADIUS,
                center.x() + AREA_RADIUS, center.y() + 5.0, center.z() + AREA_RADIUS
        );

        List<LivingEntity> targets = ctx.getEntitiesWithinArea(area).stream()
            .filter(LivingEntity.class::isInstance)
            .map(LivingEntity.class::cast)
            .collect(java.util.stream.Collectors.toList());

        if (targets.isEmpty()) return;

        // Get caster entity for damage calculation
        Object casterObj = ctx.getCasterEntity();
        if (casterObj == null) return;
        LivingEntity caster = (LivingEntity) casterObj;

        // Calculate base damage
        int casterLevel = ctx.getSkillLevel();
        double baseDamage = AREA_DAMAGE_BASE + (casterLevel * 0.9);

        int hitCount = 0;
        for (LivingEntity target : targets) {
            if (target == caster || !target.isAlive()) continue;

            hitCount++;
            // Damage scales with number of hits (diminishing returns)
            double damage = baseDamage * Math.max(0.5, 1.0 - hitCount * 0.05);

            // Apply damage - useDamageSource factory pattern
            target.hurt(target.damageSources().indirectMagic((net.minecraft.world.entity.Entity) ctx.getCasterEntity(), (net.minecraft.world.entity.Entity) caster), (float) damage);

            // Knockback outward from center
            Vec3 knockback = new Vec3(
                    target.getX() - center.x(),
                    0.5 + ThreadLocalRandom.current().nextDouble() * 0.3,
                    target.getZ() - center.z()
            ).normalize().scale(KNOCKBACK_FORCE * 0.6);

            target.push(knockback.x(), knockback.y(), knockback.z());

            // Apply debuffs
            if (target instanceof Player player) {
                // Blindness effect - reduces visibility
                player.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.BLINDNESS, BLINDNESS_DURATION, 1, false, false));
                // Weakness from sand in eyes
                player.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.WEAKNESS, BLINDNESS_DURATION, 0, false, false));
            } else {
                // For mobs, apply debuffs
                target.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.SLOWNESS, BLINDNESS_DURATION, 1, false, false));
                target.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.WEAKNESS, BLINDNESS_DURATION, 0, false, false));
            }

            // Chance to apply stun (levitation for air control)
            if (ThreadLocalRandom.current().nextDouble() < 0.3) {
                target.addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.LEVITATION, STUN_DURATION, 0, false, false));
                // Sound on stun
                if (target.level() instanceof ServerLevel targetLevel) {
                    float pitch = 0.3f + ThreadLocalRandom.current().nextFloat() * 0.2f;
                    targetLevel.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.AMBIENT_CAVE, net.minecraft.sounds.SoundSource.PLAYERS, 0.3f, pitch);
                }
            }
        }

        // Spawn falling sand projectiles
        spawnFallingSand(level, center, 20 + casterLevel);
    }

    /**
     * Spawn sand projectiles that rain down
     */
    private void spawnFallingSand(ServerLevel level, Location center, int count) {
        RandomSource random = level.getRandom();

        for (int i = 0; i < count; i++) {
            double offsetX = (random.nextDouble() - 0.5) * 10.0;
            double offsetZ = (random.nextDouble() - 0.5) * 10.0;
            double startX = center.x() + offsetX;
            double startZ = center.z() + offsetZ;
            double startY = center.y() + 8.0 + random.nextDouble() * 5.0;

            // Falling sand particles trail - use CRIMSON_SPORE for 1.21.11 compatibility
            for (int j = 0; j < 10; j++) {
                double t = j / 10.0;
                double y = startY - t * 8.0;
                double x = startX + offsetX * t * 0.2;
                double z = startZ + offsetZ * t * 0.2;

                level.sendParticles(ParticleTypes.CRIMSON_SPORE,
                        x, y, z,
                        1,
                        0, 0, 0,
                        0.1f);
            }
        }
    }
}
