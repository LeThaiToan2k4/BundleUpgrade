package com.thaistoanf.bundleupgrade.item;

import com.thaistoanf.bundleupgrade.duck.UpgradedBundleDataHolder;
import com.thaistoanf.bundleupgrade.util.BundleCapacityContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.math.Fraction;

import java.util.Optional;

/**
 * A bundle with a larger capacity. All inventory interactions are inherited unchanged from
 * vanilla {@link BundleItem} (1.21.2+ mechanics):
 * <ul>
 *     <li>Left-click the bundle with an item on the cursor, or left-click an item while holding
 *     the bundle, to insert.</li>
 *     <li>Right-click the bundle with an empty cursor to take out one stack (the selected stack,
 *     or the first one).</li>
 *     <li>Right-click an empty slot while holding the bundle to place one stack into that slot.</li>
 *     <li>Use (right-click) in the world to drop the contents stack by stack.</li>
 * </ul>
 * The only difference is the capacity, which is published through {@link BundleCapacityContext}
 * while vanilla's click logic runs.
 */
public class UpgradedBundleItem extends BundleItem {
    private static final int FULL_ITEM_BAR_COLOR = ColorHelper.fromFloats(1.0F, 1.0F, 0.33F, 0.33F);
    private static final int ITEM_BAR_COLOR = ColorHelper.fromFloats(1.0F, 0.44F, 0.53F, 1.0F);
    /** Vanilla bundle use duration in ticks. */
    private static final int VANILLA_MAX_USE_TIME = 200;

    private final int maxCapacity;
    private final int maxUseTime;

    public UpgradedBundleItem(int maxCapacity, Item.Settings settings) {
        super(settings);
        this.maxCapacity = maxCapacity;
        // Vanilla drops one stack on the first use tick, then one every 2 ticks after a 10 tick
        // delay. An upgraded bundle may contain up to maxCapacity stacks (one unit each), so the
        // use duration is scaled to guarantee everything is dropped in a single hold.
        this.maxUseTime = Math.max(VANILLA_MAX_USE_TIME, 10 + 2 * maxCapacity + 2);
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
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return this.maxUseTime;
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
