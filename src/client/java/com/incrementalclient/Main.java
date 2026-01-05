package com.incrementalclient;

import com.incrementalclient.featues.SellAllHotkey;
import com.incrementalclient.interfaces.Configurable;
import com.incrementalclient.services.MinecraftScreenAccessor;
import com.incrementalclient.internals.events.EndClientTickListenable;
import com.incrementalclient.services.BossBarReader;
import com.incrementalclient.common.utils.dependencyInjection.ServiceCollection;
import com.incrementalclient.common.utils.dependencyInjection.ServiceProvider;
import com.incrementalclient.services.CommandSender;
import com.incrementalclient.services.ConfigHandler;
import com.incrementalclient.services.KeyBindMonitor;
import net.fabricmc.api.ClientModInitializer;

public class Main implements ClientModInitializer {

    public static final ServiceProvider SERVICE_PROVIDER = new ServiceCollection()
            // Minecraft Events
            .addListenable(EndClientTickListenable.class)
            // Observables/Listenable
            .addObservable(BossBarReader.class)
            .addListenable(KeyBindMonitor.class)
            // Services
            .addSingleton(MinecraftScreenAccessor.class, MinecraftScreenAccessor.class)
            .addSingleton(CommandSender.class, CommandSender.class)
            .addSingleton(ConfigHandler.class, ConfigHandler.class)
            // Features
            .addSingleton(SellAllHotkey.class, SellAllHotkey.class).forwardSingleton(Configurable.class, SellAllHotkey.class)
            .buildServiceProvider();

    @Override
    public void onInitializeClient() {
        SERVICE_PROVIDER.initialize();
    }
}
