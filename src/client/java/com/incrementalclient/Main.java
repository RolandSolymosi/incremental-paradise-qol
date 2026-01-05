package com.incrementalclient;

import com.incrementalclient.featues.*;
import com.incrementalclient.interfaces.Configurable;
import com.incrementalclient.internals.events.ClientCommandRegistrationCallbackListenable;
import com.incrementalclient.services.*;
import com.incrementalclient.internals.events.EndClientTickListenable;
import com.incrementalclient.common.utils.dependencyInjection.ServiceCollection;
import com.incrementalclient.common.utils.dependencyInjection.ServiceProvider;
import net.fabricmc.api.ClientModInitializer;

public class Main implements ClientModInitializer {

    public static final ServiceProvider SERVICE_PROVIDER = new ServiceCollection()
            // Low level Services
            .addListenable(EndClientTickListenable.class)
            .addObservable(ClientCommandRegistrationCallbackListenable.class)
            // High level Services
            .addObservable(BossBarReader.class)
            .addSingleton(MinecraftScreenAccessor.class)
            .addSingleton(CommandHandler.class)
            .addSingleton(ChatHandler.class)
            .addSingleton(ConfigHandler.class)
            .addSingleton(KeyBindMonitor.class)
            // Features
            .addSingleton(SellAllHotkey.class).forwardSingleton(Configurable.class, SellAllHotkey.class)
            .addSingleton(LinksCommand.class)
            .addSingleton(CommandAliases.class).forwardSingleton(Configurable.class, CommandAliases.class)
            .buildServiceProvider();

    @Override
    public void onInitializeClient() {
        SERVICE_PROVIDER.initialize();
    }
}
