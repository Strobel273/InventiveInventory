package net.strobel.inventive_inventory.keybindfix;


public interface MixinIKeyBindingDisplay {
    String main$getDisplayName();

    void main$setDisplayName(String name);

    void main$resetDisplayName();
}
