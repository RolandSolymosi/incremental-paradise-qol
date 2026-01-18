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
import com.incrementalclient.services.skillCooldowns.SkillCooldownMonitor;
import net.fabricmc.api.ClientModInitializer;

public class Main implements ClientModInitializer {

    public static final ServiceProvider SERVICE_PROVIDER = new ServiceCollection()
            // Low level Services (Minecraft Event and Mixin wrappers)
            .addListenable(EndClientTickListenable.class)
            .addObservable(HudRenderCallbackObservable.class)
            .addObservable(ClientCommandRegistrationCallbackObservable.class)
            .addObservable(ClientReceiveMessageEventsObservable.class)
            .addListenable(ClientPlayConnectionObservable.class)
            .addObservable(ClientWorldEventObservable.class)
            .addObservable(BossBarObservable.class)
            .addObservable(EntityRendererObservable.class)
            .addListenable(ScoreboardChangedListenable.class)
            .addObservable(ScreenCapture.class)
            .addObservable(OverlayMessageObservable.class)
            // High level Services
            .addSingleton(MinecraftClientAccessor.class)
            .addSingleton(CommandHandler.class)
            .addObservable(ChatHandler.class)
            .addSingleton(ConfigHandler.class)
            .addListenable(KeyBindMonitor.class)
            .addObservable(TaskMonitor.class)
            .addObservable(WorldMonitor.class)
            .addSingleton(HotbarHandler.class)
            .addSingleton(GameInfoMonitor.class)
            .addObservable(HudManager.class).forwardSingleton(Configurable.class, HudManager.class)
            .addSingleton(InteractionScheduler.class)
            .addObservable(ActiveConsumableMonitor.class)
            .addObservable(ShinyOreMonitor.class)
            .addSingleton(ItemTargetMonitor.class)
            .addSingleton(SkillCooldownMonitor.class)
            // Features
            .addSingleton(SellAllHotkey.class).forwardSingleton(Configurable.class, SellAllHotkey.class)
            .addSingleton(DepositHotkey.class).forwardSingleton(Configurable.class, DepositHotkey.class)
            .addSingleton(BlueprintSwap.class).forwardSingleton(Configurable.class, BlueprintSwap.class)
            .addSingleton(LinksCommand.class)
            .addSingleton(CommandAliases.class).forwardSingleton(Configurable.class, CommandAliases.class)
            .addSingleton(LoadoutsHotkeys.class).forwardSingleton(Configurable.class, LoadoutsHotkeys.class)
            .addSingleton(TaskingOverrides.class).forwardSingleton(Configurable.class, TaskingOverrides.class)
            .addListenable(WarpNextHotkey.class).forwardSingleton(Configurable.class, WarpNextHotkey.class)
            .addSingleton(AutoSwapLoadout.class).forwardSingleton(Configurable.class, AutoSwapLoadout.class)
            .addSingleton(PxpCalculation.class).forwardSingleton(Configurable.class, PxpCalculation.class)
            .addSingleton(BalloonRopeHider.class).forwardSingleton(Configurable.class, BalloonRopeHider.class)
            .addSingleton(AutoSkill.class).forwardSingleton(Configurable.class, AutoSkill.class)
            // Hud Elements and Screens
            .addTransient(HudCustomizationScreen.class) // It must be transient as a screen shouldn't be reused
            .addSingleton(BottomBarElement.class).forwardSingleton(Configurable.class, BottomBarElement.class).forwardSingleton(HudElement.class, BottomBarElement.class)
            .addSingleton(TopBarElement.class).forwardSingleton(Configurable.class, TopBarElement.class).forwardSingleton(HudElement.class, TopBarElement.class)
            .addSingleton(ConsumableTimerElement.class).forwardSingleton(Configurable.class, ConsumableTimerElement.class).forwardSingleton(HudElement.class, ConsumableTimerElement.class)
            .addSingleton(CurrencyElement.class).forwardSingleton(Configurable.class, CurrencyElement.class).forwardSingleton(HudElement.class, CurrencyElement.class)
            .addSingleton(HPBarElement.class).forwardSingleton(Configurable.class, HPBarElement.class).forwardSingleton(HudElement.class, HPBarElement.class)
            .addSingleton(TaskTrackerElement.class).forwardSingleton(Configurable.class, TaskTrackerElement.class).forwardSingleton(HudElement.class, TaskTrackerElement.class)
            .addSingleton(PlayerNameElement.class).forwardSingleton(Configurable.class, PlayerNameElement.class).forwardSingleton(HudElement.class, PlayerNameElement.class)
            .addSingleton(AreaElement.class).forwardSingleton(Configurable.class, AreaElement.class).forwardSingleton(HudElement.class, AreaElement.class)
            .addSingleton(ProgressLayerElement.class).forwardSingleton(Configurable.class, ProgressLayerElement.class).forwardSingleton(HudElement.class, ProgressLayerElement.class)
            .addSingleton(CompletedTasksElement.class).forwardSingleton(Configurable.class, CompletedTasksElement.class).forwardSingleton(HudElement.class, CompletedTasksElement.class)
            .addSingleton(AggroElement.class).forwardSingleton(Configurable.class, AggroElement.class).forwardSingleton(HudElement.class, AggroElement.class)
//            .addSingleton(ItemTargetTrackerElement.class).forwardSingleton(Configurable.class, ItemTargetTrackerElement.class).forwardSingleton(HudElement.class, ItemTargetTrackerElement.class)
            .addSingleton(SkillCooldownElement.class).forwardSingleton(Configurable.class, SkillCooldownElement.class).forwardSingleton(HudElement.class, SkillCooldownElement.class)
            .buildServiceProvider();

    @Override
    public void onInitializeClient() {
        SERVICE_PROVIDER.initialize();
    }
}
