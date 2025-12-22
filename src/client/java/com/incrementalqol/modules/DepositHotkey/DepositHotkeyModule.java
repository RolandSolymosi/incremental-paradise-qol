package com.incrementalqol.modules.DepositHotkey;

import com.incrementalqol.common.utils.ScreenInteraction;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

public class DepositHotkeyModule implements ClientModInitializer {

    private static KeyBinding depositHotkey;
    private static ScreenInteraction screenInteraction = null;

    public static void sendMenuOpenInteraction(MinecraftClient client) {
        var player = client.player;

        if (player == null) {
            return;
        }

        if (screenInteraction == null) {
            return;
        }

        screenInteraction.startAsync(false);
    }

    private static void keybindingCheck(MinecraftClient client) {
        while (depositHotkey.wasPressed()) {
            assert MinecraftClient.getInstance().player != null;
            sendMenuOpenInteraction(MinecraftClient.getInstance());
        }
    }

    private void initializeKeybinding() {
        depositHotkey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Auto Deposit",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                com.incrementalqol.modules.OptionsModule.CATEGORY));
    }

    @Override
    public void onInitializeClient() {
        initializeKeybinding();
        screenInteraction = new ScreenInteraction.ScreenInteractionBuilder(
                "DepositHotkey",
                s -> s.contains("Bank"),
                s -> true,
                (input) -> {
                    ScreenInteraction.WellKnownInteractions.ClickSlot(input.getLeft(), (short) 21,
                            ScreenInteraction.WellKnownInteractions.Button.Left, SlotActionType.PICKUP);
                    return true;
                })
                .addInteraction(
                        s -> s.equals("Safe"),
                        s -> {
                            if (!s.isEmpty()) {
                                var lore = s.get(23).get(DataComponentTypes.LORE);
                                if (lore != null) {
                                    return !lore.lines().getLast().getString().contains("You have no items to deposit");
                                }
                            }
                            return false;
                        },
                        (input) -> {
                            ScreenInteraction.WellKnownInteractions.ClickSlot(input.getLeft(), (short) 23,
                                    ScreenInteraction.WellKnownInteractions.Button.Left, SlotActionType.PICKUP);
                            return true;
                        })
                .addInteraction(
                        s -> s.contains("Safe"),
                        s -> true,
                        (input) -> true)
                .setStartingAction(c -> c.player.networkHandler.sendChatCommand("bank"))
                .setKeepScreenHidden(true)
                .build();

        ClientTickEvents.END_CLIENT_TICK.register(DepositHotkeyModule::keybindingCheck);
    }
}