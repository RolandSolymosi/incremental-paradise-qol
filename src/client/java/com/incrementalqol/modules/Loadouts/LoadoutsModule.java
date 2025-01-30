package com.incrementalqol.modules.Loadouts;

import com.incrementalqol.config.Config;
import com.incrementalqol.config.ConfigHandler;
import com.incrementalqol.config.ConfigScreen;
import com.incrementalqol.modules.CommandAliases.AliasStorage;
import com.incrementalqol.modules.TaskTracker.Task;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
                MinecraftClient.getInstance().player.networkHandler.sendCommand("pets " + loadout);
            }
        } ));
    }

    private void initializeKeybinds() {

        loadoutKeyBindings = Map.of(
                KeyBindingHelper.registerKeyBinding(new KeyBinding("Equip Loadout 1", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_1, "Incremental QOL")), "1",
                KeyBindingHelper.registerKeyBinding(new KeyBinding("Equip Loadout 2", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_2, "Incremental QOL")), "2",
                KeyBindingHelper.registerKeyBinding(new KeyBinding("Equip Loadout 3", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_3, "Incremental QOL")), "3",
                KeyBindingHelper.registerKeyBinding(new KeyBinding("Equip Loadout 4", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_4, "Incremental QOL")), "4",
                KeyBindingHelper.registerKeyBinding(new KeyBinding("Equip Loadout 5", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_5, "Incremental QOL")), "5"
        );
    }
}
