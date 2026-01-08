package com.incrementalclient;

import com.incrementalclient.abstractions.HudElement;
import com.incrementalclient.featues.*;
import com.incrementalclient.featues.Tasking.AutoSwapLoadout;
import com.incrementalclient.featues.Tasking.TaskingOverrides;
import com.incrementalclient.featues.Tasking.WarpNextHotkey;
import com.incrementalclient.hud.*;
import com.incrementalclient.hud.internals.HudCustomizationScreen;
import com.incrementalclient.interfaces.Configurable;
import com.incrementalclient.internals.*;
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
            .addObservable(BossBarObservable.class)
            .addListenable(ScoreboardChangedListenable.class)
            .addObservable(ScreenCapture.class)
            .addObservable(OverlayMessageObservable.class)
            // High level Services
            .addSingleton(MinecraftClientAccessor.class)
            .addSingleton(CommandHandler.class)
            .addSingleton(ChatHandler.class)
            .addSingleton(ConfigHandler.class)
            .addSingleton(KeyBindMonitor.class)
            .addSingleton(TaskMonitor.class)
            .addSingleton(WorldMonitor.class)
            .addSingleton(HotbarHandler.class)
            .addSingleton(GameInfoMonitor.class)
            .addSingleton(HudManager.class).forwardSingleton(Configurable.class, HudManager.class)
            // Features
            .addSingleton(SellAllHotkey.class).forwardSingleton(Configurable.class, SellAllHotkey.class)
            .addSingleton(LinksCommand.class)
            .addSingleton(CommandAliases.class).forwardSingleton(Configurable.class, CommandAliases.class)
            .addSingleton(LoadoutsHotkeys.class).forwardSingleton(Configurable.class, LoadoutsHotkeys.class)
            .addSingleton(TaskingOverrides.class).forwardSingleton(Configurable.class, TaskingOverrides.class)
            .addSingleton(WarpNextHotkey.class).forwardSingleton(Configurable.class, WarpNextHotkey.class)
            .addSingleton(AutoSwapLoadout.class).forwardSingleton(Configurable.class, AutoSwapLoadout.class)
            // Hud Elements and Screens
            .addTransient(HudCustomizationScreen.class) // It must be transient as a screen shouldn't be reused
            .addSingleton(BottomBarElement.class).forwardSingleton(Configurable.class, BottomBarElement.class).forwardSingleton(HudElement.class, BottomBarElement.class)
            .addSingleton(ConsumableTimerElement.class).forwardSingleton(Configurable.class, ConsumableTimerElement.class).forwardSingleton(HudElement.class, ConsumableTimerElement.class)
            .addSingleton(CurrencyElement.class).forwardSingleton(Configurable.class, CurrencyElement.class).forwardSingleton(HudElement.class, CurrencyElement.class)
            .addSingleton(HPBarElement.class).forwardSingleton(Configurable.class, HPBarElement.class).forwardSingleton(HudElement.class, HPBarElement.class)
            .addSingleton(TaskTrackerElement.class).forwardSingleton(Configurable.class, TaskTrackerElement.class).forwardSingleton(HudElement.class, TaskTrackerElement.class)
            .buildServiceProvider();

    @Override
    public void onInitializeClient() {
        SERVICE_PROVIDER.initialize();
    }
}
