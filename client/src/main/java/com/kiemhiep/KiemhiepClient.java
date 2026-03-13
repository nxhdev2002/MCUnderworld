package com.kiemhiep;

import com.kiemhiep.entity.EntityRegistration;
import com.kiemhiep.entity.MeteorEntity;
import com.kiemhiep.entity.MeteorRenderer;
import com.kiemhiep.effect.SkySplitEffect;
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
import com.kiemhiep.hud.LevelManaHud;
import com.kiemhiep.hud.SkillItemCooldownOverlay;
import com.kiemhiep.hud.SkySplitOverlay;
import com.kiemhiep.hud.SkillEffectOverlay;
import com.kiemhiep.hud.ElementalHudOverlay;
import com.kiemhiep.hud.ElementalShaderOverlay;
import com.kiemhiep.network.PlayerStatsReceiver;
import com.kiemhiep.network.SkillEffectReceiver;
import com.kiemhiep.network.SkillCooldownReceiver;
import com.kiemhiep.network.SkillDefinitionsReceiver;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class KiemhiepClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Đăng ký entity types (shared) trước khi dùng (renderer, v.v.)
		EntityRegistration.register();
		// Client-specific logic (e.g. rendering). Item registration is in KiemhiepClientMain.
		EntityRendererRegistry.register(MeteorEntity.TYPE, MeteorRenderer::new);
		SkillEffectReceiver.register();
		PlayerStatsReceiver.register();
		SkillCooldownReceiver.register();
		SkillDefinitionsReceiver.register();
		LevelManaHud.register();
		SkillItemCooldownOverlay.register();
		SkySplitOverlay.register();
		SkillEffectOverlay.register();
		ElementalHudOverlay.register();
		ElementalShaderOverlay.register();
		SkySplitEffect.init();
		// Initialize custom effects
		TimeBombEffect.init();
		SentryLightEffect.init();
		StarlightHealEffect.init();
		WardBlueEffect.init();
		WardRedEffect.init();
		SpiritOwlEffect.init();
		BeastWolfEffect.init();
		PhoenixFlameEffect.init();
		CrabSummonEffect.init();
		BearSummonEffect.init();
		VoidSpawnEffect.init();
		TimeBreakerEffect.init();
		DarkRiftEffect.init();
		QuantumRayEffect.init();
		ShieldSummonEffect.init();
		FlameChompersEffect.init();
		RainArrowsEffect.init();
		LightSpikeEffect.init();
		FrozenCageEffect.init();
		ElectricSnakeEffect.init();
	}
}
