package com.incrementalqol.modules.ConsumableTimer;

import com.incrementalqol.common.data.ConsumableDatabase;
import com.incrementalqol.common.utils.ScreenInteraction;
import com.incrementalqol.common.utils.Utils;
import com.incrementalqol.config.Config;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsumableTimerModule implements ClientModInitializer {

    public static final List<ConsumableTimer> consumableList = new CopyOnWriteArrayList<>();

    private static ScreenInteraction screenInteraction;

    private static final Pattern TIME_PATTERN = Pattern.compile("Time Left:?\\s*(.+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern CONSUMED_PATTERN = Pattern.compile("You consumed a (.+)", Pattern.CASE_INSENSITIVE);
    private static final String NO_ACTIVE_CONSUMABLES = "No Active Consumables";

    private void startConsumableTimer() {
        screenInteraction.startAsync(true);
    }

    @Override
    public void onInitializeClient() {
        // Screen interaction for Active Consumables screen
        screenInteraction = new ScreenInteraction.ScreenInteractionBuilder(
                "ConsumableTimer",
                s -> s.equals("Active Consumables"),
                s -> !s.isEmpty(),
                (input) -> {
                    parseInventory(input.getRight());
                    return false;
                }
        )
                .setKeepScreenHidden(false)
                .build();
        screenInteraction.startAsync(true);

        // Chat message listener for "You consumed a X"
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            String messageText = message.getString();
            Matcher matcher = CONSUMED_PATTERN.matcher(messageText);
            if (matcher.find()) {
                String consumableName = matcher.group(1).trim();
                // Remove trailing punctuation (like !, ., etc.)
                consumableName = consumableName.replaceAll("[!.]$", "").trim();
                
                // Try exact match first
                Integer durationSeconds = ConsumableDatabase.getDuration(consumableName);
                
                // If not found, try matching any database entry that starts with the consumable name
                if (durationSeconds == null) {
                    for (Map.Entry<String, Integer> entry : ConsumableDatabase.getDatabase().entrySet()) {
                        String dbName = entry.getKey();
                        if (dbName.startsWith(consumableName)) {
                            durationSeconds = entry.getValue();
                            consumableName = dbName; // Use the full database name for display
                            break;
                        }
                    }
                }
                
                if (durationSeconds != null) {
                    long expirationTime = System.currentTimeMillis() + (durationSeconds * 1000L);
                    ConsumableTimer timer = new ConsumableTimer(consumableName, expirationTime);
                    consumableList.add(timer);
                }
            }
        });

        // Tick event to remove expired timers
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            consumableList.removeIf(ConsumableTimer::isExpired);
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            consumableList.clear();
            screenInteraction.stop();
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> startConsumableTimer());

        HudRenderCallback.EVENT.register(((drawContext, renderTickCounter) -> {
            var config = Config.HANDLER.instance();
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

            int color = ColorHelper.getArgb(config.getConsumableHudBackgroundOpacity(), 0, 0, 0);
            int textColor = ColorHelper.getArgb(255, 255, 255, 255);

            if (!MinecraftClient.getInstance().options.hudHidden && config.getIsConsumableHudEnabled() && !consumableList.isEmpty()) {
                int size = 0;
                if (!consumableList.isEmpty()) {
                    size = consumableList.get(0).getStrWidth();
                    for (ConsumableTimer timer : consumableList) {
                        if (timer.getStrWidth() > size) {
                            size = timer.getStrWidth();
                        }
                    }
                }

                float scaleFactor = (float) config.getConsumableHudScale();

                MatrixStack matrixStack = drawContext.getMatrices();
                matrixStack.push();
                matrixStack.scale(scaleFactor, scaleFactor, scaleFactor);

                int posX = config.getConsumableHudPosX();
                int posY = config.getConsumableHudPosY();

                if (config.getConsumableHudBackgroundOpacity() != 0) {
                    drawContext.fill(posX, posY, posX + ((size + 1) * 5), posY + 5 + (15 * consumableList.size()), color);
                }
                for (int i = 0; i < consumableList.size(); i++) {
                    drawContext.drawText(textRenderer, consumableList.get(i).render(), posX + 2, posY + 5 + (15 * i), textColor, true);
                }
                matrixStack.pop();
            }
        }));
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

