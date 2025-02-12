package com.incrementalqol.modules.SkillManager;

import com.incrementalqol.common.utils.ScreenInteraction;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicBoolean;

public class AutoSkillModule implements ClientModInitializer {

    private static ScreenInteraction screenInteraction = null;
    private static RegistryKey<World> lastWorldKey = null;



    @Override
    public void onInitializeClient() {

        // When joining a world (Singleplayer or Multiplayer)
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            checkWorldChange(client);
        });

        // When the client world loads (e.g., changing dimensions)
        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((client, world) -> {
            checkWorldChange(client);
        });
    }

    private static void checkWorldChange(MinecraftClient client) {
        if (client.world != null) {
            RegistryKey<World> newWorldKey = client.world.getRegistryKey();
            if (lastWorldKey == null || !lastWorldKey.equals(newWorldKey)) {
                lastWorldKey = newWorldKey;
                Identifier worldId = newWorldKey.getValue();
                System.out.println("Client switched to world: " + worldId);
            }
        }
    }
}