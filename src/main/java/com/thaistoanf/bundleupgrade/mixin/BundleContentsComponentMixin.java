package com.thaistoanf.bundleupgrade.mixin;

import com.thaistoanf.bundleupgrade.util.BundleStackHelper;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(BundleContentsComponent.class)
public abstract class BundleContentsComponentMixin {
    /**
     * Normalizes bundle contents decoded from disk: bundles saved by 1.0.0 could contain a
     * single merged stack above its max stack size (65..99). Such stacks are split into
     * standard-sized stacks so the component always satisfies {@code count <= maxCount}.
     * Total occupancy is unchanged by the split.
     */
    @ModifyVariable(method = "validateOccupancy", at = @At("HEAD"), argsOnly = true)
    private static List<ItemStack> bundleupgrade$normalizeDecodedStacks(List<ItemStack> stacks) {
        List<ItemStack> normalized = BundleStackHelper.splitOversizedStacks(stacks);
        return normalized == stacks ? stacks : List.copyOf(normalized);
    }
}

