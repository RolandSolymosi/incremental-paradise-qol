package com.incrementalclient.interfaces;

public interface Listenable<TListener extends Listener> {

    void subscribe(TListener listener);

    void unsubscribe(TListener listener);
}
