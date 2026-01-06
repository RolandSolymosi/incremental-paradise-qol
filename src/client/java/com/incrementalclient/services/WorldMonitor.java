package com.incrementalclient.services;

import com.incrementalclient.abstractions.ObservableBase;
import com.incrementalclient.common.data.World;
import com.incrementalclient.interfaces.Observer;
import com.incrementalclient.internals.events.ClientWorldEventObservable;

public class WorldMonitor extends ObservableBase<Observer<WorldMonitor.Event>, WorldMonitor.Event> implements Observer<ClientWorldEventObservable.Event> {
    private World current = World.Hub;

    public WorldMonitor(ClientWorldEventObservable clientWorldEventObservable) {
        clientWorldEventObservable.subscribe(this);
    }

    public World currentWorld() {
        return current;
    }

    @Override
    public void onEvent(ClientWorldEventObservable.Event result) {
        var newWorld = World.find(result.world());
        if (newWorld.isPresent() && newWorld.get() != current) {
            var previous = current;
            current = newWorld.get();
            notifyObservers(new Event(previous, current));
        }
    }

    public record Event(World from, World to) {
    }
}
