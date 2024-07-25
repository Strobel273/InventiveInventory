package net.origins.inventive_inventory.config.screens;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.origins.inventive_inventory.InventiveInventory;
import net.origins.inventive_inventory.config.ConfigManager;
import net.origins.inventive_inventory.config.accessibility.Tooltips;
import net.origins.inventive_inventory.config.enums.Configurable;
import net.origins.inventive_inventory.config.enums.automatic_refilling.*;
import net.origins.inventive_inventory.config.enums.profiles.ProfilesLoadMode;
import net.origins.inventive_inventory.config.enums.profiles.ProfilesLockedSlotsBehaviours;
import net.origins.inventive_inventory.config.enums.profiles.ProfilesStatus;
import net.origins.inventive_inventory.config.enums.sorting.SortingBehaviours;
import net.origins.inventive_inventory.config.enums.sorting.SortingModes;
import net.origins.inventive_inventory.config.enums.sorting.SortingStatus;
import net.origins.inventive_inventory.config.screens.widgets.CustomTextWidget;
import net.origins.inventive_inventory.features.profiles.gui.ProfilesConfigScreen;

import java.util.Objects;
import java.util.function.Function;


public class ConfigScreen extends GameOptionsScreen {

    public ConfigScreen(Screen parent) {
        super(parent, InventiveInventory.getClient().options, Text.of("Inventive Inventory Options"));
        Tooltips.init();
    }

    @Override
    protected void addOptions() {
        if (client == null || body == null) return;
        this.addTitle("Sorting");
        this.addWidget(SortingStatus.class, ConfigManager.SORTING);
        this.addWidget(SortingModes.class, ConfigManager.S_MODE);
        this.addWidget(SortingBehaviours.class, ConfigManager.S_BEHAVIOUR);

        this.addTitle("Automatic Refilling");
        this.addWidget(AutomaticRefillingStatus.class, ConfigManager.AUTOMATIC_REFILLING);
        this.addWidget(AutomaticRefillingModes.class, ConfigManager.AR_MODE);
        this.addWidget(AutomaticRefillingToolBreakingBehaviour.class, ConfigManager.AR_TOOL_BREAKING_BEHAVIOUR);
        this.addWidget(AutomaticRefillingToolBehaviours.class, ConfigManager.AR_TOOL_BEHAVIOUR);
        this.addWidget(AutomaticRefillingLockedSlotsBehaviours.class, ConfigManager.AR_LS_BEHAVIOUR);

        this.addTitle("Profiles");
        this.addWidget(ProfilesStatus.class, ConfigManager.PROFILES);
        this.body.addWidgetEntry(new CustomTextWidget(Text.of("Config Screen:"), this.client.textRenderer), ButtonWidget.builder(Text.of("Config Profiles..."), button -> this.client.setScreen(new ProfilesConfigScreen(this))).build());
        this.addWidget(ProfilesLoadMode.class, ConfigManager.P_LOAD_MODE);
        this.addWidget(ProfilesLockedSlotsBehaviours.class, ConfigManager.P_LS_BEHAVIOUR);
    }

    private void addTitle(String title) {
        if (this.body == null || client == null) return;
        TextWidget text = new CustomTextWidget(Text.of(title).copy().setStyle(Style.EMPTY.withBold(true)), client.textRenderer);
        text.setWidth(310);
        this.body.addWidgetEntry(text, null);
    }

    private <E extends Enum<E> & Configurable> void addWidget(Class<E> enumClass, Configurable config) {
        if (this.body == null || client == null) return;
        CyclingButtonWidget<Configurable> button = createButton(enumClass, config);
        this.body.addWidgetEntry(createTextWidget(config), button);
    }

    private TextWidget createTextWidget(Configurable config) {
        return new CustomTextWidget(Text.of(config.getDisplayName() + ":"), Objects.requireNonNull(client).textRenderer).alignCenter();
    }

    private <E extends Enum<E> & Configurable> CyclingButtonWidget<Configurable> createButton(Class<E> enumClass, Configurable config) {
        return CyclingButtonWidget.builder((Function<Configurable, Text>) configurable -> Text.of(configurable.getName()).copy().setStyle(configurable.getStyle()))
                .tooltip(SimpleOption.constantTooltip(config.getTooltip()))
                .omitKeyText()
                .values(enumClass.getEnumConstants()).initially(config)
                .build(Text.empty(), (button, value) -> ConfigManager.onConfigChange(value));
    }
}
