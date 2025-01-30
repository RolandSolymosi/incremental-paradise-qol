package com.incrementalqol.modules;

import com.incrementalqol.config.ConfigScreen;
import net.fabricmc.api.ClientModInitializer;
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

        ClientTickEvents.END_CLIENT_TICK.register(OptionsModule::keybindCheck);
    }
    private static void keybindCheck(MinecraftClient minecraftClient) {

        while (optionsScreen.wasPressed()) {
            MinecraftClient.getInstance().setScreen(new ConfigScreen(MinecraftClient.getInstance().currentScreen));
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
