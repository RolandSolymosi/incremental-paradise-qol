package com.incrementalclient;

import com.incrementalclient.featues.*;
import com.incrementalclient.interfaces.Configurable;
import com.incrementalclient.internals.BossBarReader;
import com.incrementalclient.internals.MinecraftScreenAccessor;
import com.incrementalclient.internals.ScreenCapture;
import com.incrementalclient.internals.events.*;
import com.incrementalclient.services.*;
import com.incrementalclient.common.utils.dependencyInjection.ServiceCollection;
import com.incrementalclient.common.utils.dependencyInjection.ServiceProvider;
import net.fabricmc.api.ClientModInitializer;

public class Main implements ClientModInitializer {

    public static final ServiceProvider SERVICE_PROVIDER = new ServiceCollection()
            // Low level Services (Minecraft Event and Mixin wrappers)
            .addListenable(EndClientTickListenable.class)
            .addObservable(HudRenderCallbackObservable.class)
            .addObservable(ClientCommandRegistrationCallbackObservable.class)
            .addObservable(ClientReceiveMessageEventsObservable.class)
            .addObservable(ClientWorldEventObservable.class)
            .addObservable(BossBarReader.class)
            .addObservable(ScreenCapture.class)
            // High level Services
            .addSingleton(MinecraftScreenAccessor.class)
            .addSingleton(CommandHandler.class)
            .addSingleton(ChatHandler.class)
            .addSingleton(ConfigHandler.class)
            .addSingleton(KeyBindMonitor.class)
            .addSingleton(TaskMonitor.class)
            .addSingleton(WorldMonitor.class)
            // Features
            .addSingleton(SellAllHotkey.class).forwardSingleton(Configurable.class, SellAllHotkey.class)
            .addSingleton(WarpNextHotkey.class).forwardSingleton(Configurable.class, WarpNextHotkey.class)
            .addSingleton(LinksCommand.class)
            .addSingleton(CommandAliases.class).forwardSingleton(Configurable.class, CommandAliases.class)
            .addSingleton(Loadouts.class).forwardSingleton(Configurable.class, Loadouts.class)
            .buildServiceProvider();

    @Override
    public void onInitializeClient() {
        SERVICE_PROVIDER.initialize();
    }
}
