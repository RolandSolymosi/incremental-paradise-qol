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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AutoBank implements ClientModInitializer {

	private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	private static KeyBinding autoBank;
	private static int autoDepositState = 0; // 0 not depositing, 1 depositing

	public static void sendMenuOpenInteraction(MinecraftClient client)
	{
		var player = client.player;

		if (player == null){
			return;
		}

		if (autoDepositState != 1){
			autoDepositState = 1;
			player.networkHandler.sendChatCommand("bank");
			scheduler.schedule(() -> {
				autoDepositState = 0;
			}, 500, TimeUnit.MILLISECONDS);
		}
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

			client.interactionManager.clickSlot(screen.getScreenHandler().syncId, slotId, 0, SlotActionType.PICKUP, client.player);
			client.setScreen(null);
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