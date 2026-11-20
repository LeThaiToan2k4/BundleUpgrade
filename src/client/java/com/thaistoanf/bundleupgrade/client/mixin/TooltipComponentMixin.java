package com.thaistoanf.bundleupgrade.client.mixin;

import com.thaistoanf.bundleupgrade.client.duck.UpgradedBundleTooltipHolder;
import com.thaistoanf.bundleupgrade.duck.UpgradedBundleDataHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(ClientTooltipComponent.class)
public interface TooltipComponentMixin {
    @Inject(method = "create(Lnet/minecraft/world/inventory/tooltip/TooltipComponent;)Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;", at = @At("RETURN"))
    private static void bundleupgrade$transferCapacity(TooltipComponent tooltipComponent,
            CallbackInfoReturnable<ClientTooltipComponent> cir) {
        if (tooltipComponent instanceof UpgradedBundleDataHolder dataHolder
                && cir.getReturnValue() instanceof UpgradedBundleTooltipHolder compHolder) {
            compHolder.bundleupgrade$setMaxCapacity(dataHolder.bundleupgrade$getMaxCapacity());
        }
    }
}
