package com.incrementalclient.featues;

import com.google.common.base.Suppliers;
import com.incrementalclient.abstractions.ListenableBase;
import com.incrementalclient.config.components.ComplexTypeController;
import com.incrementalclient.config.components.InsertableListOption;
import com.incrementalclient.config.components.KeyBindController;
import com.incrementalclient.interfaces.ComplexConfigurable;
import com.incrementalclient.interfaces.Listener;
import com.incrementalclient.services.CommandHandler;
import com.incrementalclient.services.KeyBindMonitor;
import com.incrementalclient.internals.MinecraftScreenAccessor;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class LoadoutsHotkeys extends ListenableBase<Listener> implements ComplexConfigurable<LoadoutsHotkeys.Configuration> {

    private final Configuration configuration = new Configuration();
    private final KeyBindMonitor keyBindMonitor;
    private final CommandHandler commandHandler;
    private final Supplier<List<OptionPiece>> options = Suppliers.memoize(this::createScreen);
    ;
    private final MinecraftScreenAccessor screenAccessor;

    public ConcurrentHashMap<Integer, KeyBindMonitor.KeyBindListener> registeredHotkeys = new ConcurrentHashMap<>();

    public LoadoutsHotkeys(
            KeyBindMonitor keyBindMonitor,
            CommandHandler commandHandler,
            MinecraftScreenAccessor screenAccessor
    ) {
        this.keyBindMonitor = keyBindMonitor;
        this.commandHandler = commandHandler;
        this.screenAccessor = screenAccessor;
    }

    private List<OptionPiece> createScreen() {
        return List.of(new OptionPiece(
                "Hotkeys",
                0,
                "",
                "",
                1,
                InsertableListOption.<Configuration.Loadout>createBuilder()
                        .name(Text.literal("Loadouts"))
                        .binding(
                                configuration.hotheys,
                                () -> configuration.hotheys,
                                v -> configuration.hotheys = v
                        )
                        .description(OptionDescription.of(Text.of("Wardrobe and pet hotkeys")))
                        .insertEntriesAtEnd(true)
                        .customController(o -> ComplexTypeController.create(o, screenAccessor)
                                .textProvider(opt -> Text.of("Key: " + (opt.hotkey == GLFW.GLFW_KEY_UNKNOWN ? "None" : InputUtil.fromKeyCode(opt.hotkey, 0).getLocalizedText().getString())
                                        + " [Wardrobe: " + (opt.wardrobe.isEmpty() ? "None" : opt.wardrobe)
                                        + " / Pet: " + (opt.pet.isEmpty() ? "None" : opt.pet) + "]"))
                                .screenFactory(opt -> YetAnotherConfigLib.createBuilder()
                                        .title(Text.of("Edit Loadout Settings"))
                                        .category(ConfigCategory.createBuilder()
                                                .name(Text.of("Loadouts"))
                                                .option(Option.<Integer>createBuilder()
                                                        .name(Text.of("Hotkey"))
                                                        .binding(opt.hotkey, () -> opt.hotkey, val -> opt.hotkey = val)
                                                        .controller((option) -> () -> new KeyBindController(option))
                                                        .build())
                                                .option(Option.<String>createBuilder()
                                                        .name(Text.of("Wardrobe"))
                                                        .binding(opt.wardrobe, () -> opt.wardrobe, val -> opt.wardrobe = val)
                                                        .controller(StringControllerBuilder::create)
                                                        .build())
                                                .option(Option.<String>createBuilder()
                                                        .name(Text.of("Pet"))
                                                        .binding(opt.pet, () -> opt.pet, val -> opt.pet = val)
                                                        .controller(StringControllerBuilder::create)
                                                        .build())
                                                .build())
                                        .save(this::notifyListeners))
                                .build()
                        )
                        .initial(Configuration.Loadout::new)
                        .collapsed(false)
                        .build()
        ));
    }

    @Override
    public String getJsonSection() {
        return "Loadouts";
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
        for (var registeredHotkey : registeredHotkeys.entrySet()) {
            keyBindMonitor.unsubscribe(registeredHotkey.getValue());
            registeredHotkeys.remove(registeredHotkey.getKey());
        }
        for (var savedHotkey : configuration.hotheys) {
            var keyBindListener = new KeyBindMonitor.KeyBindListener(keyBindMonitor, new KeyBinding(
                    savedHotkey.wardrobe + "/" + savedHotkey.pet,
                    InputUtil.Type.KEYSYM,
                    savedHotkey.hotkey,
                    "Incremental QOL"
            ), () -> {
                if (!savedHotkey.wardrobe.isEmpty()) {
                    commandHandler.send("wardrobe " + savedHotkey.wardrobe);
                }
                if (!savedHotkey.pet.isEmpty()) {
                    commandHandler.send("pet " + savedHotkey.pet);
                }
            }, false, false);
            registeredHotkeys.put(savedHotkey.hotkey, keyBindListener);
            keyBindMonitor.subscribe(keyBindListener);
        }
    }

    static public class Configuration {

        @SerialEntry
        public List<Loadout> hotheys = new java.util.ArrayList<>();

        static public class Loadout {
            @SerialEntry
            public int hotkey = -1;
            @SerialEntry
            public String wardrobe = "";
            @SerialEntry
            public String pet = "";
        }
    }
}
