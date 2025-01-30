package com.incrementalqol.common.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class MenuInteractions {

	public static void Hide(MinecraftClient client){
        client.setScreen(null);
    }

    public static void Close(MinecraftClient client){
        if (client.player != null){
            client.player.currentScreenHandler = client.player.playerScreenHandler; // Manually reset the screen handler to the player inventory
        }
        Hide(client);
    }

    public static @NotNull CompletableFuture<Boolean> TryWaitInventoryLoad(MinecraftClient client, GenericContainerScreen screen){
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if (screen != client.currentScreen){
            future.complete(false);
            return future;
        }
        else if (screen.getScreenHandler().getInventory().isEmpty()){
            client.execute(() -> {
                TryWaitInventoryLoad(client, screen).thenAccept(future::complete);
            });
            return future;
        }
        else{
            future.complete(true);
        }

        return future;
    }


    public static @NotNull CompletableFuture<Boolean> TryWaitSlotContentLoad(MinecraftClient client, GenericContainerScreen screen, int slotId){
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        TryWaitInventoryLoad(client, screen).thenAccept(result -> {
            if (!result){
                future.complete(false);
            }

            ItemStack stack = screen.getScreenHandler().slots.get(slotId).getStack();
            if (stack.getItem().getTranslationKey().equals("block.minecraft.air")){
                client.execute(() -> {
                    TryWaitSlotContentLoad(client, screen, slotId).thenAccept(future::complete);
                });
            }
            else{
                future.complete(true);
            }
        });
        return future;
    }
}
