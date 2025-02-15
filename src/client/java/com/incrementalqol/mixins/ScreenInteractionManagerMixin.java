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
    @Inject(method = "onInventory", at = @At("HEAD"))
    private void onInventory(InventoryS2CPacket packet, CallbackInfo ci) {
        ScreenInteraction.ScreenInteractionManager.InventoryPackage(packet);
    }

    @Inject(method = "onScreenHandlerSlotUpdate", at = @At("HEAD"))
    private void onSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci) {

    }
    @Inject(method = "onSetPlayerInventory", at = @At("HEAD"))
    private void onSlotUpdate(SetPlayerInventoryS2CPacket packet, CallbackInfo ci) {

    }
    @Inject(method = "onScreenHandlerPropertyUpdate", at = @At("HEAD"))
    private void onScreenHandlerPropertyUpdate(ScreenHandlerPropertyUpdateS2CPacket packet, CallbackInfo ci) {

    }

}