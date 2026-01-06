package com.incrementalclient.internals.events;

import com.incrementalclient.abstractions.ObservableBase;
import com.incrementalclient.interfaces.Observer;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.util.Pair;

public class ClientCommandRegistrationCallbackObservable extends ObservableBase<Observer<ClientCommandRegistrationCallbackObservable.Event>, ClientCommandRegistrationCallbackObservable.Event> {

    public ClientCommandRegistrationCallbackObservable() {
        ClientCommandRegistrationCallback.EVENT.register((a, b) -> {
            notifyObservers(new Event(a, b));
        });
    }

    public record Event(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registry) {
    }
}
