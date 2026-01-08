package com.incrementalclient.internals;

import com.incrementalclient.abstractions.ListenableBase;
import com.incrementalclient.interfaces.Listener;

import java.util.concurrent.atomic.AtomicBoolean;

public class ScoreboardChangedListenable extends ListenableBase<Listener> {

    private final AtomicBoolean ongoing = new AtomicBoolean();

    public void notifyScoreboardInfoUpdate() {
        if (ongoing.compareAndSet(false, true)) {
            notifyListeners();
            ongoing.set(false);
        }
    }
}
