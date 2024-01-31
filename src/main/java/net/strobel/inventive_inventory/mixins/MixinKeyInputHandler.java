package net.strobel.inventive_inventory.mixins;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.screen.PlayerScreenHandler;
import net.strobel.inventive_inventory.InventiveInventoryClient;
import net.strobel.inventive_inventory.handler.KeyInputHandler;
import net.strobel.inventive_inventory.features.locked_slots.LockedSlots;
import net.strobel.inventive_inventory.features.sorting.Sorter;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public class MixinKeyInputHandler {
    @Inject(method = "keyPressed", at = @At("HEAD"))
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (InventiveInventoryClient.getPlayer() != null) {
            if (KeyInputHandler.sortInventoryKey.matchesKey(keyCode, scanCode)) {
                if ((Object) this instanceof HandledScreen<?>) {
                    Sorter.sort();
                }
            } else if (KeyInputHandler.lockSlotKey.matchesKey(keyCode, scanCode)) {
                if (InventiveInventoryClient.getScreenHandler() instanceof PlayerScreenHandler) {
                    LockedSlots.setLockingSlot(true);
                }
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        long window = InventiveInventoryClient.getClient().getWindow().getHandle();

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_ALT) == GLFW.GLFW_RELEASE && LockedSlots.isLockingSlot()) {
            LockedSlots.setLockingSlot(false);
        }
    }
}
