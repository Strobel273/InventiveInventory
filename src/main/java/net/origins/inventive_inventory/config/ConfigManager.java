package net.origins.inventive_inventory.config;

import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.origins.inventive_inventory.InventiveInventory;
import net.origins.inventive_inventory.config.sorting.SortingBehaviours;
import net.origins.inventive_inventory.config.sorting.SortingModes;
import net.origins.inventive_inventory.features.locked_slots.LockedSlotsHandler;
import net.origins.inventive_inventory.util.FileHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final String CONFIG_FILE = "config.json";

    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve(InventiveInventory.MOD_ID);
    private static final Path CONFIG_FILE_PATH = CONFIG_PATH.resolve(CONFIG_FILE);

    public static SortingBehaviours SORTING_BEHAVIOUR;
    public static SortingModes SORTING_MODE;

    public static void init() throws IOException {
        Files.createDirectories(CONFIG_PATH);
        FileHandler.createFile(CONFIG_FILE_PATH);
        FileHandler.createFile(LockedSlotsHandler.LOCKED_SLOTS_PATH);
        initConfig();
        save();
    }

    public static void save() {
        JsonObject config = new JsonObject();
        config.addProperty("Sorting Behaviour", SORTING_BEHAVIOUR.toString());
        config.addProperty("Sorting Mode", SORTING_MODE.toString());

        FileHandler.write(CONFIG_FILE_PATH, config);
    }

    private static void initConfig() {
        JsonObject config = FileHandler.get(CONFIG_FILE_PATH).getAsJsonObject();
        SORTING_BEHAVIOUR = SortingBehaviours.get(config);
        SORTING_MODE = SortingModes.get(config);
    }
}