package com.kiemhiep.entity;

import com.kiemhiep.KiemhiepConstants;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Entity thiên thạch: rơi từ trời xuống, chạm đất thì nổ TNT.
 * Hitbox nhỏ (2x2); renderer vẽ hình cầu ~20 block.
 */
public class MeteorEntity extends Entity {

    private static final EntityDataAccessor<Integer> DATA_TARGET_Y = SynchedEntityData.defineId(MeteorEntity.class, EntityDataSerializers.INT);
    private static final float EXPLOSION_RADIUS = 4.0f;

    @SuppressWarnings("unchecked")
    private static final ResourceKey<EntityType<?>> REGISTRY_KEY = ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(KiemhiepConstants.MOD_ID, "meteor"));
    public static final EntityType<MeteorEntity> TYPE = (EntityType<MeteorEntity>) EntityType.Builder.<MeteorEntity>of(MeteorEntity::new, MobCategory.MISC)
        .sized(2f, 2f)
        .clientTrackingRange(128)
        .build(REGISTRY_KEY);

    public MeteorEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
        setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_TARGET_Y, 0);
    }

    public void setTargetY(double targetY) {
        getEntityData().set(DATA_TARGET_Y, (int) Math.round(targetY));
    }

    private double getTargetY() {
        return getEntityData().get(DATA_TARGET_Y);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putInt("TargetY", getEntityData().get(DATA_TARGET_Y));
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        getEntityData().set(DATA_TARGET_Y, input.getIntOr("TargetY", 0));
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            spawnTrailParticles();
            move(MoverType.SELF, getDeltaMovement());
            return;
        }
        move(MoverType.SELF, getDeltaMovement());
        double targetY = getTargetY();
        if (onGround() || getY() <= targetY) {
            level().explode(
                null,
                getX(), getY(), getZ(),
                EXPLOSION_RADIUS,
                Level.ExplosionInteraction.TNT
            );
            discard();
        }
    }

    private void spawnTrailParticles() {
        Level level = level();
        if (level == null) return;
        for (int i = 0; i < 3; i++) {
            double ox = (level.random.nextDouble() - 0.5) * 2;
            double oz = (level.random.nextDouble() - 0.5) * 2;
            level.addParticle(ParticleTypes.LARGE_SMOKE, getX() + ox, getY(), getZ() + oz, 0, -0.3, 0);
            level.addParticle(ParticleTypes.FLAME, getX() + ox * 0.5, getY(), getZ() + oz * 0.5, 0, -0.2, 0);
        }
    }

}
