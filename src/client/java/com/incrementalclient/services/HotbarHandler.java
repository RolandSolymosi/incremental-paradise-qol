package com.incrementalclient.services;

import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.internals.events.EndClientTickListenable;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;

public class HotbarHandler {
    private final static int MAX_WAIT_TICKS = 10;

    private final MinecraftClientAccessor minecraftClientAccessor;
    private int swapGuarantorTickCount = 0;
    private boolean swapTriggeredExternally = false;
    private int lastSwappedTo = 0;

    public HotbarHandler(
            MinecraftClientAccessor minecraftClientAccessor,
            EndClientTickListenable tickListenable
    ){
        this.minecraftClientAccessor = minecraftClientAccessor;
        tickListenable.subscribe(this::swapGuarantee);
    }

    public boolean swapActiveHotbarSlot(int slotId){
        var player = minecraftClientAccessor.getPlayer();
        var network = minecraftClientAccessor.getNetworkHandler();
        if (player.isPresent() && network.isPresent()){
            swapTriggeredExternally = false;
            lastSwappedTo = slotId;
            player.get().getInventory().setSelectedSlot(slotId);
            network.get().sendPacket(new UpdateSelectedSlotC2SPacket(slotId));
            swapGuarantorTickCount = MAX_WAIT_TICKS;
        }

        return false;
    }

    public void swapGuarantee(){
        if (swapGuarantorTickCount > 0){
            swapGuarantorTickCount--;
        }
        if (swapGuarantorTickCount == 0 && swapTriggeredExternally){
            swapActiveHotbarSlot(lastSwappedTo);
        }
    }

    public boolean checkSwapped(int slotId){
        if (swapGuarantorTickCount >0 && slotId != lastSwappedTo){
            swapTriggeredExternally = true;
            return true;
        }
        return false;
    }

    public int getActiveHotbarSlot(){
        var player = minecraftClientAccessor.getPlayer();
        return player.map(playerEntity -> playerEntity.getInventory().getSelectedSlot()).orElse(-1);
    }

    public ItemStack getActiveItem(){
        var player = minecraftClientAccessor.getPlayer();
        return player.map(playerEntity -> playerEntity.getInventory().getSelectedStack()).orElse(null);
    }
}
