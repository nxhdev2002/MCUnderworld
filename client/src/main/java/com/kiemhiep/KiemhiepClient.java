package com.kiemhiep;

import com.kiemhiep.hud.LevelManaHud;
import com.kiemhiep.network.PlayerStatsReceiver;
import com.kiemhiep.network.SkillEffectReceiver;
import net.fabricmc.api.ClientModInitializer;

public class KiemhiepClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Client-specific logic (e.g. rendering). Item registration is in KiemhiepClientMain.
		SkillEffectReceiver.register();
		PlayerStatsReceiver.register();
		LevelManaHud.register();
	}
}