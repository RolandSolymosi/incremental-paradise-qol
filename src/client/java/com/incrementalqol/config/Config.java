package com.incrementalqol.config;

import com.incrementalqol.common.data.TaskCollection;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Config {

    @SerialEntry
    private boolean hudBackground;
    @SerialEntry
    private int hudPosX;
    @SerialEntry
    private int hudPosY;
    @SerialEntry
    private double hudScale;
    @SerialEntry
    private boolean isSortedByType;
    @SerialEntry
    private boolean autoSwapWardrobe;
    @SerialEntry
    private boolean autoSwapTools;

    @SerialEntry
    private String combatWardrobeName = "1";
    @SerialEntry
    private int meleeWeaponSlot = 0;
    @SerialEntry
    private int rangedWeaponSlot = 5;

    @SerialEntry
    private String miningWardrobeName = "2";
    @SerialEntry
    private int miningWeaponSlot = 1;
    @SerialEntry
    private String foragingWardrobeName = "3";
    @SerialEntry
    private int foragingWeaponSlot = 2;
    @SerialEntry
    private String farmingWardrobeName = "4";
    @SerialEntry
    private int farmingWeaponSlot = 3;
    @SerialEntry
    private String fishingWardrobeName = "5";
    @SerialEntry
    private String combatFishingWardrobeName = "6";
    @SerialEntry
    private int fishingWeaponSlot = 4;

    @SerialEntry
    private boolean hudConfigurationCollapsed = false;
    @SerialEntry
    private boolean autoWardrobeCollapsed = false;
    @SerialEntry
    private boolean autoToolCollapsed = false;

    public String getWardrobeNameToDefault(TaskCollection.TaskType defaultWardrobe) {
        return switch (defaultWardrobe) {
            case TaskCollection.TaskType.Combat -> combatWardrobeName;
            case TaskCollection.TaskType.Mining -> miningWardrobeName;
            case TaskCollection.TaskType.Foraging -> foragingWardrobeName;
            case TaskCollection.TaskType.Farming -> farmingWardrobeName;
            case TaskCollection.TaskType.Fishing -> fishingWardrobeName;
            case TaskCollection.TaskType.CombatFishing -> combatFishingWardrobeName;
            default -> defaultWardrobe.getString();
        };
    }

    public int getSlotToDefault(TaskCollection.ToolType defaultToolType) {
        return switch (defaultToolType) {
            case TaskCollection.ToolType.Melee -> meleeWeaponSlot;
            case TaskCollection.ToolType.Pickaxe -> miningWeaponSlot;
            case TaskCollection.ToolType.Axe -> foragingWeaponSlot;
            case TaskCollection.ToolType.Hoe -> farmingWeaponSlot;
            case TaskCollection.ToolType.FishingRod -> fishingWeaponSlot;
            case TaskCollection.ToolType.Bow -> rangedWeaponSlot;
        };
    }

    public int getHudPosX() {
        return hudPosX;
    }

    public int getHudPosY() {
        return hudPosY;
    }

    public boolean getHudBackground() {
        return hudBackground;
    }

    public double getHudScale() {
        return hudScale;
    }

    public boolean getSortedByType() {
        return isSortedByType;
    }

    public boolean getAutoSwapWardrobe() {
        return autoSwapWardrobe;
    }

    public boolean getAutoSwapTools() {
        return autoSwapTools;
    }

    public void setHudPosX(int hudPosX) {
        this.hudPosX = hudPosX;
    }

    public void setHudPosY(int hudPosY) {
        this.hudPosY = hudPosY;
    }

    public Screen createScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .save(HANDLER::save)
                .title(Text.of("Incremental Qol"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.of("Task"))
                        .tooltip(Text.of("This category is about configuring the Task related settings, like warp customization and Hud."))
                        .group(OptionGroup.createBuilder()
                                .name(Text.of("Task HUD configuration"))
                                .description(OptionDescription.of(Text.of("These are the basic overrides of task categories.")))
                                .collapsed(hudConfigurationCollapsed)
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.of("Toggle HUD background"))
                                        .description(OptionDescription.of(Text.of("Turn on and off the gray background of the task HUD.")))
                                        .binding(true, () -> this.hudBackground, newVal -> this.hudBackground = newVal)
                                        .controller(BooleanControllerBuilder::create)
                                        .build())
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.of("Order Tasks by Type"))
                                        .description(OptionDescription.of(Text.of("Order Tasks on Hud by Type, not by default order.")))
                                        .binding(false, () -> this.isSortedByType, newVal -> this.isSortedByType = newVal)
                                        .controller(BooleanControllerBuilder::create)
                                        .build())
                                .option(Option.<Double>createBuilder()
                                        .name(Text.of("Resize the HUD."))
                                        .description(OptionDescription.of(Text.of("Setting scale ration from 0.5x-2x with 0.1 steps.")))
                                        .binding(1.0, () -> this.hudScale, newVal -> this.hudScale = newVal)
                                        .controller(o -> DoubleSliderControllerBuilder.create(o).step(0.1).range(0.5, 2.0))
                                        .build())
                                .option(ButtonOption.createBuilder()
                                        .name(Text.of("Task HUD Position"))
                                        .description(OptionDescription.of(Text.of("Activate the function to move the task HUD. Press ESC to return here.")))
                                        .action((t, o) -> MinecraftClient.getInstance().setScreen(new DraggableScreen(t)))
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.of("Task Category Auto Swap of Wardrobes"))
                                .description(OptionDescription.of(Text.of("These are the basic settings of task category based auto wardrobe swap.")))
                                .collapsed(autoWardrobeCollapsed)
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.of("Toggle the Auto Swap of Wardrobe"))
                                        .description(OptionDescription.of(Text.of("Turning on/off the auto swap of wardrobes functionality")))
                                        .binding(true, () -> this.autoSwapWardrobe, newVal -> this.autoSwapWardrobe = newVal)
                                        .controller(BooleanControllerBuilder::create)
                                        .build())
                                .option(Option.<String>createBuilder()
                                        .name(Text.of("Combat Wardrobe Name"))
                                        .description(OptionDescription.of(Text.of("The name of your wardrobe slot for Combat tasks.")))
                                        .binding("1", () -> this.combatWardrobeName, newVal -> this.combatWardrobeName = newVal)
                                        .controller(StringControllerBuilder::create)
                                        .build())
                                .option(Option.<String>createBuilder()
                                        .name(Text.of("Mining Wardrobe Name"))
                                        .description(OptionDescription.of(Text.of("The name of your wardrobe slot for Mining tasks.")))
                                        .binding("2", () -> this.miningWardrobeName, newVal -> this.miningWardrobeName = newVal)
                                        .controller(StringControllerBuilder::create)
                                        .build())
                                .option(Option.<String>createBuilder()
                                        .name(Text.of("Foraging Wardrobe Name"))
                                        .description(OptionDescription.of(Text.of("The name of your wardrobe slot for Foraging tasks.")))
                                        .binding("3", () -> this.foragingWardrobeName, newVal -> this.foragingWardrobeName = newVal)
                                        .controller(StringControllerBuilder::create)
                                        .build())
                                .option(Option.<String>createBuilder()
                                        .name(Text.of("Farming Wardrobe Name"))
                                        .description(OptionDescription.of(Text.of("The name of your wardrobe slot for Farming tasks.")))
                                        .binding("4", () -> this.farmingWardrobeName, newVal -> this.farmingWardrobeName = newVal)
                                        .controller(StringControllerBuilder::create)
                                        .build())
                                .option(Option.<String>createBuilder()
                                        .name(Text.of("Fishing Wardrobe Name"))
                                        .description(OptionDescription.of(Text.of("The name of your wardrobe slot for Fishing tasks.")))
                                        .binding("5", () -> this.fishingWardrobeName, newVal -> this.fishingWardrobeName = newVal)
                                        .controller(StringControllerBuilder::create)
                                        .build())
                                .option(Option.<String>createBuilder()
                                        .name(Text.of("Combat Fishing Wardrobe Name"))
                                        .description(OptionDescription.of(Text.of("The name of your wardrobe slot for Combat fishing tasks (e.g.: Crabs)")))
                                        .binding("6", () -> this.combatFishingWardrobeName, newVal -> this.combatFishingWardrobeName = newVal)
                                        .controller(StringControllerBuilder::create)
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.of("Task Category Auto Swap of Tools"))
                                .description(OptionDescription.of(Text.of("These are the basic settings of task category based auto tool swap.")))
                                .collapsed(autoToolCollapsed)
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.of("Toggle the Auto Swap of Tools"))
                                        .description(OptionDescription.of(Text.of("Turning on/off the auto swap of tools functionality")))
                                        .binding(true, () -> this.autoSwapTools, newVal -> this.autoSwapTools = newVal)
                                        .controller(BooleanControllerBuilder::create)
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Text.of("Melee Weapon HotBar Slot"))
                                        .description(OptionDescription.of(Text.of("The slot on the HotBar for your melee weapon. (1-8)")))
                                        .binding(1, () -> this.meleeWeaponSlot + 1, newVal -> this.meleeWeaponSlot = newVal - 1)
                                        .controller(o -> IntegerSliderControllerBuilder.create(o).step(1).range(1, 8))
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Text.of("Ranged Weapon HotBar Slot"))
                                        .description(OptionDescription.of(Text.of("The slot on the HotBar for your ranged weapon. (1-8)")))
                                        .binding(6, () -> this.rangedWeaponSlot + 1, newVal -> this.rangedWeaponSlot = newVal - 1)
                                        .controller(o -> IntegerSliderControllerBuilder.create(o).step(1).range(1, 8))
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Text.of("Pickaxe HotBar Slot"))
                                        .description(OptionDescription.of(Text.of("The slot on the HotBar for your pickaxe. (1-8)")))
                                        .binding(2, () -> this.miningWeaponSlot + 1, newVal -> this.miningWeaponSlot = newVal - 1)
                                        .controller(o -> IntegerSliderControllerBuilder.create(o).step(1).range(1, 8))
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Text.of("Axe HotBar Slot"))
                                        .description(OptionDescription.of(Text.of("The slot on the HotBar for your axe. (1-8)")))
                                        .binding(3, () -> this.foragingWeaponSlot + 1, newVal -> this.foragingWeaponSlot = newVal - 1)
                                        .controller(o -> IntegerSliderControllerBuilder.create(o).step(1).range(1, 8))
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Text.of("Hoe HotBar Slot"))
                                        .description(OptionDescription.of(Text.of("The slot on the HotBar for your hoe. (1-8)")))
                                        .binding(4, () -> this.farmingWeaponSlot + 1, newVal -> this.farmingWeaponSlot = newVal - 1)
                                        .controller(o -> IntegerSliderControllerBuilder.create(o).step(1).range(1, 8))
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Text.of("Fishing Rod HotBar Slot"))
                                        .description(OptionDescription.of(Text.of("The slot on the HotBar for your fishing rod. (1-8)")))
                                        .binding(5, () -> this.fishingWeaponSlot + 1, newVal -> this.fishingWeaponSlot = newVal - 1)
                                        .controller(o -> IntegerSliderControllerBuilder.create(o).step(1).range(1, 8))
                                        .build())
                                .build())
                        .build())
                .build()
                .generateScreen(parent);
    }

    public static ConfigClassHandler<Config> HANDLER = ConfigClassHandler.createBuilder(Config.class)
            .id(Identifier.of("incremental-qol", "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("incremental-qol.json5"))
                    .setJson5(true)
                    .build())
            .build();
}
