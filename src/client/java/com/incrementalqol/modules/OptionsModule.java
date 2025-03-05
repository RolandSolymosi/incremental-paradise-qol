package com.incrementalqol.modules;

import com.incrementalqol.config.Config;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;


public class OptionsModule implements ClientModInitializer {
    public static final String MOD_ID = "incremental-qol";

    private static KeyBinding optionsScreen;

    @Override
    public void onInitializeClient() {
        initializeKeybinds();

        ClientLifecycleEvents.CLIENT_STARTED.register(t -> Config.HANDLER.load());
        ClientTickEvents.END_CLIENT_TICK.register(OptionsModule::keybindCheck);
    }
    private static void keybindCheck(MinecraftClient minecraftClient) {

        while (optionsScreen.wasPressed()) {
            minecraftClient.setScreen(Config.HANDLER.instance().createScreen(minecraftClient.currentScreen));
        }
    }

    private void initializeKeybinds() {

        optionsScreen = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Options Screen",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                "Incremental QOL"
        ));
    }
}
