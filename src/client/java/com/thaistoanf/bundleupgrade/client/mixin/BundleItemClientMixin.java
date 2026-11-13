package com.thaistoanf.bundleupgrade.client.mixin;

import com.thaistoanf.bundleupgrade.client.duck.UpgradedBundleDataHolder;
import com.thaistoanf.bundleupgrade.item.UpgradedBundleItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Environment(EnvType.CLIENT)
@Mixin(BundleItem.class)
public class BundleItemClientMixin {
    @Inject(method = "getTooltipData", at = @At("RETURN"))
    private void bundleupgrade$transferCapacity(ItemStack stack, CallbackInfoReturnable<Optional<TooltipData>> cir) {
        if (stack.getItem() instanceof UpgradedBundleItem upgraded) {
            cir.getReturnValue().ifPresent(data -> {
                if (data instanceof UpgradedBundleDataHolder holder) {
                    holder.bundleupgrade$setMaxCapacity(upgraded.getMaxCapacity());
                }
            });
        }
    }
}
