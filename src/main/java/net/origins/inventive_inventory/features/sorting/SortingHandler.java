package net.origins.inventive_inventory.features.sorting;

import net.minecraft.item.ItemStack;
import net.origins.inventive_inventory.config.ConfigManager;
import net.origins.inventive_inventory.config.enums.sorting.SortingStatus;
import net.origins.inventive_inventory.util.InteractionHandler;
import net.origins.inventive_inventory.util.mouse.MouseLocation;
import net.origins.inventive_inventory.util.slots.ContainerSlots;
import net.origins.inventive_inventory.util.slots.PlayerSlots;
import net.origins.inventive_inventory.util.slots.SlotRange;
import net.origins.inventive_inventory.util.slots.SlotTypes;

public class SortingHandler {

    public static void sort() {
        if (ConfigManager.SORTING == SortingStatus.DISABLED) return;
        SlotRange slotRange = MouseLocation.isOverInventory() ? PlayerSlots.get().exclude(SlotTypes.LOCKED_SLOT) : ContainerSlots.get();
        ItemStack targetStack = InteractionHandler.getCursorStack().copy();
        Merger.mergeItemStacks(slotRange);
        Sorter.sortItemStacks(slotRange, targetStack);
    }
}
