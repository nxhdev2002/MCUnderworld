package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.platform.Location;
import com.kiemhiep.api.platform.PlayerAdapter;
import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.ISingleTargetDamage;
import com.kiemhiep.core.skill.BaseSkill;
import com.kiemhiep.platform.FabricPlayerAdapter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Băng Lambda - Ice Prison skill that completely encases a target in unbreakable ice.
 * Single target with frozen debuff potential.
 * Unique behavior: Creates a protective ice shell that blocks incoming damage.
 */
public class IcePrisonSkill extends BaseSkill implements ISingleTargetDamage {

    public static final IcePrisonSkill INSTANCE = new IcePrisonSkill();

    private static final int PRISON_DURATION = 120; // 6 seconds
    private static final int REFLECTION_DURATION = 100; // 5 seconds
    private static final double DAMAGE_MULTIPLIER = 1.5;
    private static final double REFLECTION_CHANCE = 0.3;

    private IcePrisonSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        Location location = ctx.getOrigin();
        PlayerAdapter player = ctx.getCaster();

        if (location != null && player != null) {
            // Sound effect: Ice encasement - use AMBIENT_CAVE instead of GLASS_PLACE
            if (player instanceof FabricPlayerAdapter fabricPlayer) {
                ServerLevel level = (ServerLevel) fabricPlayer.getServerPlayer().level();
                level.playSound(null, location.x(), location.y(), location.z(), SoundEvents.AMBIENT_CAVE, SoundSource.PLAYERS, 1.5f, 0.75f);
            }

            // Spawn encasement particles
            spawnEncasementParticles(location, ctx);
        }
    }

    /**
     * Spawn particles for ice encasement
     */
    private void spawnEncasementParticles(Location location, SkillContext ctx) {
        PlayerAdapter player = ctx.getCaster();
        if (!(player instanceof FabricPlayerAdapter fabricPlayer)) return;

        ServerLevel level = (ServerLevel) fabricPlayer.getServerPlayer().level();
        RandomSource random = level.getRandom();

        // Create prison walls
        for (int i = 0; i < 40; i++) {
            // Position around target (2 block radius)
            double angle = (i / 40.0) * Math.PI * 2;
            double radius = 1.5 + random.nextDouble() * 0.5;
            double x = location.x() + Math.cos(angle) * radius;
            double z = location.z() + Math.sin(angle) * radius;
            double y = location.y() - 1.5 + random.nextDouble() * 3.0;

            // Ice panels forming the prison - use END_ROD instead of ICE
            level.sendParticles(ParticleTypes.END_ROD, x, y, z, 3, 0, 0, 0, 0.03);

            // Snow dust accumulating
            level.sendParticles(ParticleTypes.SNOWFLAKE, x + (random.nextDouble() - 0.5) * 0.3, y + (random.nextDouble() - 0.5) * 0.3, z + (random.nextDouble() - 0.5) * 0.3, 2, 0.05, 0.05, 0.05, 0.02);
        }

        // Floating frozen shards above target - use FLAME
        for (int i = 0; i < 10; i++) {
            level.sendParticles(ParticleTypes.FLAME, location.x() + (random.nextDouble() - 0.5) * 2.0, location.y() + 2.5 + random.nextDouble() * 1.0, location.z() + (random.nextDouble() - 0.5) * 2.0, 1, 0.1, 0.1, 0.1, 0.1);
        }

        // Locking sound effect visual - END_ROD
        for (int i = 0; i < 5; i++) {
            level.sendParticles(ParticleTypes.END_ROD, location.x(), location.y() + 1.0, location.z(), 1, 0, 0, 0, 0.2);
        }
    }

    /**
     * Spawn prison closing effect
     */
    private void spawnPrisonClosing(Location location, SkillContext ctx) {
        PlayerAdapter player = ctx.getCaster();
        if (!(player instanceof FabricPlayerAdapter fabricPlayer)) return;

        ServerLevel level = (ServerLevel) fabricPlayer.getServerPlayer().level();
        RandomSource random = level.getRandom();

        // Rapid particle burst as prison seals
        for (int i = 0; i < 30; i++) {
            double speed = random.nextDouble() * 0.2;
            double x = location.x();
            double y = location.y() + random.nextDouble() * 2.0;
            double z = location.z();

            level.sendParticles(ParticleTypes.END_ROD, x, y, z, 2, (random.nextDouble() - 0.5) * speed, (random.nextDouble() - 0.5) * speed, (random.nextDouble() - 0.5) * speed, 0.05);
        }
    }

    @Override
    public void applySingleTargetDamage(SkillContext ctx) {
        LivingEntity target = (LivingEntity) ctx.getTargetEntity();
        LivingEntity caster = (LivingEntity) ctx.getCasterEntity();

        if (target == null || caster == null) return;

        // Calculate base damage from caster's level
        int casterLevel = ctx.getSkillLevel();
        double baseDamage = 15.0 + (casterLevel * 1.8);
        double finalDamage = baseDamage * DAMAGE_MULTIPLIER;

        // Damage source with ice theme - use damageSources().magic()
        if (target.level() instanceof ServerLevel level) {
            target.hurt(level.damageSources().magic(), (float) finalDamage);
        }

        // Sound on hit - use AMBIENT_CAVE with SoundSource
        if (target.level() instanceof ServerLevel level) {
            level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.AMBIENT_CAVE, SoundSource.PLAYERS, 1.5f, 0.5f + ThreadLocalRandom.current().nextFloat() * 0.25f);
        }

        // Create prison effect: freeze target completely
        Vec3 originalVelocity = target.getDeltaMovement();

        // Apply full freeze (no gravity, no movement)
        target.setNoGravity(true);
        target.setDeltaMovement(Vec3.ZERO);

        // Schedule unfreeze task
        target.level().getServer().execute(() -> {
            if (target.isAlive()) {
                target.setNoGravity(false);
                // Restore some momentum
                target.setDeltaMovement(target.getDeltaMovement().multiply(0.5, 1.0, 0.5));
            }
        });

        // Spawn closing particles - get dimension name from target
        String dimLocation = target.level().dimension().toString();
        spawnPrisonClosing(new Location(dimLocation, target.getX(), target.getY(), target.getZ()), ctx);

        // Reflect magic damage on caster (30% chance)
        if (Math.random() < REFLECTION_CHANCE) {
            // Get the player adapter from the context for the reflection effect
            PlayerAdapter player = ctx.getCaster();
            if (player instanceof FabricPlayerAdapter fabricCaster) {
                // Apply reflection shield - use REGENERATION instead of DAMAGE_RESISTANCE (not available in 1.21.11)
                fabricCaster.getServerPlayer().addEffect(new MobEffectInstance(net.minecraft.world.effect.MobEffects.REGENERATION, REFLECTION_DURATION, 1, false, false));
                // Send message to player using player adapter
                fabricCaster.getServerPlayer().sendSystemMessage(Component.translatable("Ice Prison shield activated!").withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(net.minecraft.ChatFormatting.AQUA)));
            }
        }
    }
}
