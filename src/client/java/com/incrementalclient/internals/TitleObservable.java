package com.incrementalclient.internals;

import com.incrementalclient.abstractions.ObservableBase;
import com.incrementalclient.interfaces.Observer;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.text.Text;

import java.util.concurrent.atomic.AtomicBoolean;

public class TitleObservable extends ObservableBase<Observer<TitleObservable.Title>, TitleObservable.Title> {

    private final AtomicBoolean ongoing = new AtomicBoolean();

    public void titleUpdate(TitleS2CPacket titlePacket) {
        if (ongoing.compareAndSet(false, true)) {
            notifyObservers(new Title(titlePacket.text()));
            ongoing.set(false);
        }
    }

    public record Title(Text text) {
    }
}
