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
    @ModifyVariable(method = "<init>(Ljava/util/List;)V", at = @At("HEAD"), argsOnly = true)
    private static List<ItemStack> bundleupgrade$normalizeDecodedStacks(List<ItemStack> stacks) {
        List<ItemStack> normalized = BundleStackHelper.splitOversizedStacks(stacks);
        return normalized == stacks ? stacks : List.copyOf(normalized);
    }
}

