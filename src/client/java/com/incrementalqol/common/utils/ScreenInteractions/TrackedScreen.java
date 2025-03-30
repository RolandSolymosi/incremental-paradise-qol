package com.incrementalqol.common.utils.ScreenInteractions;


import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.screen.slot.SlotActionType;

import java.util.List;

public class TrackedScreen {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private final int syncId;
    private String title = null;
    private List<ItemStack> content = null;
    private Integer revisionId = null;

    TrackedScreen(OpenScreenS2CPacket packet){
        this.syncId = packet.getSyncId();
        UpdateScreen(packet);
    }

    TrackedScreen(InventoryS2CPacket packet){
        this.syncId = packet.getSyncId();
        UpdateContent(packet);
    }

    void UpdateScreen(OpenScreenS2CPacket packet){
        this.title = packet.getName().getString();
    }

    void UpdateContent(InventoryS2CPacket packet){
        this.content = packet.getContents();
        this.revisionId = packet.getRevision();
    }

    boolean IsComplete() {
        return title != null && content != null;
    }

    int GetSyncId() {
        return syncId;
    }

    public void Click(int slotId, Button button){
        if (client.getNetworkHandler() != null) {
            client.getNetworkHandler().sendPacket(new ClickSlotC2SPacket(
                    syncId,
                    revisionId,
                    slotId,
                    button.getNumVal(),
                    SlotActionType.PICKUP,
                    ItemStack.EMPTY,
                    new Int2ObjectOpenHashMap<>()
            ));
        }
    }

    public enum Button {
        Left(0),
        Right(1);

        private final int numVal;

        Button(int numVal) {
            this.numVal = numVal;
        }

        public int getNumVal() {
            return numVal;
        }
    }
}
