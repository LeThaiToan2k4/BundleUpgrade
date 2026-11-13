package com.thaistoanf.bundleupgrade.client.mixin;

import com.thaistoanf.bundleupgrade.client.duck.UpgradedBundleTooltipHolder;
import com.thaistoanf.bundleupgrade.duck.UpgradedBundleDataHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.tooltip.TooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(TooltipComponent.class)
public interface TooltipComponentMixin {
    @Inject(method = "of(Lnet/minecraft/item/tooltip/TooltipData;)Lnet/minecraft/client/gui/tooltip/TooltipComponent;", at = @At("RETURN"))
    private static void bundleupgrade$transferCapacity(TooltipData tooltipData, CallbackInfoReturnable<TooltipComponent> cir) {
        if (tooltipData instanceof UpgradedBundleDataHolder dataHolder && cir.getReturnValue() instanceof UpgradedBundleTooltipHolder compHolder) {
            compHolder.bundleupgrade$setMaxCapacity(dataHolder.bundleupgrade$getMaxCapacity());
        }
    }
}
