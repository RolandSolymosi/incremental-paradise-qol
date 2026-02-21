package com.incrementalclient.services;

import com.incrementalclient.abstractions.ObservableBase;
import com.incrementalclient.interfaces.Listener;
import com.incrementalclient.interfaces.Observer;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.internals.events.ClientPlayConnectionObservable;
import com.incrementalclient.internals.events.EndClientTickListenable;
import com.incrementalclient.common.utils.Utils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActiveConsumableMonitor extends ObservableBase<Observer<List<ActiveConsumableMonitor.ConsumableTimer>>, List<ActiveConsumableMonitor.ConsumableTimer>> {

    private final List<ConsumableTimer> consumableList = new CopyOnWriteArrayList<>();

    private final InteractionScheduler<Void> interactionScheduler;
    private final InteractionScheduler.Builder<Void, Void> refreshConsumablesBuilder;

    private static final Pattern TIME_PATTERN = Pattern.compile("Time Left:?\\s*(.+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern CONSUMED_PATTERN = Pattern.compile("You consumed a (.+)", Pattern.CASE_INSENSITIVE);
    private static final String NO_ACTIVE_CONSUMABLES = "No Active Consumables";

    public ActiveConsumableMonitor(
            ClientPlayConnectionObservable clientPlayConnectionObservable,
            EndClientTickListenable endClientTickListenable,
            WorldMonitor worldMonitor,
            InteractionScheduler<Void> interactionScheduler,
            CommandHandler commandHandler,
            ChatHandler chatHandler,
            MinecraftClientAccessor mcAccessor
    ) {
        this.interactionScheduler = interactionScheduler;
        clientPlayConnectionObservable.subscribe(new Observer.DefaultObserver<>((e) -> {
            if (e == ClientPlayConnectionObservable.EventKind.Connect) {
                refresh();
            } else {
                consumableList.clear();
            }
        }));
        chatHandler.subscribe(new Observer.DefaultObserver<>((e)-> {
            if (CONSUMED_PATTERN.matcher(e.message().getString()).find()) {
                refresh();
            }
        }));

        endClientTickListenable.subscribe(new Listener.DefaultListener(this::updateCounter));
        worldMonitor.subscribe(new Observer.DefaultObserver<>((world) -> {
            if (world.from().getRealm() != world.to().getRealm()){
                refresh();
            }
        }));

        this.refreshConsumablesBuilder = new InteractionScheduler.Builder<Void, Void>("ConsumableRefresh", interactionScheduler, mcAccessor)
                .priority(3)
                .timeout(20)
                .retries(2)
                .startWith(() -> commandHandler.send("consumable"))

                // Step 1: Navigate to the Active Consumables Sub-menu
                .step(
                        (screen, ctx) -> screen.title().getString().contains("Consumable"),
                        ctx -> {
                            ctx.click(21);
                            return false;
                        }
                )

                // Step 2: Parse the actual timers
                .step(
                        (screen, ctx) -> screen.title().getString().equals("Active Consumables"),
                        ctx -> {
                            parseInventory(ctx.screen().contents());
                            ctx.complete();
                            return false;
                        }
                );
    }

    public List<ConsumableTimer> getConsumableList(){
        return this.consumableList;
    }

    private void updateCounter(){
        consumableList.removeIf(ConsumableTimer::isExpired);
    }

    private void refresh() {
        // Use Void context since this is a global module
        interactionScheduler.submit(refreshConsumablesBuilder.build(null));
    }

    // --- Parsing Logic (Maintained from original) ---

    public void parseInventory(List<ItemStack> content) {
        // Build a new list from the inventory instead of clearing existing one
        var newTimers = new ArrayList<ConsumableTimer>();

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
                if (displayName.contains(NO_ACTIVE_CONSUMABLES)) {
                    foundNoActiveConsumables = true;
                    continue;
                }
            }

            // This should be a consumable item - process it
            var timer = processConsumableBuff(stack);
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

    private ConsumableTimer processConsumableBuff(ItemStack stack) {
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

        // Match hours (full word only)
        Pattern hoursPattern = Pattern.compile("(\\d+)\\s+hours?", Pattern.CASE_INSENSITIVE);
        Matcher hoursMatcher = hoursPattern.matcher(timeLeft);
        if (hoursMatcher.find()) {
            totalSeconds += Long.parseLong(hoursMatcher.group(1)) * 3600;
        }

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

    public static class ConsumableTimer {
        private final String buffName;
        private final long expirationTimeMillis; // When the timer expires (System.currentTimeMillis())

        public ConsumableTimer(String buffName, long expirationTimeMillis) {
            this.buffName = buffName;
            this.expirationTimeMillis = expirationTimeMillis;
        }

        public String getBuffName() {
            return buffName;
        }

        public long getExpirationTimeMillis() {
            return expirationTimeMillis;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() >= expirationTimeMillis;
        }

        public String getTimeLeftString() {
            long now = System.currentTimeMillis();
            long timeLeftMillis = expirationTimeMillis - now;

            if (timeLeftMillis <= 0) {
                return "Expired";
            }

            long totalSeconds = timeLeftMillis / 1000;
            long hours = totalSeconds / 3600;
            long minutes = (totalSeconds % 3600) / 60;
            long seconds = totalSeconds % 60;

            if (hours > 0) {
                return String.format("%dh %dm %ds", hours, minutes, seconds);
            } else if (minutes > 0) {
                return String.format("%dm %ds", minutes, seconds);
            } else {
                return String.format("%ds", seconds);
            }
        }
    }
}