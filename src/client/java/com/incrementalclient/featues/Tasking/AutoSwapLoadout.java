package com.incrementalclient.featues.Tasking;

import com.google.common.base.Suppliers;
import com.incrementalclient.common.data.DefaultWardrobe;
import com.incrementalclient.common.data.Tool;
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
    private final TaskMonitor taskMonitor;
    private final TaskingOverrides taskingOverrides;
    private final HotbarHandler hotbarHandler;

    private final AutoSwapLoadout.Configuration configuration = new Configuration();

    private final Supplier<List<OptionPiece>> options;
    private final KeyBindMonitor.KeyBindListener keyBindListener;

    public AutoSwapLoadout(
            KeyBindMonitor keyBindMonitor,
            CommandHandler commandHandler,
            TaskMonitor taskMonitor,
            TaskingOverrides taskingOverrides,
            HotbarHandler hotbarHandler,
            WarpNextHotkey warpNextHotkey
    ) {
        this.commandHandler = commandHandler;
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
                                        configuration.keybind,
                                        () -> configuration.keybind,
                                        v -> configuration.keybind = v
                                )
                                .controller((option) -> () -> new KeyBindController(option))
                                .build()),
                Categories.Tasking.Wardrobe.createConfig(0,
                        Option.<Boolean>createBuilder()
                                .name(Text.of("Toggle the Auto Swap of Wardrobe"))
                                .description(OptionDescription.of(Text.of("Turning on/off the auto swap of wardrobes functionality")))
                                .binding(this.configuration.enableWardrobeSwap, () -> this.configuration.enableWardrobeSwap, newVal -> this.configuration.enableWardrobeSwap = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build()),
                Categories.Tasking.Wardrobe.createConfig(1,
                        Option.<String>createBuilder()
                                .name(Text.of("Combat Wardrobe Name"))
                                .description(OptionDescription.of(Text.of("The name of your wardrobe slot for Combat tasks.")))
                                .binding(this.configuration.combatWardrobeName, () -> this.configuration.combatWardrobeName, newVal -> this.configuration.combatWardrobeName = newVal)
                                .controller(StringControllerBuilder::create)
                                .build()),
                Categories.Tasking.Wardrobe.createConfig(2,
                        Option.<String>createBuilder()
                                .name(Text.of("Mining Wardrobe Name"))
                                .description(OptionDescription.of(Text.of("The name of your wardrobe slot for Mining tasks.")))
                                .binding(this.configuration.miningWardrobeName, () -> this.configuration.miningWardrobeName, newVal -> this.configuration.miningWardrobeName = newVal)
                                .controller(StringControllerBuilder::create)
                                .build()),
                Categories.Tasking.Wardrobe.createConfig(3,
                        Option.<String>createBuilder()
                                .name(Text.of("Foraging Wardrobe Name"))
                                .description(OptionDescription.of(Text.of("The name of your wardrobe slot for Foraging tasks.")))
                                .binding(this.configuration.foragingWardrobeName, () -> this.configuration.foragingWardrobeName, newVal -> this.configuration.foragingWardrobeName = newVal)
                                .controller(StringControllerBuilder::create)
                                .build()),
                Categories.Tasking.Wardrobe.createConfig(4,
                        Option.<String>createBuilder()
                                .name(Text.of("Farming Wardrobe Name"))
                                .description(OptionDescription.of(Text.of("The name of your wardrobe slot for Farming tasks.")))
                                .binding(this.configuration.farmingWardrobeName, () -> this.configuration.farmingWardrobeName, newVal -> this.configuration.farmingWardrobeName = newVal)
                                .controller(StringControllerBuilder::create)
                                .build()),
                Categories.Tasking.Wardrobe.createConfig(5,
                        Option.<String>createBuilder()
                                .name(Text.of("Fishing Wardrobe Name"))
                                .description(OptionDescription.of(Text.of("The name of your wardrobe slot for Fishing tasks.")))
                                .binding(this.configuration.fishingWardrobeName, () -> this.configuration.fishingWardrobeName, newVal -> this.configuration.fishingWardrobeName = newVal)
                                .controller(StringControllerBuilder::create)
                                .build()),
                Categories.Tasking.Wardrobe.createConfig(6,
                        Option.<String>createBuilder()
                                .name(Text.of("Combat Fishing Wardrobe Name"))
                                .description(OptionDescription.of(Text.of("The name of your wardrobe slot for Combat fishing tasks (e.g.: Crabs)")))
                                .binding(this.configuration.combatFishingWardrobeName, () -> this.configuration.combatFishingWardrobeName, newVal -> this.configuration.combatFishingWardrobeName = newVal)
                                .controller(StringControllerBuilder::create)
                                .build()),

                Categories.Tasking.Tools.createConfig(0,
                        Option.<Boolean>createBuilder()
                                .name(Text.of("Toggle the Auto Swap of Tools"))
                                .description(OptionDescription.of(Text.of("Turning on/off the auto swap of tools functionality")))
                                .binding(this.configuration.enableToolSwap, () -> this.configuration.enableToolSwap, newVal -> this.configuration.enableToolSwap = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build()),
                Categories.Tasking.Tools.createConfig(1,
                        Option.<Integer>createBuilder()
                                .name(Text.of("Melee Weapon HotBar Slot"))
                                .description(OptionDescription.of(Text.of("The slot on the HotBar for your melee weapon. (1-8)")))
                                .binding(this.configuration.meleeWeaponSlot + 1, () -> this.configuration.meleeWeaponSlot + 1, newVal -> this.configuration.meleeWeaponSlot = newVal - 1)
                                .controller(o -> IntegerSliderControllerBuilder.create(o).step(1).range(1, 8))
                                .build()),
                Categories.Tasking.Tools.createConfig(2,
                        Option.<Integer>createBuilder()
                                .name(Text.of("Ranged Weapon HotBar Slot"))
                                .description(OptionDescription.of(Text.of("The slot on the HotBar for your ranged weapon. (1-8)")))
                                .binding(this.configuration.rangedWeaponSlot + 1, () -> this.configuration.rangedWeaponSlot + 1, newVal -> this.configuration.rangedWeaponSlot = newVal - 1)
                                .controller(o -> IntegerSliderControllerBuilder.create(o).step(1).range(1, 8))
                                .build()),
                Categories.Tasking.Tools.createConfig(3,
                        Option.<Integer>createBuilder()
                                .name(Text.of("Pickaxe HotBar Slot"))
                                .description(OptionDescription.of(Text.of("The slot on the HotBar for your pickaxe. (1-8)")))
                                .binding(this.configuration.miningWeaponSlot + 1, () -> this.configuration.miningWeaponSlot + 1, newVal -> this.configuration.miningWeaponSlot = newVal - 1)
                                .controller(o -> IntegerSliderControllerBuilder.create(o).step(1).range(1, 8))
                                .build()),
                Categories.Tasking.Tools.createConfig(4,
                        Option.<Integer>createBuilder()
                                .name(Text.of("Axe HotBar Slot"))
                                .description(OptionDescription.of(Text.of("The slot on the HotBar for your axe. (1-8)")))
                                .binding(this.configuration.foragingWeaponSlot + 1, () -> this.configuration.foragingWeaponSlot + 1, newVal -> this.configuration.foragingWeaponSlot = newVal - 1)
                                .controller(o -> IntegerSliderControllerBuilder.create(o).step(1).range(1, 8))
                                .build()),
                Categories.Tasking.Tools.createConfig(5,
                        Option.<Integer>createBuilder()
                                .name(Text.of("Hoe HotBar Slot"))
                                .description(OptionDescription.of(Text.of("The slot on the HotBar for your hoe. (1-8)")))
                                .binding(this.configuration.farmingWeaponSlot + 1, () -> this.configuration.farmingWeaponSlot + 1, newVal -> this.configuration.farmingWeaponSlot = newVal - 1)
                                .controller(o -> IntegerSliderControllerBuilder.create(o).step(1).range(1, 8))
                                .build()),
                Categories.Tasking.Tools.createConfig(6,
                        Option.<Integer>createBuilder()
                                .name(Text.of("Fishing Rod HotBar Slot"))
                                .description(OptionDescription.of(Text.of("The slot on the HotBar for your fishing rod. (1-8)")))
                                .binding(this.configuration.fishingWeaponSlot + 1, () -> this.configuration.fishingWeaponSlot + 1, newVal -> this.configuration.fishingWeaponSlot = newVal - 1)
                                .controller(o -> IntegerSliderControllerBuilder.create(o).step(1).range(1, 8))
                                .build())
        ));
    }

    private void swap() {
        var nextUnfinishedTask = taskMonitor.getTaskList().stream().filter(p -> !p.isCompleted()).findFirst();
        if (nextUnfinishedTask.isPresent()) {
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
                        var slot = override != null && override.toolSlotId >= 0
                                ? override.toolSlotId
                                : getSlotToDefault(normalTask.tool());
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
        return "warpNext";
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
        @SerialEntry
        public int keybind = GLFW.GLFW_KEY_N;

        @SerialEntry
        public boolean enableWardrobeSwap = true;
        @SerialEntry
        public boolean enableToolSwap = true;

        @SerialEntry
        public String combatWardrobeName = "1";
        @SerialEntry
        public int meleeWeaponSlot = 0;
        @SerialEntry
        public int rangedWeaponSlot = 5;

        @SerialEntry
        public String miningWardrobeName = "2";
        @SerialEntry
        public int miningWeaponSlot = 1;
        @SerialEntry
        public String foragingWardrobeName = "3";
        @SerialEntry
        public int foragingWeaponSlot = 2;
        @SerialEntry
        public String farmingWardrobeName = "4";
        @SerialEntry
        public int farmingWeaponSlot = 3;
        @SerialEntry
        public String fishingWardrobeName = "5";
        @SerialEntry
        public String combatFishingWardrobeName = "6";
        @SerialEntry
        public int fishingWeaponSlot = 4;
    }
}
