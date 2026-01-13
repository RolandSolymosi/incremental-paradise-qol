package com.incrementalclient.internals.events;

import com.incrementalclient.Main;
import com.incrementalclient.abstractions.CancellableEvent;
import com.incrementalclient.abstractions.ObservableBase;
import com.incrementalclient.interfaces.Observer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;

public class ClientReceiveMessageEventsObservable extends ObservableBase<Observer<ClientReceiveMessageEventsObservable.Event>, ClientReceiveMessageEventsObservable.Event> {

    public ClientReceiveMessageEventsObservable() {
        ClientReceiveMessageEvents.ALLOW_GAME.register((a, b) -> {
            var evt = new Event(a, b);
            notifyObservers(evt);
            return !(evt.isCancelled());
        });
    }

    public static class Event extends CancellableEvent {
        // this is basically just a record class
        // but it can't be an actual record class since cancel() functionality requires a non-final varialbe
        private final Text message;
        private final boolean overlay;

        public Event(Text message, boolean overlay) {
            this.message = message;
            this.overlay = overlay;
        }

        public Text message() {
            return message;
        }

        public boolean overlay() {
            return overlay;
        }
    }
}
