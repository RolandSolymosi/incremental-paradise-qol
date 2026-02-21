package com.incrementalclient.featues.Tasking;

import com.incrementalclient.abstractions.ListenableBase;
import com.incrementalclient.common.data.World;
import com.incrementalclient.common.data.tasks.TaskType;
import com.incrementalclient.common.data.tasks.abstractions.GamingTask;
import com.incrementalclient.common.data.tasks.abstractions.ITask;
import com.incrementalclient.config.controllers.KeyBindController;
import com.incrementalclient.interfaces.Configurable;
import com.incrementalclient.interfaces.Listener;
import com.incrementalclient.interfaces.Observer;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.internals.events.EndClientTickListenable;
import com.incrementalclient.services.*;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class WarpNextHotkey extends ListenableBase<Listener> implements Configurable<WarpNextHotkey.Configuration>, Observer<EndClientTickListenable> {
    private static final int MAX_WAIT = 20;

    private final CommandHandler commandHandler;
    private final ChatHandler chatHandler;
    private final WorldMonitor worldMonitor;
    private final TaskMonitor taskMonitor;
    private final TaskingOverrides taskingOverrides;

    private final WarpNextHotkey.Configuration configuration = new Configuration();

    private final Supplier<List<OptionPiece>> options;
    private final KeyBindMonitor.KeyBindListener keyBindListener;
    private final InteractionScheduler<Void> interactionScheduler;
    private final InteractionScheduler.Builder<Void, Void> autoLevelUpTask;

    private int warpIndex = 0;
    private int tickCounter = 0;
    private ITask currentTask = null;
    private final AtomicBoolean ongoingWarp = new AtomicBoolean();

    public WarpNextHotkey(
            KeyBindMonitor keyBindMonitor,
            CommandHandler commandHandler,
            ChatHandler chatHandler,
            WorldMonitor worldMonitor,
            TaskMonitor taskMonitor,
            TaskingOverrides taskingOverrides,
            EndClientTickListenable tickListenable,
            InteractionScheduler<Void> interactionScheduler,
            MinecraftClientAccessor minecraftClientAccessor
    ) {
        this.commandHandler = commandHandler;
        this.chatHandler = chatHandler;
        this.worldMonitor = worldMonitor;
        this.taskMonitor = taskMonitor;
        this.taskingOverrides = taskingOverrides;
        keyBindListener = new KeyBindMonitor.KeyBindListener(keyBindMonitor, new KeyBinding(
                "Warp Next Task",
                InputUtil.Type.KEYSYM,
                configuration.keybind,
                "Incremental QOL"
        ), this::warpNext);
        this.interactionScheduler = interactionScheduler;
        this.autoLevelUpTask = new InteractionScheduler.Builder<Void, Void>("AutoLevelUp", interactionScheduler, minecraftClientAccessor)
                .priority(1)
                .timeout(20)
                .retries(2)
                .startWith(() -> commandHandler.send("tasks"))
                .step(
                        (screen, ctx) -> screen.title().getString().contains("Tasks"),
                        ctx -> {
                            ItemStack levelUpSlot = null;
                            short levelUpSlotId = 0;
                            for (var slot : ctx.screen().contents()) {
                                var customName = slot.get(DataComponentTypes.CUSTOM_NAME);
                                if (customName != null && customName.getString().equals("Claim Rewards")) {
                                    levelUpSlot = slot;
                                    break;
                                }
                                levelUpSlotId++;
                            }
                            if (levelUpSlot != null) {
                                var lore = levelUpSlot.get(DataComponentTypes.LORE);
                                if (lore != null && lore.lines().getLast().getString().contains("Click to claim rewards")) {
                                    ctx.click(levelUpSlotId);
                                } else {
                                    ctx.complete();
                                }
                            }

                            return false;
                        }
                )
                .step(
                        (screen, ctx) -> screen.title().getString().contains("Tasks"),
                        ctx -> false
                );

        chatHandler.subscribe(new ChatMessageObserver(this));
        worldMonitor.subscribe(new WorldChangeObserver(this));
        tickListenable.subscribe(this);

        options = () -> List.of(Categories.Tasking.General.createConfig(100,
                        Option.<Integer>createBuilder()
                                .name(Text.literal("Warp closest to Next Task"))
                                .binding(
                                        Configuration.defaultKeybind,
                                        () -> configuration.keybind,
                                        v -> configuration.keybind = v
                                )
                                .controller((option) -> () -> new KeyBindController(option))
                                .build()),
                Categories.Tasking.General.createConfig(101,
                        Option.<Boolean>createBuilder()
                                .name(Text.literal("Toggle Auto LevelUp on WarpNext"))
                                .binding(
                                        Configuration.defaultAutoLevelUp,
                                        () -> configuration.autoLevelUp,
                                        v -> configuration.autoLevelUp = v
                                )
                                .controller(BooleanControllerBuilder::create)
                                .build()),
                Categories.Tasking.General.createConfig(102,
                        Option.<Boolean>createBuilder()
                                .name(Text.literal("Warp after Auto LevelUp"))
                                .binding(
                                        Configuration.defaultWarpOnAutoLevelUp,
                                        () -> configuration.warpOnAutoLevelUp,
                                        v -> configuration.warpOnAutoLevelUp = v
                                )
                                .controller(BooleanControllerBuilder::create)
                                .build()),
                Categories.Tasking.General.createConfig(103,
                        Option.<Boolean>createBuilder()
                                .name(Text.literal("Ticket Task Skip Default"))
                                .binding(
                                        Configuration.defaultTicketTaskDefaultSkip,
                                        () -> configuration.ticketTaskDefaultSkip,
                                        v -> configuration.ticketTaskDefaultSkip = v
                                )
                                .controller(b -> BooleanControllerBuilder.create(b)
                                        .valueFormatter(val -> val ? Text.of("Skipped") : Text.of("Not Skipped")))
                                .build()));
    }

    private void warpNext() {
        if (ongoingWarp.compareAndSet(false, true)) {
            if (worldMonitor.currentWorld() != World.BossArenas) {
                var nextUnfinishedTask = taskMonitor.getTaskList().stream().filter(p -> !p.isCompleted() &&
                        // http://32x8.com/sop5_____A-B-C-D-E_____m_1-2-4-9-10-12-17-20-25_____d_0-3-5-6-7-8-11-13-14-15-16-19-21-22-23-24-27-29-30-31_____option-0_____899788866575856596687
                        (!p.isTicket() ||
                                (!configuration.ticketTaskDefaultSkip && taskingOverrides.getTicketTaskOverride(p.getTask()) != TicketTaskOverride.Skipped) ||
                                taskingOverrides.getTicketTaskOverride(p.getTask()) == TicketTaskOverride.NotSkipped)
                        )
                                .findFirst();
                if (nextUnfinishedTask.isPresent()) {
                    var task = nextUnfinishedTask.get().getTask();
                    if (task != null) {
                        if (task.getDescriptor().taskType() != TaskType.Quest && task.getDescriptor().taskType() != TaskType.Tutorial) {
                            var override = taskingOverrides.getOverrides().get(task);
                            if (override != null && !override.warp.isEmpty()) {
                                commandHandler.send("warp " + override.warp);
                                ongoingWarp.set(false);
                            } else {
                                currentTask = task.getDescriptor();
                                commandHandler.send(currentTask.warps().get(warpIndex).getWarpCommand());
                            }
                            if (task.getDescriptor() instanceof GamingTask gamingTask) {
                                commandHandler.send(gamingTask.game().getCommand());
                            }
                            return;
                        }
                    } else if (nextUnfinishedTask.get().getTaskType() == TaskType.Quest || nextUnfinishedTask.get().getTaskType() == TaskType.Tutorial) {
                        chatHandler.sendChatMessage(Text.literal("No being lazy with the quests and tutorials, go complete them!"));
                    } else {
                        chatHandler.sendChatMessage(Text.literal("The task was not correctly identified, send task description to Devs (QoL channel)."));
                    }
                } else {
                    if (this.configuration.autoLevelUp) {
                        var task = autoLevelUpTask.build(null);
                        if (this.configuration.warpOnAutoLevelUp) {
                            task.getFuture().thenRun(() -> {
                                notifyListeners();
                                this.warpNext();
                            });
                        }
                        interactionScheduler.submit(task);
                    } else {
                        chatHandler.sendChatMessage(Text.literal("No incomplete tasks available."));
                    }
                }
            } else {
                chatHandler.sendChatMessage(Text.literal("§4You're in the middle of a boss fight, I don't think it is time to task!"));
            }
            ongoingWarp.set(false);
        }
    }

    @Override
    public void onEvent(EndClientTickListenable result) {
        if (ongoingWarp.get()) {
            tickCounter++;
            if (currentTask.warps().get(warpIndex).isAtPosition() || tickCounter >= MAX_WAIT) {
                tickCounter = 0;
                warpIndex = 0;
                currentTask = null;
                ongoingWarp.set(false);
            }
        }
    }

    private void chatMessageArrived(Text text) {
        if (ongoingWarp.get()) {
            if (text.getString().contains("You don't have access to this warp.")) {
                warpIndex++;
                tickCounter = 0;
                commandHandler.send(currentTask.warps().get(warpIndex).getWarpCommand());
            }
        }
    }

    private void worldChanged(World world) {
        if (ongoingWarp.get()) {
        }
    }

    @Override
    public String getJsonSection() {
        return "warpNext";
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public List<OptionPiece> getOption() {
        return options.get();
    }

    @Override
    public void optionChanged() {
        keyBindListener.updateKeyBind(configuration.keybind);
    }

    private record WorldChangeObserver(WarpNextHotkey warpNextHotkey) implements Observer<WorldMonitor.Event> {
        @Override
        public void onEvent(WorldMonitor.Event result) {
            warpNextHotkey.worldChanged(result.to());
        }
    }

    private record ChatMessageObserver(WarpNextHotkey warpNextHotkey) implements Observer<ChatHandler.Event> {
        @Override
        public void onEvent(ChatHandler.Event result) {
            warpNextHotkey.chatMessageArrived(result.message());
        }
    }

    public static class Configuration {
        private static final int defaultKeybind = GLFW.GLFW_KEY_R;
        private static final boolean defaultAutoLevelUp = true;
        private static final boolean defaultWarpOnAutoLevelUp = true;
        private static final boolean defaultTicketTaskDefaultSkip = false;

        @SerialEntry
        public int keybind = defaultKeybind;
        @SerialEntry
        public boolean autoLevelUp = defaultAutoLevelUp;
        @SerialEntry
        public boolean warpOnAutoLevelUp = defaultWarpOnAutoLevelUp;
        @SerialEntry
        public boolean ticketTaskDefaultSkip = defaultTicketTaskDefaultSkip;
    }
}
