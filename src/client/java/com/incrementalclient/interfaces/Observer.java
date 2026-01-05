package com.incrementalclient.interfaces;

public interface Observer<T> extends Listener {
    void onEvent(T result);

    @Override
    default void onEvent() {
        onEvent(null);
    }
}

