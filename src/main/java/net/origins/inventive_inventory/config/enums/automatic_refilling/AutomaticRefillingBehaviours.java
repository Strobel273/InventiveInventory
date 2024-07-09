package net.origins.inventive_inventory.config.enums.automatic_refilling;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.origins.inventive_inventory.config.ConfigManager;
import net.origins.inventive_inventory.config.Configurable;

public enum AutomaticRefillingBehaviours implements Configurable {
    IGNORE_LOCKED_SLOTS("Ignore Locked Slots"),
    USE_LOCKED_SLOTS("Use Locked Slots");

    private static final String configKey = "Automatic Refilling Behaviour";
    private final String name;

    AutomaticRefillingBehaviours(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void toggle() {
        ConfigManager.AR_LS_BEHAVIOUR = values()[(this.ordinal() + 1) % values().length];
    }

    public static Configurable get(JsonObject config) {
        JsonElement element = config.get(configKey);
        if (element != null) {
            for (Configurable configurable : values()) {
                if (configurable.getName().equals(element.getAsString())) return configurable;
            }
        }
        return values()[0];
    }
}
