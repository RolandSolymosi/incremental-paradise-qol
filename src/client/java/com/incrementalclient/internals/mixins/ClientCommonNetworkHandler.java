package com.incrementalclient.internals.mixins;

import com.google.common.base.Suppliers;
import com.incrementalclient.Main;
import com.incrementalclient.internals.InventoryInteractionInterceptor;
import com.incrementalclient.internals.ScoreboardChangedListenable;
import com.incrementalclient.internals.ScreenCapture;
import com.incrementalclient.internals.interfaces.ReentryPacket;
import com.incrementalclient.services.HotbarHandler;
import com.incrementalclient.services.ShinyOreMonitor;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.screen.sync.ItemStackHash;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(net.minecraft.client.network.ClientCommonNetworkHandler.class)
public class ClientCommonNetworkHandler {

    @Unique
    private static final Supplier<InventoryInteractionInterceptor> inventoryInteractionInterceptor = Suppliers.memoize(() ->
            Main.SERVICE_PROVIDER.getService(InventoryInteractionInterceptor.class));

    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
    private void sendPacket(net.minecraft.network.packet.Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof ClickSlotC2SPacket clickSlotC2SPacket && clickSlotC2SPacket.cursor() == ItemStackHash.EMPTY && ReentryPacket.shouldCancel(packet, ci)) {
            inventoryInteractionInterceptor.get().intercept(clickSlotC2SPacket, p -> {
                ((net.minecraft.client.network.ClientCommonNetworkHandler)(Object)this).sendPacket(p);
            });
        }
    }
}