package com.thaistoanf.bundleupgrade.mixin;

import com.thaistoanf.bundleupgrade.duck.UpgradedBundleDataHolder;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BundleTooltip.class)
public class BundleTooltipDataMixin implements UpgradedBundleDataHolder {
    @Unique
    private int bundleupgrade$maxCapacity = 64;

    @Override
    public int bundleupgrade$getMaxCapacity() {
        return this.bundleupgrade$maxCapacity;
    }

    @Override
    public void bundleupgrade$setMaxCapacity(int maxCapacity) {
        this.bundleupgrade$maxCapacity = maxCapacity;
    }
}
