package com.incrementalclient.featues;

import com.incrementalclient.common.data.Warp;
import com.incrementalclient.common.data.World;
import com.incrementalclient.common.data.tasks.TaskType;
import com.incrementalclient.common.data.tasks.abstractions.ITask;
import com.incrementalclient.config.components.KeyBindController;
import com.incrementalclient.interfaces.Configurable;
import com.incrementalclient.interfaces.Observer;
import com.incrementalclient.internals.events.EndClientTickListenable;
import com.incrementalclient.services.*;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.atomic.AtomicBoolean;

public class WarpNextHotkey implements Configurable<WarpNextHotkey.Configuration, Option<Integer>>, Observer<EndClientTickListenable> {
    private static final int MAX_WAIT = 20;

    private final CommandHandler commandHandler;
    @org.jetbrains.annotations.NotNull
    private final ChatHandler chatHandler;
    @org.jetbrains.annotations.NotNull
    private final WorldMonitor worldMonitor;
    private final TaskMonitor taskMonitor;

    private final WarpNextHotkey.Configuration configuration = new Configuration();

    private final Option<Integer> options;
    private final KeyBinding keyBind;

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
            EndClientTickListenable tickListenable
    ) {
        this.commandHandler = commandHandler;
        this.chatHandler = chatHandler;
        this.worldMonitor = worldMonitor;
        this.taskMonitor = taskMonitor;
        keyBind = new KeyBinding(
                "Warp Next Task",
                InputUtil.Type.KEYSYM,
                configuration.keybind,
                "Incremental QOL"
        );
        keyBindMonitor.subscribe(new KeyBindMonitor.KeyBindListener(keyBind, this::warpNext));
        chatHandler.subscribe(new ChatMessageObserver(this));
        worldMonitor.subscribe(new WorldChangeObserver(this));
        tickListenable.subscribe(this);

        options = Option.<Integer>createBuilder()
                .name(Text.literal("Warp closest to Next Task"))
                .binding(
                        configuration.keybind,
                        () -> configuration.keybind,
                        v -> configuration.keybind = v
                )
                .controller((option) -> () -> new KeyBindController(option))
                .build();
    }

    private void warpNext() {
        if (ongoingWarp.compareAndSet(false, true)) {
            if (worldMonitor.currentWorld() != World.BossArenas) {
                var nextUnfinishedTask = taskMonitor.getTaskList().stream().filter(p -> !p.isComplete()).findFirst();
                if (nextUnfinishedTask.isPresent()) {
                    var task = nextUnfinishedTask.get().getTask();
                    if (task != null) {
                        if (task.getDescriptor().taskType() != TaskType.Quest && task.getDescriptor().taskType() != TaskType.Tutorial) {
                            currentTask = task.getDescriptor();
                            commandHandler.send(currentTask.warps().get(warpIndex).getWarpCommand());
                            return;
                        } else {
                            chatHandler.sendChatMessage(Text.literal("No being lazy with the quests and tutorials, go complete them!"));
                        }
                    } else {
                        chatHandler.sendChatMessage(Text.literal("The task was not correctly identified, send task description to Devs (QoL channel)."));
                    }
                } else {
                    chatHandler.sendChatMessage(Text.literal("No incomplete tasks available."));
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
            if (currentTask.warps().get(warpIndex).isAtPosition() || tickCounter >= MAX_WAIT){
                tickCounter = 0;
                warpIndex = 0;
                currentTask = null;
                ongoingWarp.set(false);
            }
        }
    }

    private void chatMessageArrived(Text text) {
        if (ongoingWarp.get()) {
            if (text.getString().contains("You don't have access to this warp.")){
                warpIndex++;
                tickCounter = 0;
                commandHandler.send(currentTask.warps().get(warpIndex).getWarpCommand());
            }
        }
    }

    private void worldChanged(World world) {
        if (ongoingWarp.get()) {}
    }


    @Override
    public String getCategory() {
        return "Hotkeys";
    }

    @Override
    public String getGroupName() {
        return "Tasking";
    }

    @Override
    public String getJsonSection() {
        return "warpNext";
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public Option<Integer> getOption() {
        return options;
    }

    @Override
    public void optionChanged() {
        keyBind.setBoundKey(InputUtil.fromKeyCode(configuration.keybind, 0));
        KeyBinding.updateKeysByCode();
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
        @SerialEntry
        public int keybind = GLFW.GLFW_KEY_N;
    }
}
