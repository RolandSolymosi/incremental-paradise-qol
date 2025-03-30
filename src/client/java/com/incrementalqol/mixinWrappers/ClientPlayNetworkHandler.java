package com.incrementalqol.mixinWrappers;

import com.incrementalqol.modules.Glowing;
import com.incrementalqol.common.utils.ScreenInteraction;
import net.minecraft.network.packet.s2c.play.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ClientPlayNetworkHandler {
    public static void onOpenScreen(OpenScreenS2CPacket packet, CallbackInfo ci) {
        //ScreenManager.OpenScreenPacket(packet, ci);
        ScreenInteraction.ScreenInteractionManager.OpenScreen(packet, ci);
    }
    public static void onInventory(InventoryS2CPacket packet, CallbackInfo ci) {
        //ScreenManager.InventoryPacket(packet, ci);
        ScreenInteraction.ScreenInteractionManager.InventoryPackage(packet, ci);
    }
    public static void onCloseScreen(CloseScreenS2CPacket packet, CallbackInfo ci) {
        //ScreenManager.CloseScreenPacket(packet, ci);
    }
    public static void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci) {
        //ScreenManager.SlotUpdate(packet, ci);
    }
    public static void onSetPlayerInventory(SetPlayerInventoryS2CPacket packet, CallbackInfo ci) {
        //ScreenManager.SetInventory(packet, ci);
    }
    public static void onScreenHandlerPropertyUpdate(ScreenHandlerPropertyUpdateS2CPacket packet, CallbackInfo ci) {
        //ScreenManager.ScreenPropertyUpdate(packet, ci);
    }
    public static void onEntityStatus(EntityStatusS2CPacket packet, CallbackInfo ci) {
        //ScreenManager.EntityStatus(packet, ci);
    }
    public static void onPlayerRespawn(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
        //ScreenManager.Respawn(packet, ci);
        // World change and death triggers this
    }
    public static void onGameStateChange(GameStateChangeS2CPacket packet, CallbackInfo ci) {
        //ScreenManager.GameStateChange(packet, ci);
        // Spectator mode and such change triggers this
    }
    public static void onParticle(ParticleS2CPacket packet, CallbackInfo ci) {
        Glowing.onParticle(packet, ci);
    }
}
