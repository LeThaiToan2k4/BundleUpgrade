package com.thaistoanf.bundleupgrade.client.mixin;

import com.thaistoanf.bundleupgrade.client.duck.UpgradedBundleDataHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(BundleTooltipComponent.class)
public class BundleTooltipComponentMixin implements UpgradedBundleDataHolder {
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

    @Redirect(
        method = "drawItems",
        at = @At(
            value = "FIELD",
            target = "Lorg/apache/commons/lang3/math/Fraction;ONE:Lorg/apache/commons/lang3/math/Fraction;",
            remap = false
        )
    )
    private Fraction bundleupgrade$scaledFullThreshold() {
        return Fraction.getFraction(this.bundleupgrade$maxCapacity, 64);
    }
}
