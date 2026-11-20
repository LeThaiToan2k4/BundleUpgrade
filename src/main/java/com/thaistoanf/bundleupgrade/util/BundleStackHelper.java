package com.thaistoanf.bundleupgrade.util;

import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Helpers that keep every stack stored inside a bundle within
 * {@code 1 <= count <= item.getMaxStackSize()}.
 */
public final class BundleStackHelper {
    private BundleStackHelper() {}

    public static int maxStackSize(ItemInstance item) {
        return Math.max(1, item.getMaxStackSize());
    }

    public static boolean isOversized(ItemInstance item) {
        return item.count() > maxStackSize(item);
    }

    public static List<ItemStack> splitOversizedStacks(List<ItemStack> stacks) {
        boolean needsSplit = false;
        for (ItemStack stack : stacks) {
            if (stack.isEmpty() || isOversized(stack)) {
                needsSplit = true;
                break;
            }
        }
        if (!needsSplit) {
            return stacks;
        }

        List<ItemStack> result = new ArrayList<>(stacks.size() + 4);
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                appendSplit(result, stack);
            }
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

    public static List<ItemStackTemplate> splitOversizedTemplates(List<ItemStackTemplate> templates) {
        boolean needsSplit = false;
        for (ItemStackTemplate template : templates) {
            if (isOversized(template)) {
                needsSplit = true;
                break;
            }
        }
        if (!needsSplit) {
            return templates;
        }

        List<ItemStackTemplate> result = new ArrayList<>(templates.size() + 4);
        for (ItemStackTemplate template : templates) {
            int max = maxStackSize(template);
            int remaining = template.count();
            while (remaining > 0) {
                int chunk = Math.min(max, remaining);
                result.add(template.withCount(chunk));
                remaining -= chunk;
            }
        }
        return List.copyOf(result);
    }
}
