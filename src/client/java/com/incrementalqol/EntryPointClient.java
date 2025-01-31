package com.incrementalqol;

import com.incrementalqol.config.Config;
import com.incrementalqol.config.ConfigHandler;
import com.incrementalqol.config.ConfigScreen;
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
import net.minecraft.client.gui.screen.TaskScreen;
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
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EntryPointClient implements ClientModInitializer {
    public static final String MOD_ID = "incremental-qol";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Item harvestItem = null;
    private boolean commandsRegistered = false;
    private static final Map<String, String> aliasMap = new HashMap<>();
    public static List<Task> taskList = new ArrayList<>();
    private static Screen previousScreen = null;

    private static Map<KeyBinding, String> loadoutKeyBindings;

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static KeyBinding autoBank;
    private static int autoDepositState = 0; // 0 not depositing, 1 depositing



    private static KeyBinding taskWarp;
    private static KeyBinding optionsScreen;


    private final Config config = ConfigHandler.getConfig();



    @Override
    public void onInitializeClient() {
        System.out.println("------- LOADING ---------");

        initializeKeybinds();

        ClientTickEvents.END_CLIENT_TICK.register(EntryPointClient::loop);

        MinecraftClient.getInstance().execute(() -> {
            ClientTickEvents.END_CLIENT_TICK.register(client -> {

                keybindCheck(MinecraftClient.getInstance());

                if (ClientCommandManager.getActiveDispatcher() != null && !commandsRegistered) {
                    System.out.println("Registering commands....");
                    AliasStorage.load();
                    setupCommands();
                    commandsRegistered = true;
                }
            });
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

        ClientTickEvents.END_CLIENT_TICK.register(EntryPointClient::keybindCheck);

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof GenericContainerScreen) {
                handleOpenedMenu(client);
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

                if(config.getSortedByType()){
                    taskList.sort(Comparator.comparing(Task::getTaskType));
                }


                float scaleFactor = (float) config.getHudScale();

                MatrixStack matrixStack = drawContext.getMatrices();
                matrixStack.push();
                matrixStack.scale(scaleFactor, scaleFactor, scaleFactor);


                if(config.getHudBackground()) {
                    drawContext.fill(config.getHudPosX(), config.getHudPosY(), config.getHudPosX() + ((size+1) * 5), config.getHudPosY() + 5 + (15 * taskList.size()), color);
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
    public static void sendMenuOpenInteraction(MinecraftClient client)
    {
        var player = client.player;

        if (player == null){
            return;
        }

        if (autoDepositState != 1){
            autoDepositState = 1;
            player.networkHandler.sendChatCommand("bank");
            scheduler.schedule(() -> {
                autoDepositState = 0;
            }, 500, TimeUnit.MILLISECONDS);
        }
    }

    public static void handleOpenedMenu(MinecraftClient client){
        if (autoDepositState == 0 || client.interactionManager == null || client.player == null) {
            return;
        }

        if (client.currentScreen instanceof GenericContainerScreen screen) {
            int slotId;
            if (screen.getTitle().getString().contains("Bank")) {
                slotId = 21;
            }
            else if (screen.getTitle().getString().contains("Safe")){
                slotId = 23;
            }
            else {
                return;
            }

            client.interactionManager.clickSlot(screen.getScreenHandler().syncId, slotId, 0, SlotActionType.PICKUP, client.player);
            client.setScreen(null);
        }
    }
    private static void keybindCheck(MinecraftClient minecraftClient) {

        loadoutKeyBindings.forEach(((keyBinding, loadout) ->{
            while (keyBinding.wasPressed()){
                assert MinecraftClient.getInstance().player != null;
                MinecraftClient.getInstance().player.networkHandler.sendCommand("wardrobe " + loadout);
                MinecraftClient.getInstance().player.networkHandler.sendCommand("pets " + loadout);
            }
        } ));

        while (optionsScreen.wasPressed()) {
            MinecraftClient.getInstance().setScreen(new ConfigScreen(MinecraftClient.getInstance().currentScreen));
        }

        while (taskWarp.wasPressed()) {
            Optional<Task> firstIncompleteTask = taskList.stream()
                    .filter(task -> !task.isCompleted())
                    .findFirst();

            firstIncompleteTask.ifPresentOrElse(
                    task -> {
                        assert MinecraftClient.getInstance().player != null;
                        MinecraftClient.getInstance().player.networkHandler.sendCommand(task.getWarp().replace(")", ""));
                    },
                    () -> {
                        assert MinecraftClient.getInstance().player != null;
                        MinecraftClient.getInstance().player.sendMessage(Text.literal("No incomplete tasks available."), false);
                    }
            );
        }

        while (autoBank.wasPressed()) {
            assert MinecraftClient.getInstance().player != null;
            sendMenuOpenInteraction(MinecraftClient.getInstance());
        }
    }

    private void setupCommands() {
        ClientCommandManager.getActiveDispatcher().register(
                ClientCommandManager.literal("a")
                        .then(ClientCommandManager.literal("list")
                                .executes(context -> {
                                    context.getSource().sendFeedback(Text.literal("§bListing aliases:§r"));
                                    AliasStorage.getAliases().forEach((alias, command) ->
                                            context.getSource().sendFeedback(Text.literal("§9" + alias + " §a==> §6" + command + "§a.§r"))
                                    );
                                    return 1;
                                })
                        )
                        .then(ClientCommandManager.argument("alias", StringArgumentType.string())
                                .executes(context -> {  // Handles basic alias execution without option
                                    String alias = StringArgumentType.getString(context, "alias");
                                    if (AliasStorage.getAliases().containsKey(alias)) {
                                        String commands = AliasStorage.getCommands(alias);
                                        runCommands(commands, MinecraftClient.getInstance().player, "");
                                        return 1;
                                    } else {
                                        context.getSource().sendFeedback(Text.literal("§cAlias not found: §4" + alias + "§r"));
                                        return 1;
                                    }
                                })
                                .then(ClientCommandManager.argument("option", StringArgumentType.greedyString())
                                        .executes(context -> {  // Handles alias with options
                                            String alias = StringArgumentType.getString(context, "alias");
                                            String option = StringArgumentType.getString(context, "option");
                                            Character mode = option.charAt(0);
                                            String commands = option.substring(1);

                                            if (mode.equals('+')) {
                                                AliasStorage.saveAlias(alias, commands);
                                                context.getSource().sendFeedback(Text.literal("§aSaved alias: §9" + alias + " §a==> §6" + commands + "§a.§r"));
                                            } else if (mode.equals('-')) {
                                                if (!AliasStorage.getAliases().containsKey(alias)) {
                                                    context.getSource().sendFeedback(Text.literal("§cAlias not found: §4" + alias + "§r"));
                                                } else {
                                                    AliasStorage.getAliases().remove(alias);
                                                    AliasStorage.save();
                                                    context.getSource().sendFeedback(Text.literal("§aRemoved alias: §6" + alias + "§a.§r"));
                                                }
                                            } else {
                                                runCommands(AliasStorage.getCommands(alias), MinecraftClient.getInstance().player, option);
                                            }
                                            return 1;
                                        })
                                )
                        )
        );
    }

    private void runCommands(String commands, ClientPlayerEntity player, String warpTo) {
        String[] tokens = commands.split(" ");
        ArrayList<String> toRun = new ArrayList<>();

        int modifying = 0;
        for (String token : tokens) {
            if (token.startsWith("/")) {
                toRun.add(token);
                modifying++;
            } else {
                toRun.set(modifying - 1, toRun.get(modifying - 1) + " " + token);
            }
        }

        for (String command : toRun) {
            if (command != null && !command.isEmpty()) {
                if (player != null) {
                    player.networkHandler.sendChatCommand(command.substring(1));
                }
            }
        }

        if (warpTo != null && !warpTo.isEmpty()) {
            player.networkHandler.sendChatCommand("warp " + warpLocation(warpTo));
        }
    }

    private String warpLocation(String location) {
        if (Arrays.asList("hub", "h").contains(location)) return "hub";

        else if (Arrays.asList("world1", "w1", "1").contains(location)) return "1";
        else if (Arrays.asList("wheat", "wh").contains(location)) return "wheat";
        else if (Arrays.asList("carrot", "car").contains(location)) return "carrot";
        else if (Arrays.asList("beetroot", "beet").contains(location)) return "beetroot";
        else if (Arrays.asList("potato", "pot").contains(location)) return "potato";
        else if (Arrays.asList("coal", "coa").contains(location)) return "coal";
        else if (Arrays.asList("iron", "ir").contains(location)) return "iron";
        else if (Arrays.asList("copper", "cop").contains(location)) return "copper";
        else if (Arrays.asList("gold", "go").contains(location)) return "gold";
        else if (Arrays.asList("redstone", "red").contains(location)) return "redstone";
        else if (Arrays.asList("crab", "cr").contains(location)) return "crab";
        else if (Arrays.asList("hoglin", "hog").contains(location)) return "hoglin";

        else if (Arrays.asList("world2", "w2", "2").contains(location)) return "2";
        else if (Arrays.asList("forge", "for").contains(location)) return "forge";
        else if (Arrays.asList("lush", "lu").contains(location)) return "lush";
        else if (Arrays.asList("veil", "ve").contains(location)) return "veil";
        else if (Arrays.asList("infernal", "inf").contains(location)) return "infernal";
        else if (Arrays.asList("abyss", "ab").contains(location)) return "abyss";
        else if (Arrays.asList("shimmer", "sh").contains(location)) return "shimmer";
        else if (Arrays.asList("garlic", "ga").contains(location)) return "garlic";
        else if (Arrays.asList("corn", "cor").contains(location)) return "corn";
        else if (Arrays.asList("rodrick", "ro").contains(location)) return "rodrick";
        else if (Arrays.asList("sky", "sk").contains(location)) return "sky";
        else if (Arrays.asList("bakery", "ba").contains(location)) return "bakery";
        else return location;
    }

    private void initializeKeybinds() {

        loadoutKeyBindings = Map.of(
                KeyBindingHelper.registerKeyBinding(new KeyBinding("Equip Loadout 1", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_1, "Incremental QOL")), "1",
                KeyBindingHelper.registerKeyBinding(new KeyBinding("Equip Loadout 2", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_2, "Incremental QOL")), "2",
                KeyBindingHelper.registerKeyBinding(new KeyBinding("Equip Loadout 3", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_3, "Incremental QOL")), "3",
                KeyBindingHelper.registerKeyBinding(new KeyBinding("Equip Loadout 4", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_4, "Incremental QOL")), "4",
                KeyBindingHelper.registerKeyBinding(new KeyBinding("Equip Loadout 5", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_5, "Incremental QOL")), "5"
        );


        optionsScreen = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Options Screen",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                "Incremental QOL"
        ));

        autoBank = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Auto Bank",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                "Incremental QOL"
        ));

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
                }
                else if(isPlayerHead(stack)){
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

            Task newTask = new Task(stack.getName().getString(), description, "/warp ", 1, false, world, number, type,true);
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

        LOGGER.warn(type + " xDDDDDD");

        Task newTask = new Task(stack.getName().getString(), description, "/warp ", 1, false, world, number, type,false);
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

        return new String[] {world, number, type};
    }

    public static void loop(MinecraftClient client) {
        Screen screen = client.currentScreen;
        if (screen == null || screen.equals(previousScreen)) {
            return;
        }
        previousScreen = screen;
        parseInventory(screen);
    }
}
