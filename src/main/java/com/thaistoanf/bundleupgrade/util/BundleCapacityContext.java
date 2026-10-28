package com.thaistoanf.bundleupgrade.util;

import com.thaistoanf.bundleupgrade.item.UpgradedBundleItem;
import net.minecraft.item.ItemStack;

import java.util.ArrayDeque;
import java.util.Deque;

public final class BundleCapacityContext {
    public static final int VANILLA_CAPACITY = 64;

    private static final ThreadLocal<Deque<Integer>> CONTEXT_STACK = ThreadLocal.withInitial(ArrayDeque::new);

    private BundleCapacityContext() {}

    public static void push(ItemStack stack) {
        int capacity = VANILLA_CAPACITY;
        if (stack != null && !stack.isEmpty()) {
            if (stack.getItem() instanceof UpgradedBundleItem upgraded) {
                capacity = upgraded.getMaxCapacity();
            }
        }
        CONTEXT_STACK.get().push(capacity);
    }

    public static void pop() {
        Deque<Integer> deque = CONTEXT_STACK.get();
        if (!deque.isEmpty()) {
            deque.pop();
        }
    }

    public static int getCurrentMaxCapacity() {
        Deque<Integer> deque = CONTEXT_STACK.get();
        return deque.isEmpty() ? VANILLA_CAPACITY : deque.peek();
    }
}
