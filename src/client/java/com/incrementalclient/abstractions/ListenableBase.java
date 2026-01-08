package com.incrementalclient.abstractions;

import com.incrementalclient.interfaces.Listenable;
import com.incrementalclient.interfaces.Listener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class ListenableBase<TListener extends Listener>  implements Listenable<TListener> {

    private final List<TListener> listeners = new CopyOnWriteArrayList<>();

    protected List<TListener> getListeners() {
        if (getComparator() == null){
            return listeners;
        }
        else{
            return listeners.stream().sorted(getComparator()).toList();
        }
    }

    protected Comparator<TListener> getComparator(){
        return null;
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
