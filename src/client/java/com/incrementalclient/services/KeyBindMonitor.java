package com.incrementalclient.services;

import com.incrementalclient.abstractions.ListenableBase;
import com.incrementalclient.interfaces.Listener;
import com.incrementalclient.internals.events.EndClientTickListenable;
import net.minecraft.client.option.KeyBinding;

public class KeyBindMonitor extends ListenableBase<KeyBindMonitor.KeyBindListener> implements Listener {

    public KeyBindMonitor(EndClientTickListenable endClientTickListenable){
        endClientTickListenable.subscribe(this);
    }

    @Override
    public void onEvent() {
        notifyListeners();
    }

    public static final class KeyBindListener implements Listener {

        private final KeyBinding keyBinding;
        private final Runnable runnable;

        public KeyBindListener(KeyBinding keyBinding, Runnable runnable){
            this.keyBinding = keyBinding;
            this.runnable = runnable;
        }

        @Override
        public void onEvent() {
            while (keyBinding.wasPressed()) {
                runnable.run();
            }
        }
    }
}
