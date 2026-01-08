package com.incrementalclient.featues.Tasking;

import com.incrementalclient.common.data.World;
import com.incrementalclient.common.data.tasks.TaskType;
import com.incrementalclient.common.data.tasks.abstractions.ITask;
import com.incrementalclient.config.controllers.KeyBindController;
import com.incrementalclient.interfaces.Configurable;
import com.incrementalclient.interfaces.Observer;
import com.incrementalclient.internals.events.EndClientTickListenable;
import com.incrementalclient.services.*;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class WarpNextHotkey implements Configurable<WarpNextHotkey.Configuration>, Observer<EndClientTickListenable> {
    private static final int MAX_WAIT = 20;

    private final CommandHandler commandHandler;
    private final ChatHandler chatHandler;
    private final WorldMonitor worldMonitor;
    private final TaskMonitor taskMonitor;
    private final TaskingOverrides taskingOverrides;

    private final WarpNextHotkey.Configuration configuration = new Configuration();

    private final List<OptionPiece> options;
    private final KeyBindMonitor.KeyBindListener keyBindListener;

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
            EndClientTickListenable tickListenable
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
        ),this::warpNext);
        chatHandler.subscribe(new ChatMessageObserver(this));
        worldMonitor.subscribe(new WorldChangeObserver(this));
        tickListenable.subscribe(this);

        options = List.of(new OptionPiece(
                "Tasking",
                0,
                "Hotkeys",
                "",
                0,
                Option.<Integer>createBuilder()
                .name(Text.literal("Warp closest to Next Task"))
                .binding(
                        configuration.keybind,
                        () -> configuration.keybind,
                        v -> configuration.keybind = v
                )
                .controller((option) -> () -> new KeyBindController(option))
                .build()));
    }

    private void warpNext() {
        if (ongoingWarp.compareAndSet(false, true)) {
            if (worldMonitor.currentWorld() != World.BossArenas) {
                var nextUnfinishedTask = taskMonitor.getTaskList().stream().filter(p ->
                        !p.isCompleted() && (!p.isTicket() || !taskingOverrides.getOverrides().containsKey(p.getTask()) || !taskingOverrides.getOverrides().get(p.getTask()).skipTicket)
                ).findFirst();
                if (nextUnfinishedTask.isPresent()) {
                    var task = nextUnfinishedTask.get().getTask();
                    if (task != null) {
                        if (task.getDescriptor().taskType() != TaskType.Quest && task.getDescriptor().taskType() != TaskType.Tutorial) {
                            var override = taskingOverrides.getOverrides().get(task);
                            if (override != null && !override.warp.isEmpty()){
                                commandHandler.send(override.warp);
                                return;
                            }
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
    public String getJsonSection() {
        return "warpNext";
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public List<OptionPiece> getOption() {
        return options;
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
        @SerialEntry
        public int keybind = GLFW.GLFW_KEY_N;
    }
}
