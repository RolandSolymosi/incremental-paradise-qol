package com.incrementalclient.internals.events;

import com.incrementalclient.abstractions.ObservableBase;
import com.incrementalclient.interfaces.Observer;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;

public class ClientReceiveMessageEventsObservable extends ObservableBase<Observer<ClientReceiveMessageEventsObservable.Event>, ClientReceiveMessageEventsObservable.Event> {

    public ClientReceiveMessageEventsObservable() {
        ClientReceiveMessageEvents.GAME.register((a, b) -> {
            notifyObservers(new Event(a, b));
        });
    }

    public record Event(Text message, boolean overlay) {
    }
}
