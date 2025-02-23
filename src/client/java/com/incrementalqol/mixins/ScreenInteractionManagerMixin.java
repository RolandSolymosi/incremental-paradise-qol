package com.incrementalqol.mixins;

import com.incrementalqol.common.utils.ScreenInteraction;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ScreenInteractionManagerMixin {

    @Inject(method = "onOpenScreen", at = @At("HEAD"), cancellable = true)
    private void onOpenScreen(OpenScreenS2CPacket packet, CallbackInfo ci) {
        ScreenInteraction.ScreenInteractionManager.OpenScreen(packet, ci);
    }
    @Inject(method = "onInventory", at = @At("HEAD"), cancellable = true)
    private void onInventory(InventoryS2CPacket packet, CallbackInfo ci) {
        ScreenInteraction.ScreenInteractionManager.InventoryPackage(packet, ci);
    }

    @Inject(method = "onScreenHandlerSlotUpdate", at = @At("HEAD"), cancellable = true)
    private void onSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci) {
        ScreenInteraction.ScreenInteractionManager.SlotUpdate(packet, ci);
    }

    @Inject(method = "onSetPlayerInventory", at = @At("HEAD"), cancellable = true)
    private void onSlotUpdate(SetPlayerInventoryS2CPacket packet, CallbackInfo ci) {
        ScreenInteraction.ScreenInteractionManager.SetInventory(packet, ci);
    }

    @Inject(method = "onScreenHandlerPropertyUpdate", at = @At("HEAD"), cancellable = true)
    private void onScreenHandlerPropertyUpdate(ScreenHandlerPropertyUpdateS2CPacket packet, CallbackInfo ci) {
        ScreenInteraction.ScreenInteractionManager.ScreenPropertyUpdate(packet, ci);
    }

    @Inject(method = "onEntityStatus", at = @At("HEAD"), cancellable = true)
    private void onEntityStatus(EntityStatusS2CPacket packet, CallbackInfo ci) {
        ScreenInteraction.ScreenInteractionManager.EntityStatus(packet, ci);
    }

    @Inject(method = "onPlayerRespawn", at = @At("HEAD"), cancellable = true)
    private void onPlayerRespawn(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
        ScreenInteraction.ScreenInteractionManager.Respawn(packet, ci);
        // World change and death triggers this
    }

    @Inject(method = "onGameStateChange", at = @At("HEAD"), cancellable = true)
    private void onGameStateChange(GameStateChangeS2CPacket packet, CallbackInfo ci) {
        ScreenInteraction.ScreenInteractionManager.GameStateChange(packet, ci);
        // Spectator mode and such change triggers this
    }
}