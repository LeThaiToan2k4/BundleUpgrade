package com.thaistoanf.bundleupgrade.client.mixin;

import com.thaistoanf.bundleupgrade.client.duck.UpgradedBundleTooltipHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.tooltip.BundleTooltipComponent;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(BundleTooltipComponent.class)
public class BundleTooltipComponentMixin implements UpgradedBundleTooltipHolder {
    @Shadow @Final
    private BundleContentsComponent bundleContents;

    @Shadow @Final
    private static Identifier BUNDLE_PROGRESS_BAR_FILL_TEXTURE;
    @Shadow @Final
    private static Identifier BUNDLE_PROGRESS_BAR_FULL_TEXTURE;

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

    @Inject(method = "getProgressBarFill", at = @At("HEAD"), cancellable = true)
    private void bundleupgrade$scaledProgressBarFill(CallbackInfoReturnable<Integer> cir) {
        if (this.bundleupgrade$maxCapacity != 64) {
            Fraction maxFraction = Fraction.getFraction(this.bundleupgrade$maxCapacity, 64);
            Fraction ratio = this.bundleContents.getOccupancy().divideBy(maxFraction);
            cir.setReturnValue(MathHelper.clamp(MathHelper.multiplyFraction(ratio, 94), 0, 94));
        }
    }

    @Inject(method = "getProgressBarFillTexture", at = @At("HEAD"), cancellable = true)
    private void bundleupgrade$scaledFillTexture(CallbackInfoReturnable<Identifier> cir) {
        if (this.bundleupgrade$maxCapacity != 64) {
            Fraction maxFraction = Fraction.getFraction(this.bundleupgrade$maxCapacity, 64);
            cir.setReturnValue(this.bundleContents.getOccupancy().compareTo(maxFraction) >= 0 
                ? BUNDLE_PROGRESS_BAR_FULL_TEXTURE 
                : BUNDLE_PROGRESS_BAR_FILL_TEXTURE);
        }
    }

    @Inject(method = "getProgressBarLabel", at = @At("HEAD"), cancellable = true)
    private void bundleupgrade$customProgressBarLabel(CallbackInfoReturnable<Text> cir) {
        if (this.bundleupgrade$maxCapacity != 64) {
            int currentUnits = Math.round(this.bundleContents.getOccupancy().floatValue() * 64.0f);
            cir.setReturnValue(Text.translatable("tooltip.bundleupgrade.capacity", currentUnits, this.bundleupgrade$maxCapacity));
        }
    }
}
