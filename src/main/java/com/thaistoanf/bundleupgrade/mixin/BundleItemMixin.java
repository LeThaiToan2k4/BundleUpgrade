package com.thaistoanf.bundleupgrade.mixin;

import com.thaistoanf.bundleupgrade.item.UpgradedBundleItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleItem.class)
public class BundleItemMixin {
    @Inject(method = "getFullnessDisplay", at = @At("HEAD"), cancellable = true)
    private static void bundleupgrade$getFullnessDisplay(ItemStack stack, CallbackInfoReturnable<Float> cir) {
        if (stack.getItem() instanceof UpgradedBundleItem upgraded) {
            BundleContents contents = stack.get(DataComponents.BUNDLE_CONTENTS);
            if (contents == null || contents.isEmpty()) {
                cir.setReturnValue(0.0F);
            } else {
                Fraction maxFraction = upgraded.getMaxFraction();
                Fraction ratio = UpgradedBundleItem.getWeightSafe(contents, maxFraction).divideBy(maxFraction);
                cir.setReturnValue(ratio.floatValue());
            }
        }
    }
}
