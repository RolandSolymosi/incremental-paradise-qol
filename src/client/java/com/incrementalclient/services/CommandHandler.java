package com.incrementalclient.services;

import com.incrementalclient.interfaces.Observer;
import com.incrementalclient.internals.events.ClientCommandRegistrationCallbackObservable;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.util.Pair;

import java.util.HashSet;

public class CommandHandler implements Observer<ClientCommandRegistrationCallbackObservable.Event> {
    private final MinecraftClient client;

    private final HashSet<CommandRegistration> commandRegistrations = new HashSet<>();

    public CommandHandler(ClientCommandRegistrationCallbackObservable clientCommandRegistrationCallbackObservable) {
        clientCommandRegistrationCallbackObservable.subscribe(this);
        client = MinecraftClient.getInstance();
    }

    public boolean send(String command) {
        if (client.player != null) {
            return client.player.networkHandler.sendCommand(command);
        }
        return false;
    }

    public boolean register(CommandRegistration registration) {
        return commandRegistrations.add(registration);
    }

    @Override
    public void onEvent(ClientCommandRegistrationCallbackObservable.Event result) {
        for (CommandRegistration registration : commandRegistrations) {
            result.dispatcher().register(registration.argumentBuilder);
        }
    }

    public static class CommandRegistration {
        private final LiteralArgumentBuilder<FabricClientCommandSource> argumentBuilder;

        public CommandRegistration(LiteralArgumentBuilder<FabricClientCommandSource> argumentBuilder) {
            this.argumentBuilder = argumentBuilder;
        }
        public CommandRegistration(String command, Runnable runnable){
            this(ClientCommandManager.literal(command).executes(context -> {
                runnable.run();
                return 0;
            }));
        }

    }
}
