package com.thaistoanf.bundleupgrade.mixin;

import com.thaistoanf.bundleupgrade.item.UpgradedBundleItem;
import com.thaistoanf.bundleupgrade.util.BundleCapacityContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Mixin(BundleItem.class)
public class BundleItemMixin {
    @Unique
    private static final String BUNDLEUPGRADE_ITEMS_KEY = "Items";

    @Inject(method = "onStackClicked", at = @At("HEAD"))
    private void bundleupgrade$onStackClickedHead(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        BundleCapacityContext.push(stack);
    }

    @Inject(method = "onStackClicked", at = @At("RETURN"))
    private void bundleupgrade$onStackClickedReturn(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        BundleCapacityContext.pop();
    }

    @Inject(method = "onClicked", at = @At("HEAD"))
    private void bundleupgrade$onClickedHead(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir) {
        BundleCapacityContext.push(stack);
    }

    @Inject(method = "onClicked", at = @At("RETURN"))
    private void bundleupgrade$onClickedReturn(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir) {
        BundleCapacityContext.pop();
    }

    @ModifyConstant(method = {"onStackClicked", "addToBundle"}, constant = @Constant(intValue = 64))
    private static int bundleupgrade$expandCapacity(int original) {
        return BundleCapacityContext.getCurrentMaxCapacity();
    }

    @Inject(method = "getItemOccupancy", at = @At("HEAD"), cancellable = true)
    private static void bundleupgrade$nestedUpgradedBundleOccupancy(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (stack.getItem() instanceof UpgradedBundleItem) {
            cir.setReturnValue(4 + BundleItemInvoker.bundleupgrade$getBundleOccupancy(stack));
        }
    }

    /**
     * Prevents identical items from merging into a single NBT entry with {@code Count > maxCount} (64).
     * Existing partial stacks are topped up only to {@code existing.getMaxCount()}, and any excess
     * count is split into new separate {@link NbtCompound} entries at index 0.
     *
     * <p>Note: in 1.20.4, {@code addToBundle} must not mutate {@code stack} directly because
     * {@code BundleItem#onClicked} decrements {@code otherStack} by the returned count.</p>
     */
    @Inject(method = "addToBundle", at = @At("HEAD"), cancellable = true)
    private static void bundleupgrade$addToBundleSplitStacks(ItemStack bundle, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (stack.isEmpty() || !stack.getItem().canBeNested()) {
            cir.setReturnValue(0);
            return;
        }

        NbtCompound nbt = bundle.getOrCreateNbt();
        if (!nbt.contains(BUNDLEUPGRADE_ITEMS_KEY)) {
            nbt.put(BUNDLEUPGRADE_ITEMS_KEY, new NbtList());
        }

        bundleupgrade$normalizeItemsList(bundle);

        int maxCapacity = bundle.getItem() instanceof UpgradedBundleItem upgraded
                ? upgraded.getMaxCapacity()
                : BundleCapacityContext.getCurrentMaxCapacity();
        int currentOccupancy = BundleItemInvoker.bundleupgrade$getBundleOccupancy(bundle);
        int itemOccupancy = BundleItemInvoker.bundleupgrade$getItemOccupancy(stack);
        int maxAllowed = Math.max(0, (maxCapacity - currentOccupancy) / itemOccupancy);
        int toInsert = Math.min(stack.getCount(), maxAllowed);
        if (toInsert <= 0) {
            cir.setReturnValue(0);
            return;
        }

        NbtList itemsList = nbt.getList(BUNDLEUPGRADE_ITEMS_KEY, NbtElement.COMPOUND_TYPE);
        int remaining = toInsert;
        int maxPerStack = Math.max(1, stack.getMaxCount());

        boolean canMerge = stack.isStackable()
                && !stack.isOf(Items.BUNDLE)
                && !(stack.getItem() instanceof BundleItem);

        if (canMerge) {
            List<NbtCompound> toppedUp = new ArrayList<>();
            int i = 0;
            while (i < itemsList.size() && remaining > 0) {
                NbtCompound entryNbt = itemsList.getCompound(i);
                ItemStack existing = ItemStack.fromNbt(entryNbt);
                int space = existing.getMaxCount() - existing.getCount();
                if (space > 0 && ItemStack.canCombine(existing, stack)) {
                    int moved = Math.min(space, remaining);
                    itemsList.remove(i);
                    existing.increment(moved);
                    NbtCompound updatedNbt = new NbtCompound();
                    existing.writeNbt(updatedNbt);
                    toppedUp.add(updatedNbt);
                    remaining -= moved;
                } else {
                    i++;
                }
            }
            for (int j = toppedUp.size() - 1; j >= 0; j--) {
                itemsList.add(0, toppedUp.get(j));
            }
        }

        while (remaining > 0) {
            int chunk = Math.min(remaining, maxPerStack);
            ItemStack chunkStack = stack.copyWithCount(chunk);
            NbtCompound chunkNbt = new NbtCompound();
            chunkStack.writeNbt(chunkNbt);
            itemsList.add(0, chunkNbt);
            remaining -= chunk;
        }

        cir.setReturnValue(toInsert);
    }

    @Inject(method = "removeFirstStack", at = @At("HEAD"))
    private static void bundleupgrade$normalizeBeforeRemove(ItemStack stack, CallbackInfoReturnable<Optional<ItemStack>> cir) {
        bundleupgrade$normalizeItemsList(stack);
    }

    @Inject(method = "getBundledStacks", at = @At("HEAD"))
    private static void bundleupgrade$normalizeBeforeStream(ItemStack stack, CallbackInfoReturnable<Stream<ItemStack>> cir) {
        bundleupgrade$normalizeItemsList(stack);
    }

    @Inject(method = "getAmountFilled", at = @At("HEAD"), cancellable = true)
    private static void bundleupgrade$getAmountFilled(ItemStack stack, CallbackInfoReturnable<Float> cir) {
        if (stack.getItem() instanceof UpgradedBundleItem upgraded) {
            int occupancy = BundleItemInvoker.bundleupgrade$getBundleOccupancy(stack);
            cir.setReturnValue((float) occupancy / (float) upgraded.getMaxCapacity());
        }
    }

    /**
     * Splits any legacy oversized NBT stack entries ({@code Count > maxCount}) in place into
     * standard-sized entries so every stored stack strictly satisfies {@code 1 <= Count <= maxCount}.
     */
    @Unique
    private static void bundleupgrade$normalizeItemsList(ItemStack bundle) {
        NbtCompound nbt = bundle.getNbt();
        if (nbt == null || !nbt.contains(BUNDLEUPGRADE_ITEMS_KEY, NbtElement.LIST_TYPE)) {
            return;
        }
        NbtList itemsList = nbt.getList(BUNDLEUPGRADE_ITEMS_KEY, NbtElement.COMPOUND_TYPE);
        boolean hasOversized = false;
        for (int i = 0; i < itemsList.size(); i++) {
            NbtCompound entry = itemsList.getCompound(i);
            int rawCount = entry.getByte("Count") & 0xFF;
            ItemStack parsed = ItemStack.fromNbt(entry);
            int max = Math.max(1, parsed.getMaxCount());
            if (rawCount > max) {
                hasOversized = true;
                break;
            }
        }
        if (!hasOversized) {
            return;
        }

        List<NbtCompound> normalized = new ArrayList<>(itemsList.size() + 4);
        for (int i = 0; i < itemsList.size(); i++) {
            NbtCompound entry = itemsList.getCompound(i);
            int rawCount = entry.getByte("Count") & 0xFF;
            ItemStack parsed = ItemStack.fromNbt(entry);
            if (parsed.isEmpty() && rawCount > 0) {
                NbtCompound copy = entry.copy();
                copy.putByte("Count", (byte) 1);
                parsed = ItemStack.fromNbt(copy);
            }
            if (parsed.isEmpty()) {
                continue;
            }
            int max = Math.max(1, parsed.getMaxCount());
            int remaining = rawCount;
            while (remaining > 0) {
                int chunk = Math.min(remaining, max);
                ItemStack piece = parsed.copyWithCount(chunk);
                NbtCompound pieceNbt = new NbtCompound();
                piece.writeNbt(pieceNbt);
                normalized.add(pieceNbt);
                remaining -= chunk;
            }
        }

        itemsList.clear();
        itemsList.addAll(normalized);
    }
}
