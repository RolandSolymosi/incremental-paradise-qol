package com.incrementalqol.modules.Loadouts;

import com.incrementalqol.modules.OptionsModule;
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

                loadoutKeyBindings.forEach(((keyBinding, loadout) -> {
                        while (keyBinding.wasPressed()) {
                                assert MinecraftClient.getInstance().player != null;
                                MinecraftClient.getInstance().player.networkHandler
                                                .sendChatCommand("wardrobe " + loadout);
                        }
                }));
        }

        private void initializeKeybinds() {
                loadoutKeyBindings = Map.of(
                                KeyBindingHelper.registerKeyBinding(new KeyBinding("Equip Loadout 1",
                                                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_1, OptionsModule.CATEGORY)),
                                "1",
                                KeyBindingHelper.registerKeyBinding(new KeyBinding("Equip Loadout 2",
                                                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_2, OptionsModule.CATEGORY)),
                                "2",
                                KeyBindingHelper.registerKeyBinding(new KeyBinding("Equip Loadout 3",
                                                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_3, OptionsModule.CATEGORY)),
                                "3",
                                KeyBindingHelper.registerKeyBinding(new KeyBinding("Equip Loadout 4",
                                                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_4, OptionsModule.CATEGORY)),
                                "4",
                                KeyBindingHelper.registerKeyBinding(new KeyBinding("Equip Loadout 5",
                                                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_5, OptionsModule.CATEGORY)),
                                "5",
                                KeyBindingHelper.registerKeyBinding(new KeyBinding("Equip Loadout 6",
                                                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_6, OptionsModule.CATEGORY)),
                                "6",
                                KeyBindingHelper.registerKeyBinding(new KeyBinding("Equip Loadout 7",
                                                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_7, OptionsModule.CATEGORY)),
                                "7",
                                KeyBindingHelper.registerKeyBinding(new KeyBinding("Equip Loadout 8",
                                                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_8, OptionsModule.CATEGORY)),
                                "8",
                                KeyBindingHelper.registerKeyBinding(new KeyBinding("Equip Loadout 9",
                                                InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_9, OptionsModule.CATEGORY)),
                                "9");
        }
}
