package com.thaistoanf.bundleupgrade.mixin;

import com.thaistoanf.bundleupgrade.duck.BundleBuilderDuck;
import com.thaistoanf.bundleupgrade.util.BundleCapacityContext;
import com.thaistoanf.bundleupgrade.util.BundleStackHelper;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.math.Fraction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(BundleContentsComponent.Builder.class)
public abstract class BundleContentsBuilderMixin implements BundleBuilderDuck {
    @Shadow @Final
    private List<ItemStack> stacks;

    @Shadow
    private Fraction occupancy;

    @Shadow
    private int selectedStackIndex;

    @Shadow
    private int getMaxAllowed(ItemStack stack) {
        throw new AssertionError();
    }

    @Unique
    private Fraction bundleupgrade$maxOccupancy = Fraction.ONE;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void bundleupgrade$initMaxOccupancy(BundleContentsComponent base, CallbackInfo ci) {
        Fraction ctx = BundleCapacityContext.getCurrentMaxFraction();
        if (ctx != null) {
            this.bundleupgrade$maxOccupancy = ctx;
        }

        List<ItemStack> normalized = BundleStackHelper.splitOversizedStacks(this.stacks);
        if (normalized != this.stacks) {
            this.stacks.clear();
            this.stacks.addAll(normalized);
            this.selectedStackIndex = -1;
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

    @Inject(method = "add(Lnet/minecraft/item/ItemStack;)I", at = @At("HEAD"), cancellable = true)
    private void bundleupgrade$addRespectingMaxStackSize(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (!BundleContentsComponent.canBeBundled(stack)) {
            cir.setReturnValue(0);
            return;
        }

        int toInsert = Math.min(stack.getCount(), this.getMaxAllowed(stack));
        if (toInsert <= 0) {
            cir.setReturnValue(0);
            return;
        }

        Fraction itemOccupancy = BundleContentsComponentAccessor.bundleupgrade$invokeGetItemOccupancy(stack);
        this.occupancy = this.occupancy.add(itemOccupancy.multiplyBy(Fraction.getFraction(toInsert, 1)));

        int remaining = toInsert;

        if (stack.isStackable()) {
            List<ItemStack> toppedUp = new ArrayList<>();
            int i = 0;
            while (i < this.stacks.size() && remaining > 0) {
                ItemStack existing = this.stacks.get(i);
                int space = existing.getMaxCount() - existing.getCount();
                if (space > 0 && ItemStack.areItemsAndComponentsEqual(existing, stack)) {
                    int moved = Math.min(space, remaining);
                    this.stacks.remove(i);
                    toppedUp.add(existing.copyWithCount(existing.getCount() + moved));
                    stack.decrement(moved);
                    remaining -= moved;
                } else {
                    i++;
                }
            }
            this.stacks.addAll(0, toppedUp);
        }

        int maxPerStack = BundleStackHelper.maxStackSize(stack);
        while (remaining > 0) {
            int chunk = Math.min(remaining, maxPerStack);
            this.stacks.add(0, stack.split(chunk));
            remaining -= chunk;
        }

        cir.setReturnValue(toInsert);
    }
}
