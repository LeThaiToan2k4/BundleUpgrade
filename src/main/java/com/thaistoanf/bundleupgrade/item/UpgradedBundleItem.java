package com.thaistoanf.bundleupgrade.item;

import com.thaistoanf.bundleupgrade.util.BundleCapacityContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.math.Fraction;

import java.util.List;

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
        BundleContentsComponent contents = stack.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
        return contents.getOccupancy().compareTo(Fraction.ZERO) > 0;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        BundleContentsComponent contents = stack.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
        Fraction maxFraction = Fraction.getFraction(this.maxCapacity, 64);
        Fraction ratio = contents.getOccupancy().divideBy(maxFraction);
        return Math.min(1 + MathHelper.multiplyFraction(ratio, 12), 13);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        BundleContentsComponent contents = stack.getOrDefault(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT);
        Fraction maxFraction = Fraction.getFraction(this.maxCapacity, 64);
        return contents.getOccupancy().compareTo(maxFraction) >= 0 ? FULL_ITEM_BAR_COLOR : ITEM_BAR_COLOR;
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        BundleContentsComponent contents = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
        if (contents != null) {
            int currentUnits = MathHelper.multiplyFraction(contents.getOccupancy(), 64);
            tooltip.add(Text.translatable("item.minecraft.bundle.fullness", currentUnits, this.maxCapacity).formatted(Formatting.GRAY));
        }
    }
}
