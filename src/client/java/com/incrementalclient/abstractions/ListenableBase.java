package com.incrementalclient.abstractions;

import com.incrementalclient.interfaces.Listenable;
import com.incrementalclient.interfaces.Listener;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class ListenableBase<TListener extends Listener>  implements Listenable<TListener> {

    private final Set<TListener> listeners = new CopyOnWriteArraySet<>();

    protected Set<TListener> getListeners() {
        return listeners;
    }

    public void subscribe(TListener listener) {
        synchronized (listeners){
            listeners.add(listener);
        }
    }

    public void unsubscribe(TListener listener) {
        synchronized (listeners){
            listeners.remove(listener);
        }
    }

    protected void notifyListeners() {
        for (TListener listener : listeners) {
            listener.onEvent();
        }
    }
}
