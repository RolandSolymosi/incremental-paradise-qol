package com.incrementalclient.internals.mixins;

import com.google.common.base.Suppliers;
import com.incrementalclient.Main;
import com.incrementalclient.internals.ScreenCapture;
import com.incrementalclient.internals.interfaces.ReentryPacket;
import net.minecraft.network.packet.s2c.play.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(net.minecraft.client.network.ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandler {

    @Unique
    private static final Supplier<ScreenCapture> screenReader = Suppliers.memoize(() ->
            Main.SERVICE_PROVIDER.getService(ScreenCapture.class));

    @Inject(method = "onOpenScreen", at = @At("HEAD"), cancellable = true)
    private void onOpenScreen(OpenScreenS2CPacket packet, CallbackInfo ci) {
        if (ReentryPacket.shouldCancel(packet, ci)) {
            screenReader.get().screenOpened((net.minecraft.client.network.ClientPlayNetworkHandler) (Object)this, packet);
        }
    }
    @Inject(method = "onInventory", at = @At("HEAD"), cancellable = true)
    private void onInventory(InventoryS2CPacket packet, CallbackInfo ci) {
        if (ReentryPacket.shouldCancel(packet, ci)) {
            screenReader.get().contentArrived((net.minecraft.client.network.ClientPlayNetworkHandler) (Object)this, packet);
        }
    }
    @Inject(method = "onCloseScreen", at = @At("HEAD"), cancellable = true)
    private void onCloseScreen(CloseScreenS2CPacket packet, CallbackInfo ci) {
        if (ReentryPacket.shouldCancel(packet, ci)) {
            screenReader.get().screenClosed(packet.getSyncId());
        }

    }
    @Inject(method = "onScreenHandlerSlotUpdate", at = @At("HEAD"), cancellable = true)
    private void onCloseScreen(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci) {
        if (ReentryPacket.shouldCancel(packet, ci)) {
            screenReader.get().slotUpdate((net.minecraft.client.network.ClientPlayNetworkHandler) (Object)this, packet);
        }

    }
    //@Inject(method = "onScreenHandlerPropertyUpdate", at = @At("HEAD"), cancellable = true)
    //private void onCloseScreen(ScreenHandlerPropertyUpdateS2CPacket packet, CallbackInfo ci) {
    //    if (ReentryPacket.shouldCancel(packet, ci)) return;
    //
    //    screenReader.get().propertyUpdate(packet.getSyncId(), packet.getPropertyId(), packet.getValue());
    //}
    //@Inject(method = "onParticle", at = @At("HEAD"), cancellable = true)
    //private void onParticle(ParticleS2CPacket packet, CallbackInfo ci) {
    //    com.incrementalqol.mixinWrappers.ClientPlayNetworkHandler.onParticle(packet, ci);
    //}
}