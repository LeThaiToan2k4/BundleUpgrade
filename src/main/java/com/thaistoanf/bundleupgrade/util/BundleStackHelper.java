package com.thaistoanf.bundleupgrade.util;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Helpers that keep every {@link ItemStack} stored inside a bundle within
 * {@code 1 <= count <= stack.getMaxCount()}.
 *
 * <p>Upgraded bundles can hold more than 64 items of the same type, so identical
 * items must be spread across several standard-sized stacks instead of being merged
 * into a single oversized stack (which breaks {@code ItemStack.CODEC}, limited to 99,
 * and lets players pull more than one stack onto the cursor at once).</p>
 */
public final class BundleStackHelper {
    private BundleStackHelper() {}

    /** Maximum count a single stored stack may have. Never lower than 1. */
    public static int maxStackSize(ItemStack stack) {
        return Math.max(1, stack.getMaxCount());
    }

    /** Whether the given stack violates {@code 1 <= count <= maxCount}. */
    public static boolean isOversized(ItemStack stack) {
        return stack.getCount() > maxStackSize(stack);
    }

    /**
     * Returns {@code stacks} unchanged when every entry already respects its max stack size,
     * otherwise returns a new list in which each oversized entry is replaced (in place) by
     * consecutive standard-sized copies. Empty stacks are dropped from the new list.
     */
    public static List<ItemStack> splitOversizedStacks(List<ItemStack> stacks) {
        boolean needsSplit = false;
        for (ItemStack stack : stacks) {
            if (isOversized(stack)) {
                needsSplit = true;
                break;
            }
        }
        if (!needsSplit) {
            return stacks;
        }

        List<ItemStack> result = new ArrayList<>(stacks.size() + 4);
        for (ItemStack stack : stacks) {
            appendSplit(result, stack);
        }
        return result;
    }

    /** Appends {@code stack} to {@code out} as one or more copies of at most max stack size. */
    public static void appendSplit(List<ItemStack> out, ItemStack stack) {
        int max = maxStackSize(stack);
        int remaining = stack.getCount();
        while (remaining > 0) {
            int chunk = Math.min(max, remaining);
            out.add(stack.copyWithCount(chunk));
            remaining -= chunk;
        }
    }
}

