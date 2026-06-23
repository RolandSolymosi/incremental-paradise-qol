package com.incrementalclient.featues;

import com.google.common.base.Suppliers;
import com.incrementalclient.config.controllers.KeyBindController;
import com.incrementalclient.interfaces.Configurable;
import com.incrementalclient.services.CommandHandler;
import com.incrementalclient.services.KeyBindMonitor;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Supplier;

public class SellAllHotkey implements Configurable<SellAllHotkey.Configuration> {

    private final CommandHandler commandHandler;

    private final Configuration configuration = new Configuration();

    private final Supplier<List<OptionPiece>> options;
    private final KeyBindMonitor.KeyBindListener keyBindListener;

    public SellAllHotkey(
            KeyBindMonitor keyBindMonitor,
            CommandHandler commandHandler
    ) {
        this.commandHandler = commandHandler;
        keyBindListener = new KeyBindMonitor.KeyBindListener(keyBindMonitor, new KeyBinding(
                "Auto Sell",
                InputUtil.Type.KEYSYM,
                configuration.keybind,
                "Incremental QOL"
        ), this::sellAll);

        options = Suppliers.memoize(() -> List.of(Categories.Hotkeys.Bank.createConfig(2,
                Option.<Integer>createBuilder()
                        .name(Text.literal("Sell all"))
                        .binding(
                                Configuration.defaultKeybind,
                                () -> configuration.keybind,
                                v -> configuration.keybind = v
                        )
                        .controller((option) -> () -> new KeyBindController(option))
                        .build())
        ));
    }

    private void sellAll() {
        commandHandler.send("sellall");
    }

    @Override
    public String getJsonSection() {
        return "sellAll";
    }

    @Override
    public SellAllHotkey.Configuration getConfiguration() {
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

    static public class Configuration {
        private final static int defaultKeybind = GLFW.GLFW_KEY_COMMA;

        @SerialEntry
        public int keybind = defaultKeybind;
    }
}
