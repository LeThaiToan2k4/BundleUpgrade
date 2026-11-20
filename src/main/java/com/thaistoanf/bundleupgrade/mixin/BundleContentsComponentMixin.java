package com.thaistoanf.bundleupgrade.mixin;

import com.thaistoanf.bundleupgrade.util.BundleStackHelper;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(BundleContents.class)
public abstract class BundleContentsComponentMixin {
    @ModifyVariable(method = "<init>(Ljava/util/List;I)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private static List<ItemStackTemplate> bundleupgrade$normalizeDecodedStacks(List<ItemStackTemplate> items) {
        return BundleStackHelper.splitOversizedTemplates(items);
    }
}
