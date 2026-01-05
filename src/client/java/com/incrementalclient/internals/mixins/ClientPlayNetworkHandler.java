package com.incrementalclient.internals.mixins;

import net.minecraft.network.packet.s2c.play.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.network.ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandler {

    //@Inject(method = "onOpenScreen", at = @At("HEAD"), cancellable = true)
    //private void onOpenScreen(OpenScreenS2CPacket packet, CallbackInfo ci) {
    //    com.incrementalqol.mixinWrappers.ClientPlayNetworkHandler.onOpenScreen(packet, ci);
    //}
    //@Inject(method = "onInventory", at = @At("HEAD"), cancellable = true)
    //private void onInventory(InventoryS2CPacket packet, CallbackInfo ci) {
    //    com.incrementalqol.mixinWrappers.ClientPlayNetworkHandler.onInventory(packet, ci);
    //}
    //@Inject(method = "onParticle", at = @At("HEAD"), cancellable = true)
    //private void onParticle(ParticleS2CPacket packet, CallbackInfo ci) {
    //    com.incrementalqol.mixinWrappers.ClientPlayNetworkHandler.onParticle(packet, ci);
    //}
}