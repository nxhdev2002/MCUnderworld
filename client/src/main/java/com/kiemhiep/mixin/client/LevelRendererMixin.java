package com.kiemhiep.mixin.client;

import com.kiemhiep.effect.SkySplitEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.renderer.LevelRenderer;

/**
 * Mixin inject sky split effect vào LevelRenderer.renderSky.
 */
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(at = @At("TAIL"), method = "renderSky")
    private void onRenderSky(CallbackInfo ci) {
        // Sky split effect được render qua HudRenderCallback trong SkySplitOverlay
        // Method này giữ lại để future enhancement nếu cần
    }
}
