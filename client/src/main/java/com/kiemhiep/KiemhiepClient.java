package com.kiemhiep;

import com.kiemhiep.entity.EntityRegistration;
import com.kiemhiep.entity.MeteorEntity;
import com.kiemhiep.entity.MeteorRenderer;
import com.kiemhiep.effect.SkySplitEffect;
import com.kiemhiep.hud.LevelManaHud;
import com.kiemhiep.hud.SkillItemCooldownOverlay;
import com.kiemhiep.hud.SkySplitOverlay;
import com.kiemhiep.hud.ElementalHudOverlay;
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
		ElementalHudOverlay.register();
		SkySplitEffect.init();
	}
}