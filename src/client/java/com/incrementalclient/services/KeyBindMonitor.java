package com.incrementalclient.services;

import com.incrementalclient.abstractions.ListenableBase;
import com.incrementalclient.interfaces.Listener;
import com.incrementalclient.internals.events.EndClientTickListenable;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class KeyBindMonitor extends ListenableBase<KeyBindMonitor.KeyBindListener> implements Listener {

    private final Map<String, List<KeyBindListener>> listenersByKeyBind = new ConcurrentHashMap<>();
    private final Map<KeyBindListener, String> vanillaRegisteredKeyBinds = new ConcurrentHashMap<>();

    private int keyMapCheckCounter = 0;

    public KeyBindMonitor(EndClientTickListenable endClientTickListenable) {
        endClientTickListenable.subscribe(this);
    }

    @Override
    public void subscribe(KeyBindListener listener) {
        synchronized (listenersByKeyBind) {
            super.subscribe(listener);
            listenersByKeyBind.computeIfAbsent(listener.keyBinding.getBoundKeyTranslationKey(), k -> new CopyOnWriteArrayList<>())
                    .add(listener);
            if (listener.registerInVanilla){
                vanillaRegisteredKeyBinds.put(listener, listener.keyBinding.getBoundKeyTranslationKey());
            }
        }
    }

    @Override
    public void unsubscribe(KeyBindListener listener) {
        synchronized (listenersByKeyBind) {
            super.unsubscribe(listener);
            var listeners = listenersByKeyBind.get(listener.keyBinding.getBoundKeyTranslationKey());
            if (listeners != null) {
                listeners.remove(listener);
                if (listeners.isEmpty()) {
                    listenersByKeyBind.remove(listener.keyBinding.getBoundKeyTranslationKey());
                }
            }
            vanillaRegisteredKeyBinds.remove(listener);
        }
    }

    @Override
    public void onEvent() {
        if (keyMapCheckCounter >= 20){
            for (var entry: vanillaRegisteredKeyBinds.entrySet()){
                if (!entry.getKey().keyBinding.getBoundKeyTranslationKey().equals(entry.getValue())){
                    unsubscribe(entry.getKey());
                    subscribe(entry.getKey());
                }
            }
            keyMapCheckCounter = 0;
        }
        else{
            keyMapCheckCounter++;
        }
        for (var entry : listenersByKeyBind.entrySet()) {
            var tasks = entry.getValue();
            while (tasks.stream().anyMatch(t -> t.keyBinding.wasPressed())) {
                for (var task : tasks) {
                    task.onEvent();
                }
            }
        }
    }

    public static final class KeyBindListener implements Listener {

        private final KeyBindMonitor monitor;
        private final KeyBinding keyBinding;
        private final Runnable runnable;
        private final boolean registerInVanilla;

        public KeyBindListener(KeyBindMonitor monitor, KeyBinding keyBinding, Runnable runnable){
            this(monitor, keyBinding, runnable, false,true);
        }

        public KeyBindListener(KeyBindMonitor monitor, KeyBinding keyBinding, Runnable runnable, boolean registerInVanilla){
            this(monitor, keyBinding, runnable, registerInVanilla,true);
        }
        public KeyBindListener(KeyBindMonitor monitor, KeyBinding keyBinding, Runnable runnable, boolean registerInVanilla, boolean subscribeOnCreate) {
            this.monitor = monitor;
            this.keyBinding = keyBinding;
            this.runnable = runnable;
            this.registerInVanilla = registerInVanilla;
            if (subscribeOnCreate){
                monitor.subscribe(this);
            }
            if (registerInVanilla){
                KeyBindingHelper.registerKeyBinding(keyBinding);
            }
        }

        @Override
        public void onEvent() {
            runnable.run();
        }

        public void updateKeyBind(int keyBind){
            monitor.unsubscribe(this);
            keyBinding.setBoundKey(InputUtil.fromKeyCode(keyBind, 0));
            KeyBinding.updateKeysByCode();
            monitor.subscribe(this);
        }
    }
}
