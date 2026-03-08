package com.kiemhiep;

import net.fabricmc.api.ModInitializer;

/**
 * Main entrypoint for client mod: registers skill items so client registry matches server.
 * Must run in "main" phase so registries are still mutable.
 */
public class KiemhiepClientMain implements ModInitializer {
	@Override
	public void onInitialize() {
		ClientSkillItemRegistration.registerAll();
	}
}
