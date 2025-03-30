package com.incrementalqol.mixins;

import net.minecraft.network.packet.s2c.play.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.network.ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandler {

    @Inject(method = "onOpenScreen", at = @At("HEAD"), cancellable = true)
    private void onOpenScreen(OpenScreenS2CPacket packet, CallbackInfo ci) {
        com.incrementalqol.mixinWrappers.ClientPlayNetworkHandler.onOpenScreen(packet, ci);
    }
    @Inject(method = "onInventory", at = @At("HEAD"), cancellable = true)
    private void onInventory(InventoryS2CPacket packet, CallbackInfo ci) {
        com.incrementalqol.mixinWrappers.ClientPlayNetworkHandler.onInventory(packet, ci);
    }
    @Inject(method = "onCloseScreen", at = @At("HEAD"), cancellable = true)
    private void onCloseScreen(CloseScreenS2CPacket packet, CallbackInfo ci) {
        com.incrementalqol.mixinWrappers.ClientPlayNetworkHandler.onCloseScreen(packet, ci);
    }

    @Inject(method = "onScreenHandlerSlotUpdate", at = @At("HEAD"), cancellable = true)
    private void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci) {
        com.incrementalqol.mixinWrappers.ClientPlayNetworkHandler.onScreenHandlerSlotUpdate(packet, ci);
    }

    @Inject(method = "onSetPlayerInventory", at = @At("HEAD"), cancellable = true)
    private void onSetPlayerInventory(SetPlayerInventoryS2CPacket packet, CallbackInfo ci) {
        com.incrementalqol.mixinWrappers.ClientPlayNetworkHandler.onSetPlayerInventory(packet, ci);
    }

    @Inject(method = "onScreenHandlerPropertyUpdate", at = @At("HEAD"), cancellable = true)
    private void onScreenHandlerPropertyUpdate(ScreenHandlerPropertyUpdateS2CPacket packet, CallbackInfo ci) {
        com.incrementalqol.mixinWrappers.ClientPlayNetworkHandler.onScreenHandlerPropertyUpdate(packet, ci);
    }

    @Inject(method = "onEntityStatus", at = @At("HEAD"), cancellable = true)
    private void onEntityStatus(EntityStatusS2CPacket packet, CallbackInfo ci) {
        com.incrementalqol.mixinWrappers.ClientPlayNetworkHandler.onEntityStatus(packet, ci);
    }

    @Inject(method = "onPlayerRespawn", at = @At("HEAD"), cancellable = true)
    private void onPlayerRespawn(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
        com.incrementalqol.mixinWrappers.ClientPlayNetworkHandler.onPlayerRespawn(packet, ci);
        // World change and death triggers this
    }

    @Inject(method = "onGameStateChange", at = @At("HEAD"), cancellable = true)
    private void onGameStateChange(GameStateChangeS2CPacket packet, CallbackInfo ci) {
        com.incrementalqol.mixinWrappers.ClientPlayNetworkHandler.onGameStateChange(packet, ci);
        // Spectator mode and such change triggers this
    }
    @Inject(method = "onParticle", at = @At("HEAD"), cancellable = true)
    private void onParticle(ParticleS2CPacket packet, CallbackInfo ci) {
        com.incrementalqol.mixinWrappers.ClientPlayNetworkHandler.onParticle(packet, ci);
    }
}