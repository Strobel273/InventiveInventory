package net.strobel.inventive_inventory.mixins;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.strobel.inventive_inventory.features.sorting.SortingHandler;
import net.strobel.inventive_inventory.handler.AdvancedOperationHandler;
import net.strobel.inventive_inventory.handler.KeyInputHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public class MixinKeyInputHandler {
    @Inject(method = "keyPressed", at = @At("HEAD"))
    private void onKeyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (KeyInputHandler.advancedOperationKey.matchesKey(keyCode, scanCode)) {
            AdvancedOperationHandler.press();
        }
        if (KeyInputHandler.sortInventoryKey.matchesKey(keyCode, scanCode)) {
            SortingHandler.sort();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (AdvancedOperationHandler.isReleased()) {
            AdvancedOperationHandler.release();
        }
    }

    @Inject(method = "onMouseClick(I)V", at = @At("HEAD"))
    private void onMouseClick(int button, CallbackInfo ci) {
        if (KeyInputHandler.advancedOperationKey.matchesMouse(button)) {
            AdvancedOperationHandler.press();
        }
        if (KeyInputHandler.sortInventoryKey.matchesMouse(button)) {
            SortingHandler.sort();
        }
    }
}
