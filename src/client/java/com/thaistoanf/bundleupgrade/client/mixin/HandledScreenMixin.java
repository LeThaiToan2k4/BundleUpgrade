package com.thaistoanf.bundleupgrade.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
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
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {
    @Shadow @Final
    protected ScreenHandler handler;

    @Shadow
    protected boolean cursorDragging;

    @Shadow @Final
    protected Set<Slot> cursorDragSlots;

    @Shadow
    private int heldButtonCode;

    @Shadow
    private @Nullable Slot getSlotAt(double x, double y) {
        throw new AssertionError();
    }

    @Shadow
    protected abstract void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType);

    @Unique
    private final Set<Slot> bundleupgrade$bundleDragSlots = new HashSet<>();

    @Unique
    private boolean bundleupgrade$wasCursorDragging;

    @Unique
    private boolean bundleupgrade$bundleDragArmed;

    @Unique
    private boolean bundleupgrade$extractMode;

    @Unique
    private @Nullable Slot bundleupgrade$bundleDragStartSlot;

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void bundleupgrade$beforeMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        this.bundleupgrade$wasCursorDragging = this.cursorDragging;
    }

    @Inject(method = "mouseClicked", at = @At("RETURN"))
    private void bundleupgrade$afterMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        boolean startedRightDrag = !this.bundleupgrade$wasCursorDragging
                && this.cursorDragging
                && this.heldButtonCode == GLFW.GLFW_MOUSE_BUTTON_RIGHT;
        ItemStack cursorStack = this.handler.getCursorStack();
        if (startedRightDrag && bundleupgrade$isBundle(cursorStack)) {
            Slot startSlot = this.getSlotAt(mouseX, mouseY);
            this.bundleupgrade$bundleDragArmed = true;
            this.bundleupgrade$bundleDragStartSlot = startSlot;
            this.bundleupgrade$extractMode = startSlot == null || !startSlot.hasStack();
            this.bundleupgrade$bundleDragSlots.clear();
        } else if (!this.cursorDragging) {
            this.bundleupgrade$resetBundleDrag();
        }
    }

    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void bundleupgrade$onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> cir) {
        if (!this.bundleupgrade$bundleDragArmed) {
            return;
        }
        ItemStack cursorStack = this.handler.getCursorStack();
        if (!this.cursorDragging
                || button != GLFW.GLFW_MOUSE_BUTTON_RIGHT
                || !bundleupgrade$isBundle(cursorStack)) {
            this.bundleupgrade$resetBundleDrag();
            return;
        }

        Slot startSlot = this.bundleupgrade$bundleDragStartSlot;
        this.bundleupgrade$bundleDragStartSlot = null;
        if (startSlot != null) {
            this.bundleupgrade$interactWithSlot(startSlot);
        }
        this.bundleupgrade$interactWithSlot(this.getSlotAt(mouseX, mouseY));

        this.cursorDragSlots.clear();
        cir.setReturnValue(true);
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void bundleupgrade$onMouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (!this.bundleupgrade$bundleDragArmed) {
            return;
        }
        boolean handledAny = !this.bundleupgrade$bundleDragSlots.isEmpty();
        boolean sameButton = button == this.heldButtonCode;
        this.bundleupgrade$resetBundleDrag();

        if (handledAny && sameButton) {
            this.cursorDragging = false;
            this.cursorDragSlots.clear();
            cir.setReturnValue(true);
        }
    }

    @Unique
    private void bundleupgrade$interactWithSlot(@Nullable Slot slot) {
        if (slot == null || this.bundleupgrade$bundleDragSlots.contains(slot)) {
            return;
        }
        ItemStack bundle = this.handler.getCursorStack();
        if (!bundleupgrade$isBundle(bundle)) {
            return;
        }

        if (this.bundleupgrade$extractMode) {
            BundleContentsComponent contents = bundle.get(DataComponentTypes.BUNDLE_CONTENTS);
            if (slot.hasStack() || contents == null || contents.isEmpty()) {
                return;
            }
            if (!this.handler.canInsertIntoSlot(slot)) {
                return;
            }
        } else {
            if (!slot.hasStack() || !slot.getStack().getItem().canBeNested()) {
                return;
            }
        }

        this.bundleupgrade$bundleDragSlots.add(slot);
        this.onMouseClick(slot, slot.id, GLFW.GLFW_MOUSE_BUTTON_RIGHT, SlotActionType.PICKUP);
    }

    @Unique
    private void bundleupgrade$resetBundleDrag() {
        this.bundleupgrade$bundleDragArmed = false;
        this.bundleupgrade$bundleDragStartSlot = null;
        this.bundleupgrade$bundleDragSlots.clear();
    }

    @Unique
    private static boolean bundleupgrade$isBundle(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof BundleItem && stack.contains(DataComponentTypes.BUNDLE_CONTENTS);
    }
}
