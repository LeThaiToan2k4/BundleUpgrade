package com.thaistoanf.bundleupgrade.client.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

@Environment(EnvType.CLIENT)
@Mixin(AbstractContainerScreen.class)
public abstract class HandledScreenMixin {
    @Shadow @Final
    protected AbstractContainerMenu menu;

    @Shadow
    protected boolean isQuickCrafting;

    @Shadow @Final
    protected Set<Slot> quickCraftSlots;

    @Shadow
    private int quickCraftingButton;

    @Shadow
    private @Nullable Slot getHoveredSlot(double mouseX, double mouseY) {
        throw new AssertionError();
    }

    @Shadow
    protected abstract void slotClicked(Slot slot, int slotId, int mouseButton, ContainerInput containerInput);

    @Unique
    private final Set<Slot> bundleupgrade$bundleDragSlots = new HashSet<>();

    @Unique
    private boolean bundleupgrade$wasQuickCrafting;

    @Unique
    private boolean bundleupgrade$bundleDragArmed;

    @Unique
    private @Nullable Slot bundleupgrade$bundleDragStartSlot;

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void bundleupgrade$beforeMouseClicked(MouseButtonEvent event, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        this.bundleupgrade$wasQuickCrafting = this.isQuickCrafting;
    }

    @Inject(method = "mouseClicked", at = @At("RETURN"))
    private void bundleupgrade$afterMouseClicked(MouseButtonEvent event, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        boolean startedRightDrag = !this.bundleupgrade$wasQuickCrafting
                && this.isQuickCrafting
                && this.quickCraftingButton == InputConstants.MOUSE_BUTTON_RIGHT;
        if (startedRightDrag && bundleupgrade$hasContents(this.menu.getCarried())) {
            this.bundleupgrade$bundleDragArmed = true;
            this.bundleupgrade$bundleDragStartSlot = this.getHoveredSlot(event.x(), event.y());
            this.bundleupgrade$bundleDragSlots.clear();
        } else if (!this.isQuickCrafting) {
            this.bundleupgrade$resetBundleDrag();
        }
    }

    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void bundleupgrade$onMouseDragged(MouseButtonEvent event, double dx, double dy, CallbackInfoReturnable<Boolean> cir) {
        if (!this.bundleupgrade$bundleDragArmed) {
            return;
        }
        if (!this.isQuickCrafting
                || event.button() != InputConstants.MOUSE_BUTTON_RIGHT
                || !bundleupgrade$isBundle(this.menu.getCarried())) {
            this.bundleupgrade$resetBundleDrag();
            return;
        }

        Slot startSlot = this.bundleupgrade$bundleDragStartSlot;
        this.bundleupgrade$bundleDragStartSlot = null;
        if (startSlot != null) {
            this.bundleupgrade$placeOneStack(startSlot);
        }
        this.bundleupgrade$placeOneStack(this.getHoveredSlot(event.x(), event.y()));

        this.quickCraftSlots.clear();
        cir.setReturnValue(true);
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void bundleupgrade$onMouseReleased(MouseButtonEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (!this.bundleupgrade$bundleDragArmed) {
            return;
        }
        boolean placedAny = !this.bundleupgrade$bundleDragSlots.isEmpty();
        boolean sameButton = event.button() == this.quickCraftingButton;
        this.bundleupgrade$resetBundleDrag();

        if (placedAny && sameButton) {
            this.isQuickCrafting = false;
            this.quickCraftSlots.clear();
            cir.setReturnValue(true);
        }
    }

    @Unique
    private void bundleupgrade$placeOneStack(@Nullable Slot slot) {
        if (slot == null || slot.hasItem() || this.bundleupgrade$bundleDragSlots.contains(slot)) {
            return;
        }
        ItemStack bundle = this.menu.getCarried();
        BundleContents contents = bundle.get(DataComponents.BUNDLE_CONTENTS);
        if (contents == null || contents.isEmpty()) {
            return;
        }
        int selected = contents.getSelectedItemIndex();
        ItemStackTemplate nextTemplate = selected >= 0 && selected < contents.size()
                ? contents.items().get(selected)
                : contents.items().getFirst();
        ItemStack next = nextTemplate.create();
        if (next.isEmpty() || !slot.mayPlace(next) || !this.menu.canDragTo(slot)) {
            return;
        }

        this.bundleupgrade$bundleDragSlots.add(slot);
        this.slotClicked(slot, slot.index, InputConstants.MOUSE_BUTTON_RIGHT, ContainerInput.PICKUP);
    }

    @Unique
    private void bundleupgrade$resetBundleDrag() {
        this.bundleupgrade$bundleDragArmed = false;
        this.bundleupgrade$bundleDragStartSlot = null;
        this.bundleupgrade$bundleDragSlots.clear();
    }

    @Unique
    private static boolean bundleupgrade$isBundle(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof BundleItem && stack.has(DataComponents.BUNDLE_CONTENTS);
    }

    @Unique
    private static boolean bundleupgrade$hasContents(ItemStack stack) {
        if (!bundleupgrade$isBundle(stack)) {
            return false;
        }
        BundleContents contents = stack.get(DataComponents.BUNDLE_CONTENTS);
        return contents != null && !contents.isEmpty();
    }
}
