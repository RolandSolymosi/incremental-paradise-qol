package com.incrementalqol;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;
import org.lwjgl.glfw.GLFW;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EntryPointClient implements ClientModInitializer {
    public static final String MOD_ID = "incremental-qol";
    //public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Item harvestItem = null;
    private boolean commandsRegistered = false;
    private static final Map<String, String> aliasMap = new HashMap<>();
    public static List<Task> taskList = new ArrayList<>();
    private static Screen previousScreen = null;


    private KeyBinding loadout1;
    private KeyBinding loadout2;
    private KeyBinding loadout3;
    private KeyBinding loadout4;
    private KeyBinding loadout5;


    @Override
    public void onInitializeClient() {
        System.out.println("------- LOADING ---------");

        initializeKeybinds();
        ClientTickEvents.END_CLIENT_TICK.register(EntryPointClient::loop);

        MinecraftClient.getInstance().execute(() -> {
            ClientTickEvents.END_CLIENT_TICK.register(client -> {

                keybindCheck();

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

        HudRenderCallback.EVENT.register(((drawContext, renderTickCounter) -> {

            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

            int color = ColorHelper.getArgb(100, 0, 0, 0);
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

                drawContext.fill(rectangleX, rectangleY, rectangleX + (size * 6), rectangleY + 5 + (15 * taskList.size()), color);
                for (int i = 0; i < taskList.size(); i++) {

                    if (taskList.get(i).isCompleted()) {
                        drawContext.drawText(textRenderer, taskList.get(i).render(), rectangleX + 2, rectangleY + 5 + (15 * i), CompletedGreen, true);
                    } else {
                        drawContext.drawText(textRenderer, taskList.get(i).render(), rectangleX + 2, rectangleY + 5 + (15 * i), toComplete, true);

                    }
                }
            }
        }));
    }

    private void keybindCheck() {

        while (loadout1.wasPressed()) {
            assert MinecraftClient.getInstance().player != null;
            MinecraftClient.getInstance().player.networkHandler.sendCommand("wardrobe 1");
            MinecraftClient.getInstance().player.networkHandler.sendCommand("pets 1");

        }
        while (loadout2.wasPressed()) {
            assert MinecraftClient.getInstance().player != null;
            MinecraftClient.getInstance().player.networkHandler.sendCommand("wardrobe 2");
            MinecraftClient.getInstance().player.networkHandler.sendCommand("pets 2");

        }
        while (loadout3.wasPressed()) {
            assert MinecraftClient.getInstance().player != null;
            MinecraftClient.getInstance().player.networkHandler.sendCommand("wardrobe 3");
            MinecraftClient.getInstance().player.networkHandler.sendCommand("pets 3");

        }
        while (loadout4.wasPressed()) {
            assert MinecraftClient.getInstance().player != null;
            MinecraftClient.getInstance().player.networkHandler.sendCommand("wardrobe 4");
            MinecraftClient.getInstance().player.networkHandler.sendCommand("pets 4");

        }
        while (loadout5.wasPressed()) {
            assert MinecraftClient.getInstance().player != null;
            MinecraftClient.getInstance().player.networkHandler.sendCommand("wardrobe 5");
            MinecraftClient.getInstance().player.networkHandler.sendCommand("pets 5");

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
        else return "";
    }

    private void initializeKeybinds() {

        loadout1 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Equip Loadout 1",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_1,
                "Incremental QOL"
        ));

        loadout2 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Equip Loadout 2",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_2,
                "Incremental QOL"
        ));

        loadout3 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Equip Loadout 3",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_3,
                "Incremental QOL"
        ));
        loadout4 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Equip Loadout 4",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_4,
                "Incremental QOL"
        ));
        loadout5 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Equip Loadout 5",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_5,
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
                ItemStack stack = inventory.getStack(i); // Get the item stack
                Item currentItem = stack.getItem(); // Get the item

                Text name = stack.getName(); // Get the display name
                if (currentItem.getName().getString().contains("Book")) {

                    LoreComponent lore = stack.get(DataComponentTypes.LORE);
                    List<Text> text = lore.styledLines();

                    String description = text.get(2).getString() + text.get(3).getString();
                    int pixelSize = text.get(2).getString().length() + text.get(3).getString().length();
                    Task newTask = new Task(name.getString(), description, "/warp ", pixelSize, false);

                    if (currentItem.getName().getString().contains("Written")) {
                        newTask.setCompleted();
                    }
                    taskList.add(newTask);
                }
            }
        }
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
