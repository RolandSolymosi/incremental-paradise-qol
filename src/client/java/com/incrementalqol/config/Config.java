package com.incrementalqol.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
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
    private int hudSize;
    @SerialEntry
    private double hudScale;
    @SerialEntry
    private boolean autoGZ;
    @SerialEntry
    private boolean isSortedByType;
    @SerialEntry
    private boolean autoSwapWardrobe;

    @SerialEntry
    private String combatWardrobeName = "Combat";
    @SerialEntry
    private int meleeWeaponSlot;
    @SerialEntry
    private int rangedWeaponSlot;

    @SerialEntry
    private String miningWardrobeName = "Mining";
    @SerialEntry
    private int miningWeaponSlot;
    @SerialEntry
    private String foragingWardrobeName = "Foraging";
    @SerialEntry
    private int foragingWeaponSlot;
    @SerialEntry
    private String farmingWardrobeName = "Farming";
    @SerialEntry
    private int farmingWeaponSlot;
    @SerialEntry
    private String fishingWardrobeName = "Fishing";
    @SerialEntry
    private int fishingWeaponSlot;

    public String getWardrobeNameToDefault(String defaultName){
        return switch (defaultName){
            case "Combat" -> combatWardrobeName;
            case "Mining" -> miningWardrobeName;
            case "Foraging" -> foragingWardrobeName;
            case "Farming" -> farmingWardrobeName;
            case "Fishing" -> fishingWardrobeName;
            default -> defaultName;
        };
    }

    public int getSlotToDefault(int defaultSlot){
        return switch (defaultSlot){
            case 0 -> meleeWeaponSlot;
            case 1 -> miningWeaponSlot;
            case 2 -> foragingWeaponSlot;
            case 3 -> farmingWeaponSlot;
            case 4 -> fishingWeaponSlot;
            case 5 -> rangedWeaponSlot;
            default -> defaultSlot;
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

    public boolean getAutoGZ() {
        return autoGZ;
    }

    public double getHudScale() {
        return hudScale;
    }

    public int getHudSize() {
        return hudSize;
    }

    public boolean getSortedByType() {
        return isSortedByType;
    }

    public boolean getAutoSwapWardrobe() {
        return autoSwapWardrobe;
    }


    public void setAutoGZ(boolean autoGZ) {
        this.autoGZ = autoGZ;
    }

    public void setHudBackground(boolean hudBackground) {
        this.hudBackground = hudBackground;
    }

    public void setHudPosX(int hudPosX) {
        this.hudPosX = hudPosX;
    }

    public void setHudPosY(int hudPosY) {
        this.hudPosY = hudPosY;
    }

    public void setHudScale(double hudScale) {
        this.hudScale = hudScale;
    }

    public void setHudSize(int hudSize) {
        this.hudSize = hudSize;
    }

    public void setSortedByType(boolean sortedByType) {
        this.isSortedByType = sortedByType;
    }

    public void setAutoSwapWardrobe(boolean isAutoSwap) {
        this.autoSwapWardrobe = isAutoSwap;
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
                                .option(Option.<Boolean>createBuilder()
                                        .name(Text.of("Order Tasks by Type"))
                                        .description(OptionDescription.of(Text.of("Order Tasks on Hud by Type, not by default order.")))
                                        .binding(false, () -> this.isSortedByType, newVal -> this.isSortedByType = newVal)
                                        .controller(BooleanControllerBuilder::create)
                                        .build())
                                .option(ButtonOption.createBuilder()
                                        .name(Text.of("Order Tasks by Type"))
                                        .description(OptionDescription.of(Text.of("Order Tasks on Hud by Type, not by default order.")))
                                        .action((t, o) -> MinecraftClient.getInstance().setScreen(new DraggableScreen(t)))
                                        .build())
                                .build())
                        .group(OptionGroup.createBuilder()
                                .name(Text.of("Task Category Auto Swap"))
                                .description(OptionDescription.of(Text.of("These are the basic overrides of task categories.")))
                                .option(Option.<String>createBuilder()
                                        .name(Text.of("Combat Wardrobe Name"))
                                        .description(OptionDescription.of(Text.of("The name of your wardrobe slot for Combat tasks")))
                                        .binding("Combat", () -> this.combatWardrobeName, newVal -> this.combatWardrobeName = newVal)
                                        .controller(StringControllerBuilder::create)
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Text.of("Melee Weapon HotBar Slot"))
                                        .description(OptionDescription.of(Text.of("The slot on the HotBar for your melee weapon. (0-8)")))
                                        .binding(0, () -> this.meleeWeaponSlot, newVal -> this.meleeWeaponSlot = newVal)
                                        .controller(o -> IntegerFieldControllerBuilder.create(o).min(0).max(8))
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Text.of("Ranged Weapon HotBar Slot"))
                                        .description(OptionDescription.of(Text.of("The slot on the HotBar for your ranged weapon. (0-8)")))
                                        .binding(5, () -> this.rangedWeaponSlot, newVal -> this.rangedWeaponSlot = newVal)
                                        .controller(o -> IntegerFieldControllerBuilder.create(o).min(0).max(8))
                                        .build())
                                .option(Option.<String>createBuilder()
                                        .name(Text.of("Mining Wardrobe Name"))
                                        .description(OptionDescription.of(Text.of("The name of your wardrobe slot for Mining tasks")))
                                        .binding("Mining", () -> this.miningWardrobeName, newVal -> this.miningWardrobeName = newVal)
                                        .controller(StringControllerBuilder::create)
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Text.of("Pickaxe HotBar Slot"))
                                        .description(OptionDescription.of(Text.of("The slot on the HotBar for your pickaxe. (0-8)")))
                                        .binding(1, () -> this.miningWeaponSlot, newVal -> this.miningWeaponSlot = newVal)
                                        .controller(o -> IntegerFieldControllerBuilder.create(o).min(0).max(8))
                                        .build())
                                .option(Option.<String>createBuilder()
                                        .name(Text.of("Foraging Wardrobe Name"))
                                        .description(OptionDescription.of(Text.of("The name of your wardrobe slot for Foraging tasks")))
                                        .binding("Foraging", () -> this.foragingWardrobeName, newVal -> this.foragingWardrobeName = newVal)
                                        .controller(StringControllerBuilder::create)
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Text.of("Axe HotBar Slot"))
                                        .description(OptionDescription.of(Text.of("The slot on the HotBar for your axe. (0-8)")))
                                        .binding(2, () -> this.foragingWeaponSlot, newVal -> this.foragingWeaponSlot = newVal)
                                        .controller(o -> IntegerFieldControllerBuilder.create(o).min(0).max(8))
                                        .build())
                                .option(Option.<String>createBuilder()
                                        .name(Text.of("Farming Wardrobe Name"))
                                        .description(OptionDescription.of(Text.of("The name of your wardrobe slot for Farming tasks")))
                                        .binding("Farming", () -> this.farmingWardrobeName, newVal -> this.farmingWardrobeName = newVal)
                                        .controller(StringControllerBuilder::create)
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Text.of("Hoe HotBar Slot"))
                                        .description(OptionDescription.of(Text.of("The slot on the HotBar for your hoe. (0-8)")))
                                        .binding(3, () -> this.farmingWeaponSlot, newVal -> this.farmingWeaponSlot = newVal)
                                        .controller(o -> IntegerFieldControllerBuilder.create(o).min(0).max(8))
                                        .build())
                                .option(Option.<String>createBuilder()
                                        .name(Text.of("Fishing Wardrobe Name"))
                                        .description(OptionDescription.of(Text.of("The name of your wardrobe slot for Mining tasks")))
                                        .binding("Fishing", () -> this.fishingWardrobeName, newVal -> this.fishingWardrobeName = newVal)
                                        .controller(StringControllerBuilder::create)
                                        .build())
                                .option(Option.<Integer>createBuilder()
                                        .name(Text.of("Fishing Rod HotBar Slot"))
                                        .description(OptionDescription.of(Text.of("The slot on the HotBar for your fishing rod. (0-8)")))
                                        .binding(4, () -> this.fishingWeaponSlot, newVal -> this.fishingWeaponSlot = newVal)
                                        .controller(o -> IntegerFieldControllerBuilder.create(o).min(0).max(8))
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
