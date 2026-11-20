package com.thaistoanf.bundleupgrade.mixin;

import com.thaistoanf.bundleupgrade.item.UpgradedBundleItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShapedRecipe.class)
public class ShapedRecipeMixin {
    @Inject(
        method = "assemble(Lnet/minecraft/world/item/crafting/CraftingInput;)Lnet/minecraft/world/item/ItemStack;",
        at = @At("RETURN")
    )
    private void bundleupgrade$preserveContentsAndName(CraftingInput input, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack crafted = cir.getReturnValue();
        if (crafted != null && !crafted.isEmpty() && crafted.getItem() instanceof UpgradedBundleItem) {
            for (ItemStack ingredient : input.items()) {
                if (!ingredient.isEmpty() && ingredient.getItem() instanceof BundleItem) {
                    BundleContents contents = ingredient.get(DataComponents.BUNDLE_CONTENTS);
                    if (contents != null && !contents.isEmpty()) {
                        crafted.set(DataComponents.BUNDLE_CONTENTS, contents);
                    }
                    Component customName = ingredient.get(DataComponents.CUSTOM_NAME);
                    if (customName != null) {
                        crafted.set(DataComponents.CUSTOM_NAME, customName);
                    }
                    break;
                }
            }
        }
    }
}
