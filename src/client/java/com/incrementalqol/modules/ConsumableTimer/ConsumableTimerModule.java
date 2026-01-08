package com.incrementalqol.modules.ConsumableTimer;

import com.incrementalqol.common.data.World;
import com.incrementalqol.common.utils.ScreenInteraction;
import com.incrementalqol.common.utils.Utils;
import com.incrementalqol.common.utils.WorldChangeNotifier;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.util.Pair;

public class ConsumableTimerModule implements ClientModInitializer {

    public static final List<ConsumableTimer> consumableList = new CopyOnWriteArrayList<>();

    private static ScreenInteraction screenInteraction;
    private static ScreenInteraction enforceRefreshScreenInteraction;

    private static final Pattern TIME_PATTERN = Pattern.compile("Time Left:?\\s*(.+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern CONSUMED_PATTERN = Pattern.compile("You consumed a (.+)", Pattern.CASE_INSENSITIVE);
    private static final String NO_ACTIVE_CONSUMABLES = "No Active Consumables";

    private void startConsumableTimer() {
        screenInteraction.startAsync(true);
    }

    @Override
    public void onInitializeClient() {
        // Screen interaction for Consumables menu navigation
        // First step: detect initial consumables screen and click the "Active Consumable Buffs" cookie
        screenInteraction = new ScreenInteraction.ScreenInteractionBuilder(
                "ConsumableTimer",
                s -> s.contains("Consumable") && !s.equals("Active Consumables"),
                s -> !s.isEmpty(),
                (input) -> {
                    List<ItemStack> content = input.getRight();
                    
                    // First try to find by name "Active Consumable Buffs"
                    for (short slotId = 0; slotId < content.size(); slotId++) {
                        ItemStack stack = content.get(slotId);
                        if (stack.isEmpty()) continue;
                        
                        var customName = stack.get(DataComponentTypes.CUSTOM_NAME);
                        if (customName != null) {
                            String displayName = customName.getString();
                            if (displayName.contains("Active Consumable Buffs")) {
                                // Found it by name, click it
                                ScreenInteraction.WellKnownInteractions.ClickSlot(
                                    input.getLeft(),
                                    slotId,
                                    ScreenInteraction.WellKnownInteractions.Button.Left,
                                    SlotActionType.PICKUP
                                );
                                return true;
                            }
                        }
                    }
                    
                    // Fallback: click at specific position (3rd row, 4th column = row 2, col 3 in 0-indexed)
                    // Slot = row * 9 + col = 2 * 9 + 3 = 21
                    short targetSlot = (short)(2 * 9 + 3); // 21
                    if (targetSlot < content.size() && !content.get(targetSlot).isEmpty()) {
                        ScreenInteraction.WellKnownInteractions.ClickSlot(
                            input.getLeft(),
                            targetSlot,
                            ScreenInteraction.WellKnownInteractions.Button.Left,
                            SlotActionType.PICKUP
                        );
                        return true;
                    }
                    
                    return false; // No valid item found
                }
        )
                .addInteraction(
                        // Second step: detect "Active Consumables" screen and parse inventory
                        s -> s.equals("Active Consumables"),
                        s -> !s.isEmpty(),
                        (input) -> {
                            parseInventory(input.getRight());
                            return false;
                        }
                )
                .setKeepScreenHidden(true)
                .build();
        screenInteraction.startAsync(true);

        // Screen interaction for refreshing consumables when "You consumed a X" is detected
        // This will open the menu and get the exact time left from the server
        // First interaction: handle "Active Consumables" directly (in case it opens immediately)
        // or handle the initial consumables menu
        enforceRefreshScreenInteraction = new ScreenInteraction.ScreenInteractionBuilder(
                "ConsumableTimerRefresh",
                // First step: detect "Active Consumables" screen (both screens have this name)
                // We differentiate by checking if the cookie "Active Consumable Buffs" exists
                s -> s.equals("Active Consumables"),
                s -> {
                    // Check if this is the first screen (with the cookie) or the second screen (with consumables list)
                    for (ItemStack stack : s) {
                        if (stack.isEmpty()) continue;
                        var customName = stack.get(DataComponentTypes.CUSTOM_NAME);
                        if (customName != null) {
                            String displayName = customName.getString();
                            // If we find "Active Consumable Buffs", this is the first screen
                            if (displayName.contains("Active Consumable Buffs")) {
                                return true; // First screen - needs to click
                            }
                        }
                    }
                    // If we don't find the cookie, check if there are actual consumable items with lore
                    // This would be the second screen with the list
                    for (ItemStack stack : s) {
                        if (stack.isEmpty()) continue;
                        String itemName = stack.getItem().getName().getString();
                        // Skip borders and empty slots
                        if (itemName.contains("Black Stained Glass Pane") || 
                            itemName.contains("black_stained_glass_pane") ||
                            itemName.contains("White Stained Glass Pane") ||
                            itemName.contains("white_stained_glass_pane")) {
                            continue;
                        }
                        var customName = stack.get(DataComponentTypes.CUSTOM_NAME);
                        if (customName != null && customName.getString().contains("Go Back")) {
                            continue;
                        }
                        // If we find an item with lore (likely a consumable), this is the second screen
                        if (stack.get(DataComponentTypes.LORE) != null) {
                            return true; // Second screen - needs to parse
                        }
                    }
                    return false;
                },
                (input) -> {
                    List<ItemStack> content = input.getRight();
                    
                    // Check if this is the first screen (has the cookie) or second screen (has consumables)
                    boolean hasCookie = false;
                    for (short slotId = 0; slotId < content.size(); slotId++) {
                        ItemStack stack = content.get(slotId);
                        if (stack.isEmpty()) continue;
                        
                        var customName = stack.get(DataComponentTypes.CUSTOM_NAME);
                        if (customName != null) {
                            String displayName = customName.getString();
                            if (displayName.contains("Active Consumable Buffs")) {
                                hasCookie = true;
                                // Found the cookie, click it
                                ScreenInteraction.WellKnownInteractions.ClickSlot(
                                    input.getLeft(),
                                    slotId,
                                    ScreenInteraction.WellKnownInteractions.Button.Left,
                                    SlotActionType.PICKUP
                                );
                                return true;
                            }
                        }
                    }
                    
                    // If no cookie found, this is the second screen - parse directly
                    if (!hasCookie) {
                        parseInventory(content);
                        return true; // Mark as complete
                    }
                    
                    // Fallback: try clicking at specific position if cookie not found by name
                    short targetSlot = (short)(2 * 9 + 3); // 21
                    if (targetSlot < content.size() && !content.get(targetSlot).isEmpty()) {
                        ScreenInteraction.WellKnownInteractions.ClickSlot(
                            input.getLeft(),
                            targetSlot,
                            ScreenInteraction.WellKnownInteractions.Button.Left,
                            SlotActionType.PICKUP
                        );
                        return true;
                    }
                    
                    return false;
                }
        )
                .addInteraction(
                        // Second step: detect "Active Consumables" screen after clicking the cookie
                        s -> s.equals("Active Consumables"),
                        s -> !s.isEmpty(),
                        (input) -> {
                            // Parse the inventory and mark as complete
                            parseInventory(input.getRight());
                            return true;
                        }
                )
                .setStartingAction((c) -> 
                        c.player.networkHandler.sendChatCommand("consumable")
                )
                .setKeepScreenHidden(true)
                .build();
        
        // Register the listener immediately so it's always ready to intercept screens
        // We'll only activate it when needed via startAsync
        enforceRefreshScreenInteraction.register();

        // Chat message listener for "You consumed a X"
        // Instead of using the database, we open the menu to get the exact time left from the server
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            String messageText = message.getString();
            Matcher matcher = CONSUMED_PATTERN.matcher(messageText);
            if (matcher.find() && MinecraftClient.getInstance().player != null) {
                // Execute on client thread to ensure listener activation happens before command is sent
                // This ensures the listener is active and ready to intercept OpenScreenS2CPacket
                MinecraftClient.getInstance().execute(() -> {
                    // Start the interaction - this will activate the listener and then send the command
                    // The listener is already registered, so it will be ready to intercept immediately
                    enforceRefreshScreenInteraction.startAsync(false);
                });
            }
        });

        // Tick event to remove expired timers and hide screen if needed
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            consumableList.removeIf(ConsumableTimer::isExpired);
            
            // Immediately hide screen if it's a consumables screen during active interaction
            // This ensures the screen doesn't appear even if there's a timing issue
            if (client.currentScreen != null && 
                ScreenInteraction.ScreenInteractionManager.anyActiveInteractionOngoing()) {
                String screenTitle = client.currentScreen.getTitle().getString();
                // Hide consumables-related screens that appear during interaction
                if (screenTitle.contains("Consumable") && 
                    !(client.currentScreen instanceof net.minecraft.client.gui.screen.ingame.InventoryScreen)) {
                    client.setScreen(null);
                }
            }
        });

        // World change listener - refresh consumables when switching worlds
        WorldChangeNotifier.Register((Pair<World, Boolean> input) -> {
            var future = new CompletableFuture<Boolean>();
            // input.getRight() is true when switching between Normal and Nightmare realms
            // Always refresh consumables when changing worlds, but clear list only when changing realms
            if (input.getRight()) {
                // Clear the list when changing realms (different consumables in different realms)
                consumableList.clear();
            }
            // Refresh consumables for the new world/realm
            MinecraftClient.getInstance().execute(() -> {
                enforceRefreshScreenInteraction.startAsync(false).thenAccept(future::complete);
            });
            return future;
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            consumableList.clear();
            screenInteraction.stop();
            enforceRefreshScreenInteraction.stop();
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            startConsumableTimer();
            // Check consumables when joining the game
            MinecraftClient.getInstance().execute(() -> {
                enforceRefreshScreenInteraction.startAsync(false);
            });
        });

        // HUD rendering is now handled by HudModule
    }

    public static void parseInventory(List<ItemStack> content) {
        // Build a new list from the inventory instead of clearing existing one
        List<ConsumableTimer> newTimers = new ArrayList<>();

        // Screen is 9x6 = 54 slots
        // Borders are black_stained_glass_pane
        // "Go Back" item is in the middle of the lowest row (row 5, column 4 = slot index 49 in 0-based)
        // Items to read are between borders, either consumable items or white_stained_glass_pane (empty slots)

        boolean foundNoActiveConsumables = false;
        
        for (int i = 0; i < content.size() && i < 54; i++) {
            ItemStack stack = content.get(i);
            
            // Get item name using the same method as TaskTracker
            String itemName = stack.getItem().getName().getString();
            
            // Check if it's a border item (black_stained_glass_pane)
            if (itemName.contains("Black Stained Glass Pane") || itemName.contains("black_stained_glass_pane")) {
                continue;
            }
            
            // Check if it's an empty slot (white_stained_glass_pane)
            if (itemName.contains("White Stained Glass Pane") || itemName.contains("white_stained_glass_pane")) {
                continue;
            }
            
            // Check if it's the "Go Back" item (in the middle of the lowest row, slot 49)
            var customName = stack.get(DataComponentTypes.CUSTOM_NAME);
            if (customName != null && customName.getString().contains("Go Back")) {
                continue;
            }
            
            // Check if it's the "No Active Consumables" item
            if (customName != null) {
                String displayName = customName.getString();
                if (displayName.contains(NO_ACTIVE_CONSUMABLES) || displayName.equals(NO_ACTIVE_CONSUMABLES)) {
                    foundNoActiveConsumables = true;
                    continue;
                }
            }
            
            // This should be a consumable item - process it
            ConsumableTimer timer = processConsumableBuff(stack);
            if (timer != null) {
                newTimers.add(timer);
            }
        }
        
        // Only update the list if we found "No Active Consumables" or if we found items in the inventory
        // This preserves existing timers (from chat messages) if the inventory is empty or still loading
        if (foundNoActiveConsumables) {
            consumableList.clear();
        } else if (!newTimers.isEmpty()) {
            // Replace with server state (inventory contents take precedence)
            consumableList.clear();
            consumableList.addAll(newTimers);
        }
        // Otherwise, keep existing list unchanged
    }

    private static ConsumableTimer processConsumableBuff(ItemStack stack) {
        // Use stack.getName() like TaskTracker does - gets display name (custom name if present, otherwise item name)
        String buffName = stack.getName().getString();
        LoreComponent lore = stack.get(DataComponentTypes.LORE);
        
        if (lore == null) {
            return null;
        }

        List<Text> text = lore.lines();
        List<String> blocks = Utils.parseLoreLines(text);

        // Parse "Time Left: X" to calculate expiration time
        String timeLeft = "";
        for (String block : blocks) {
            Matcher matcher = TIME_PATTERN.matcher(block);
            if (matcher.find()) {
                timeLeft = matcher.group(1).trim();
                break;
            }
        }

        if (!timeLeft.isEmpty()) {
            long expirationTime = parseTimeLeftToExpiration(timeLeft);
            if (expirationTime > 0) {
                return new ConsumableTimer(buffName, expirationTime);
            }
        }
        return null;
    }

    private static long parseTimeLeftToExpiration(String timeLeft) {
        // Parse strings like "5 Minutes", "57 Seconds", "5 Minutes 30 Seconds", etc.
        long totalSeconds = 0;
        
        // Match minutes (full word only)
        Pattern minutesPattern = Pattern.compile("(\\d+)\\s+minutes?", Pattern.CASE_INSENSITIVE);
        Matcher minutesMatcher = minutesPattern.matcher(timeLeft);
        if (minutesMatcher.find()) {
            totalSeconds += Long.parseLong(minutesMatcher.group(1)) * 60;
        }
        
        // Match seconds (full word only)
        Pattern secondsPattern = Pattern.compile("(\\d+)\\s+seconds?", Pattern.CASE_INSENSITIVE);
        Matcher secondsMatcher = secondsPattern.matcher(timeLeft);
        if (secondsMatcher.find()) {
            totalSeconds += Long.parseLong(secondsMatcher.group(1));
        }
        
        if (totalSeconds > 0) {
            return System.currentTimeMillis() + (totalSeconds * 1000);
        }
        
        return 0;
    }
}

