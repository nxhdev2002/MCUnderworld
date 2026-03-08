package com.kiemhiep.core.skill.impl;

import com.kiemhiep.api.platform.PlayerAdapter;
import com.kiemhiep.api.skill.SkillContext;
import com.kiemhiep.api.skill.effect.IAreaDamage;
import com.kiemhiep.api.skill.effect.IMeteor;
import com.kiemhiep.core.skill.BaseSkill;
import com.kiemhiep.entity.MeteorEntity;
import com.kiemhiep.platform.FabricPlayerAdapter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;

/**
 * Fireball skill: triệu hồi thiên thạch (MeteorEntity) từ trên trời rơi xuống mục tiêu (origin), chạm đất thì nổ TNT.
 */
public class FireballSkill extends BaseSkill implements IMeteor, IAreaDamage {

    public static final FireballSkill INSTANCE = new FireballSkill();

    private static final double METEOR_FALL_HEIGHT = 50.0;
    private static final double METEOR_FALL_SPEED = 1.2;

    @Override
    protected void onExecute(SkillContext ctx) {
        // Optional: pre-checks or order of effects
    }

    @Override
    public void applyMeteor(SkillContext ctx) {
        PlayerAdapter caster = ctx.getCaster();
        if (!(caster instanceof FabricPlayerAdapter fabricCaster)) {
            return;
        }
        ServerLevel level = (ServerLevel) fabricCaster.getServerPlayer().level();
        double x = ctx.getOrigin().x();
        double y = ctx.getOrigin().y();
        double z = ctx.getOrigin().z();

        MeteorEntity meteor = MeteorEntity.TYPE.create(level, EntitySpawnReason.TRIGGERED);
        if (meteor != null) {
            meteor.setPos(x, y + METEOR_FALL_HEIGHT, z);
            meteor.setTargetY(y);
            meteor.setDeltaMovement(0, -METEOR_FALL_SPEED, 0);
            level.addFreshEntity(meteor);
        }
    }

    @Override
    public void applyAreaDamage(SkillContext ctx) {
        // Damage handled by MeteorEntity explosion (TNT); optional formula via CombatService later
    }
}
