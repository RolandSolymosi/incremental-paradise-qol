package com.incrementalclient.services;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;

public class HotbarHandler {
    private final MinecraftClient client;

    public HotbarHandler(){
        client = MinecraftClient.getInstance();
    }

    public boolean swapActiveHotbarSlot(int slotId){
        if (client.player != null){
            client.player.getInventory().setSelectedSlot(slotId);
            client.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slotId));
            // TODO: Check if I can get a response packet to confirm change

            return true;
        }
        return false;
    }

    public int getActiveHotbarSlot(int slotId){
        if (client.player != null) {
            return client.player.getInventory().getSelectedSlot();
        }
        return -1;
    }

    public ItemStack getActiveItem(int slotId){
        if (client.player != null) {
            return client.player.getInventory().getSelectedStack();
        }
        return null;
    }
}
