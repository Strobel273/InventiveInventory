package net.strobel.inventive_inventory.keybindfix.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.strobel.inventive_inventory.handler.AdvancedOperationHandler;
import net.strobel.inventive_inventory.handler.KeyInputHandler;
import net.strobel.inventive_inventory.keybindfix.KeybindFixer;
import net.strobel.inventive_inventory.keybindfix.MixinIKeyBindingDisplay;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(value = KeyBinding.class, priority = 10000)
public abstract class MixinKeyBinding implements MixinIKeyBindingDisplay {
    @Unique
    private String displayName;
    @Final
    @Shadow
    private static Map<String, KeyBinding> KEYS_BY_ID;
    @Final
    @Shadow
    private static Map<InputUtil.Key, KeyBinding> KEY_TO_BINDINGS;
    @Shadow
    private InputUtil.Key boundKey;
    @Unique
    private static final KeybindFixer keybindFixer = new KeybindFixer();

    @Inject(method = "onKeyPressed", at = @At(value = "HEAD"), cancellable = true)
    private static void onKeyPressedFixed(InputUtil.Key key, CallbackInfo ci) {
        if (keybindFixer.checkHotbarKeys(key)) ci.cancel();
    }

    @Inject(method = "onKeyPressed", at = @At(value = "TAIL"))
    private static void onKeyPressedFixed(InputUtil.Key key, CallbackInfo ci, @Local KeyBinding original) {
        keybindFixer.onKeyPressed(key, original, KEY_TO_BINDINGS.get(key));
    }

    @Inject(method = "setKeyPressed", at = @At(value = "TAIL"))
    private static void setKeyPressedFixed(InputUtil.Key key, boolean pressed, CallbackInfo ci, @Local KeyBinding original) {
        keybindFixer.setKeyPressed(key, pressed, original, KEY_TO_BINDINGS.get(key));
    }

    @Inject(method = "updateKeysByCode", at = @At(value = "TAIL"))
    private static void updateByCodeToMultiMap(CallbackInfo ci) {
        keybindFixer.clearMap();
        for (KeyBinding keyBinding : KEYS_BY_ID.values()) {
            keybindFixer.putKey(((MixinIKeyBindingAccessor) keyBinding).getBoundKey(), keyBinding);
        }
    }

    @Inject(method = "<init>(Ljava/lang/String;Lnet/minecraft/client/util/InputUtil$Type;ILjava/lang/String;)V", at = @At(value = "TAIL"))
    private void putToMultiMap(String translationKey, InputUtil.Type type, int code, String category, CallbackInfo ci) {
        keybindFixer.putKey(boundKey, (KeyBinding) (Object) this);
    }

    @Inject(method = "setBoundKey", at = @At("TAIL"))
    private void onSetBoundKey(InputUtil.Key boundKey, CallbackInfo ci) {
        if ((Object) this == KeyInputHandler.advancedOperationKey) {
            AdvancedOperationHandler.setBoundKey(KeyBindingHelper.getBoundKeyOf(KeyInputHandler.advancedOperationKey));
        }
    }

    @Inject(method = "getTranslationKey", at = @At("HEAD"), cancellable = true)
    private void onGetTranslationKey(CallbackInfoReturnable<String> cir) {
        if (this.displayName != null) {
            cir.setReturnValue("Profile: " + this.displayName);
            cir.cancel();
        }
    }

    @Override
    public String main$getDisplayName() {
        return this.displayName;
    }

    @Override
    public void main$setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public void main$resetDisplayName() {
        this.displayName = null;
    }
}