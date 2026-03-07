package com.kiemhiep.fabric.components;

import com.kiemhiep.fabric.KiemHiepMod;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

/**
 * Component registry for Cardinal Components API.
 */
public class ModComponents implements EntityComponentInitializer {

    // Cultivation Component
    public static final ComponentKey<CultivationComponent> CULTIVATION =
            ComponentRegistryV3.INSTANCE.getOrCreate(
                    Identifier.of(KiemHiepMod.MOD_ID, "cultivation"),
                    CultivationComponent.class
            );

    // Wallet Component
    public static final ComponentKey<WalletComponent> WALLET =
            ComponentRegistryV3.INSTANCE.getOrCreate(
                    Identifier.of(KiemHiepMod.MOD_ID, "wallet"),
                    WalletComponent.class
            );

    // Skill Component
    public static final ComponentKey<SkillComponent> SKILL =
            ComponentRegistryV3.INSTANCE.getOrCreate(
                    Identifier.of(KiemHiepMod.MOD_ID, "skill"),
                    SkillComponent.class
            );

    @Override
    public void registerEntityComponentFactories() {
        // Register Cultivation Component for all players
        CULTIVATION.registerForPlayers(LivingEntity::new);

        // Register Wallet Component for all players
        WALLET.registerForPlayers(LivingEntity::new);

        // Register Skill Component for all players
        SKILL.registerForPlayers(LivingEntity::new);
    }

    /**
     * Get the cultivation component for a player.
     */
    public static CultivationComponent getCultivation(LivingEntity entity) {
        return CULTIVATION.get(entity);
    }

    /**
     * Get the wallet component for a player.
     */
    public static WalletComponent getWallet(LivingEntity entity) {
        return WALLET.get(entity);
    }

    /**
     * Get the skill component for a player.
     */
    public static SkillComponent getSkill(LivingEntity entity) {
        return SKILL.get(entity);
    }
}
