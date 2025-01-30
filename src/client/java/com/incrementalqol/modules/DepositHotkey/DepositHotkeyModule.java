package com.incrementalqol.modules.DepositHotkey;

import com.incrementalqol.common.utils.MenuInteractions;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

public class DepositHotkeyModule implements ClientModInitializer {

	private static KeyBinding depositHotkey;
	private static boolean autoDepositOngoing = false;

	public static void sendMenuOpenInteraction(MinecraftClient client)
	{
		var player = client.player;

		if (player == null){
			return;
		}

		if (!autoDepositOngoing){
			autoDepositOngoing = true;
			player.networkHandler.sendChatCommand("bank");
		}
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
				"Incremental QOL"
		));
	}

	@Override
	public void onInitializeClient() {
		initializeKeybinding();

		ClientTickEvents.END_CLIENT_TICK.register(DepositHotkeyModule::keybindingCheck);

		ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
			if (autoDepositOngoing){
				if (screen instanceof GenericContainerScreen containerScreen) {
					if (containerScreen.getTitle().getString().contains("Bank")) {
						bankInteraction(client, containerScreen);
					}
					else if (containerScreen.getTitle().getString().contains("Safe")){
						safeInteraction(client, containerScreen);
					}
					else{
						autoDepositOngoing = false;
					}
				}
				else{
					autoDepositOngoing = false;
				}
			}
		});
	}

	private void bankInteraction(MinecraftClient client, GenericContainerScreen screen){
		if (client.interactionManager == null){
			autoDepositOngoing = false;
			return;
		}
		MenuInteractions.ClickSlot(client, screen, 21, MenuInteractions.Button.Left, SlotActionType.PICKUP);
		MenuInteractions.Hide(client);
	}

	private void safeInteraction(MinecraftClient client, GenericContainerScreen screen){
		if (client.interactionManager == null){
			autoDepositOngoing = false;
			return;
		}
		MenuInteractions.TryWaitSlotContentLoad(client, screen, 23).thenAccept(result -> {
			if (result){
				ItemStack stack = screen.getScreenHandler().slots.get(23).getStack();
				var loreComponent = stack.get(DataComponentTypes.LORE);
				if (loreComponent == null){
					autoDepositOngoing = false;
					return;
				}
				var nothingToDeposit = loreComponent.lines().getLast().getString().equals("You have no items to deposit");
				if (!nothingToDeposit){
					MenuInteractions.ClickSlot(client, screen, 23, MenuInteractions.Button.Left, SlotActionType.PICKUP);
					MenuInteractions.Hide(client);
				}
				else {
					autoDepositOngoing = false;
					MenuInteractions.Close(client);
				}
			}
			else{
				autoDepositOngoing = false;
			}
		});
	}
}