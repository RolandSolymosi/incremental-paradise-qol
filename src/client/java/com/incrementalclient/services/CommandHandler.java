package com.incrementalclient.services;

import com.incrementalclient.interfaces.Observer;
import com.incrementalclient.internals.MinecraftClientAccessor;
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
    private final HashSet<CommandRegistration> commandRegistrations = new HashSet<>();
    private final MinecraftClientAccessor minecraftClientAccessor;

    public CommandHandler(ClientCommandRegistrationCallbackObservable clientCommandRegistrationCallbackObservable, MinecraftClientAccessor minecraftClientAccessor) {
        this.minecraftClientAccessor = minecraftClientAccessor;
        clientCommandRegistrationCallbackObservable.subscribe(this);
    }

    public void send(String command) {
        minecraftClientAccessor.getClient().execute(() -> {
            minecraftClientAccessor.getNetworkHandler().get().sendCommand(command);
        });
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
