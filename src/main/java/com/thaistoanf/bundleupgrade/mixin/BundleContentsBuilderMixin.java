package com.thaistoanf.bundleupgrade.mixin;

import com.mojang.serialization.DataResult;
import com.thaistoanf.bundleupgrade.duck.BundleBuilderDuck;
import com.thaistoanf.bundleupgrade.util.BundleCapacityContext;
import com.thaistoanf.bundleupgrade.util.BundleStackHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
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

@Mixin(BundleContents.Mutable.class)
public abstract class BundleContentsBuilderMixin implements BundleBuilderDuck {
    @Shadow @Final
    private List<ItemStack> items;

    @Shadow
    private Fraction weight;

    @Shadow
    private int selectedItem;

    @Shadow
    private int getMaxAmountToAdd(Fraction itemWeight) {
        throw new AssertionError();
    }

    @Unique
    private Fraction bundleupgrade$maxOccupancy = Fraction.ONE;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void bundleupgrade$initMaxOccupancy(BundleContents base, CallbackInfo ci) {
        Fraction ctx = BundleCapacityContext.getCurrentMaxFraction();
        if (ctx != null) {
            this.bundleupgrade$maxOccupancy = ctx;
        }

        List<ItemStack> normalized = BundleStackHelper.splitOversizedStacks(this.items);
        if (normalized != this.items) {
            this.items.clear();
            this.items.addAll(normalized);
            this.selectedItem = -1;
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
        method = "getMaxAmountToAdd",
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

    @Inject(method = "tryInsert(Lnet/minecraft/world/item/ItemStack;)I", at = @At("HEAD"), cancellable = true)
    private void bundleupgrade$tryInsertRespectingMaxStackSize(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (!BundleContents.canItemBeInBundle(stack)) {
            cir.setReturnValue(0);
            return;
        }

        DataResult<Fraction> weightResult = BundleContentsComponentAccessor.bundleupgrade$invokeGetWeight(stack);
        if (weightResult.isError()) {
            cir.setReturnValue(0);
            return;
        }

        Fraction itemWeight = weightResult.getOrThrow();
        int toInsert = Math.min(stack.getCount(), this.getMaxAmountToAdd(itemWeight));
        if (toInsert <= 0) {
            cir.setReturnValue(0);
            return;
        }

        this.weight = this.weight.add(itemWeight.multiplyBy(Fraction.getFraction(toInsert, 1)));

        int remaining = toInsert;

        if (stack.isStackable()) {
            List<ItemStack> toppedUp = new ArrayList<>();
            int i = 0;
            while (i < this.items.size() && remaining > 0) {
                ItemStack existing = this.items.get(i);
                int space = existing.getMaxStackSize() - existing.getCount();
                if (space > 0 && ItemStack.isSameItemSameComponents(existing, stack)) {
                    int moved = Math.min(space, remaining);
                    this.items.remove(i);
                    toppedUp.add(existing.copyWithCount(existing.getCount() + moved));
                    stack.shrink(moved);
                    remaining -= moved;
                } else {
                    i++;
                }
            }
            this.items.addAll(0, toppedUp);
        }

        int maxPerStack = BundleStackHelper.maxStackSize(stack);
        while (remaining > 0) {
            int chunk = Math.min(remaining, maxPerStack);
            this.items.add(0, stack.split(chunk));
            remaining -= chunk;
        }

        cir.setReturnValue(toInsert);
    }
}
