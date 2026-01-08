package com.incrementalclient.internals;

import com.incrementalclient.abstractions.ObservableBase;
import com.incrementalclient.interfaces.Observer;
import net.minecraft.text.Text;

import java.util.concurrent.atomic.AtomicBoolean;

public class OverlayMessageObservable extends ObservableBase<Observer<OverlayMessageObservable.Event>, OverlayMessageObservable.Event> {

    private final AtomicBoolean ongoing = new AtomicBoolean();

    public void messageUpdate(Text message) {
        if (ongoing.compareAndSet(false, true)) {
            notifyObservers(new Event(message));
            ongoing.set(false);
        }
    }

    public record Event(Text message) {
    }
}
