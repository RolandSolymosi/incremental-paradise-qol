package com.incrementalclient.internals;

import com.incrementalclient.internals.abstractions.AsyncPacketInterceptor;
import com.incrementalclient.internals.events.EndClientTickListenable;
import com.incrementalclient.interfaces.Observer;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;

public class InventoryInteractionInterceptor
        extends AsyncPacketInterceptor<ClickSlotC2SPacket, ScreenCapture.Screen, Integer> {

    public InventoryInteractionInterceptor(
            EndClientTickListenable tickListenable,
            ScreenCapture screenCapture
    ) {
        super(tickListenable);
        screenCapture.subscribe(new Observer.DefaultObserver<>(this::onScreenUpdated));
    }

    private void onScreenUpdated(ScreenCapture.Screen screen) {
        this.onResponseReceived(screen.syncId(), screen);
    }

    @Override
    protected Integer getTrackingKey(ClickSlotC2SPacket packet) {
        return packet.syncId();
    }

    @Override
    protected int getTimeoutTicks(ClickSlotC2SPacket packet) {
        return 20;
    }
}