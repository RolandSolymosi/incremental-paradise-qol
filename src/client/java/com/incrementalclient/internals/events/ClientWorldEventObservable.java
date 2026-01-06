package com.incrementalclient.internals.events;

import com.incrementalclient.abstractions.ObservableBase;
import com.incrementalclient.interfaces.Observer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.minecraft.world.World;

public class ClientWorldEventObservable extends ObservableBase<Observer<ClientWorldEventObservable.Event>, ClientWorldEventObservable.Event> {

    public ClientWorldEventObservable(){
        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((client, world) -> notifyObservers(new Event(world)));
    }

    public record Event(World world) {
    }
}
