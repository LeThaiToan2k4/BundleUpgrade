package com.thaistoanf.bundleupgrade.util;

import com.thaistoanf.bundleupgrade.item.UpgradedBundleItem;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.math.Fraction;

import java.util.ArrayDeque;
import java.util.Deque;

public final class BundleCapacityContext {
    private static final ThreadLocal<Deque<Fraction>> CONTEXT_STACK = ThreadLocal.withInitial(ArrayDeque::new);

    private BundleCapacityContext() {}

    public static void push(ItemStack stack) {
        Fraction fraction = Fraction.ONE;
        if (stack != null && !stack.isEmpty()) {
            if (stack.getItem() instanceof UpgradedBundleItem upgraded) {
                fraction = Fraction.getFraction(upgraded.getMaxCapacity(), 64);
            }
        }
        CONTEXT_STACK.get().push(fraction);
    }

    public static void pop() {
        Deque<Fraction> deque = CONTEXT_STACK.get();
        if (!deque.isEmpty()) {
            deque.pop();
        }
    }

    public static Fraction getCurrentMaxFraction() {
        Deque<Fraction> deque = CONTEXT_STACK.get();
        return deque.isEmpty() ? null : deque.peek();
    }
}
