package com.incrementalqol.common.utils.ScreenInteractions;

import com.incrementalqol.common.utils.ConfiguredLogger;
import com.incrementalqol.config.Config;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class ScreenManager implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenManager.class);
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static final Config config = Config.HANDLER.instance();

    private static final Set<ReadOnlyScreenReader> registeredScreenReaders = ConcurrentHashMap.newKeySet();
    private static final Queue<ActiveScreenInteraction> queuedScreenInteractions = new UniqueConcurrentQueue<>();

    private static final AtomicReference<ActiveScreenInteraction> actualInteraction = new AtomicReference<>();
    private static TrackedScreen actualScreen = null;

    private static int abortTickCounter = 0;

    private static void reset() {
        for (var reader : registeredScreenReaders) {
            reader.reset();
        }
        for (var listener : queuedScreenInteractions) {
            listener.reset();
        }
        queuedScreenInteractions.clear();
        actualInteraction.get().reset();
        actualInteraction.set(null);
    }

    private static synchronized void HandlePackets(Packet<ClientPlayPacketListener> packet, CallbackInfo ci) {
        TrackedScreen screen = actualScreen;
        switch (packet) {
            case OpenScreenS2CPacket x -> {
                if (actualScreen == null || actualScreen.GetSyncId() != x.getSyncId()) {
                    screen = new TrackedScreen(x);
                }
            }
            case InventoryS2CPacket x -> {
                if (actualScreen == null || actualScreen.GetSyncId() != x.getSyncId()) {
                    screen = new TrackedScreen(x);
                }
                else {
                    screen.UpdateContent(x);
                }
            }
            case CloseScreenS2CPacket x -> {
                if (actualScreen != null && actualScreen.GetSyncId() == x.getSyncId()) {
                    actualScreen = null;
                }
            }
            default -> {
                return;
            }
        }

        if (screen != null){
            if (screen.IsComplete()){
                registeredScreenReaders.parallelStream().forEach(r -> {

                });
            }
        }
        else {
            registeredScreenReaders.forEach(x -> x.reset());
        }
    }

    private synchronized static void HandleTimeout() {
        if (actualInteraction.get() != null){
            abortTickCounter += 1;
            if (abortTickCounter >= config.getInteractionTimeout()){
                // TODO: Abort actual interaction
            }
        }
        else{
            abortTickCounter = 0;
        }
    }

    public static void OpenScreenPacket(OpenScreenS2CPacket packet, CallbackInfo ci) {
        ConfiguredLogger.LogInfo(LOGGER, "SyncId of OpenScreen: " + packet.getSyncId() + " with title: '" + packet.getName().getString() + "'");
        HandlePackets(packet, ci);
        //var shouldHide = false;
        //for (var listener : registeredInteractions) {
        //    shouldHide = shouldHide || listener.listen(packet);
        //    interactionOngoing.compareAndSet(false, true);
        //}
        //if (shouldHide) {
        //    ci.cancel();
        //}
    }

    public static void InventoryPacket(InventoryS2CPacket packet, CallbackInfo ci) {
        ConfiguredLogger.LogInfo(LOGGER, "SyncId of InventoryContent: " + packet.getSyncId() + " revision: " + packet.getRevision());
        HandlePackets(packet, ci);

        //for (var listener : registeredInteractions) {
        //    listener.listen(packet);
        //    interactionOngoing.compareAndSet(false, true);
        //}
    }

    public static void CloseScreenPacket(CloseScreenS2CPacket packet, CallbackInfo ci) {
        ConfiguredLogger.LogInfo(LOGGER, "SyncId of CloseScreen: " + packet.getSyncId());
        HandlePackets(packet, ci);

        //for (var listener : registeredInteractions) {
        //    listener.listen(packet);
        //    interactionOngoing.compareAndSet(false, true);
        //}
    }

    public static void SlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci) {
        //ConfiguredLogger.LogInfo(LOGGER, "[SHARED]: SyncId of SlotUpdate: " + packet.getSyncId() + " slotId: "+packet.getSlot() + " revision: "+packet.getRevision());
    }

    public static void SetInventory(SetPlayerInventoryS2CPacket packet, CallbackInfo ci) {
        //ConfiguredLogger.LogInfo(LOGGER, "[SHARED]: Set Inventory slotId: "+packet.slot());
    }

    public static void ScreenPropertyUpdate(ScreenHandlerPropertyUpdateS2CPacket packet, CallbackInfo ci) {
        //ConfiguredLogger.LogInfo(LOGGER, "[SHARED]: SyncId of ScreenPropertyUpdate: " + packet.getSyncId() + " propertyId: "+packet.getPropertyId());
    }

    public static void EntityStatus(EntityStatusS2CPacket packet, CallbackInfo ci) {
        //ConfiguredLogger.LogInfo(LOGGER, "[SHARED] EntityStatusUpdate: " + packet.getStatus());
    }

    public static void Respawn(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
        //ConfiguredLogger.LogInfo(LOGGER, "[SHARED] Respawn: " + packet.commonPlayerSpawnInfo().dimension().getValue().toShortTranslationKey());
    }

    public static void GameStateChange(GameStateChangeS2CPacket packet, CallbackInfo ci) {
        //ConfiguredLogger.LogInfo(LOGGER, "[SHARED] GameStateChange: " + packet.getValue());
    }

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            reset();
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            reset();
        });
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            //if (screen instanceof InventoryScreen) {
            //    if (client.player != null) {
            //        if (interactionOngoing.get()) {
            //            interactionOngoing.set(false);
            //            ConfiguredLogger.LogInfo(LOGGER, "[SHARED]: Inventory Opened, reset handler to inventory.");
            //            client.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
            //        }
            //    }
            //}
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            HandleTimeout();
        });
    }

    private static class UniqueConcurrentQueue<T> extends ConcurrentLinkedQueue<T> {
        private final Set<T> seen = ConcurrentHashMap.newKeySet();

        @Override
        public synchronized boolean offer(T item) {
            if (seen.add(item)) {
                super.offer(item);
                return true;
            }
            return false;
        }

        @Override
        public synchronized T poll() {
            T item = super.poll();
            if (item != null) {
                seen.remove(item);
            }
            return item;
        }

        @Override
        public T peek() {
            return super.peek();
        }

        @Override
        public int size() {
            return super.size();
        }

        @Override
        public boolean isEmpty() {
            return super.isEmpty();
        }

        @Override
        public boolean contains(Object item) {
            return seen.contains(item);
        }

        @Override
        public void clear() {
            super.clear();
        }
    }
}
