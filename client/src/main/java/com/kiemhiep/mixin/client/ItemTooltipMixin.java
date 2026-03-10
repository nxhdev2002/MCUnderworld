package com.kiemhiep.mixin.client;

import com.kiemhiep.hud.SkillItemTooltip;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Mixin to inject skill tooltip into Item.getTooltipLines.
 */
@Mixin(Item.class)
public class ItemTooltipMixin {

    @Inject(at = @At("RETURN"), method = "appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V")
    private void appendHoverText(ItemStack stack, Level level, List<Component> tooltipComponents, TooltipFlag tooltipFlag, CallbackInfo ci) {
        SkillItemTooltip.addTooltip(stack, tooltipFlag, tooltipComponents);
    }
}