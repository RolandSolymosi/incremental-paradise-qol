package com.incrementalqol.modules.Loadouts;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.*;


public class LoadoutsModule implements ClientModInitializer {
    private static Map<KeyBinding, String> loadoutKeyBindings;

    @Override
    public void onInitializeClient() {

        initializeKeybinds();

        ClientTickEvents.END_CLIENT_TICK.register(LoadoutsModule::keybindCheck);
    }
    private static void keybindCheck(MinecraftClient minecraftClient) {

        loadoutKeyBindings.forEach(((keyBinding, loadout) ->{
            while (keyBinding.wasPressed()){
                assert MinecraftClient.getInstance().player != null;
                MinecraftClient.getInstance().player.networkHandler.sendCommand("wardrobe " + loadout);
            }
        } ));
    }

    private void initializeKeybinds() {

        loadoutKeyBindings = Map.of(
                KeyBindingHelper.registerKeyBinding(new KeyBinding("Equip Loadout 1", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_1, "Incremental QOL")), "1",
                KeyBindingHelper.registerKeyBinding(new KeyBinding("Equip Loadout 2", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_2, "Incremental QOL")), "2",
                KeyBindingHelper.registerKeyBinding(new KeyBinding("Equip Loadout 3", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_3, "Incremental QOL")), "3",
                KeyBindingHelper.registerKeyBinding(new KeyBinding("Equip Loadout 4", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_4, "Incremental QOL")), "4",
                KeyBindingHelper.registerKeyBinding(new KeyBinding("Equip Loadout 5", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_5, "Incremental QOL")), "5",
                KeyBindingHelper.registerKeyBinding(new KeyBinding("Equip Loadout 6", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_6, "Incremental QOL")), "6",
                KeyBindingHelper.registerKeyBinding(new KeyBinding("Equip Loadout 7", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_7, "Incremental QOL")), "7",
                KeyBindingHelper.registerKeyBinding(new KeyBinding("Equip Loadout 8", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_8, "Incremental QOL")), "8",
                KeyBindingHelper.registerKeyBinding(new KeyBinding("Equip Loadout 9", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_9, "Incremental QOL")), "9"
        );
    }
}
