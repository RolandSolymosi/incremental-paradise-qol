package com.incrementalclient.interfaces;

import org.jetbrains.annotations.NotNull;

public interface Listener {
    void onEvent();

    final class DefaultListener implements Listener{

        private final Runnable runnable;

        public DefaultListener(Runnable runnable){
            this.runnable = runnable;
        }

        @Override
        public void onEvent() {
            runnable.run();
        }
    }
}
