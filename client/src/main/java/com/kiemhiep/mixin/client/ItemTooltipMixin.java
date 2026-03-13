package com.kiemhiep.mixin.client;

import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * ItemTooltipMixin - safe no-op mixin that prevents errors when target methods don't exist.
 * The actual tooltip injection is done via Fabric's ItemComponentTooltipCallback in KiemhiepClientMain.
 * This mixin exists to prevent runtime crashes from missing injection targets.
 */
@Mixin(Item.class)
public class ItemTooltipMixin {

    /**
     * Safe no-op constant modifier that always returns the original value.
     * This prevents mixin errors when the actual target method doesn't exist.
     */
    @ModifyConstant(
        method = "*",
        constant = @Constant(intValue = 0)
    )
    private int safeNoOp(int value) {
        return value;
    }
}
