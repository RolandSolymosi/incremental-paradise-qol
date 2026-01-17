package com.incrementalclient.common.utils;

import com.incrementalclient.common.CurrencyValue;
import com.incrementalclient.common.data.CurrencyType;
import com.incrementalclient.common.data.ProgressLayer;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses scoreboard lines to extract player progress information.
 */
public class PlayerProgressParser {
    private static final Logger LOGGER = LoggerFactory.getLogger("incremental-qol-PlayerProgressParser");

    // Date pattern: D/M/YYYY or DD/MM/YYYY (no leading zeros for single digits)
    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{1,2})/(\\d{1,2})/(\\d{4})");

    // Rank pattern: [Text inside brackets]
    private static final Pattern RANK_PATTERN = Pattern.compile("\\[([^\\]]+)\\]");

    // Completed tasks pattern: "Completed Tasks X/Y" or "Completed Tasks X / Y"
    private static final Pattern TASKS_PATTERN = Pattern.compile("(?i)Completed\\s+Tasks\\s+(\\d+)\\s*/\\s*(\\d+)");

    /**
     * Parses the scoreboard text to extract all player progress information.
     * Returns plain text - styling applied later by individual HUD elements.
     *
     * @param scoreboardText Full concatenated scoreboard text (plain, for regex matching)
     * @return PlayerProgressSnapshot with all parsed data, or empty snapshot if parsing fails
     */
    public static PlayerProgressSnapshot parse(String scoreboardText) {
        if (scoreboardText == null || scoreboardText.trim().isEmpty()) {
            return createEmptySnapshot();
        }

        // Remove color codes and normalize whitespace
        String cleanText = removeColorCodes(scoreboardText);
        cleanText = cleanText.replaceAll("\\s+", " ").trim();

        try {
            LocalDate date = parseDate(cleanText);
            Text area = parseAreaAsText(cleanText);
            Text rank = parseRankAsText(cleanText);

            EnumMap<ProgressLayer, Text> layers = parseProgressLayers(cleanText);

            int completedTasks = 0;
            int totalTasks = 0;
            Matcher tasksMatcher = TASKS_PATTERN.matcher(cleanText);
            if (tasksMatcher.find()) {
                try {
                    completedTasks = Integer.parseInt(tasksMatcher.group(1));
                    totalTasks = Integer.parseInt(tasksMatcher.group(2));
                } catch (NumberFormatException e) {
                    // Ignore
                }
            }

            EnumMap<CurrencyType, CurrencyValue> currencies = parseCurrenciesPlain(cleanText);

            return new PlayerProgressSnapshot(
                    date,
                    area,
                    rank,
                    layers,
                    completedTasks,
                    totalTasks,
                    currencies
            );
        } catch (Exception e) {
            LOGGER.error("Error parsing scoreboard: " + e.getMessage(), e);
            return createEmptySnapshot();
        }
    }

    /**
     * Parses date from scoreboard text.
     * Format: D/M/YYYY or DD/MM/YYYY (no leading zeros for single digits)
     */
    private static LocalDate parseDate(String text) {
        Matcher matcher = DATE_PATTERN.matcher(text);
        if (matcher.find()) {
            try {
                int day = Integer.parseInt(matcher.group(1));
                int month = Integer.parseInt(matcher.group(2));
                int year = Integer.parseInt(matcher.group(3));
                return LocalDate.of(year, month, day);
            } catch (Exception e) {
                // Invalid date
            }
        }
        return null;
    }

    /**
     * Parses area from scoreboard text.
     * Area is the text between the date and "Stats".
     */
    private static String parseArea(String text) {
        Matcher dateMatcher = DATE_PATTERN.matcher(text);
        if (dateMatcher.find()) {
            int dateEnd = dateMatcher.end();

            // Find "Stats" after the date
            int statsIndex = text.indexOf("Stats", dateEnd);
            if (statsIndex > dateEnd) {
                String areaText = text.substring(dateEnd, statsIndex).trim();
                // Remove any extra whitespace or separators
                areaText = areaText.replaceAll("^[\\s\\-]+|[\\s\\-]+$", "");
                return areaText;
            }
        }
        return "";
    }

    /**
     * Parses rank from scoreboard text.
     * Rank is inside square brackets.
     */
    private static String parseRank(String text) {
        Matcher matcher = RANK_PATTERN.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }

    /**
     * Parses progress layers (Level, Prestige, Ascension, etc.) from scoreboard text.
     * Returns plain text - styling will be applied later.
     */
    private static EnumMap<ProgressLayer, Text> parseProgressLayers(String text) {
        EnumMap<ProgressLayer, Text> layers = new EnumMap<>(ProgressLayer.class);

        for (ProgressLayer layer : ProgressLayer.values()) {
            String[] variants = layer.getNameVariants();
            StringBuilder patternBuilder = new StringBuilder();
            patternBuilder.append("(?i)(");

            for (int i = 0; i < variants.length; i++) {
                if (i > 0) patternBuilder.append("|");
                patternBuilder.append(Pattern.quote(variants[i]));
            }

            patternBuilder.append(")\\s+(\\d+)");

            Pattern layerPattern = Pattern.compile(patternBuilder.toString());
            Matcher matcher = layerPattern.matcher(text);

            if (matcher.find()) {
                // Extract the matched text and create plain Text
                String matchedText = text.substring(matcher.start(), matcher.end()).trim();
                layers.put(layer, Text.literal(matchedText));
            }
        }

        return layers;
    }

    /**
     * Parses area from scoreboard text as Text.
     * Returns plain text - styling will be applied later.
     */
    private static Text parseAreaAsText(String cleanText) {
        String areaStr = parseArea(cleanText);
        return areaStr.isEmpty() ? Text.empty() : Text.literal(areaStr);
    }

    /**
     * Parses rank from scoreboard text as Text.
     * Returns plain text - styling will be applied later.
     */
    private static Text parseRankAsText(String cleanText) {
        String rankStr = parseRank(cleanText);
        return rankStr.isEmpty() ? Text.empty() : Text.literal(rankStr);
    }

    /**
     * Parses currency values from scoreboard text.
     * Returns plain text - styling will be applied later.
     */
    private static EnumMap<CurrencyType, CurrencyValue> parseCurrenciesPlain(String text) {
        EnumMap<CurrencyType, CurrencyValue> currencies = new EnumMap<>(CurrencyType.class);
        text = text.replace('‐', '-').replace('–', '-').replace('—', '-');

        for (CurrencyType currency : CurrencyType.values()) {
            String[] aliases = currency.getAliases();
            StringBuilder patternBuilder = new StringBuilder();
            patternBuilder.append("(?i)(");

            for (int i = 0; i < aliases.length; i++) {
                if (i > 0) patternBuilder.append("|");
                patternBuilder.append(Pattern.quote(aliases[i]));
            }

            patternBuilder.append(")\\s+([0-9.,]+[a-zA-Z]*)");

            Pattern currencyPattern = Pattern.compile(patternBuilder.toString());
            Matcher matcher = currencyPattern.matcher(text);

            if (matcher.find()) {
                try {
                    String valueStr = matcher.group(matcher.groupCount());
                    ParsedNumber parsed = parseNumberWithSuffix(valueStr);
                    CurrencyValue cv = CurrencyValue.of(parsed.doubleValue, parsed.originalFormat);
                    currencies.put(currency, cv);
                } catch (Exception e) {
                    // Ignore invalid numbers
                }
            }
        }

        return currencies;
    }

    /**
     * Result of parsing a number string.
     */
    private static class ParsedNumber {
        final double doubleValue;
        final String originalFormat;

        ParsedNumber(double doubleValue, String originalFormat) {
            this.doubleValue = doubleValue;
            this.originalFormat = originalFormat;
        }
    }

    /**
     * Parses a number string that may contain suffixes like k, M, B, t, Qt, Qa, etc.
     * Preserves decimal precision and original formatting.
     * Examples: "1000", "5.2k", "1.5M", "67.750t", "77.98Qt", "6,450.1"
     */
    private static ParsedNumber parseNumberWithSuffix(String valueStr) {
        // Preserve original format
        // Remove commas and spaces for parsing, but keep original for display
        String cleanValueStr = valueStr.replaceAll("[,\\s]", "").trim();

        if (cleanValueStr.isEmpty()) {
            return new ParsedNumber(0.0, valueStr);
        }

        // Check for suffix (can be multiple letters like "Qt", "Qa")
        String numberPart = cleanValueStr;
        double multiplier = 1.0;

        // Check if ends with letters (suffix)
        int lastDigitIndex = -1;
        for (int i = cleanValueStr.length() - 1; i >= 0; i--) {
            char c = cleanValueStr.charAt(i);
            if (Character.isDigit(c) || c == '.') {
                lastDigitIndex = i;
                break;
            }
        }

        if (lastDigitIndex >= 0 && lastDigitIndex < cleanValueStr.length() - 1) {
            // Has suffix
            numberPart = cleanValueStr.substring(0, lastDigitIndex + 1);
            String suffix = cleanValueStr.substring(lastDigitIndex + 1).toLowerCase();

            // Map suffix to multiplier
            multiplier = getSuffixMultiplier(suffix);
        }

        try {
            double number = Double.parseDouble(numberPart);
            double exactValue = number * multiplier;

            return new ParsedNumber(exactValue, valueStr);
        } catch (NumberFormatException e) {
            return new ParsedNumber(0.0, valueStr);
        }
    }

    /**
     * Gets the multiplier for a number suffix.
     * Supports: k (thousand), M (million), B (billion), t/T (trillion), Qt (quadrillion), Qa (quintillion), etc.
     */
    private static double getSuffixMultiplier(String suffix) {
        if (suffix.isEmpty()) {
            return 1.0;
        }

        // Single letter suffixes
        if (suffix.length() == 1) {
            return switch (suffix.charAt(0)) {
                case 'k' -> 1_000.0;
                case 'm' -> 1_000_000.0;
                case 'b' -> 1_000_000_000.0;
                case 't' -> 1_000_000_000_000.0; // trillion
                default -> 1.0;
            };
        }

        // Multi-letter suffixes
        return switch (suffix.toLowerCase()) {
            case "qt" -> 1_000_000_000_000_000.0; // quadrillion
            case "qa" -> 1_000_000_000_000_000_000.0; // quintillion
            case "sx" -> 1e21; // sextillion
            case "sp" -> 1e24; // septillion
            case "oc" -> 1e27; // octillion
            case "no" -> 1e30; // nonillion
            case "dc" -> 1e33; // decillion
            default -> 1.0;
        };
    }

    /**
     * Removes Minecraft color codes from text.
     */
    private static String removeColorCodes(String text) {
        return text.replaceAll("§[0-9a-fk-or]", "");
    }

    /**
     * Creates an empty snapshot.
     */
    private static PlayerProgressSnapshot createEmptySnapshot() {
        return new PlayerProgressSnapshot(
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
     * Immutable snapshot of player progress information parsed from the scoreboard.
     */
    public static final class PlayerProgressSnapshot {
        // Context information
        public final LocalDate date;
        public final Text area;  // Plain text - styling should be applied later
        public final Text rank;  // Plain text - styling should be applied later

        // Progress layers (Level, Prestige, Ascension, etc.) - plain text, styling should be applied later
        public final EnumMap<ProgressLayer, Text> layers;

        // Tasks
        public final int completedTasks;
        public final int totalTasks;

        // Currencies (with original formatting preserved)
        public final EnumMap<CurrencyType, CurrencyValue> currencies;

        public PlayerProgressSnapshot(
                LocalDate date,
                Text area,
                Text rank,
                EnumMap<ProgressLayer, Text> layers,
                int completedTasks,
                int totalTasks,
                EnumMap<CurrencyType, CurrencyValue> currencies
        ) {
            this.date = date;
            this.area = area != null ? area : Text.empty();
            this.rank = rank != null ? rank : Text.empty();
            this.layers = layers != null ? layers : new EnumMap<>(ProgressLayer.class);
            this.completedTasks = completedTasks;
            this.totalTasks = totalTasks;
            this.currencies = currencies != null ? currencies : new EnumMap<>(CurrencyType.class);
        }

        /**
         * Gets the styled Text for a specific progress layer, or empty Text if not present.
         */
        public Text getLayer(ProgressLayer layer) {
            return layers.getOrDefault(layer, Text.empty());
        }

        /**
         * Gets the numeric value for a specific progress layer, or 0 if not present.
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

        /**
         * Gets the value for a specific currency, or 0 if not present.
         */
        public long getCurrencyValue(CurrencyType currency) {
            CurrencyValue cv = currencies.get(currency);
            return cv != null ? cv.getLongValue() : 0L;
        }

        /**
         * Gets the CurrencyValue for a specific currency, or null if not present.
         */
        public CurrencyValue getCurrency(CurrencyType currency) {
            return currencies.get(currency);
        }

        /**
         * Checks if this snapshot has any data (not all empty).
         */
        public boolean isEmpty() {
            return date == null && (area == null || area.getString().isEmpty()) &&
                    (rank == null || rank.getString().isEmpty()) &&
                    layers.isEmpty() && completedTasks == 0 && totalTasks == 0 &&
                    currencies.isEmpty();
        }
    }
}
