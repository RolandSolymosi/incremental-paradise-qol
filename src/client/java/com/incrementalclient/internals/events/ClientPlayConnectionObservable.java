package com.incrementalclient.internals.events;

import com.incrementalclient.abstractions.ObservableBase;
import com.incrementalclient.interfaces.Observer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class ClientPlayConnectionObservable extends ObservableBase<Observer<ClientPlayConnectionObservable.EventKind>, ClientPlayConnectionObservable.EventKind> {

    public ClientPlayConnectionObservable(){
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> notifyObservers(EventKind.Disconnect));
        ClientPlayConnectionEvents.JOIN.register((handler, packetSender, client) -> notifyObservers(EventKind.Connect));
    }

    public enum EventKind{
        Connect,
        Disconnect,
    }
}
