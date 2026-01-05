package com.incrementalclient.interfaces;

import java.util.function.Consumer;

public interface Observer<T> extends Listener {
    void onEvent(T result);

    @Override
    default void onEvent() {
        onEvent(null);
    }

    final class DefaultObserver<T> implements Observer<T> {

        private final Consumer<T> runnable;

        public DefaultObserver(Consumer<T> runnable){
            this.runnable = runnable;
        }

        @Override
        public void onEvent(T result) {
            runnable.accept(result);
        }
    }
}

