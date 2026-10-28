package com.thaistoanf.bundleupgrade.mixin;

import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BundleItem.class)
public interface BundleItemInvoker {
    @Invoker("getBundleOccupancy")
    static int bundleupgrade$getBundleOccupancy(ItemStack stack) {
        throw new AssertionError();
    }
}

