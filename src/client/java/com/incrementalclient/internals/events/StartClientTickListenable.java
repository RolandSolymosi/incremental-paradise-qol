package com.incrementalclient.internals.events;

import com.incrementalclient.abstractions.ListenableBase;
import com.incrementalclient.interfaces.Listener;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

/**
 * This is identical to EndClientTick in MOST circumstances, however there are some where it is different.
 * For example: When warping to a different world while an active ability is active, the order is:
 * Tell client ability is over (Chat event), EndClientTick, ClientWorldEventObservable, StartClientTick (start of next tick).
 * Therefore, SkillCooldownMonitor (which wants to run a check after ChatEvent AND after WorldEvent but BEFORE the
 * next tick's chat event), MUST use this listenable, and NOT EndClientTickListenable.
 */
public class StartClientTickListenable extends ListenableBase<Listener> {

    public StartClientTickListenable() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> notifyListeners());
    }

}
