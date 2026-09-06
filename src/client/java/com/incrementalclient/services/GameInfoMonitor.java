package com.incrementalclient.services;

import com.incrementalclient.common.CurrencyValue;
import com.incrementalclient.common.data.CurrencyType;
import com.incrementalclient.common.data.ProgressLayer;
import com.incrementalclient.common.utils.PlayerProgressParser;
import com.incrementalclient.interfaces.Listener;
import com.incrementalclient.interfaces.Observable;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.internals.OverlayMessageObservable;
import com.incrementalclient.internals.ScoreboardChangedListenable;
import com.incrementalclient.internals.events.ClientReceiveMessageEventsObservable;
import com.incrementalclient.internals.events.EndClientTickListenable;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.*;
import net.minecraft.text.Text;

import java.time.LocalDate;
import java.util.*;

/**
 * Centralized class for collecting game data before vanilla HUD elements are rendered.
 * This allows us to hide vanilla elements while still having access to the data.
 */
public class GameInfoMonitor {
    private boolean debugMode = false;
    
    // Action bar / overlay event storage
    private Text clientOverlay = Text.empty();
    
    // Scoreboard data
    private Text scoreboardTitle = Text.empty();
    private final List<String> scoreboardLines = new ArrayList<>();
    private final List<Text> scoreboardComponents = new ObjectArrayList<>();
    
    // Persistent player progress data (accumulates across worlds)
    private final PlayerProgressData persistentProgressData = new PlayerProgressData();
    
    // Current snapshot from the latest scoreboard parse (for current currencies only)
    private PlayerProgressParser.PlayerProgressSnapshot currentSnapshot = null;
    
    // Player stats (from action bar or directly from player)
    private float playerHealth = 0;
    private float playerMaxHealth = 20;
    private float lastParsedHealth = 0;
    private float lastParsedMaxHealth = 20;
    private int playerFoodLevel = 20;
    private float playerSaturation = 5.0f;
    private int playerExperienceLevel = 0;
    private float playerExperienceProgress = 0.0f;
    private final ChatHandler chatHandler;
    private final MinecraftClientAccessor mcAccessor;

    public GameInfoMonitor(
            EndClientTickListenable endClientTickListenable,
            ClientReceiveMessageEventsObservable clientReceiveMessageEventsObservable,
            OverlayMessageObservable overlayMessageObservable,
            ScoreboardChangedListenable scoreboardChangedListenable,
            ChatHandler chatHandler,
            MinecraftClientAccessor mcAccessor
    ){
        this.chatHandler = chatHandler;
        this.mcAccessor = mcAccessor;
        endClientTickListenable.subscribe(new Listener.DefaultListener(this::processTick));
        clientReceiveMessageEventsObservable.subscribe(new Observable.DefaultObserver<>(this::processMessageEvent));
        overlayMessageObservable.subscribe(new Observable.DefaultObserver<>(this::captureOverlayMessage));
        scoreboardChangedListenable.subscribe(new Listener.DefaultListener(this::refreshScoreboard));
    }

    private void processMessageEvent(ClientReceiveMessageEventsObservable.Event event){
        if (event.overlay()) {
            processOverlayMessage(event.message());
        }
    }
    
    /**
     * Public method to capture overlay event from mixin.
     * This is called from MixinGui before rendering.
     */
    public void captureOverlayMessage(OverlayMessageObservable.Event event) {
        if (event.message() != null) {
            processOverlayMessage(event.message());
        }
    }
    
    /**
     * Processes overlay messages (action bar) to extract health, mana, etc.
     */
    private void processOverlayMessage(Text message) {
        String messageText = message.getString();
        
        // Store the overlay event for later parsing
        // This typically contains health (ÔØñ), mana, or other stats
        clientOverlay = message;
        
        // Always parse, not just in debug mode
        parsePlayerStatsFromOverlay(messageText);
    }
    
    /**
     * Main tick processing - collects scoreboard and player data.
     */
    private void processTick() {
        var player = mcAccessor.getPlayer();
        if (player.isEmpty() || mcAccessor.getWorld().isEmpty()) {
            // Clear scoreboard when there is no world/player to avoid stale data
            scoreboardTitle = Text.empty();
            scoreboardLines.clear();
            scoreboardComponents.clear();
            return;
        }
        
        // Re-parse overlay event in case it was updated
        if (!clientOverlay.getString().isEmpty()) {
            parsePlayerStatsFromOverlay(clientOverlay.getString());
        }

        // Update player stats from entity
        updatePlayerStatsFromEntity(player.get());
    }

    /**
     * Public method to force a scoreboard refresh.

     */
    public void refreshScoreboard() {
        collectScoreboardData();
    }
    
    /**
     * Collects scoreboard lines and title from the client.
     */
    private void collectScoreboardData() {
        var world = mcAccessor.getWorld();
        if (world.isEmpty()) {
            scoreboardTitle = Text.empty();
            scoreboardLines.clear();
            scoreboardComponents.clear();
            return;
        }
        
        Scoreboard scoreboard = world.get().getScoreboard();
        ScoreboardObjective objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
        
        if (objective == null) {
            scoreboardTitle = Text.empty();
            scoreboardLines.clear();
            scoreboardComponents.clear();
            return;
        }
        
        scoreboardTitle = objective.getDisplayName();
        scoreboardLines.clear();
        scoreboardComponents.clear();
        
        // Collect scoreboard lines using teams (returns pure strings, sorted by score)
        try {
            // Collect entries with their scores first
            List<ScoreEntry> scoreEntries = new ArrayList<>();
            Collection<Team> teams = scoreboard.getTeams();
            if (teams != null) {
                for (Team team : teams) {
                    Collection<String> entries = team.getPlayerList();
                    if (entries != null) {
                        for (String entry : entries) {
                            try {
                                net.minecraft.scoreboard.ReadableScoreboardScore score = scoreboard.getScore(ScoreHolder.fromName(entry), objective);
                                if (score != null) {
                                    // Get formatted text with Team.decorateName (contains the actual text with styling)
                                    Text lineText = Team.decorateName(team, Text.literal(entry));
                                    String lineString = lineText.getString();
                                    // Remove color codes to get pure text for parsing
                                    String pureText = removeColorCodes(lineString);
                                    // Store both plain text (for parsing) and styled text (for display)
                                    scoreEntries.add(new ScoreEntry(pureText, lineText, score.getScore()));
                                }
                            } catch (Exception e) {
                                // Skip
                            }
                        }
                    }
                }
            }
            
            // Sort by score value (descending - highest scores first, like vanilla scoreboard)
            scoreEntries.sort(Comparator.comparingInt((ScoreEntry e) -> e.score).reversed());
            
            // Add sorted entries to lists
            for (ScoreEntry entry : scoreEntries) {
                scoreboardLines.add(entry.text);
                // Use the original styled text to preserve formatting
                scoreboardComponents.add(entry.styledText);
            }
            
            // Parse the collected scoreboard lines for player progress
            parseScoreboardForProgress();
        } catch (Exception e) {
            // Ignore scoreboard collection errors
        }
    }
    
    /**
     * Parses the collected scoreboard lines to extract player progress information.
     * This concatenates all lines, parses them, and merges with persistent data.
     * Values are accumulated across worlds (never lost when changing worlds).
     */
    private void parseScoreboardForProgress() {
        if (scoreboardLines.isEmpty()) {
            return;
        }
        
        // Concatenate all scoreboard lines into one string (for regex parsing)
        String fullText = String.join(" ", scoreboardLines);
        
        // Parse using the PlayerProgressParser (plain text - styling applied later)
        var newSnapshot = PlayerProgressParser.parse(fullText);
        
        // Store current snapshot (for displaying only current currencies)
        currentSnapshot = newSnapshot;
        
        // Merge with persistent data (accumulates values across worlds)
        if (!newSnapshot.isEmpty()) {
            persistentProgressData.updateFromSnapshot(newSnapshot);
        }
    }
    
    /**
     * Helper class to store scoreboard entry with its score value for sorting.
     * Stores both plain text (for parsing) and styled text (for display).
     */
    private static class ScoreEntry {
        final String text;  // Plain text without color codes (for parsing)
        final Text styledText;  // Original text with styling preserved (for display)
        final int score;
        
        ScoreEntry(String text, Text styledText, int score) {
            this.text = text;
            this.styledText = styledText;
            this.score = score;
        }
    }
    
    /**
     * Parses player stats from overlay event (action bar).
     * Looks for patterns like "28.83/70.43 ÔØñ" to extract health values.
     */
    private void parsePlayerStatsFromOverlay(String messageText) {
        if (messageText == null || messageText.isEmpty()) {
            return;
        }
        
        try {
            // Remove color codes for easier parsing
            String cleanMessage = removeColorCodes(messageText);
            
            // Try multiple patterns - the heart symbol might be before or after the numbers
            // Pattern 1: "number/number ÔØñ" or "number/numberÔØñ" (heart after)
            java.util.regex.Pattern healthPattern1 = java.util.regex.Pattern.compile("(\\d+\\.?\\d*)\\s*/\\s*(\\d+\\.?\\d*)\\s*ÔØñ");
            java.util.regex.Matcher matcher1 = healthPattern1.matcher(cleanMessage);
            
            // Pattern 2: "ÔØñ number/number" (heart before)
            java.util.regex.Pattern healthPattern2 = java.util.regex.Pattern.compile("ÔØñ\\s*(\\d+\\.?\\d*)\\s*/\\s*(\\d+\\.?\\d*)");
            java.util.regex.Matcher matcher2 = healthPattern2.matcher(cleanMessage);
            
            // Pattern 3: Just "number/number" (no heart, but in overlay event)
            java.util.regex.Pattern healthPattern3 = java.util.regex.Pattern.compile("(\\d+\\.\\d+)\\s*/\\s*(\\d+\\.\\d+)");
            java.util.regex.Matcher matcher3 = healthPattern3.matcher(cleanMessage);
            
            if (matcher1.find()) {
                float current = Float.parseFloat(matcher1.group(1));
                float max = Float.parseFloat(matcher1.group(2));
                lastParsedHealth = playerHealth = current;
                lastParsedMaxHealth = playerMaxHealth = max;
            } else if (matcher2.find()) {
                float current = Float.parseFloat(matcher2.group(1));
                float max = Float.parseFloat(matcher2.group(2));
                lastParsedHealth = playerHealth = current;
                lastParsedMaxHealth = playerMaxHealth = max;
            } else if (matcher3.find()) {
                float current = Float.parseFloat(matcher3.group(1));
                float max = Float.parseFloat(matcher3.group(2));
                lastParsedHealth = playerHealth = current;
                lastParsedMaxHealth = playerMaxHealth = max;
            }
            
        } catch (Exception e) {
            // Ignore parsing errors
        }
    }
    
    /**
     * Removes Minecraft color codes from a string.
     */
    private static String removeColorCodes(String text) {
        return text.replaceAll("§[0-9a-fk-or]", "");
    }
    
    /**
     * Updates player stats directly from the player entity.
     * This is called every tick.
     * Note: Health values are primarily parsed from overlay messages,
     * but we update food/experience from entity here.
     */
    private void updatePlayerStatsFromEntity(PlayerEntity player) {
        if (player == null) {
            return;
        }
        
        // Only update health from entity if we haven't parsed from overlay yet
        // (fallback for when overlay isn't available)
        if (lastParsedHealth == 0 && lastParsedMaxHealth == 20) {
            playerHealth = player.getHealth();
            playerMaxHealth = player.getMaxHealth();
        }
        
        playerFoodLevel = player.getHungerManager().getFoodLevel();
        playerSaturation = player.getHungerManager().getSaturationLevel();
        
        // Get experience level and progress
        playerExperienceLevel = player.experienceLevel;
        playerExperienceProgress = player.experienceProgress;
        
    }
    
    /**
     * Enable or disable debug logging mode.
     * When enabled, logs will be printed showing what data is being captured.
     */
    public void setDebugMode(boolean enabled) {
        debugMode = enabled;
    }
    
    /**
     * Gets current debug mode status.
     */
    public boolean isDebugMode() {
        return debugMode;
    }
    
    /**
     * Prints current game data to chat. Useful for testing.
     */
    public void printDebugInfo() {
        var player = mcAccessor.getPlayer();
        if (player.isEmpty()) {
            return;
        }
        
        // Build event with styled text support
        net.minecraft.text.MutableText message = Text.literal("");
        
        // Header and basic info
        message.append(Text.literal("§6=== GameInfo Debug ===\n"));
        message.append(Text.literal("§aHealth: §f").append(String.format("%.2f", playerHealth)).append(" / ").append(String.format("%.2f", playerMaxHealth)).append("\n"));
        message.append(Text.literal("§aFood: §f").append(String.valueOf(playerFoodLevel)).append(" (Saturation: ").append(String.format("%.1f", playerSaturation)).append(")\n"));
        message.append(Text.literal("§aExperience: §fLevel ").append(String.valueOf(playerExperienceLevel)).append(" (").append(String.format("%.1f", playerExperienceProgress * 100)).append("%)\n"));
        
        // Scoreboard title with styling
        if (scoreboardTitle.getString().isEmpty()) {
            message.append(Text.literal("§aScoreboard Title: §fNone\n"));
        } else {
            message.append(Text.literal("§aScoreboard Title: §f"));
            message.append(scoreboardTitle.copy());
            message.append(Text.literal("\n"));
        }
        
        message.append(Text.literal("§aScoreboard Lines: §f").append(String.valueOf(scoreboardLines.size())).append("\n"));
        
        // Scoreboard lines with preserved styling
        if (!scoreboardComponents.isEmpty()) {
            message.append(Text.literal("§7All scoreboard lines:\n"));
            for (int i = 0; i < scoreboardComponents.size(); i++) {
                message.append(Text.literal(String.format("§7  %2d: ", i + 1)));
                message.append(scoreboardComponents.get(i).copy());
                message.append(Text.literal("\n"));
            }
        }
        // Overlay event with styling
        String overlayText = clientOverlay.getString();
        if (overlayText.isEmpty()) {
            message.append(Text.literal("§aOverlay Message: §fNone\n"));
        } else {
            message.append(Text.literal("§aOverlay Message: §f"));
            message.append(clientOverlay.copy());
            message.append(Text.literal("\n"));
            message.append(Text.literal("§7(clean: ").append(removeColorCodes(overlayText)).append(")\n"));
        }
        
        // Show parsed progress information (from persistent data)
        PlayerProgressData progressData = getPersistentProgressData();
        if (progressData != null) {
            message.append(Text.literal("\n§6=== Persistent Progress (All Worlds) ===\n"));
            if (progressData.getDate() != null) {
                message.append(Text.literal("§aDate: §f").append(progressData.getDate().toString()).append("\n"));
            }
            Text area = progressData.getArea();
            if (area != null && !area.getString().isEmpty()) {
                message.append(Text.literal("§aArea: §f"));
                message.append(area.copy());
                message.append(Text.literal("\n"));
            }
            Text rank = progressData.getRank();
            if (rank != null && !rank.getString().isEmpty()) {
                message.append(Text.literal("§aRank: §f["));
                message.append(rank.copy());
                message.append(Text.literal("]\n"));
            }
            
            EnumMap<ProgressLayer, Text> layers = progressData.getAllLayers();
            if (!layers.isEmpty()) {
                message.append(Text.literal("§aLayers:\n"));
                for (var entry : layers.entrySet()) {
                    message.append(Text.literal("§7  ").append(entry.getKey().getFullName()).append(": "));
                    message.append(entry.getValue().copy());  // Preserve original styling
                    message.append(Text.literal("\n"));
                }
            }
            
            if (progressData.getTotalTasks() > 0) {
                message.append(Text.literal("§aTasks: §f").append(String.valueOf(progressData.getCompletedTasks())).append("/").append(String.valueOf(progressData.getTotalTasks())).append("\n"));
            }
            
            var currencies = progressData.getAllCurrencies();
            if (!currencies.isEmpty()) {
                message.append(Text.literal("§aCurrencies:\n"));
                for (var entry : currencies.entrySet()) {
                    CurrencyValue cv = entry.getValue();
                    message.append(Text.literal("§7  ").append(entry.getKey().name()).append(": "));
                    message.append(cv.getStyledText().copy());  // Preserve original styling
                    message.append(Text.literal("\n"));
                }
            }
        } else {
            message.append(Text.literal("\n§7No parsed progress data available"));
        }

        chatHandler.sendChatMessage(message);
    }
    
    // Getters for accessing collected data
    
    public Text getClientOverlay() {
        return clientOverlay;
    }
    
    public String getClientOverlayString() {
        return clientOverlay.getString();
    }
    
    public Text getScoreboardTitle() {
        return scoreboardTitle;
    }
    
    public String getScoreboardTitleString() {
        return scoreboardTitle.getString();
    }
    
    public List<String> getScoreboardLines() {
        return new ArrayList<>(scoreboardLines); // Return copy for safety
    }
    
    public List<Text> getScoreboardComponents() {
        return new ObjectArrayList<>(scoreboardComponents); // Return copy for safety
    }
    
    public float getPlayerHealth() {
        return playerHealth;
    }
    
    public float getPlayerMaxHealth() {
        return playerMaxHealth;
    }
    
    public int getPlayerFoodLevel() {
        return playerFoodLevel;
    }
    
    public float getPlayerSaturation() {
        return playerSaturation;
    }
    
    public int getPlayerExperienceLevel() {
        return playerExperienceLevel;
    }
    
    public float getPlayerExperienceProgress() {
        return playerExperienceProgress;
    }
    
    /**
     * Gets the persistent player progress data (accumulated across all worlds).
     * This data persists even when changing worlds.
     */
    public PlayerProgressData getPersistentProgressData() {
        return persistentProgressData;
    }
    
    /**
     * Gets the current progress as a snapshot (for compatibility).
     * This returns the accumulated data across all worlds.
     */
    public PlayerProgressParser.PlayerProgressSnapshot getLastProgressSnapshot() {
        return persistentProgressData.toSnapshot();
    }
    
    /**
     * Gets the current snapshot from the latest scoreboard parse.
     * Returns only currencies that are currently on the scoreboard.
     */
    public PlayerProgressParser.PlayerProgressSnapshot getCurrentSnapshot() {
        return currentSnapshot != null ? currentSnapshot : createEmptySnapshot();
    }
    
    /**
     * Creates an empty snapshot (for fallback).
     */
    private PlayerProgressParser.PlayerProgressSnapshot createEmptySnapshot() {
        return new PlayerProgressParser.PlayerProgressSnapshot(
                null,
                Text.empty(),
                Text.empty(),
                new EnumMap<>(ProgressLayer.class),
                0,
                0,
                new EnumMap<>(CurrencyType.class)
        );
    }

    /**
     * Persistent player progress data that persists values across worlds.
     * Updates values when present in the scoreboard, keeps old values if not present.
     * This means values are never lost when changing worlds - if a currency doesn't
     * appear in the current world's scoreboard, the last known value is preserved.
     */
    public static class PlayerProgressData {
        // Context information (latest from scoreboard)
        private LocalDate date;
        private Text area;  // Styled text preserved
        private Text rank;  // Styled text preserved

        // Progress layers (updated from scoreboard, persists if not present) - stored as styled Text
        private final EnumMap<ProgressLayer, Text> layers = new EnumMap<>(ProgressLayer.class);

        // Tasks (updated from scoreboard, persists if not present)
        private int completedTasks = 0;
        private int totalTasks = 0;

        // Currencies (updated from scoreboard, persists if not present)
        private final EnumMap<CurrencyType, CurrencyValue> currencies = new EnumMap<>(CurrencyType.class);

        private Text miscTask = Text.empty();

        /**
         * Updates this data with values from a new snapshot.
         * Updates values that are present in the snapshot, keeps old values if not present.
         * This allows persistence across worlds - if a value doesn't appear in the current
         * scoreboard, it keeps the last known value.
         */
        public void updateFromSnapshot(PlayerProgressParser.PlayerProgressSnapshot snapshot) {
            if (snapshot == null || snapshot.isEmpty()) {
                return;
            }

            // Update context (always use latest from scoreboard)
            if (snapshot.date != null) {
                this.date = snapshot.date;
            }
            if (!snapshot.area.getString().isEmpty()) {
                this.area = snapshot.area;
            }
            if (!snapshot.rank.getString().isEmpty()) {
                this.rank = snapshot.rank;
            }

            // Update layers (update with scoreboard values, keep old if not present)
            // Always update if present in snapshot (preserves styling)
            layers.putAll(snapshot.layers);

            // Update tasks (update with scoreboard values, keep old if not present)
            if (snapshot.completedTasks > 0) {
                this.completedTasks = snapshot.completedTasks;
            }
            if (snapshot.totalTasks > 0) {
                this.totalTasks = snapshot.totalTasks;
            }

            // Update currencies (update with scoreboard values, preserve formatting)
            // If currency is in snapshot, update it. If not, keep old value.
            // Always update if present in snapshot (preserves original formatting)
            currencies.putAll(snapshot.currencies);

            if (!snapshot.miscTask.getString().isEmpty()) {
                this.miscTask = snapshot.miscTask;
            }
        }

        /**
         * Creates a snapshot from this persistent data.
         */
        public PlayerProgressParser.PlayerProgressSnapshot toSnapshot() {
            // Return CurrencyValue map directly (preserves formatting)
            return new PlayerProgressParser.PlayerProgressSnapshot(
                    date,
                    area,
                    rank,
                    new EnumMap<>(layers),
                    completedTasks,
                    totalTasks,
                    new EnumMap<>(currencies)
            );
        }

        // Getters
        public LocalDate getDate() { return date; }
        public Text getArea() { return area != null ? area : Text.empty(); }
        public Text getRank() { return rank != null ? rank : Text.empty(); }
        public Text getMiscTask() { return miscTask != null ? miscTask : Text.empty(); }

        /**
         * Gets the styled Text for a specific progress layer.
         */
        public Text getLayer(ProgressLayer layer) {
            return layers.getOrDefault(layer, Text.empty());
        }

        /**
         * Gets the numeric value for a specific progress layer.
         * Parses the number from the styled text.
         */
        public int getLayerValue(ProgressLayer layer) {
            Text layerText = layers.get(layer);
            if (layerText == null || layerText.getString().isEmpty()) {
                return 0;
            }
            // Extract number from text (e.g., "Level 5" -> 5)
            String text = layerText.getString();
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\d+");
            java.util.regex.Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                try {
                    return Integer.parseInt(matcher.group());
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
            return 0;
        }

        public int getCompletedTasks() { return completedTasks; }
        public int getTotalTasks() { return totalTasks; }
        public CurrencyValue getCurrencyValue(CurrencyType currency) { return currencies.get(currency); }
        public EnumMap<CurrencyType, CurrencyValue> getAllCurrencies() { return new EnumMap<>(currencies); }
        public EnumMap<ProgressLayer, Text> getAllLayers() { return new EnumMap<>(layers); }
    }
}

