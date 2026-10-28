package com.thaistoanf.bundleupgrade.client.mixin;

import com.thaistoanf.bundleupgrade.client.duck.UpgradedBundleDataHolder;
import com.thaistoanf.bundleupgrade.item.UpgradedBundleItem;
import com.thaistoanf.bundleupgrade.util.BundleCapacityContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
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

    @Inject(method = "appendTooltip", at = @At("HEAD"))
    private void bundleupgrade$appendTooltipHead(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        BundleCapacityContext.push(stack);
    }

    @Inject(method = "appendTooltip", at = @At("RETURN"))
    private void bundleupgrade$appendTooltipReturn(ItemStack stack, World world, List<Text> tooltip, TooltipContext context, CallbackInfo ci) {
        BundleCapacityContext.pop();
    }

    @ModifyConstant(method = "appendTooltip", constant = @Constant(intValue = 64))
    private int bundleupgrade$tooltipCapacity(int original) {
        return BundleCapacityContext.getCurrentMaxCapacity();
    }
}

