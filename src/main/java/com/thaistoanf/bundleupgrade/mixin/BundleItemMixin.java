package com.thaistoanf.bundleupgrade.mixin;

import com.thaistoanf.bundleupgrade.item.UpgradedBundleItem;
import com.thaistoanf.bundleupgrade.util.BundleCapacityContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleItem.class)
public class BundleItemMixin {
    @Inject(method = "onStackClicked", at = @At("HEAD"))
    private void bundleupgrade$onStackClickedHead(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        BundleCapacityContext.push(stack);
    }

    @Inject(method = "onStackClicked", at = @At("RETURN"))
    private void bundleupgrade$onStackClickedReturn(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        BundleCapacityContext.pop();
    }

    @Inject(method = "onClicked", at = @At("HEAD"))
    private void bundleupgrade$onClickedHead(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir) {
        BundleCapacityContext.push(stack);
    }

    @Inject(method = "onClicked", at = @At("RETURN"))
    private void bundleupgrade$onClickedReturn(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir) {
        BundleCapacityContext.pop();
    }

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
