package com.incrementalclient.internals.events;

import com.incrementalclient.abstractions.ListenableBase;
import com.incrementalclient.abstractions.ObservableBase;
import com.incrementalclient.interfaces.Listener;
import com.incrementalclient.interfaces.Observer;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.util.Pair;

public class ClientCommandRegistrationCallbackListenable extends ObservableBase<Observer<Pair<CommandDispatcher<FabricClientCommandSource>, CommandRegistryAccess>>, Pair<CommandDispatcher<FabricClientCommandSource>, CommandRegistryAccess>> {

    public ClientCommandRegistrationCallbackListenable(){
        ClientCommandRegistrationCallback.EVENT.register((a, b) -> {
            notifyObservers(new Pair<>(a, b));
        });
    }
}
