package com.incrementalclient.internals;

import com.incrementalclient.abstractions.ObservableBase;
import com.incrementalclient.interfaces.Observer;
import com.incrementalclient.internals.interfaces.ReentryPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.text.Text;

import java.util.*;
import java.util.function.Predicate;

public class ScreenCapture extends ObservableBase<Observer<ScreenCapture.Screen>, ScreenCapture.Screen> {
    private static final int MAX_PENDING = 10;
    private static final long STALE_TIMEOUT_MS = 60000;

    private final Map<Integer, PendingData> pending = new LinkedHashMap<>();
    private final Map<Integer, MonitoredScreenState> monitoredScreens = new HashMap<>();
    private final List<Predicate<Screen>> silencingDecisions = new ArrayList<>();

    private volatile int actualSyncId = 0;

    public ScreenCapture() {
    }

    public void registerSilencer(Predicate<Screen> decision) {
        silencingDecisions.add(decision);
    }

    public void unregisterSilencer(Predicate<Screen> decision) {
        silencingDecisions.remove(decision);
    }

    public int getActualSyncId() {
        return actualSyncId;
    }

    public void screenOpened(ClientPlayNetworkHandler handler, OpenScreenS2CPacket packet) {
        MinecraftClient.getInstance().execute(() -> {
            screenUpdateCore(handler, packet);
        });
    }

    private void screenUpdateCore(ClientPlayNetworkHandler handler, OpenScreenS2CPacket packet) {
        var syncId = packet.getSyncId();
        cleanupStale();
        // Clear any existing tracking for this ID to handle SyncId wrap-around/reuse
        pending.remove(syncId);
        monitoredScreens.remove(syncId);

        var data = pending.computeIfAbsent(syncId, k -> new PendingData());
        data.openPacket = packet;
        data.handler = handler;
        processUpdate(syncId);
    }

    public void contentArrived(ClientPlayNetworkHandler handler, InventoryS2CPacket packet) {
        MinecraftClient.getInstance().execute(() -> {
            contentUpdateCore(handler, packet);
        });
    }

    private void contentUpdateCore(ClientPlayNetworkHandler handler, InventoryS2CPacket packet) {
        var syncId = packet.syncId();

        if (syncId == 0) {
            this.actualSyncId = 0;
        }

        if (monitoredScreens.containsKey(syncId)) {
            var state = monitoredScreens.get(syncId);
            state.updateFull(packet.contents());
            notifyObservers(new Screen(state.title, state.getContents(), state.syncId, state.revision));

            // 2. If it's visible, we MUST pass the packet back to Vanilla
            //if (!state.isSilenced) {
            //    handler.onInventory(packet);
            //}

            return;
        }

        var data = pending.computeIfAbsent(syncId, k -> new PendingData());
        data.contentPacket = packet;
        data.handler = handler;
        processUpdate(syncId);
    }

    public void slotUpdate(ClientPlayNetworkHandler handler, ScreenHandlerSlotUpdateS2CPacket packet) {
        MinecraftClient.getInstance().execute(() -> {
            slotUpdateCore(handler, packet);
        });
    }

    private void slotUpdateCore(ClientPlayNetworkHandler handler, ScreenHandlerSlotUpdateS2CPacket packet) {
        var syncId = packet.getSyncId();

        // Case 1: Already silenced, just update and notify
        if (monitoredScreens.containsKey(syncId)) {
            var state = monitoredScreens.get(syncId);
            state.updateSlot(packet.getSlot(), packet.getStack());
            notifyObservers(new Screen(state.title, state.getContents(), state.syncId, state.revision));

            // 2. If it's visible, we MUST pass the packet back to Vanilla
            //if (!state.isSilenced) {
            //    handler.onScreenHandlerSlotUpdate(packet);
            //}
            return;
        }

        // Case 2: Still pending, store the slot update so it's included in the first decision/notification
        var data = pending.get(syncId);
        if (data != null) {
            data.earlySlotUpdates.add(packet);
        }
        processUpdate(syncId);
    }

    public void screenClosed(int syncId) {
        MinecraftClient.getInstance().execute(() -> {
            screenClosedCore(syncId);
        });
    }

    private void screenClosedCore(int syncId) {
        pending.remove(syncId);
        monitoredScreens.remove(syncId);

        if (this.actualSyncId == syncId) {
            this.actualSyncId = 0;
        }

    }

    private void processUpdate(int syncId) {
        var data = pending.get(syncId);
        if (data == null || !data.isComplete()) {
            if (pending.size() > MAX_PENDING) {
                // FIFO: Remove oldest entry from LinkedHashMap
                pending.remove(pending.keySet().iterator().next());
            }
            return;
        }

        pending.remove(syncId);
        handleDecision(data);
    }

    private void handleDecision(PendingData data) {
        var title = data.getName();
        actualSyncId = data.getSyncId();
        var contents = new ArrayList<>(data.contentPacket.contents());
        for (ScreenHandlerSlotUpdateS2CPacket slotPacket : data.earlySlotUpdates) {
            var slotId = slotPacket.getSlot();
            if (slotId >= 0 && slotId < contents.size()) {
                contents.set(slotId, slotPacket.getStack());
            }
        }

        int revision = data.earlySlotUpdates.stream().map(ScreenHandlerSlotUpdateS2CPacket::getRevision).max(Integer::compareTo).orElse(0);
        if (data.contentPacket.revision() > revision){
            revision = data.contentPacket.revision();
        }
        var screen = new Screen(title, contents, actualSyncId, revision);
        var state = new MonitoredScreenState(title, contents, shouldSilence(screen), actualSyncId, revision);
        monitoredScreens.put(data.getSyncId(), state);


        notifyObservers(screen);

        if (!state.isSilenced) {
            // Decision: Show the screen to the player
            resendAll(data);
        }
    }

    private void resendAll(PendingData data) {
        if (data == null || data.handler == null) return;

        // Mark all packets for re-entry to bypass Mixin cancellation
        if (data.openPacket != null) {
            data.handler.onOpenScreen(data.openPacket);
        }
        /*
        if (data.contentPacket != null) {
            data.handler.onInventory(data.contentPacket);
        }
        // Also re-send any slot updates that arrived during the pending phase
        for (ScreenHandlerSlotUpdateS2CPacket slotPacket : data.earlySlotUpdates) {
            data.handler.onScreenHandlerSlotUpdate(slotPacket);
        }*/
    }

    private boolean shouldSilence(Screen screen) {
        for (var decision : silencingDecisions) {
            if (decision.test(screen)) return true;
        }
        return false;
    }

    private void cleanupStale() {
        var now = System.currentTimeMillis();
        monitoredScreens.values().removeIf(s -> (now - s.lastUpdate) > STALE_TIMEOUT_MS);
    }

    public record Screen(Text title, List<ItemStack> contents, int syncId, int revision) {
    }

    private static class MonitoredScreenState {
        private final Text title;
        private final boolean isSilenced;
        private final List<ItemStack> contents;
        private final int syncId;
        private final int revision;
        private long lastUpdate;

        MonitoredScreenState(Text title, List<ItemStack> contents, boolean isSilenced, int syncId, int revision) {
            this.syncId = syncId;
            this.revision = revision;
            this.title = title;
            this.contents = new ArrayList<>(contents);
            this.isSilenced = isSilenced;
            this.lastUpdate = System.currentTimeMillis();
        }

        void updateFull(List<ItemStack> newContents) {
            this.contents.clear();
            this.contents.addAll(newContents);
            this.lastUpdate = System.currentTimeMillis();
        }

        void updateSlot(int index, ItemStack stack) {
            if (index >= 0 && index < contents.size()) {
                contents.set(index, stack);
                this.lastUpdate = System.currentTimeMillis();
            }
        }

        List<ItemStack> getContents() {
            return Collections.unmodifiableList(contents);
        }
    }

    private static class PendingData {
        OpenScreenS2CPacket openPacket;
        InventoryS2CPacket contentPacket;
        List<ScreenHandlerSlotUpdateS2CPacket> earlySlotUpdates = new ArrayList<>();
        ClientPlayNetworkHandler handler;

        private static final Text inventoryName = Text.of("Inventory");

        boolean isComplete() {
            return contentPacket != null && (openPacket != null || contentPacket.syncId() == 0);
        }

        int getSyncId() {
            return contentPacket.syncId();
        }

        public Text getName() {
            return openPacket != null
                    ? openPacket.getName()
                    : inventoryName;
        }
    }
}