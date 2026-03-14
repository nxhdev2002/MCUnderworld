package com.kiemhiep.hud;

import com.kiemhiep.effect.TimeBombEffect;
import com.kiemhiep.effect.SentryLightEffect;
import com.kiemhiep.effect.StarlightHealEffect;
import com.kiemhiep.effect.WardBlueEffect;
import com.kiemhiep.effect.WardRedEffect;
import com.kiemhiep.effect.SpiritOwlEffect;
import com.kiemhiep.effect.BeastWolfEffect;
import com.kiemhiep.effect.PhoenixFlameEffect;
import com.kiemhiep.effect.CrabSummonEffect;
import com.kiemhiep.effect.BearSummonEffect;
import com.kiemhiep.effect.VoidSpawnEffect;
import com.kiemhiep.effect.TimeBreakerEffect;
import com.kiemhiep.effect.DarkRiftEffect;
import com.kiemhiep.effect.QuantumRayEffect;
import com.kiemhiep.effect.ShieldSummonEffect;
import com.kiemhiep.effect.FlameChompersEffect;
import com.kiemhiep.effect.RainArrowsEffect;
import com.kiemhiep.effect.LightSpikeEffect;
import com.kiemhiep.effect.FrozenCageEffect;
import com.kiemhiep.effect.ElectricSnakeEffect;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

/**
 * HUD overlay that calls render() on each active skill effect each frame.
 * Ensures effect state (progress, position) is updated for visuals; individual effects
 * may draw via GuiGraphics later or use ShaderManager when shaders are loaded.
 * SkySplit has its own SkySplitOverlay and is not included here.
 */
public final class SkillEffectOverlay {

    private SkillEffectOverlay() {}

    public static void register() {
        HudRenderCallback.EVENT.register(SkillEffectOverlay::render);
    }

    private static void render(GuiGraphics graphics, DeltaTracker tickCounter) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        if (TimeBombEffect.isActive()) TimeBombEffect.render();
        if (SentryLightEffect.isActive()) SentryLightEffect.render();
        if (StarlightHealEffect.isActive()) StarlightHealEffect.render();
        if (WardBlueEffect.isActive()) WardBlueEffect.render();
        if (WardRedEffect.isActive()) WardRedEffect.render();
        if (SpiritOwlEffect.isActive()) SpiritOwlEffect.render();
        if (BeastWolfEffect.isActive()) BeastWolfEffect.render();
        if (PhoenixFlameEffect.isActive()) PhoenixFlameEffect.render();
        if (CrabSummonEffect.isActive()) CrabSummonEffect.render();
        if (BearSummonEffect.isActive()) BearSummonEffect.render();
        if (VoidSpawnEffect.isActive()) VoidSpawnEffect.render();
        if (TimeBreakerEffect.isActive()) TimeBreakerEffect.render();
        if (DarkRiftEffect.isActive()) DarkRiftEffect.render();
        if (QuantumRayEffect.isActive()) QuantumRayEffect.render();
        if (ShieldSummonEffect.isActive()) ShieldSummonEffect.render();
        if (FlameChompersEffect.isActive()) FlameChompersEffect.render();
        if (RainArrowsEffect.isActive()) RainArrowsEffect.render();
        if (LightSpikeEffect.isActive()) LightSpikeEffect.render();
        if (FrozenCageEffect.isActive()) FrozenCageEffect.render();
        if (ElectricSnakeEffect.isActive()) ElectricSnakeEffect.render();
    }
}
