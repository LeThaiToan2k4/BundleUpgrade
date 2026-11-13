package com.thaistoanf.bundleupgrade.util;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class BundleStackHelper {
    private BundleStackHelper() {}

    public static int maxStackSize(ItemStack stack) {
        return Math.max(1, stack.getMaxCount());
    }

    public static boolean isOversized(ItemStack stack) {
        return stack.getCount() > maxStackSize(stack);
    }

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

