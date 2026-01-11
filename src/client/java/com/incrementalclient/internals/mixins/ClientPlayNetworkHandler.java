package com.incrementalclient.internals.mixins;

import com.google.common.base.Suppliers;
import com.incrementalclient.Main;
import com.incrementalclient.internals.ScoreboardChangedListenable;
import com.incrementalclient.internals.ScreenCapture;
import com.incrementalclient.internals.interfaces.ReentryPacket;
import com.incrementalclient.services.HotbarHandler;
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
    private static final Supplier<ScoreboardChangedListenable> scoreboardListener = Suppliers.memoize(() ->
            Main.SERVICE_PROVIDER.getService(ScoreboardChangedListenable.class));

    @Unique
    private static final Supplier<ScreenCapture> screenCapture = Suppliers.memoize(() ->
            Main.SERVICE_PROVIDER.getService(ScreenCapture.class));

    @Unique
    private static final Supplier<HotbarHandler> hotbarHandle = Suppliers.memoize(() ->
            Main.SERVICE_PROVIDER.getService(HotbarHandler.class));

    @Inject(method = "onOpenScreen", at = @At("HEAD"), cancellable = true)
    private void onOpenScreen(OpenScreenS2CPacket packet, CallbackInfo ci) {
        if (ReentryPacket.shouldCancel(packet, ci)) {
            screenCapture.get().screenOpened((net.minecraft.client.network.ClientPlayNetworkHandler) (Object)this, packet);
        }
    }
    @Inject(method = "onInventory", at = @At("HEAD"), cancellable = true)
    private void onInventory(InventoryS2CPacket packet, CallbackInfo ci) {
        //if (ReentryPacket.shouldCancel(packet, ci)) {
            screenCapture.get().contentArrived((net.minecraft.client.network.ClientPlayNetworkHandler) (Object)this, packet);
        //}
    }
    @Inject(method = "onCloseScreen", at = @At("HEAD"), cancellable = true)
    private void onCloseScreen(CloseScreenS2CPacket packet, CallbackInfo ci) {
        //if (ReentryPacket.shouldCancel(packet, ci)) {
            screenCapture.get().screenClosed(packet.getSyncId());
        //}

    }
    @Inject(method = "onUpdateSelectedSlot", at = @At("HEAD"), cancellable = true)
    private void onUpdateSelectedSlot(UpdateSelectedSlotS2CPacket packet, CallbackInfo ci) {
        if (hotbarHandle.get().checkSwapped(packet.slot())){
            ci.cancel();
        }
    }
    @Inject(method = "onScreenHandlerSlotUpdate", at = @At("HEAD"), cancellable = true)
    private void onScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci) {
        //if (ReentryPacket.shouldCancel(packet, ci)) {
            screenCapture.get().slotUpdate((net.minecraft.client.network.ClientPlayNetworkHandler) (Object)this, packet);
        //}
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

    // Scoreboard-related packets: refresh GameInfo scoreboard only when these arrive
    @Inject(method = "onScoreboardDisplay", at = @At("TAIL"))
    private void onScoreboardDisplay(ScoreboardDisplayS2CPacket packet, CallbackInfo ci) {
        scoreboardListener.get().notifyScoreboardInfoUpdate();
    }

    @Inject(method = "onScoreboardObjectiveUpdate", at = @At("TAIL"))
    private void onScoreboardObjectiveUpdate(ScoreboardObjectiveUpdateS2CPacket packet, CallbackInfo ci) {
        scoreboardListener.get().notifyScoreboardInfoUpdate();
    }

    @Inject(method = "onTeam", at = @At("TAIL"))
    private void onTeam(TeamS2CPacket packet, CallbackInfo ci) {
        scoreboardListener.get().notifyScoreboardInfoUpdate();
    }
}