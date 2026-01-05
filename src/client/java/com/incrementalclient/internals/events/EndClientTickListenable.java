package com.incrementalclient.internals.events;

import com.incrementalclient.abstractions.ListenableBase;
import com.incrementalclient.interfaces.Listener;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class EndClientTickListenable extends ListenableBase<Listener> {

    public EndClientTickListenable(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> notifyListeners());
    }
}
