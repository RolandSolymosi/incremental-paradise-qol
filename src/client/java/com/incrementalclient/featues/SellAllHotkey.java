package com.incrementalclient.featues;

import com.incrementalclient.config.components.KeyBindController;
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

public class SellAllHotkey implements Configurable<SellAllHotkey.Configuration> {

    private final CommandHandler commandHandler;

    private final Configuration configuration = new Configuration();

    private final List<OptionPiece> options;
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

        options = List.of(new OptionPiece(
                "Hotkeys",
                0,
                "Store",
                "",
                0,
                Option.<Integer>createBuilder()
                        .name(Text.literal("Sell all"))
                        .binding(
                                configuration.keybind,
                                () -> configuration.keybind,
                                v -> configuration.keybind = v
                        )
                        .controller((option) -> () -> new KeyBindController(option))
                        .build())
        );
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
        return options;
    }

    @Override
    public void optionChanged() {
        keyBindListener.updateKeyBind(configuration.keybind);
    }

    static public class Configuration {
        @SerialEntry
        public int keybind = GLFW.GLFW_KEY_COMMA;
    }
}
