package com.incrementalclient.featues.Tasking;

import com.google.common.base.Suppliers;
import com.incrementalclient.common.data.DefaultWardrobe;
import com.incrementalclient.common.data.Tool;
import com.incrementalclient.common.data.World;
import com.incrementalclient.common.data.tasks.TaskType;
import com.incrementalclient.common.data.tasks.abstractions.NormalTask;
import com.incrementalclient.config.controllers.KeyBindController;
import com.incrementalclient.interfaces.Configurable;
import com.incrementalclient.interfaces.Listener;
import com.incrementalclient.services.*;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Supplier;

public class AutoSwapLoadout implements Configurable<AutoSwapLoadout.Configuration> {
    private final CommandHandler commandHandler;
    private final WorldMonitor worldMonitor;
    private final TaskMonitor taskMonitor;
    private final TaskingOverrides taskingOverrides;
    private final HotbarHandler hotbarHandler;

    private final AutoSwapLoadout.Configuration configuration = new Configuration();

    private final Supplier<List<OptionPiece>> options;
    private final KeyBindMonitor.KeyBindListener keyBindListener;

    public AutoSwapLoadout(
            KeyBindMonitor keyBindMonitor,
            CommandHandler commandHandler,
            WorldMonitor worldMonitor,
            TaskMonitor taskMonitor,
            TaskingOverrides taskingOverrides,
            HotbarHandler hotbarHandler,
            WarpNextHotkey warpNextHotkey
    ) {
        this.commandHandler = commandHandler;
        this.worldMonitor = worldMonitor;
        this.taskMonitor = taskMonitor;
        this.taskingOverrides = taskingOverrides;
        this.hotbarHandler = hotbarHandler;
        keyBindListener = new KeyBindMonitor.KeyBindListener(keyBindMonitor, new KeyBinding(
                "Swap Loadout for Next Task",
                InputUtil.Type.KEYSYM,
                configuration.keybind,
                "Incremental QOL"
        ), this::swap);

        warpNextHotkey.subscribe(new Listener.DefaultListener(this::swap));
        options = Suppliers.memoize(() -> List.of(
                Categories.Tasking.General.createConfig(0,
                        Option.<Integer>createBuilder()
                                .name(Text.literal("Swap Loadout for Next Task"))
                                .binding(
                                        Configuration.defaultKeybind,
                                        () -> configuration.keybind,
                                        v -> configuration.keybind = v
                                )
                                .controller((option) -> () -> new KeyBindController(option))
                                .build()),
                Categories.Tasking.Wardrobe.createConfig(0,
                        Option.<Boolean>createBuilder()
                                .name(Text.of("Toggle the Auto Swap of Wardrobe"))
                                .description(OptionDescription.of(Text.of("Turning on/off the auto swap of wardrobes functionality")))
                                .binding(Configuration.defaultEnableWardrobeSwap, () -> this.configuration.enableWardrobeSwap, newVal -> this.configuration.enableWardrobeSwap = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build()),
                Categories.Tasking.Wardrobe.createConfig(1,
                        Option.<String>createBuilder()
                                .name(Text.of("Combat Wardrobe Name"))
                                .description(OptionDescription.of(Text.of("The name of your wardrobe slot for Combat tasks.")))
                                .binding(Configuration.defaultCombatWardrobeName, () -> this.configuration.combatWardrobeName, newVal -> this.configuration.combatWardrobeName = newVal)
                                .controller(StringControllerBuilder::create)
                                .build()),
                Categories.Tasking.Wardrobe.createConfig(2,
                        Option.<String>createBuilder()
                                .name(Text.of("Mining Wardrobe Name"))
                                .description(OptionDescription.of(Text.of("The name of your wardrobe slot for Mining tasks.")))
                                .binding(Configuration.defaultMiningWardrobeName, () -> this.configuration.miningWardrobeName, newVal -> this.configuration.miningWardrobeName = newVal)
                                .controller(StringControllerBuilder::create)
                                .build()),
                Categories.Tasking.Wardrobe.createConfig(3,
                        Option.<String>createBuilder()
                                .name(Text.of("Foraging Wardrobe Name"))
                                .description(OptionDescription.of(Text.of("The name of your wardrobe slot for Foraging tasks.")))
                                .binding(Configuration.defaultForagingWardrobeName, () -> this.configuration.foragingWardrobeName, newVal -> this.configuration.foragingWardrobeName = newVal)
                                .controller(StringControllerBuilder::create)
                                .build()),
                Categories.Tasking.Wardrobe.createConfig(4,
                        Option.<String>createBuilder()
                                .name(Text.of("Farming Wardrobe Name"))
                                .description(OptionDescription.of(Text.of("The name of your wardrobe slot for Farming tasks.")))
                                .binding(Configuration.defaultFarmingWardrobeName, () -> this.configuration.farmingWardrobeName, newVal -> this.configuration.farmingWardrobeName = newVal)
                                .controller(StringControllerBuilder::create)
                                .build()),
                Categories.Tasking.Wardrobe.createConfig(5,
                        Option.<String>createBuilder()
                                .name(Text.of("Fishing Wardrobe Name"))
                                .description(OptionDescription.of(Text.of("The name of your wardrobe slot for Fishing tasks.")))
                                .binding(Configuration.defaultFishingWardrobeName, () -> this.configuration.fishingWardrobeName, newVal -> this.configuration.fishingWardrobeName = newVal)
                                .controller(StringControllerBuilder::create)
                                .build()),
                Categories.Tasking.Wardrobe.createConfig(6,
                        Option.<String>createBuilder()
                                .name(Text.of("Combat Fishing Wardrobe Name"))
                                .description(OptionDescription.of(Text.of("The name of your wardrobe slot for Combat fishing tasks (e.g.: Crabs)")))
                                .binding(Configuration.defaultCombatFishingWardrobeName, () -> this.configuration.combatFishingWardrobeName, newVal -> this.configuration.combatFishingWardrobeName = newVal)
                                .controller(StringControllerBuilder::create)
                                .build()),

                Categories.Tasking.Tools.createConfig(0,
                        Option.<Boolean>createBuilder()
                                .name(Text.of("Toggle the Auto Swap of Tools"))
                                .description(OptionDescription.of(Text.of("Turning on/off the auto swap of tools functionality")))
                                .binding(Configuration.defaultEnableToolSwap, () -> this.configuration.enableToolSwap, newVal -> this.configuration.enableToolSwap = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build()),
                Categories.Tasking.Tools.createConfig(1,
                        Option.<Integer>createBuilder()
                                .name(Text.of("Melee Weapon HotBar Slot"))
                                .description(OptionDescription.of(Text.of("The slot on the HotBar for your melee weapon. (1-8)")))
                                .binding(Configuration.defaultMeleeWeaponSlot + 1, () -> this.configuration.meleeWeaponSlot + 1, newVal -> this.configuration.meleeWeaponSlot = newVal - 1)
                                .controller(o -> IntegerSliderControllerBuilder.create(o).step(1).range(1, 8))
                                .build()),
                Categories.Tasking.Tools.createConfig(2,
                        Option.<Integer>createBuilder()
                                .name(Text.of("Ranged Weapon HotBar Slot"))
                                .description(OptionDescription.of(Text.of("The slot on the HotBar for your ranged weapon. (1-8)")))
                                .binding(Configuration.defaultRangedWeaponSlot + 1, () -> this.configuration.rangedWeaponSlot + 1, newVal -> this.configuration.rangedWeaponSlot = newVal - 1)
                                .controller(o -> IntegerSliderControllerBuilder.create(o).step(1).range(1, 8))
                                .build()),
                Categories.Tasking.Tools.createConfig(3,
                        Option.<Integer>createBuilder()
                                .name(Text.of("Pickaxe HotBar Slot"))
                                .description(OptionDescription.of(Text.of("The slot on the HotBar for your pickaxe. (1-8)")))
                                .binding(Configuration.defaultMiningWeaponSlot + 1, () -> this.configuration.miningWeaponSlot + 1, newVal -> this.configuration.miningWeaponSlot = newVal - 1)
                                .controller(o -> IntegerSliderControllerBuilder.create(o).step(1).range(1, 8))
                                .build()),
                Categories.Tasking.Tools.createConfig(4,
                        Option.<Integer>createBuilder()
                                .name(Text.of("Axe HotBar Slot"))
                                .description(OptionDescription.of(Text.of("The slot on the HotBar for your axe. (1-8)")))
                                .binding(Configuration.defaultForagingWeaponSlot + 1, () -> this.configuration.foragingWeaponSlot + 1, newVal -> this.configuration.foragingWeaponSlot = newVal - 1)
                                .controller(o -> IntegerSliderControllerBuilder.create(o).step(1).range(1, 8))
                                .build()),
                Categories.Tasking.Tools.createConfig(5,
                        Option.<Integer>createBuilder()
                                .name(Text.of("Hoe HotBar Slot"))
                                .description(OptionDescription.of(Text.of("The slot on the HotBar for your hoe. (1-8)")))
                                .binding(Configuration.defaultFarmingWeaponSlot + 1, () -> this.configuration.farmingWeaponSlot + 1, newVal -> this.configuration.farmingWeaponSlot = newVal - 1)
                                .controller(o -> IntegerSliderControllerBuilder.create(o).step(1).range(1, 8))
                                .build()),
                Categories.Tasking.Tools.createConfig(6,
                        Option.<Integer>createBuilder()
                                .name(Text.of("Fishing Rod HotBar Slot"))
                                .description(OptionDescription.of(Text.of("The slot on the HotBar for your fishing rod. (1-8)")))
                                .binding(Configuration.defaultFishingWeaponSlot + 1, () -> this.configuration.fishingWeaponSlot + 1, newVal -> this.configuration.fishingWeaponSlot = newVal - 1)
                                .controller(o -> IntegerSliderControllerBuilder.create(o).step(1).range(1, 8))
                                .build())
        ));
    }

    private void swap() {
        var nextUnfinishedTask = taskMonitor.getTaskList().stream().filter(p -> !p.isCompleted()).findFirst();
        if (nextUnfinishedTask.isPresent() && worldMonitor.currentWorld() != World.BossArenas) {
            var task = nextUnfinishedTask.get().getTask();
            if (task != null) {
                if (task.getDescriptor().taskType() != TaskType.Quest && task.getDescriptor().taskType() != TaskType.Tutorial) {
                    var taskDescriptor = task.getDescriptor();
                    if (taskDescriptor instanceof NormalTask normalTask) {
                        var override = taskingOverrides.getOverrides().get(task);
                        if (configuration.enableWardrobeSwap) {
                            var wardrobe = override != null && !override.wardrobe.isEmpty()
                                    ? override.wardrobe
                                    : normalTask.wardrobe() != null
                                    ? getWardrobeNameToDefault(normalTask.wardrobe())
                                    : null;
                            if (wardrobe != null) {
                                commandHandler.send("wardrobe " + wardrobe);
                            }
                            var pet = override != null && !override.pet.isEmpty()
                                    ? override.pet
                                    : null;
                            if (pet != null) {
                                commandHandler.send("pet " + pet);
                            }
                        }
                        var slot = getSlotToDefault((override == null || override.tool == Tool.Default ? normalTask.tool() : override.tool));
                        if (configuration.enableToolSwap) {
                            hotbarHandler.swapActiveHotbarSlot(slot);
                        }
                    }
                }
            }
        }
    }

    public String getWardrobeNameToDefault(DefaultWardrobe defaultWardrobe) {
        return switch (defaultWardrobe) {
            case Combat -> configuration.combatWardrobeName;
            case Mining -> configuration.miningWardrobeName;
            case Foraging -> configuration.foragingWardrobeName;
            case Farming -> configuration.farmingWardrobeName;
            case Fishing -> configuration.fishingWardrobeName;
            case CombatFishing -> configuration.combatFishingWardrobeName;
            default -> defaultWardrobe.getString();
        };
    }

    public int getSlotToDefault(Tool defaultToolType) {
        return switch (defaultToolType) {
            case Melee -> configuration.meleeWeaponSlot;
            case Pickaxe -> configuration.miningWeaponSlot;
            case Axe -> configuration.foragingWeaponSlot;
            case Hoe -> configuration.farmingWeaponSlot;
            case Spear -> configuration.fishingWeaponSlot;
            case Bow -> configuration.rangedWeaponSlot;
            default -> 0;
        };
    }

    @Override
    public String getJsonSection() {
        return "autoSwapLoadout";
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public List<OptionPiece> getOption() {
        return options.get();
    }

    @Override
    public void optionChanged() {
        keyBindListener.updateKeyBind(configuration.keybind);
    }

    public static class Configuration {
        private static final int defaultKeybind = GLFW.GLFW_KEY_R;
        private static final boolean defaultEnableWardrobeSwap = true;
        private static final boolean defaultEnableToolSwap = true;
        private static final String defaultCombatWardrobeName = "1";
        private static final int defaultMeleeWeaponSlot = 0;
        private static final int defaultRangedWeaponSlot = 5;
        private static final String defaultMiningWardrobeName = "2";
        private static final int defaultMiningWeaponSlot = 1;
        private static final String defaultForagingWardrobeName = "3";
        private static final int defaultForagingWeaponSlot = 2;
        private static final String defaultFarmingWardrobeName = "4";
        private static final int defaultFarmingWeaponSlot = 3;
        private static final String defaultFishingWardrobeName = "5";
        private static final String defaultCombatFishingWardrobeName = "6";
        private static final int defaultFishingWeaponSlot = 4;

        @SerialEntry
        public int keybind = defaultKeybind;
        @SerialEntry
        public boolean enableWardrobeSwap = defaultEnableWardrobeSwap;
        @SerialEntry
        public boolean enableToolSwap = defaultEnableToolSwap;
        @SerialEntry
        public String combatWardrobeName = defaultCombatWardrobeName;
        @SerialEntry
        public int meleeWeaponSlot = defaultMeleeWeaponSlot;
        @SerialEntry
        public int rangedWeaponSlot = defaultRangedWeaponSlot;
        @SerialEntry
        public String miningWardrobeName = defaultMiningWardrobeName;
        @SerialEntry
        public int miningWeaponSlot = defaultMiningWeaponSlot;
        @SerialEntry
        public String foragingWardrobeName = defaultForagingWardrobeName;
        @SerialEntry
        public int foragingWeaponSlot = defaultForagingWeaponSlot;
        @SerialEntry
        public String farmingWardrobeName = defaultFarmingWardrobeName;
        @SerialEntry
        public int farmingWeaponSlot = defaultFarmingWeaponSlot;
        @SerialEntry
        public String fishingWardrobeName = defaultFishingWardrobeName;
        @SerialEntry
        public String combatFishingWardrobeName = defaultCombatFishingWardrobeName;
        @SerialEntry
        public int fishingWeaponSlot = defaultFishingWeaponSlot;
    }
}
