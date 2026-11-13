package com.thaistoanf.bundleupgrade.mixin;

import com.thaistoanf.bundleupgrade.item.UpgradedBundleItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleItem.class)
public class BundleItemMixin {
    @Inject(method = "getAmountFilled", at = @At("HEAD"), cancellable = true)
    private static void bundleupgrade$getAmountFilled(ItemStack stack, CallbackInfoReturnable<Float> cir) {
        if (stack.getItem() instanceof UpgradedBundleItem upgraded) {
            BundleContentsComponent contents = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
            if (contents == null || contents.isEmpty()) {
                cir.setReturnValue(0.0f);
            } else {
                Fraction maxFraction = Fraction.getFraction(upgraded.getMaxCapacity(), 64);
                Fraction ratio = contents.getOccupancy().divideBy(maxFraction);
                cir.setReturnValue(ratio.floatValue());
            }
        }
    }
}
