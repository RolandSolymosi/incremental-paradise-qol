package com.incrementalqol.modules.TaskTracker;

import com.incrementalqol.common.utils.ScreenInteraction;
import com.incrementalqol.config.Config;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TaskTrackerModule implements ClientModInitializer {
    public static final String MOD_ID = "incremental-qol";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static List<Task> taskList = new ArrayList<>();

    private static KeyBinding taskWarp;

    private ScreenInteraction screenInteraction;

    private static final AtomicReference<Task> activeWarp = new AtomicReference<>(null);
    private static int fallbackIndex = 0;
    private static int tickCounter = 0;
    private static final AtomicBoolean warpTickOngoing = new AtomicBoolean(false);

    @Override
    public void onInitializeClient() {
        System.out.println("------- LOADING ---------");

        initializeKeybinds();

        screenInteraction = new ScreenInteraction.ScreenInteractionBuilder(
                s -> s.equals("Tasks"),
                s -> !s.isEmpty(),
                (syncId, content) -> parseInventory(content)
        )
                .setKeepScreenHidden(false)
                .build();


        ClientTickEvents.END_CLIENT_TICK.register(TaskTrackerModule::keybindCheck);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            resetWarp();
            screenInteraction.stop();
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            resetWarp();
            screenInteraction.startAsync(true);
        });

        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (message.getString().contains("Completed task")) {

                String pattern = "(?<=^Completed task\\s).*";

                Pattern regex = Pattern.compile(pattern);
                Matcher matcher = regex.matcher(message.getString());

                if (matcher.find()) {
                    String result = matcher.group();
                    for (Task task : taskList) {
                        if (result.equals(task.getName())) {
                            task.setCompleted();
                        }
                    }
                }
            }
        });
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (MinecraftClient.getInstance().player == null) {
                return;
            }
            if (message.getString().contains("You don't have access to this warp.")) {
                if (activeWarp.get() != null) {
                    var command = activeWarp.get().getFallbackWarp(fallbackIndex);
                    if (command == null) {
                        return;
                    }
                    MinecraftClient.getInstance().player.networkHandler.sendCommand(command);
                    tickCounter = 0;
                    fallbackIndex++;
                }
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register((client) -> {
            if (warpTickOngoing.compareAndSet(false, true)) {
                if (activeWarp.get() != null) {
                    tickCounter++;
                    if (tickCounter > 5) {
                        resetWarp();
                    }
                }
                warpTickOngoing.set(false);
            }
        });

        HudRenderCallback.EVENT.register(((drawContext, renderTickCounter) -> {

            var config = Config.HANDLER.instance();
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

            int color = ColorHelper.getArgb(80, 0, 0, 0);
            int CompletedGreen = ColorHelper.getArgb(255, 0, 194, 32);
            int toComplete = ColorHelper.getArgb(255, 255, 255, 255);
            int rectangleX = 10;
            int rectangleY = 10;
            // x1, y1, x2, y2, color

            if (!taskList.isEmpty()) {
                int size = taskList.getFirst().getStrWidth();
                for (Task task : taskList) {
                    if (task.getStrWidth() > size) {
                        size = task.getStrWidth();
                    }
                }

                if (config.getSortedByType()) {
                    taskList.sort(Comparator.comparing(Task::getTaskType));
                }


                float scaleFactor = (float) config.getHudScale();

                MatrixStack matrixStack = drawContext.getMatrices();
                matrixStack.push();
                matrixStack.scale(scaleFactor, scaleFactor, scaleFactor);


                if (config.getHudBackground()) {
                    drawContext.fill(config.getHudPosX(), config.getHudPosY(), config.getHudPosX() + ((size + 1) * 5), config.getHudPosY() + 5 + (15 * taskList.size()), color);
                }
                for (int i = 0; i < taskList.size(); i++) {

                    if (taskList.get(i).isCompleted()) {

                        drawContext.drawText(textRenderer, taskList.get(i).render(true), config.getHudPosX() + 2, config.getHudPosY() + 5 + (15 * i), CompletedGreen, true);
                    } else {
                        drawContext.drawText(textRenderer, taskList.get(i).render(false), config.getHudPosX() + 2, config.getHudPosY() + 5 + (15 * i), toComplete, true);

                    }
                }
                matrixStack.pop();
            }
        }));
    }

    private static void resetWarp() {
        activeWarp.set(null);
        fallbackIndex = 0;
        tickCounter = 0;
        warpTickOngoing.set(false);
    }

    private static void keybindCheck(MinecraftClient minecraftClient) {

        while (taskWarp.wasPressed()) {
            Optional<Task> firstIncompleteTask = taskList.stream()
                    .filter(task -> !task.isCompleted())
                    .findFirst();
            assert MinecraftClient.getInstance().player != null;
            firstIncompleteTask.ifPresentOrElse(
                    task -> {
                        assert MinecraftClient.getInstance().player != null;
                        var config = Config.HANDLER.instance();
                        tickCounter = 0;
                        if (activeWarp.compareAndSet(null, task)) {
                            if (task.descriptor != null) {
                                if (Config.HANDLER.instance().getAutoSwapWardrobe() && task.descriptor.getDefaultWardrobe() != null) {
                                    MinecraftClient.getInstance().player.networkHandler.sendCommand("wardrobe " + config.getWardrobeNameToDefault(task.descriptor.getDefaultWardrobe()));
                                }
                                if (Config.HANDLER.instance().getAutoSwapTools() && task.descriptor.getDefaultHotBarSlot() != null) {
                                    var slotId = config.getSlotToDefault(task.descriptor.getDefaultHotBarSlot());
                                    MinecraftClient.getInstance().player.getInventory().selectedSlot = slotId;
                                    MinecraftClient.getInstance().player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slotId));
                                }
                            }

                            MinecraftClient.getInstance().player.networkHandler.sendCommand(task.getWarp().replace(")", ""));
                        }
                    },
                    () -> {
                        assert MinecraftClient.getInstance().player != null;
                        MinecraftClient.getInstance().player.sendMessage(Text.literal("No incomplete tasks available."), false);
                    }
            );
        }
    }

    private void initializeKeybinds() {

        taskWarp = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Warp to Task Location",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "Incremental QOL"
        ));

    }

    public static void parseInventory(List<ItemStack> content) {
        taskList.clear();

        for (ItemStack stack : content) {
            if (isTaskBook(stack)) {
                processTaskBook(stack);
            } else if (isPlayerHead(stack)) {
                processTicketTask(stack);
            }
        }
    }

    private static boolean isTaskBook(ItemStack stack) {
        Item currentItem = stack.getItem();
        return currentItem.getName().getString().contains("Book");
    }

    private static boolean isPlayerHead(ItemStack stack) {
        Item currentItem = stack.getItem();
        return currentItem.getName().getString().contains("Head");
    }


    private static void processTicketTask(ItemStack stack) {
        LoreComponent lore = stack.get(DataComponentTypes.LORE);
        List<Text> text = lore.lines();
        List<String> blocks = parseLoreLines(text);


        if (blocks.get(0).contains("Ticket Task")) {
            String[] taskDetails = extractTaskDetails(blocks.get(0));

            String description = blocks.size() > 1 ? blocks.get(1) : "";

            String world = taskDetails[0];
            String number = taskDetails[1];
            String type = taskDetails[2];

            Task newTask = new Task(stack.getName().getString(), description, "/warp ", 1, false, world, number, type, true);
            if (stack.getItem().getName().getString().contains("Written")) {
                newTask.setCompleted();
            }
            taskList.add(newTask);
        }
    }

    private static void processTaskBook(ItemStack stack) {
        LoreComponent lore = stack.get(DataComponentTypes.LORE);
        List<Text> text = lore.lines();
        List<String> blocks = parseLoreLines(text);

        String[] taskDetails = extractTaskDetails(blocks.get(0));
        String description = blocks.size() > 1 ? blocks.get(1) : "";

        String world = taskDetails[0];
        String number = taskDetails[1];
        String type = taskDetails[2];

        Task newTask = new Task(stack.getName().getString(), description, "/warp ", 1, false, world, number, type, false);
        if (stack.getItem().getName().getString().contains("Written")) {
            newTask.setCompleted();
        }
        taskList.add(newTask);
    }

    private static List<String> parseLoreLines(List<Text> text) {
        List<String> blocks = new ArrayList<>();
        StringBuilder blockBuilder = new StringBuilder();
        for (Text line : text) {
            if (line.getString().equals(" ")) {
                blocks.add(blockBuilder.toString());
                blockBuilder.setLength(0);
            } else {
                blockBuilder.append(line.getString());
            }
        }
        if (!blockBuilder.isEmpty()) {
            blocks.add(blockBuilder.toString());
        }
        return blocks;
    }


    private static String[] extractTaskDetails(String block) {
        String world = "";
        String number = "";
        String type = "";

        if (block.contains("World") || block.contains("Nightmare")) {
            Pattern descriptorPattern = Pattern.compile("(?<world>World|Nightmare) #(?<number>\\d+)\\s*(?<type>.+?)\\s*Task");
            Matcher m = descriptorPattern.matcher(block);
            if (m.find()) {
                world = m.group("world");
                number = m.group("number");
                type = m.group("type");
            }
        } else {
            Pattern questPattern = Pattern.compile("(?<type>.+) Task");
            Matcher m = questPattern.matcher(block);
            if (m.find()) {
                world = "-";
                type = m.group("type");
            }
        }

        return new String[]{world, number, type};
    }
}
