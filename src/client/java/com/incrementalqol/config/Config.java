package com.incrementalqol.config;

import com.incrementalqol.common.data.Skills.*;
import com.incrementalqol.common.data.TaskCollection;
import com.incrementalqol.common.data.ToolType;
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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Config {
    @SerialEntry
    private boolean loggingEnabled;

    @SerialEntry
    private boolean balloonRopeEnabled;

    @SerialEntry
    private int interactionTimeout = 5;

    @SerialEntry
    private boolean interactionIsPriority = true;


    @SerialEntry
    private boolean isHudEnabled = true;
    @SerialEntry
    private boolean isHudDisabledDuringBossFight;
    @SerialEntry
    private double hudBackgroundOpacity = 0.3;
    @SerialEntry
    private int hudPosX;
    @SerialEntry
    private int hudPosY;
    @SerialEntry
    private double hudScale = 1.0;
    @SerialEntry
    private boolean isSortedByType;
    @SerialEntry
    private boolean autoSwapWardrobe;
    @SerialEntry
    private boolean autoSwapTools;
    @SerialEntry
    private boolean autoLevelUp;
    @SerialEntry
    private boolean warpOnAutoLevelUp = true;
    @SerialEntry
    private Color textColor = new Color(0xfcfcfc);
    @SerialEntry
    private Color worldColor = new Color(0x555555);
    @SerialEntry
    private Color taskColor = new Color(0xffaa00);
    @SerialEntry
    private Color socialiteColor = new Color(0x55ffff);
    @SerialEntry
    private Color progressColor = new Color(0x5555ff);
    @SerialEntry
    private Color targetColor = new Color(0xff5555);
    @SerialEntry
    private Color completeColor = new Color(0x00aa00);
    @SerialEntry
    private Color ticketColor = new Color(0x8845d1);

    @SerialEntry
    private int legendaryPxpValue = 75;
    @SerialEntry
    private int mythicPxpValue = 500;

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
    private boolean autoSkillLeveling;
    @SerialEntry
    public List<NormalCombatSkill> normalCombatSkills = new ArrayList<>();
    @SerialEntry
    public List<NormalMiningSkill> normalMiningSkills = new ArrayList<>();
    @SerialEntry
    public List<NormalForagingSkill> normalForagingSkills = new ArrayList<>();
    @SerialEntry
    public List<NormalFarmingSkill> normalFarmingSkills = new ArrayList<>();
    @SerialEntry
    public List<NormalSpearFishingSkill> normalSpearFishingSkills = new ArrayList<>();
    @SerialEntry
    public List<NormalSharpshootingSkill>  normalSharpshootingSkills = new ArrayList<>();
    @SerialEntry
    public List<NormalExcavationSkill>  normalExcavationSkills = new ArrayList<>();

    @SerialEntry
    public List<NightmareCombatSkill> nightmareCombatSkills = new ArrayList<>();
    @SerialEntry
    public List<NightmareMiningSkill> nightmareMiningSkills = new ArrayList<>();
    @SerialEntry
    public List<NightmareForagingSkill> nightmareForagingSkills = new ArrayList<>();
    @SerialEntry
    public List<NightmareFarmingSkill> nightmareFarmingSkills = new ArrayList<>();
    @SerialEntry
    public List<NightmareSpearFishingSkill> nightmareSpearFishingSkills = new ArrayList<>();
    @SerialEntry
    public List<NightmareSharpshootingSkill> nightmareSharpshootingSkills = new ArrayList<>();

    public static class Overrides {
        @SerialEntry
        private String warp;
        @SerialEntry
        private String wardrobe;
        @SerialEntry
        private String pet;
        @SerialEntry
        private Boolean skipTicketTask;

        public Overrides() {
            this.warp = "";
            this.wardrobe = "";
            this.pet = "";
            this.skipTicketTask = false;
        }

        public String getWarp() {
            return warp;
        }

        public void setWarp(String warp) {
            this.warp = warp;
        }

        public String getWardrobe() {
            return wardrobe;
        }

        public void setWardrobe(String wardrobe) {
            this.wardrobe = wardrobe;
        }

        public String getPet() {
            return pet;
        }

        public void setPet(String pet) {
            this.pet = pet;
        }

        public Boolean getSkipTicketTask() { return skipTicketTask; }

        public void setSkipTicketTask(Boolean skipTicketTask) { this.skipTicketTask = skipTicketTask; }
    }

    @SerialEntry
    public HashMap<TaskCollection.TaskTarget, Overrides> taskOverrides = new HashMap<>();

    public String getWardrobeNameToDefault(TaskCollection.TaskType defaultWardrobe) {
        return switch (defaultWardrobe) {
            case Combat -> combatWardrobeName;
            case Mining -> miningWardrobeName;
            case Foraging -> foragingWardrobeName;
            case Farming -> farmingWardrobeName;
            case Fishing -> fishingWardrobeName;
            case CombatFishing -> combatFishingWardrobeName;
            default -> defaultWardrobe.getString();
        };
    }

    public int getSlotToDefault(ToolType defaultToolType) {
        return switch (defaultToolType) {
            case Melee -> meleeWeaponSlot;
            case Pickaxe -> miningWeaponSlot;
            case Axe -> foragingWeaponSlot;
            case Hoe -> farmingWeaponSlot;
            case FishingRod -> fishingWeaponSlot;
            case Bow -> rangedWeaponSlot;
        };
    }

    public String getTaskOverrideWarp(TaskCollection.TaskTarget taskTarget) {
        return taskOverrides.computeIfAbsent(taskTarget, k -> new Overrides()).getWarp();
    }
    public void setTaskOverrideWarp(TaskCollection.TaskTarget taskTarget, String warp) {
        taskOverrides.computeIfAbsent(taskTarget, k -> new Overrides()).setWarp(warp);
    }

    public String getTaskOverrideWardrobe(TaskCollection.TaskTarget taskTarget) {
        return taskOverrides.computeIfAbsent(taskTarget, k -> new Overrides()).getWardrobe();
    }

    public void setTaskOverrideWardrobe(TaskCollection.TaskTarget taskTarget, String wardrobe) {
        taskOverrides.computeIfAbsent(taskTarget, k -> new Overrides()).setWardrobe(wardrobe);
    }

    public String getTaskOverridePet(TaskCollection.TaskTarget taskTarget) {
        return taskOverrides.computeIfAbsent(taskTarget, k -> new Overrides()).getPet();
    }

    public void setTaskOverridePet(TaskCollection.TaskTarget taskTarget, String pet) {
        taskOverrides.computeIfAbsent(taskTarget, k -> new Overrides()).setPet(pet);
    }

    public Boolean getTaskOverrideSkipTicketTask(TaskCollection.TaskTarget taskTarget) {
        return taskOverrides.computeIfAbsent(taskTarget, k -> new Overrides()).getSkipTicketTask();
    }

    public void setTaskOverrideSkipTicketTask(TaskCollection.TaskTarget taskTarget, Boolean val) {
        taskOverrides.computeIfAbsent(taskTarget, k -> new Overrides()).setSkipTicketTask(val);
    }

    public void setAllTaskOverrideSkipTicketTask(Boolean val) {
        for (TaskCollection.TaskTarget taskTarget : taskOverrides.keySet()) {
            setTaskOverrideSkipTicketTask(taskTarget, val);
        }
    }

    public int getLegendaryPxpValue() {
        return legendaryPxpValue;
    }

    public int getMythicPxpValue() {
        return mythicPxpValue;
    }

    public Color getTextColor() {
        return textColor;
    }

    public Color getWorldColor() {
        return worldColor;
    }

    public Color getTaskColor() {
        return taskColor;
    }

    public Color getSocialiteColor() {
        return socialiteColor;
    }

    public Color getProgressColor() {
        return progressColor;
    }

    public Color getTargetColor() {
        return targetColor;
    }

    public Color getCompleteColor() {
        return completeColor;
    }

    public Color getTicketColor() {
        return ticketColor;
    }

    public static ConfigClassHandler<Config> HANDLER = ConfigClassHandler.createBuilder(Config.class)
            .id(Identifier.of("incremental-qol", "config"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("incremental-qol.json5"))
                    .setJson5(true)
                    .build())
            .build();

    public Screen createScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .save(HANDLER::save)
                .title(Text.of("Incremental Qol"))
                .categories(Arrays.asList(
                        TaskCategory(),
                        SkillLeveling(),
                        TaskOverride(),
                        Others(),
                        Debug())
                )
                .build()
                .generateScreen(parent);
    }

    public ConfigCategory TaskCategory() {
        return ConfigCategory.createBuilder()
                .name(Text.of("Task"))
                .tooltip(Text.of("This category is about configuring the Task related settings, like warp customization and Hud."))
                .group(OptionGroup.createBuilder()
                        .name(Text.of("Task HUD configuration"))
                        .description(OptionDescription.of(Text.of("These are the basic overrides of task categories.")))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.of("Toggle HUD on and off"))
                                .description(OptionDescription.of(Text.of("Turn on and off the task HUD.")))
                                .binding(true, () -> this.isHudEnabled, newVal -> this.isHudEnabled = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.of("Turn off HUD during boss fights"))
                                .description(OptionDescription.of(Text.of("Hides the task HUD during boss fights.")))
                                .binding(false, () -> this.isHudDisabledDuringBossFight, newVal -> this.isHudDisabledDuringBossFight = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build())
                        .option(Option.<Double>createBuilder()
                                .name(Text.of("Task HUD background opacity"))
                                .description(OptionDescription.of(Text.of("Set the opacity of the HUD background.")))
                                .binding(0.3, () -> this.hudBackgroundOpacity, newVal -> this.hudBackgroundOpacity = newVal)
                                .controller(o -> DoubleSliderControllerBuilder.create(o).step(0.01).range(0.0, 1.0))
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
                        .name(Text.of("Task Warp Extras"))
                        .description(OptionDescription.of(Text.of("These are some extra functions for Task Warp.")))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.of("Toggle Auto LevelUp"))
                                .description(OptionDescription.of(Text.of("Turn on and off auto LevelUp when no task remained when using Next Warp.")))
                                .binding(false, () -> this.autoLevelUp, newVal -> this.autoLevelUp = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build())
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.of("Warp On Auto LevelUp"))
                                .description(OptionDescription.of(Text.of("Turn on and off if you warp on auto LevelUp")))
                                .binding(true, () -> this.warpOnAutoLevelUp, newVal -> this.warpOnAutoLevelUp = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build())
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Text.of("Task Category Auto Swap of Wardrobes"))
                        .description(OptionDescription.of(Text.of("These are the basic settings of task category based auto wardrobe swap.")))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.of("Toggle the Auto Swap of Wardrobe"))
                                .description(OptionDescription.of(Text.of("Turning on/off the auto swap of wardrobes functionality")))
                                .binding(false, () -> this.autoSwapWardrobe, newVal -> this.autoSwapWardrobe = newVal)
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
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.of("Toggle the Auto Swap of Tools"))
                                .description(OptionDescription.of(Text.of("Turning on/off the auto swap of tools functionality")))
                                .binding(false, () -> this.autoSwapTools, newVal -> this.autoSwapTools = newVal)
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
                .group(OptionGroup.createBuilder()
                        .name(Text.of("Task HUD Color configuration"))
                        .description(OptionDescription.of(Text.of("These allow you to set the colors of your hud. Press enter or esc to exit the color selection.")))
                        .option(Option.<Color>createBuilder()
                                .name(Text.of("Color of the base text"))
                                .description(OptionDescription.of(Text.of("The color of the base text.")))
                                .binding(new Color(0xfcfcfc), () -> this.textColor, newVal -> this.textColor = newVal)
                                .controller(ColorControllerBuilder::create)
                                .build())
                        .option(Option.<Color>createBuilder()
                                .name(Text.of("Color of the world text"))
                                .description(OptionDescription.of(Text.of("The color of the world text.")))
                                .binding(new Color(0x555555), () -> this.worldColor, newVal -> this.worldColor = newVal)
                                .controller(ColorControllerBuilder::create)
                                .build())
                        .option(Option.<Color>createBuilder()
                                .name(Text.of("Color of the task text"))
                                .description(OptionDescription.of(Text.of("The color of the task target.")))
                                .binding(new Color(0xffaa00), () -> this.taskColor, newVal -> this.taskColor = newVal)
                                .controller(ColorControllerBuilder::create)
                                .build())
                        .option(Option.<Color>createBuilder()
                                .name(Text.of("Color of the socialite text"))
                                .description(OptionDescription.of(Text.of("The highlight color used for socialite tasks.")))
                                .binding(new Color(0x55ffff), () -> this.socialiteColor, newVal -> this.socialiteColor = newVal)
                                .controller(ColorControllerBuilder::create)
                                .build())
                        .option(Option.<Color>createBuilder()
                                .name(Text.of("Color of the progress text"))
                                .description(OptionDescription.of(Text.of("The color used for the current task progress number.")))
                                .binding(new Color(0x5555ff), () -> this.progressColor, newVal -> this.progressColor = newVal)
                                .controller(ColorControllerBuilder::create)
                                .build())
                        .option(Option.<Color>createBuilder()
                                .name(Text.of("Color of the target text"))
                                .description(OptionDescription.of(Text.of("The color used for the target amount required.")))
                                .binding(new Color(0xff5555), () -> this.targetColor, newVal -> this.targetColor = newVal)
                                .controller(ColorControllerBuilder::create)
                                .build())
                        .option(Option.<Color>createBuilder()
                                .name(Text.of("Color of the completed text"))
                                .description(OptionDescription.of(Text.of("The text color used when a task is completed.")))
                                .binding(new Color(0x00aa00), () -> this.completeColor, newVal -> this.completeColor = newVal)
                                .controller(ColorControllerBuilder::create)
                                .build())
                        .option(Option.<Color>createBuilder()
                                .name(Text.of("Color of the ticket text"))
                                .description(OptionDescription.of(Text.of("The color used to highlight ticket tasks.")))
                                .binding(new Color(0x8845d1), () -> this.ticketColor, newVal -> this.ticketColor = newVal)
                                .controller(ColorControllerBuilder::create)
                                .build())
                        .collapsed(true)
                        .build())
                .build();
    }

    public ConfigCategory SkillLeveling() {
        return ConfigCategory.createBuilder()
                .name(Text.of("Skill Auto Leveling"))
                .tooltip(Text.of("This category is about configuring the order in which the skills should auto unlock on level up. The skills will expected to be leveled to the level of the number of their appearance from top to bottom."))
                .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Toggle Auto Skill Leveling"))
                        .description(OptionDescription.of(Text.of("Turn on and off the auto leveling function.")))
                        .binding(false, () -> this.autoSkillLeveling, newVal -> this.autoSkillLeveling = newVal)
                        .controller(BooleanControllerBuilder::create)
                        .build())
                // Normal section
                .group(ListOption.<NormalCombatSkill>createBuilder()
                        .name(Text.of("Combat [Normal]"))
                        .binding(this.normalCombatSkills, () -> this.normalCombatSkills, newVal -> this.normalCombatSkills = newVal)
                        .description(OptionDescription.of(Text.of("The combat skill level up order in Normal World.")))
                        .insertEntriesAtEnd(true)
                        .controller(o -> EnumDropdownControllerBuilder.create(o)
                                .formatValue(NormalCombatSkill::getDisplayName)
                        )
                        .initial(NormalCombatSkill.SweepingStrike)
                        .collapsed(true)
                        .build())
                .group(ListOption.<NormalMiningSkill>createBuilder()
                        .name(Text.of("Mining [Normal]"))
                        .binding(this.normalMiningSkills, () -> this.normalMiningSkills, newVal -> this.normalMiningSkills = newVal)
                        .description(OptionDescription.of(Text.of("The mining skill level up order in Normal World.")))
                        .insertEntriesAtEnd(true)
                        .controller(o -> EnumDropdownControllerBuilder.create(o)
                                .formatValue(NormalMiningSkill::getDisplayName)
                        )
                        .initial(NormalMiningSkill.Ricochet)
                        .collapsed(true)
                        .build())
                .group(ListOption.<NormalForagingSkill>createBuilder()
                        .name(Text.of("Foraging [Normal]"))
                        .binding(this.normalForagingSkills, () -> this.normalForagingSkills, newVal -> this.normalForagingSkills = newVal)
                        .description(OptionDescription.of(Text.of("The foraging skill level up order in Normal World.")))
                        .insertEntriesAtEnd(true)
                        .controller(o -> EnumDropdownControllerBuilder.create(o)
                                .formatValue(NormalForagingSkill::getDisplayName)
                        )
                        .initial(NormalForagingSkill.Timberstrike)
                        .collapsed(true)
                        .build())
                .group(ListOption.<NormalFarmingSkill>createBuilder()
                        .name(Text.of("Farming [Normal]"))
                        .binding(this.normalFarmingSkills, () -> this.normalFarmingSkills, newVal -> this.normalFarmingSkills = newVal)
                        .description(OptionDescription.of(Text.of("The farming skill level up order in Normal World.")))
                        .insertEntriesAtEnd(true)
                        .controller(o -> EnumDropdownControllerBuilder.create(o)
                                .formatValue(NormalFarmingSkill::getDisplayName)
                        )
                        .initial(NormalFarmingSkill.Harvester)
                        .collapsed(true)
                        .build())
                .group(ListOption.<NormalSpearFishingSkill>createBuilder()
                        .name(Text.of("Spear Fishing [Normal]"))
                        .binding(this.normalSpearFishingSkills, () -> this.normalSpearFishingSkills, newVal -> this.normalSpearFishingSkills = newVal)
                        .description(OptionDescription.of(Text.of("The spear fishing skill level up order in Normal World.")))
                        .insertEntriesAtEnd(true)
                        .controller(o -> EnumDropdownControllerBuilder.create(o)
                                .formatValue(NormalSpearFishingSkill::getDisplayName)
                        )
                        .initial(NormalSpearFishingSkill.SpoonBender)
                        .collapsed(true)
                        .build())
                .group(ListOption.<NormalSharpshootingSkill>createBuilder()
                        .name(Text.of("Sharpshooting [Normal]"))
                        .binding(this.normalSharpshootingSkills, () -> this.normalSharpshootingSkills, newVal -> this.normalSharpshootingSkills = newVal)
                        .description(OptionDescription.of(Text.of("The sharpshooting skill level up order in Normal World.")))
                        .insertEntriesAtEnd(true)
                        .controller(o -> EnumDropdownControllerBuilder.create(o)
                                .formatValue(NormalSharpshootingSkill::getDisplayName)
                        )
                        .initial(NormalSharpshootingSkill.ExplosiveArrow)
                        .collapsed(true)
                        .build())
                .group(ListOption.<NormalExcavationSkill>createBuilder()
                        .name(Text.of("Excavation [Normal]"))
                        .binding(this.normalExcavationSkills, () -> this.normalExcavationSkills, newVal -> this.normalExcavationSkills = newVal)
                        .description(OptionDescription.of(Text.of("The sharpshooting skill level up order in Normal World.")))
                        .insertEntriesAtEnd(true)
                        .controller(o -> EnumDropdownControllerBuilder.create(o)
                                .formatValue(NormalExcavationSkill::getDisplayName)
                        )
                        .initial(NormalExcavationSkill.SeismicResonance)
                        .collapsed(true)
                        .build())
                // Nightmare section
                .group(ListOption.<NightmareCombatSkill>createBuilder()
                        .name(Text.of("Combat [Nightmare]"))
                        .binding(this.nightmareCombatSkills, () -> this.nightmareCombatSkills, newVal -> this.nightmareCombatSkills = newVal)
                        .description(OptionDescription.of(Text.of("The combat skill level up order in Nightmare World.")))
                        .insertEntriesAtEnd(true)
                        .controller(o -> EnumDropdownControllerBuilder.create(o)
                                .formatValue(NightmareCombatSkill::getDisplayName)
                        )
                        .initial(NightmareCombatSkill.DevilsGambit)
                        .collapsed(true)
                        .build())
                .group(ListOption.<NightmareMiningSkill>createBuilder()
                        .name(Text.of("Mining [Nightmare]"))
                        .binding(this.nightmareMiningSkills, () -> this.nightmareMiningSkills, newVal -> this.nightmareMiningSkills = newVal)
                        .description(OptionDescription.of(Text.of("The mining skill level up order in Nightmare World.")))
                        .insertEntriesAtEnd(true)
                        .controller(o -> EnumDropdownControllerBuilder.create(o)
                                .formatValue(NightmareMiningSkill::getDisplayName)
                        )
                        .initial(NightmareMiningSkill.Shatterpoint)
                        .collapsed(true)
                        .build())
                .group(ListOption.<NightmareForagingSkill>createBuilder()
                        .name(Text.of("Foraging [Nightmare]"))
                        .binding(this.nightmareForagingSkills, () -> this.nightmareForagingSkills, newVal -> this.nightmareForagingSkills = newVal)
                        .description(OptionDescription.of(Text.of("The foraging skill level up order in Nightmare World.")))
                        .insertEntriesAtEnd(true)
                        .controller(o -> EnumDropdownControllerBuilder.create(o)
                                .formatValue(NightmareForagingSkill::getDisplayName)
                        )
                        .initial(NightmareForagingSkill.AxeJuggling)
                        .collapsed(true)
                        .build())
                .group(ListOption.<NightmareFarmingSkill>createBuilder()
                        .name(Text.of("Farming [Nightmare]"))
                        .binding(this.nightmareFarmingSkills, () -> this.nightmareFarmingSkills, newVal -> this.nightmareFarmingSkills = newVal)
                        .description(OptionDescription.of(Text.of("The farming skill level up order in Nightmare World.")))
                        .insertEntriesAtEnd(true)
                        .controller(o -> EnumDropdownControllerBuilder.create(o)
                                .formatValue(NightmareFarmingSkill::getDisplayName)
                        )
                        .initial(NightmareFarmingSkill.Landscaper)
                        .collapsed(true)
                        .build())
                .group(ListOption.<NightmareSpearFishingSkill>createBuilder()
                        .name(Text.of("Spear Fishing [Nightmare]"))
                        .binding(this.nightmareSpearFishingSkills, () -> this.nightmareSpearFishingSkills, newVal -> this.nightmareSpearFishingSkills = newVal)
                        .description(OptionDescription.of(Text.of("The spear fishing skill level up order in Nightmare World.")))
                        .insertEntriesAtEnd(true)
                        .controller(o -> EnumDropdownControllerBuilder.create(o)
                                .formatValue(NightmareSpearFishingSkill::getDisplayName)
                        )
                        .initial(NightmareSpearFishingSkill.FishSenses)
                        .collapsed(true)
                        .build())
                .group(ListOption.<NightmareSharpshootingSkill>createBuilder()
                        .name(Text.of("Sharpshooting [Nightmare]"))
                        .binding(this.nightmareSharpshootingSkills, () -> this.nightmareSharpshootingSkills, newVal -> this.nightmareSharpshootingSkills = newVal)
                        .description(OptionDescription.of(Text.of("The sharpshooting skill level up order in Nightmare World.")))
                        .insertEntriesAtEnd(true)
                        .controller(o -> EnumDropdownControllerBuilder.create(o)
                                .formatValue(NightmareSharpshootingSkill::getDisplayName)
                        )
                        .initial(NightmareSharpshootingSkill.PeaShooter)
                        .collapsed(true)
                        .build())
                .build();
    }

    public ConfigCategory TaskOverride() {
        var builder = ConfigCategory.createBuilder()
                .name(Text.of("Task override"))
                .tooltip(Text.of("This category allows you to override warps and wardrobes for tasks"));

        for (TaskCollection.TaskTarget taskTarget : TaskCollection.TaskTarget.values())
        {
            builder.group(GetTaskTargetGroup(taskTarget));
        }

        builder
            .group(OptionGroup.createBuilder()
                .name(Text.of("Ticket Task Skip Option"))
                .description(OptionDescription.of(Text.of("Set all ticket tasks at once. These will not automatically update and require you to close the Options window and reopen for these to take effect.")))
                .option(ButtonOption.createBuilder()
                        .name(Text.of("Skip all ticket tasks"))
                        .description(OptionDescription.of(Text.of("Sets all of the ticket tasks to be skipped.")))
                        .action((t, o) -> setAllTaskOverrideSkipTicketTask(true))
                        .build())
                .option(ButtonOption.createBuilder()
                        .name(Text.of("Do not skip all ticket tasks"))
                        .description(OptionDescription.of(Text.of("Sets all of the ticket tasks to not be skipped.")))
                        .action((t, o) -> setAllTaskOverrideSkipTicketTask(false))
                        .build())
                .collapsed(true)
                .build());

        return builder.build();
    }

    public OptionGroup GetTaskTargetGroup(TaskCollection.TaskTarget taskTarget) {
        String enumName = String.join(" ", taskTarget.name().split("(?<=[a-z])(?=[A-Z0-9])|(?<=[A-Z])(?=[A-Z][a-z])|(?<=[0-9])(?=[A-Za-z])"));
        return OptionGroup.createBuilder()
                .name(Text.of(enumName + " task override"))
                .option(Option.<String>createBuilder()
                        .name(Text.of(enumName + " warp override"))
                        .description(OptionDescription.of(Text.of("Which warp to go to instead")))
                        .binding("", () -> this.getTaskOverrideWarp(taskTarget), newVal -> this.setTaskOverrideWarp(taskTarget, newVal))
                        .controller(StringControllerBuilder::create)
                        .build())
                .option(Option.<String>createBuilder()
                        .name(Text.of(enumName + " wardrobe override"))
                        .description(OptionDescription.of(Text.of("Which wardrobe to use instead")))
                        .binding("", () -> this.getTaskOverrideWardrobe(taskTarget), newVal -> this.setTaskOverrideWardrobe(taskTarget, newVal))
                        .controller(StringControllerBuilder::create)
                        .build())
                .option(Option.<String>createBuilder()
                        .name(Text.of(enumName + " pet override"))
                        .description(OptionDescription.of(Text.of("Which pet to use")))
                        .binding("", () -> this.getTaskOverridePet(taskTarget), newVal -> this.setTaskOverridePet(taskTarget, newVal))
                        .controller(StringControllerBuilder::create)
                        .build())
                .option(Option.<Boolean>createBuilder()
                        .name(Text.of(enumName + " ticket task skip"))
                        .description(OptionDescription.of(Text.of("Whether to skip this task if it is a ticket task")))
                        .binding(false, () -> this.getTaskOverrideSkipTicketTask(taskTarget), newVal -> this.setTaskOverrideSkipTicketTask(taskTarget, newVal))
                        .controller(opt -> BooleanControllerBuilder.create(opt)
                                .valueFormatter(val -> val ? Text.of("Skipped") : Text.of("Not Skipped")))
                        .build())
                .collapsed(true)
                .build();
    }

    public ConfigCategory Others() {
        return ConfigCategory.createBuilder()
                .name(Text.of("Others"))
                .tooltip(Text.of("This category is smaller extra options not connected to any big category."))
                .option(Option.<Boolean>createBuilder()
                        .name(Text.of("Toggle balloon ropes for self."))
                        .description(OptionDescription.of(Text.of("Turn on and off the balloon rope attached to the player.")))
                        .binding(false, () -> this.balloonRopeEnabled, newVal -> this.balloonRopeEnabled = newVal)
                        .controller(BooleanControllerBuilder::create)
                        .build())
                .group(OptionGroup.createBuilder()
                        .name(Text.of("PXP Calculator settings"))
                        .description(OptionDescription.of(Text.of("These are the setting for the PXP Calculator.")))
                        .option(Option.<Integer>createBuilder()
                                .name(Text.of("Legendary Pet Value"))
                                .description(OptionDescription.of(Text.of("The PXP value of a legendary pet")))
                                .binding(75, () -> this.legendaryPxpValue, newVal -> this.legendaryPxpValue = newVal)
                                .controller(opt -> IntegerFieldControllerBuilder.create(opt)
                                        .min(50).max(500))
                                .build())
                        .option(Option.<Integer>createBuilder()
                                .name(Text.of("Mythic Pet Value"))
                                .description(OptionDescription.of(Text.of("The PXP value of a mythic pet")))
                                .binding(500, () -> this.mythicPxpValue, newVal -> this.mythicPxpValue = newVal)
                                .controller(opt -> IntegerFieldControllerBuilder.create(opt)
                                        .min(500).max(10000))
                                        .build())
                        .build())
                .build();
    }

    public ConfigCategory Debug() {
        return ConfigCategory.createBuilder()
                .name(Text.of("Development"))
                .tooltip(Text.of("This category is about configurations to help development and debug."))
                .group(OptionGroup.createBuilder()
                        .name(Text.of("Logging settings"))
                        .description(OptionDescription.of(Text.of("These are the Logging related settings.")))
                        .option(Option.<Boolean>createBuilder()
                                .name(Text.of("Toggle global logging"))
                                .description(OptionDescription.of(Text.of("Turn on and off the logging in the mod on all feature.")))
                                .binding(false, () -> this.loggingEnabled, newVal -> this.loggingEnabled = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build())
                        .build())
                .build();
    }

    public boolean getBalloonRopeEnabled() {
        return balloonRopeEnabled;
    }

    public boolean getLoggingEnabled() {
        return loggingEnabled;
    }

    public boolean getIsHudEnabled() { return isHudEnabled; }

    public boolean getIsHudDisabledDuringBossFight() { return isHudDisabledDuringBossFight; }

    public int getHudPosX() {
        return hudPosX;
    }

    public int getHudPosY() {
        return hudPosY;
    }

    public int getHudBackgroundOpacity() {
        return (int) (hudBackgroundOpacity * 255);
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

    public boolean getAutoSkillLeveling() {
        return autoSkillLeveling;
    }

    public boolean getAutoLevelUp() {
        return autoLevelUp;
    }

    public boolean getWarpOnAutoLevelUp() {
        return warpOnAutoLevelUp;
    }

    public void setHudPosX(int hudPosX) {
        this.hudPosX = hudPosX;
    }

    public void setHudPosY(int hudPosY) {
        this.hudPosY = hudPosY;
    }

    public int getInteractionTimeout() {
        return interactionTimeout;
    }

    public boolean isInteractionIsPriority() {
        return interactionIsPriority;
    }
}
