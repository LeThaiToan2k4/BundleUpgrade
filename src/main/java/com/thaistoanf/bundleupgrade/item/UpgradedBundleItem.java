package com.thaistoanf.bundleupgrade.item;

import com.thaistoanf.bundleupgrade.duck.UpgradedBundleDataHolder;
import com.thaistoanf.bundleupgrade.util.BundleCapacityContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import org.apache.commons.lang3.math.Fraction;

import java.util.Optional;

/**
 * A bundle with expanded storage capacity. All inventory interactions inherit vanilla
 * {@link BundleItem} behaviour while publishing the tier capacity via {@link BundleCapacityContext}.
 */
public class UpgradedBundleItem extends BundleItem {
    private static final int FULL_BAR_COLOR = ARGB.colorFromFloat(1.0F, 1.0F, 0.33F, 0.33F);
    private static final int BAR_COLOR = ARGB.colorFromFloat(1.0F, 0.44F, 0.53F, 1.0F);
    private static final int VANILLA_MAX_USE_DURATION = 200;

    private final int maxCapacity;
    private final Fraction maxFraction;
    private final int maxUseDuration;

    public UpgradedBundleItem(int maxCapacity, Item.Properties properties) {
        super(properties);
        this.maxCapacity = maxCapacity;
        this.maxFraction = Fraction.getFraction(maxCapacity, 64);
        this.maxUseDuration = Math.max(VANILLA_MAX_USE_DURATION, 10 + 2 * maxCapacity + 2);
    }

    public int getMaxCapacity() {
        return this.maxCapacity;
    }

    public Fraction getMaxFraction() {
        return this.maxFraction;
    }

    public static Fraction getWeightSafe(BundleContents contents, Fraction fallback) {
        return contents.weight().result().orElse(fallback);
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        BundleCapacityContext.push(stack);
        try {
            return super.overrideStackedOnOther(stack, slot, action, player);
        } finally {
            BundleCapacityContext.pop();
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        BundleCapacityContext.push(stack);
        try {
            return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
        } finally {
            BundleCapacityContext.pop();
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return this.maxUseDuration;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        BundleContents contents = stack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
        return getWeightSafe(contents, this.maxFraction).compareTo(Fraction.ZERO) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        BundleContents contents = stack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
        Fraction ratio = getWeightSafe(contents, this.maxFraction).divideBy(this.maxFraction);
        return Math.min(1 + Mth.mulAndTruncate(ratio, 12), 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        BundleContents contents = stack.getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
        return getWeightSafe(contents, this.maxFraction).compareTo(this.maxFraction) >= 0 ? FULL_BAR_COLOR : BAR_COLOR;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        Optional<TooltipComponent> opt = super.getTooltipImage(stack);
        opt.ifPresent(data -> {
            if (data instanceof UpgradedBundleDataHolder holder) {
                holder.bundleupgrade$setMaxCapacity(this.maxCapacity);
            }
        });
        return opt;
    }
}
