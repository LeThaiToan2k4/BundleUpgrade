package com.thaistoanf.bundleupgrade.client.mixin;

import com.thaistoanf.bundleupgrade.client.duck.UpgradedBundleTooltipHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientBundleTooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(ClientBundleTooltip.class)
public class BundleTooltipComponentMixin implements UpgradedBundleTooltipHolder {
    @Shadow @Final
    private static Identifier PROGRESSBAR_FILL_SPRITE;

    @Shadow @Final
    private static Identifier PROGRESSBAR_FULL_SPRITE;

    @Unique
    private static final ThreadLocal<Integer> BUNDLEUPGRADE$ACTIVE_MAX_CAPACITY = ThreadLocal.withInitial(() -> 64);

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

    @Inject(method = "extractImage", at = @At("HEAD"))
    private void bundleupgrade$pushTooltipCapacity(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics, CallbackInfo ci) {
        BUNDLEUPGRADE$ACTIVE_MAX_CAPACITY.set(this.bundleupgrade$maxCapacity);
    }

    @Inject(method = "extractImage", at = @At("RETURN"))
    private void bundleupgrade$popTooltipCapacity(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics, CallbackInfo ci) {
        BUNDLEUPGRADE$ACTIVE_MAX_CAPACITY.remove();
    }

    @Inject(method = "getProgressBarFill", at = @At("HEAD"), cancellable = true)
    private static void bundleupgrade$scaledProgressBarFill(Fraction weight, CallbackInfoReturnable<Integer> cir) {
        int maxCapacity = BUNDLEUPGRADE$ACTIVE_MAX_CAPACITY.get();
        if (maxCapacity != 64) {
            Fraction maxFraction = Fraction.getFraction(maxCapacity, 64);
            Fraction ratio = weight.divideBy(maxFraction);
            cir.setReturnValue(Mth.clamp(Mth.mulAndTruncate(ratio, 94), 0, 94));
        }
    }

    @Inject(method = "getProgressBarTexture", at = @At("HEAD"), cancellable = true)
    private static void bundleupgrade$scaledFillTexture(Fraction weight, CallbackInfoReturnable<Identifier> cir) {
        int maxCapacity = BUNDLEUPGRADE$ACTIVE_MAX_CAPACITY.get();
        if (maxCapacity != 64) {
            Fraction maxFraction = Fraction.getFraction(maxCapacity, 64);
            cir.setReturnValue(weight.compareTo(maxFraction) >= 0
                    ? PROGRESSBAR_FULL_SPRITE
                    : PROGRESSBAR_FILL_SPRITE);
        }
    }

    @Inject(method = "getProgressBarFillText", at = @At("HEAD"), cancellable = true)
    private static void bundleupgrade$customProgressBarLabel(Fraction weight, CallbackInfoReturnable<Component> cir) {
        int maxCapacity = BUNDLEUPGRADE$ACTIVE_MAX_CAPACITY.get();
        if (maxCapacity != 64) {
            int currentUnits = Math.round(weight.floatValue() * 64.0F);
            cir.setReturnValue(Component.translatable("tooltip.bundleupgrade.capacity", currentUnits, maxCapacity));
        }
    }
}
