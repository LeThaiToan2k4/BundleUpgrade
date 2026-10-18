package com.thaistoanf.bundleupgrade.item;

import com.thaistoanf.bundleupgrade.duck.UpgradedBundleDataHolder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.math.Fraction;

import java.util.Optional;

public class UpgradedBundleItem extends BundleItem {
    private static final int FULL_ITEM_BAR_COLOR = ColorHelper.fromFloats(1.0F, 1.0F, 0.33F, 0.33F);
    private static final int ITEM_BAR_COLOR = ColorHelper.fromFloats(1.0F, 0.44F, 0.53F, 1.0F);

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
        BundleContentsComponent bundleContentsComponent = stack.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
        return bundleContentsComponent.getOccupancy().compareTo(Fraction.ZERO) > 0;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        BundleContentsComponent bundleContentsComponent = stack.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
        Fraction maxFraction = Fraction.getFraction(this.maxCapacity, 64);
        Fraction ratio = bundleContentsComponent.getOccupancy().divideBy(maxFraction);
        return Math.min(1 + MathHelper.multiplyFraction(ratio, 12), 13);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        BundleContentsComponent bundleContentsComponent = stack.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
        Fraction maxFraction = Fraction.getFraction(this.maxCapacity, 64);
        return bundleContentsComponent.getOccupancy().compareTo(maxFraction) >= 0 ? FULL_ITEM_BAR_COLOR : ITEM_BAR_COLOR;
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        Optional<TooltipData> opt = super.getTooltipData(stack);
        opt.ifPresent(data -> {
            if (data instanceof UpgradedBundleDataHolder holder) {
                holder.bundleupgrade$setMaxCapacity(this.maxCapacity);
            }
        });
        return opt;
    }
}
