package com.kiemhiep;

import com.kiemhiep.hud.SkillItemTooltip;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * Initializes tooltip registration on the client.
 * Uses Fabric's ItemTooltipCallback API to inject skill tooltips.
 */
public final class ClientTooltipInitializer implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register skill tooltip via Fabric callback
        ItemTooltipCallback.EVENT.register((stack, context, tooltipContext, lines) -> {
            SkillItemTooltip.addTooltip(stack, tooltipContext, lines);
        });
    }
}
