package com.thaistoanf.bundleupgrade.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.Nullable;
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
    private @Nullable Slot getSlotAt(double mouseX, double mouseY) {
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
    private @Nullable Slot bundleupgrade$bundleDragStartSlot;

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void bundleupgrade$beforeMouseClicked(Click click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        this.bundleupgrade$wasCursorDragging = this.cursorDragging;
    }

    @Inject(method = "mouseClicked", at = @At("RETURN"))
    private void bundleupgrade$afterMouseClicked(Click click, boolean doubled, CallbackInfoReturnable<Boolean> cir) {
        boolean startedRightDrag = !this.bundleupgrade$wasCursorDragging
                && this.cursorDragging
                && this.heldButtonCode == InputUtil.GLFW_MOUSE_BUTTON_RIGHT;
        if (startedRightDrag && bundleupgrade$hasContents(this.handler.getCursorStack())) {
            this.bundleupgrade$bundleDragArmed = true;
            this.bundleupgrade$bundleDragStartSlot = this.getSlotAt(click.x(), click.y());
            this.bundleupgrade$bundleDragSlots.clear();
        } else if (!this.cursorDragging) {
            this.bundleupgrade$resetBundleDrag();
        }
    }

    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void bundleupgrade$onMouseDragged(Click click, double offsetX, double offsetY, CallbackInfoReturnable<Boolean> cir) {
        if (!this.bundleupgrade$bundleDragArmed) {
            return;
        }
        if (!this.cursorDragging
                || click.button() != InputUtil.GLFW_MOUSE_BUTTON_RIGHT
                || !bundleupgrade$isBundle(this.handler.getCursorStack())) {
            this.bundleupgrade$resetBundleDrag();
            return;
        }

        Slot startSlot = this.bundleupgrade$bundleDragStartSlot;
        this.bundleupgrade$bundleDragStartSlot = null;
        if (startSlot != null) {
            this.bundleupgrade$placeOneStack(startSlot);
        }
        this.bundleupgrade$placeOneStack(this.getSlotAt(click.x(), click.y()));

        this.cursorDragSlots.clear();
        cir.setReturnValue(true);
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void bundleupgrade$onMouseReleased(Click click, CallbackInfoReturnable<Boolean> cir) {
        if (!this.bundleupgrade$bundleDragArmed) {
            return;
        }
        boolean placedAny = !this.bundleupgrade$bundleDragSlots.isEmpty();
        boolean sameButton = click.button() == this.heldButtonCode;
        this.bundleupgrade$resetBundleDrag();

        if (placedAny && sameButton) {
            this.cursorDragging = false;
            this.cursorDragSlots.clear();
            cir.setReturnValue(true);
        }
    }

    @Unique
    private void bundleupgrade$placeOneStack(@Nullable Slot slot) {
        if (slot == null || slot.hasStack() || this.bundleupgrade$bundleDragSlots.contains(slot)) {
            return;
        }
        ItemStack bundle = this.handler.getCursorStack();
        BundleContentsComponent contents = bundle.get(DataComponentTypes.BUNDLE_CONTENTS);
        if (contents == null || contents.isEmpty()) {
            return;
        }
        int selected = contents.getSelectedStackIndex();
        ItemStack next = selected >= 0 && selected < contents.size() ? contents.get(selected) : contents.get(0);
        if (!slot.canInsert(next) || !this.handler.canInsertIntoSlot(slot)) {
            return;
        }

        this.bundleupgrade$bundleDragSlots.add(slot);
        this.onMouseClick(slot, slot.id, InputUtil.GLFW_MOUSE_BUTTON_RIGHT, SlotActionType.PICKUP);
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

    @Unique
    private static boolean bundleupgrade$hasContents(ItemStack stack) {
        if (!bundleupgrade$isBundle(stack)) {
            return false;
        }
        BundleContentsComponent contents = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
        return contents != null && !contents.isEmpty();
    }
}
