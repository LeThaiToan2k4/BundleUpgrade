package com.thaistoanf.bundleupgrade.item;

import com.thaistoanf.bundleupgrade.mixin.BundleItemInvoker;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
