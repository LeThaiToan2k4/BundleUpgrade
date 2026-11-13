package com.thaistoanf.bundleupgrade.item;

import com.thaistoanf.bundleupgrade.mixin.BundleItemInvoker;
import com.thaistoanf.bundleupgrade.util.BundleCapacityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;
import net.minecraft.util.math.MathHelper;

public class UpgradedBundleItem extends BundleItem {
    private static final int FULL_ITEM_BAR_COLOR = MathHelper.packRgb(1.0F, 0.33F, 0.33F);
    private static final int ITEM_BAR_COLOR = MathHelper.packRgb(0.44F, 0.53F, 1.0F);

    private final int maxCapacity;

    public UpgradedBundleItem(int maxCapacity, Item.Settings settings) {
        super(settings);
        this.maxCapacity = maxCapacity;
    }

    public int getMaxCapacity() {
        return this.maxCapacity;
    }

    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        BundleCapacityContext.push(stack);
        try {
            return super.onStackClicked(stack, slot, clickType, player);
        } finally {
            BundleCapacityContext.pop();
        }
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        BundleCapacityContext.push(stack);
        try {
            return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
        } finally {
            BundleCapacityContext.pop();
        }
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return BundleItemInvoker.bundleupgrade$getBundleOccupancy(stack) > 0;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        int occupancy = BundleItemInvoker.bundleupgrade$getBundleOccupancy(stack);
        return Math.min(1 + 12 * occupancy / this.maxCapacity, 13);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        int occupancy = BundleItemInvoker.bundleupgrade$getBundleOccupancy(stack);
        return occupancy >= this.maxCapacity ? FULL_ITEM_BAR_COLOR : ITEM_BAR_COLOR;
    }
}
