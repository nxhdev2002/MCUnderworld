package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.platform.PlayerAdapter;
import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.IThunder;
import com.kiemhiep.core.skill.BaseSkill;
import com.kiemhiep.platform.FabricPlayerAdapter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;

/**
 * Thunder skill: tia sấm sét đánh xuống vùng đất tại origin, phát nổ giống TNT.
 * Spawn LightningBolt + explosion tại ctx.getOrigin().
 */
public class ThunderSkill extends BaseSkill implements IThunder {

    public static final ThunderSkill INSTANCE = new ThunderSkill();

    private static final float EXPLOSION_RADIUS = 4.0f;

    private ThunderSkill() {}

    @Override
    protected void onExecute(SkillContext ctx) {
        // Optional: pre-checks
    }

    @Override
    public void applyThunder(SkillContext ctx) {
        if (ctx.getEffectRunner() != null) {
            ctx.getEffectRunner().playEffect(ctx, "thunder");
        }

        PlayerAdapter caster = ctx.getCaster();
        if (!(caster instanceof FabricPlayerAdapter fabricCaster)) {
            return;
        }
        ServerLevel level = (ServerLevel) fabricCaster.getServerPlayer().level();
        double x = ctx.getOrigin().x();
        double y = ctx.getOrigin().y();
        double z = ctx.getOrigin().z();

        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
        if (lightning != null) {
            lightning.setPos(x, y, z);
            level.addFreshEntity(lightning);
        }

        level.explode(
            null,
            x, y, z,
            EXPLOSION_RADIUS,
            Level.ExplosionInteraction.TNT
        );
    }
}
