package com.incrementalqol.modules.CommandAliases;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;


public class CommandAliasModule implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> setupCommands(dispatcher));
    }

    private void setupCommands(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        System.out.println("Registering commands....");
        AliasStorage.load();
        dispatcher.register(
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
}
