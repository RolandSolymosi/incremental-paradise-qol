package com.incrementalqol;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import net.minecraft.screen.slot.SlotActionType;

public class AutoBank implements ClientModInitializer {

	private static KeyBinding autoBank;
	private static int autoDepositState = 0; // 0 not depositing, 1 depositing, 2 already deposited

	public static void sendMenuOpenInteraction(MinecraftClient client)
	{
		var player = client.player;

		if (player == null){
			return;
		}

		autoDepositState = 1;
		player.networkHandler.sendChatCommand("bank");
	}

	public static void handleOpenedMenu(MinecraftClient client){
		if (autoDepositState == 0 || client.interactionManager == null || client.player == null) {
			return;
		}

		if (client.currentScreen instanceof GenericContainerScreen screen) {
			int slotId;
			if (screen.getTitle().getString().contains("Bank")) {
				slotId = 21;
			}
			else if (screen.getTitle().getString().contains("Safe")){
				slotId = 23;
			}
			else {
				return;
			}

			if (autoDepositState == 1) {
				int mouseButton = 0; // 0 for left click, 1 for right click
				SlotActionType clickType = SlotActionType.PICKUP; // Replace with your desired click type
				client.interactionManager.clickSlot(screen.getScreenHandler().syncId, slotId, mouseButton, clickType, client.player);

				if (slotId == 23) {
					autoDepositState = 2;
				}

				client.setScreen(null);
			}
			else if (autoDepositState == 2) {
				autoDepositState = 0;
				client.setScreen(null);
			}
		}
		else{
			autoDepositState = 0;
		}
	}

	private static void keybindCheck(MinecraftClient client) {
		while (autoBank.wasPressed()) {
			assert MinecraftClient.getInstance().player != null;
			sendMenuOpenInteraction(MinecraftClient.getInstance());
		}
	}

	private void initializeKeybinds() {

		autoBank = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"Auto Bank",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_B,
				"Incremental QOL"
		));
	}

	@Override
	public void onInitializeClient() {
		initializeKeybinds();

		ClientTickEvents.END_CLIENT_TICK.register(AutoBank::keybindCheck);

		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (screen instanceof GenericContainerScreen) {
				handleOpenedMenu(client);
			}
		});
	}
}