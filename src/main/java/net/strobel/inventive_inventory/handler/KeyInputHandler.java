package net.strobel.inventive_inventory.handler;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.strobel.inventive_inventory.InventiveInventory;
import net.strobel.inventive_inventory.config.ConfigManager;
import net.strobel.inventive_inventory.config.Mode;
import net.strobel.inventive_inventory.features.automatic_refilling.AutomaticRefillingHandler;
import net.strobel.inventive_inventory.features.profiles.ProfileHandler;
import net.strobel.inventive_inventory.keybindfix.IKeyBindingDisplay;
import net.strobel.inventive_inventory.util.FileHandler;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
    public static final String INVENTIVE_INVENTORY_CATEGORY = "key.inventive_inventory.category.inventive_inventory";
    public static final String INVENTIVE_INVENTORY_PROFILES_CATEGORY = "key.inventive_inventory.category.inventive_inventory_profiles";
    private static final String KEY_ADVANCED_OPERATION = "key.inventive_inventory.advanced_operation";
    private static final String KEY_PROFILE_SAVING = "key.inventive_inventory.profile_saving";
    private static final String KEY_PROFILE_LOADING = "key.inventive_inventory.profile_loading";
    private static final String KEY_SORT_INVENTORY = "key.inventive_inventory.sort_inventory";
    private static final String KEY_DEBUGGING = "key.inventive_inventory.debugging";
    public static KeyBinding debuggingKey;
    public static KeyBinding advancedOperationKey;
    public static KeyBinding sortInventoryKey;
    public static KeyBinding profileSavingKey;
    public static KeyBinding profileLoadingKey;

    public static KeyBinding[] profileKeys = new KeyBinding[9];
    private static final boolean[] executed = new boolean[9];


    public static void register() {
        advancedOperationKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_ADVANCED_OPERATION,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT,
                INVENTIVE_INVENTORY_CATEGORY
        ));
        sortInventoryKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_SORT_INVENTORY,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                INVENTIVE_INVENTORY_CATEGORY
        ));
        profileSavingKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_PROFILE_SAVING,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_CONTROL,
                INVENTIVE_INVENTORY_PROFILES_CATEGORY
        ));
        profileLoadingKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_PROFILE_LOADING,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT,
                INVENTIVE_INVENTORY_PROFILES_CATEGORY
        ));
        for (int i = 0; i < 9; i++) {
            profileKeys[i] = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    "key.inventive_inventory.profile_" + (i + 1),
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_1 + i,
                    INVENTIVE_INVENTORY_PROFILES_CATEGORY
            ));
        }
    }

    public static void registerKeyInputs() {
        ClientTickEvents.START_CLIENT_TICK.register(AutomaticRefillingHandler::captureCurrentState);
        ClientTickEvents.END_CLIENT_TICK.register(KeyInputHandler::automaticRefilling);
        ClientTickEvents.END_CLIENT_TICK.register(KeyInputHandler::saveProfile);
        ClientTickEvents.END_CLIENT_TICK.register(KeyInputHandler::loadProfile);
    }

    private static void automaticRefilling(MinecraftClient client) {
        if (client.currentScreen == null && client.player != null) {
            KeyBinding useKey = client.options.useKey;
            if (ConfigManager.AUTOMATIC_REFILLING == Mode.STANDARD) {
                if (advancedOperationKey.isPressed()) {
                    AutomaticRefillingHandler.run();
                } else AutomaticRefillingHandler.reset();
            } else if (ConfigManager.AUTOMATIC_REFILLING == Mode.INVERTED) {
                if (!advancedOperationKey.isPressed()) {
                    if (useKey.isPressed()) {
                        AutomaticRefillingHandler.run();
                    }
                } else AutomaticRefillingHandler.reset();
            }
        }
    }

    private static void saveProfile(MinecraftClient ignored) {
        if (profileSavingKey.isPressed()) {
            for (int i = 0; i < profileKeys.length; i++) {
                if (profileKeys[i].isPressed() && !executed[i]) {
                    KeyBinding keyBinding = profileKeys[i];
                    String name = ((IKeyBindingDisplay) keyBinding).main$getDisplayName();
                    ProfileHandler.save(name, keyBinding.getBoundKeyLocalizedText().getString());
                    executed[i] = true;
                } else if (!profileKeys[i].isPressed()) {
                    executed[i] = false;
                }
            }
        }
    }

    private static void loadProfile(MinecraftClient ignored) {
        if (profileLoadingKey.isPressed()) {
            for (int i = 0; i < profileKeys.length; i++) {
                if (profileKeys[i].isPressed() && !executed[i]) {
                    executed[i] = true;
                    JsonObject profilesFile = FileHandler.getJsonFile(ProfileHandler.PROFILES_PATH);
                    for (String profileKey : profilesFile.keySet()) {
                        JsonElement keybind = FileHandler.getJsonObject(ProfileHandler.PROFILES_PATH, profileKey).get("keybind");
                        if (keybind.getAsString().equals(String.valueOf(i + 1))) {
                            ProfileHandler.load(profileKey);
                            return;
                        }
                    }
                    Text text = Text.of("Profile for Keybind '" + (i + 1) + "' not found!").copy().setStyle(Style.EMPTY.withColor(Formatting.RED).withBold(true));
                    InventiveInventory.getPlayer().sendMessage(text, true);
                } else if (!profileKeys[i].isPressed()) {
                    executed[i] = false;
                }
            }
        }
    }
}
