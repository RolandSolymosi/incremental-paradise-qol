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

public class SellAllHotkey implements Configurable<SellAllHotkey.Configuration, Integer> {

    private final CommandHandler commandHandler;

    private final Configuration configuration = new Configuration();

    private final Option<Integer> options;
    private final KeyBinding keyBind;

    public SellAllHotkey(
            KeyBindMonitor keyBindMonitor,
            CommandHandler commandHandler
    ){
        this.commandHandler = commandHandler;
        keyBind = new KeyBinding(
                "Auto Sell",
                InputUtil.Type.KEYSYM,
                configuration.keybind,
                "Incremental QOL"
        );
        keyBindMonitor.subscribe(new KeyBindMonitor.KeyBindListener(keyBind, this::sellAll));

        options = Option.<Integer>createBuilder()
                .name(Text.literal("My Hotkey"))
                .binding(
                        configuration.keybind,
                        () -> configuration.keybind,
                        v -> configuration.keybind = v
                )
                .controller((option)-> () -> new KeyBindController(option))
                .build();
    }

    private void sellAll(){
        commandHandler.send("sellall");
    }

    @Override
    public String getCategory() {
        return "Hotkeys";
    }

    @Override
    public String getJsonSection() {
        return "sellAll";
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public SellAllHotkey.Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public Option<Integer> getOption() {
        return options;
    }

    @Override
    public void optionChanged() {
        keyBind.setBoundKey(InputUtil.fromKeyCode(configuration.keybind, 0));
        KeyBinding.updateKeysByCode();
    }

    static public class Configuration {
        @SerialEntry
        public int keybind = GLFW.GLFW_KEY_COMMA;
    }
}
