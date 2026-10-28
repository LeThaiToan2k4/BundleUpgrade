package com.thaistoanf.bundleupgrade.client.mixin;

import com.thaistoanf.bundleupgrade.client.duck.UpgradedBundleDataHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.client.item.BundleTooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(BundleTooltipComponent.class)
public class BundleTooltipComponentMixin {
    @Unique
    private int bundleupgrade$maxCapacity = 64;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void bundleupgrade$readCapacity(BundleTooltipData data, CallbackInfo ci) {
        if (data instanceof UpgradedBundleDataHolder holder) {
            this.bundleupgrade$maxCapacity = holder.bundleupgrade$getMaxCapacity();
        }
    }

    @ModifyConstant(method = "drawItems", constant = @Constant(intValue = 64))
    private int bundleupgrade$scaledFullThreshold(int original) {
        return this.bundleupgrade$maxCapacity;
    }
}
