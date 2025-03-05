package com.incrementalqol.modules.DepositHotkey;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class SellAllHotkeyModule implements ClientModInitializer {

    private static KeyBinding sellHotkey;

    public static void sendMenuOpenInteraction(MinecraftClient client) {
        var player = client.player;

        if (player == null) {
            return;
        }

        player.networkHandler.sendChatCommand("sellall");
    }

    private static void keybindingCheck(MinecraftClient client) {
        while (sellHotkey.wasPressed()) {
            assert MinecraftClient.getInstance().player != null;
            sendMenuOpenInteraction(MinecraftClient.getInstance());
        }
    }

    private void initializeKeybinding() {

        sellHotkey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Auto Sell",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_COMMA,
                "Incremental QOL"
        ));
    }

    @Override
    public void onInitializeClient() {
        initializeKeybinding();
        ClientTickEvents.END_CLIENT_TICK.register(SellAllHotkeyModule::keybindingCheck);
    }
}