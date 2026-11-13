package com.thaistoanf.bundleupgrade.mixin;

import com.thaistoanf.bundleupgrade.item.UpgradedBundleItem;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShapedRecipe.class)
public class ShapedRecipeMixin {
    @Inject(method = "craft(Lnet/minecraft/inventory/RecipeInputInventory;Lnet/minecraft/registry/RegistryWrapper$WrapperLookup;)Lnet/minecraft/item/ItemStack;", at = @At("RETURN"), cancellable = true)
    private void bundleupgrade$preserveContentsAndName(RecipeInputInventory inventory, RegistryWrapper.WrapperLookup registryLookup, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack crafted = cir.getReturnValue();
        if (crafted != null && !crafted.isEmpty() && crafted.getItem() instanceof UpgradedBundleItem) {
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack ingredient = inventory.getStack(i);
                if (!ingredient.isEmpty() && ingredient.getItem() instanceof BundleItem) {
                    BundleContentsComponent contents = ingredient.get(DataComponentTypes.BUNDLE_CONTENTS);
                    if (contents != null && !contents.isEmpty()) {
                        crafted.set(DataComponentTypes.BUNDLE_CONTENTS, contents);
                    }
                    Text customName = ingredient.get(DataComponentTypes.CUSTOM_NAME);
                    if (customName != null) {
                        crafted.set(DataComponentTypes.CUSTOM_NAME, customName);
                    }
                    break;
                }
            }
        }
    }
}
