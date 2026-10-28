package com.thaistoanf.bundleupgrade.mixin;

import com.thaistoanf.bundleupgrade.duck.BundleBuilderDuck;
import com.thaistoanf.bundleupgrade.util.BundleCapacityContext;
import net.minecraft.component.type.BundleContentsComponent;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BundleContentsComponent.Builder.class)
public class BundleContentsBuilderMixin implements BundleBuilderDuck {
    @Unique
    private Fraction bundleupgrade$maxOccupancy = Fraction.ONE;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void bundleupgrade$initMaxOccupancy(BundleContentsComponent base, CallbackInfo ci) {
        Fraction ctx = BundleCapacityContext.getCurrentMaxFraction();
        if (ctx != null) {
            this.bundleupgrade$maxOccupancy = ctx;
        }
    }

    @Override
    public void bundleupgrade$setMaxOccupancy(Fraction maxOccupancy) {
        this.bundleupgrade$maxOccupancy = maxOccupancy != null ? maxOccupancy : Fraction.ONE;
    }

    @Override
    public Fraction bundleupgrade$getMaxOccupancy() {
        return this.bundleupgrade$maxOccupancy;
    }

    @Redirect(
        method = "getMaxAllowed",
        at = @At(
            value = "FIELD",
            target = "Lorg/apache/commons/lang3/math/Fraction;ONE:Lorg/apache/commons/lang3/math/Fraction;"
        )
    )
    private Fraction bundleupgrade$redirectMaxOccupancy() {
        Fraction max = this.bundleupgrade$maxOccupancy;
        if (max == null || Fraction.ONE.equals(max)) {
            Fraction ctx = BundleCapacityContext.getCurrentMaxFraction();
            if (ctx != null) {
                return ctx;
            }
        }
        return max != null ? max : Fraction.ONE;
    }
}
