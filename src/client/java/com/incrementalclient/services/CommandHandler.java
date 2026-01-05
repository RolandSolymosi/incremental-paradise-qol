package com.incrementalclient.services;

import com.incrementalclient.interfaces.Observer;
import com.incrementalclient.internals.events.ClientCommandRegistrationCallbackListenable;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.util.Pair;

import java.util.HashSet;

public class CommandHandler implements Observer<Pair<CommandDispatcher<FabricClientCommandSource>, CommandRegistryAccess>> {
    private final MinecraftClient client;

    private final HashSet<CommandRegistration> commandRegistrations = new HashSet<>();

    public CommandHandler(ClientCommandRegistrationCallbackListenable clientCommandRegistrationCallbackListenable) {
        clientCommandRegistrationCallbackListenable.subscribe(this);
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
    public void onEvent(Pair<CommandDispatcher<FabricClientCommandSource>, CommandRegistryAccess> result) {
        for (CommandRegistration registration : commandRegistrations) {
            result.getLeft().register(
                    ClientCommandManager.literal(registration.command).executes(context -> {
                        registration.runnable.run();
                        return 0;
                    })
            );
        }
    }

    public record CommandRegistration(String command, Runnable runnable) {
    }
}
