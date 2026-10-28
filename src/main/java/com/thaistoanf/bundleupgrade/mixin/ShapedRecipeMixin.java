package com.thaistoanf.bundleupgrade.mixin;

import com.thaistoanf.bundleupgrade.item.UpgradedBundleItem;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShapedRecipe.class)
public class ShapedRecipeMixin {
    @Inject(method = "craft(Lnet/minecraft/inventory/RecipeInputInventory;Lnet/minecraft/registry/DynamicRegistryManager;)Lnet/minecraft/item/ItemStack;", at = @At("RETURN"), cancellable = true)
    private void bundleupgrade$preserveContentsAndName(RecipeInputInventory inventory, DynamicRegistryManager registryManager, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack crafted = cir.getReturnValue();
        if (crafted != null && !crafted.isEmpty() && crafted.getItem() instanceof UpgradedBundleItem) {
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack ingredient = inventory.getStack(i);
                if (!ingredient.isEmpty() && ingredient.getItem() instanceof BundleItem) {
                    NbtCompound nbt = ingredient.getNbt();
                    if (nbt != null) {
                        crafted.setNbt(nbt.copy());
                    }
                    break;
                }
            }
        }
    }
}
