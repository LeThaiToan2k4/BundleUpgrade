package com.thaistoanf.bundleupgrade.client.mixin;

import com.thaistoanf.bundleupgrade.client.duck.UpgradedBundleDataHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.BundleTooltipData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Environment(EnvType.CLIENT)
@Mixin(BundleTooltipData.class)
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

