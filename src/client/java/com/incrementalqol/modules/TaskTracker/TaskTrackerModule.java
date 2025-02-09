package com.incrementalqol.modules.TaskTracker;

import com.incrementalqol.common.utils.MenuInteractions;
import com.incrementalqol.common.utils.ScreenInteraction;
import com.incrementalqol.config.Config;
import com.incrementalqol.config.ConfigHandler;
import com.incrementalqol.config.ConfigScreen;
import com.incrementalqol.modules.CommandAliases.AliasStorage;
import com.incrementalqol.modules.DepositHotkey.DepositHotkeyModule;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TaskTrackerModule implements ClientModInitializer {
    public static final String MOD_ID = "incremental-qol";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static List<Task> taskList = new ArrayList<>();

    private static KeyBinding taskWarp;

    private static final Config config = ConfigHandler.getConfig();

    private ScreenInteraction screenInteraction;

    @Override
    public void onInitializeClient() {
        System.out.println("------- LOADING ---------");

        initializeKeybinds();

        screenInteraction = new ScreenInteraction.ScreenInteractionBuilder(
                s -> s instanceof GenericContainerScreen containerScreen && containerScreen.getTitle().getString().equals("Tasks"),
                s -> false,
                s -> s instanceof GenericContainerScreen containerScreen && !containerScreen.getScreenHandler().getInventory().isEmpty(),
                s -> false,
                TaskTrackerModule::parseInventory
        )
                .setAbortCondition(s -> !(s instanceof GenericContainerScreen containerScreen && containerScreen.getTitle().getString().equals("Tasks")))
                .setAbortDelay(5)
                .build();

        ScreenEvents.AFTER_INIT.register((client, screen, w, h) -> {
            if (screen instanceof GenericContainerScreen containerScreen && containerScreen.getTitle().getString().equals("Tasks")) {
                screenInteraction.startAsync();
            }
        });


        ClientTickEvents.END_CLIENT_TICK.register(TaskTrackerModule::keybindCheck);

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

        HudRenderCallback.EVENT.register(((drawContext, renderTickCounter) -> {

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

    private static void keybindCheck(MinecraftClient minecraftClient) {

        while (taskWarp.wasPressed()) {
            Optional<Task> firstIncompleteTask = taskList.stream()
                    .filter(task -> !task.isCompleted())
                    .findFirst();
            assert MinecraftClient.getInstance().player != null;
            firstIncompleteTask.ifPresentOrElse(
                    task -> {
                        assert MinecraftClient.getInstance().player != null;
                        if (task.descriptor != null && config.getAutoSwapWardrobe()) {
                            if (task.descriptor.getDefaultWardrobe() != null) {
                                MinecraftClient.getInstance().player.networkHandler.sendCommand("wardrobe " + task.descriptor.getDefaultWardrobe());
                            }
                            if (task.descriptor.getDefaultHotBarSlot() != null){
                                MinecraftClient.getInstance().player.getInventory().selectedSlot = task.descriptor.getDefaultHotBarSlot();
                                MinecraftClient.getInstance().player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(task.descriptor.getDefaultHotBarSlot()));
                            }
                        }
                        MinecraftClient.getInstance().player.networkHandler.sendCommand(task.getWarp().replace(")", ""));
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

    public static void parseInventory(Screen screen) {
        if (!(screen instanceof GenericContainerScreen containerScreen)) {
            return;
        }

        Inventory inventory = containerScreen.getScreenHandler().getInventory();
        if (inventory.isEmpty()) {
            return;
        }

        if (Objects.equals(containerScreen.getTitle().getString(), "Tasks")) {
            int inventorySize = inventory.size();
            taskList.clear();

            for (int i = 0; i < inventorySize; i++) {
                ItemStack stack = inventory.getStack(i);
                if (isTaskBook(stack)) {
                    processTaskBook(stack);
                } else if (isPlayerHead(stack)) {
                    processTicketTask(stack);
                }
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
