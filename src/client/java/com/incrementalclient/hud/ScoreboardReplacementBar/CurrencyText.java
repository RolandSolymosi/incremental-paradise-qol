package com.incrementalclient.hud.ScoreboardReplacementBar;

import com.incrementalclient.common.CurrencyValue;
import com.incrementalclient.common.data.CurrencyType;
import com.incrementalclient.common.utils.PlayerProgressParser;
import com.incrementalclient.services.GameInfoMonitor;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

public class CurrencyText {

    // Custom color RGB values
    private static final int COLOR_BUBBLES_NAME = 0xC6C6FC;
    private static final int COLOR_BUBBLES_VALUE = 0xADADFC;
    private static final int COLOR_SHEEP_NAME = 0xFCFCFC;
    private static final int COLOR_SHEEP_VALUE = 0xA8A8A8;
    private static final int COLOR_TICKET_NAME = 0x8845D1;
    private static final int COLOR_TICKET_VALUE = 0xCF90E0;

    // Fuel colors
    private static final int COLOR_ROCKET_FUEL_NAME = 0x944A00;
    private static final int COLOR_NATURAL_FUEL_NAME = 0x00A800;
    private static final int COLOR_FOSSIL_FUEL_NAME = 0xA800A8;
    private static final int COLOR_HYDRO_FUEL_NAME = 0x5454FC;
    private static final int COLOR_FUEL_VALUE = 0xFCFCFC; // All fuel values use this color

    private static final int COLOR_TRANSCENDENCE_TOKENS = 0xFCA800;

    // Starbits gradient colors (one per letter: S, t, a, r, b, i, t, s)
    private static final int[] STARBITS_GRADIENT = {
            0x00FFFF, // S
            0x01EEF9, // t
            0x03DEF3, // a
            0x05CEED, // r
            0x07BEE8, // b
            0x09ADE2, // i
            0x0B9DDC, // t
            0x0D8DD6  // s
    };

    public static List<Text> getCurrencyText(PlayerProgressParser.PlayerProgressSnapshot currentSnapshot) {
        EnumMap<CurrencyType, CurrencyValue> currencies = currentSnapshot.currencies;

        // Collect currency entries and sort by CurrencyType enum order
        List<Map.Entry<CurrencyType, CurrencyValue>> currencyEntries = new ArrayList<>(currencies.entrySet());
        currencyEntries.sort(Comparator.comparing(entry -> entry.getKey()));
        Collections.reverse(currencyEntries);

        // Build styled currency texts with colors
        List<Text> currencyTexts = new ArrayList<>();
        int maxWidth = 0;
        for (var entry : currencyEntries) {
            CurrencyType currencyType = entry.getKey();
            CurrencyValue currencyValue = entry.getValue();
            if (currencyValue != null) {
                Text currencyText = buildCurrencyText(currencyType, currencyValue);
                currencyTexts.add(currencyText);
            }
        }

        return currencyTexts;
    }

    /**
     * Builds a styled Text for a currency with appropriate colors for name and value.
     */
    private static Text buildCurrencyText(CurrencyType currencyType, CurrencyValue currencyValue) {
        String currencyName = currencyType.getAliases()[currencyType.getAliases().length - 1];
        String valueString = currencyValue.getFormattedString();

        // Special handling for Starbits (gradient effect)
        if (currencyType == CurrencyType.STARBITS) {
            return buildStarbitsGradientText(currencyName, valueString);
        }

        // Get colors for currency name and value
        Text nameText = getCurrencyNameText(currencyType, currencyName);
        Text valueText = getCurrencyValueText(currencyType, valueString);

        return Text.literal("").append(nameText).append(" ").append(valueText);
    }

    /**
     * Gets the styled Text for a currency name.
     */
    private static Text getCurrencyNameText(CurrencyType currencyType, String name) {
        return switch (currencyType) {
            case GOLD -> Text.literal(name).styled(s -> s.withColor(Formatting.GOLD));
            case TICKETS -> Text.literal(name).styled(s -> s.withColor(COLOR_TICKET_NAME));
            case PRESTIGE_TOKENS -> Text.literal(name).styled(s -> s.withColor(Formatting.AQUA));
            case ASCENSION_TOKENS -> Text.literal(name).styled(s -> s.withColor(Formatting.RED));
            case SILVER -> Text.literal(name).styled(s -> s.withColor(Formatting.DARK_GRAY));
            case BUBBLES -> Text.literal(name).styled(s -> s.withColor(COLOR_BUBBLES_NAME));
            case SHEEP -> Text.literal(name).styled(s -> s.withColor(COLOR_SHEEP_NAME));
            case ROCKET_FUEL -> Text.literal(name).styled(s -> s.withColor(COLOR_ROCKET_FUEL_NAME));
            case NATURAL_FUEL -> Text.literal(name).styled(s -> s.withColor(COLOR_NATURAL_FUEL_NAME));
            case FOSSIL_FUEL -> Text.literal(name).styled(s -> s.withColor(COLOR_FOSSIL_FUEL_NAME));
            case HYDRO_FUEL -> Text.literal(name).styled(s -> s.withColor(COLOR_HYDRO_FUEL_NAME));
            case TRANSCENDENCE_TOKENS -> Text.literal(name).styled(s -> s.withColor(COLOR_TRANSCENDENCE_TOKENS));
            default -> Text.literal(name);
        };
    }

    /**
     * Gets the styled Text for a currency value.
     */
    private static Text getCurrencyValueText(CurrencyType currencyType, String value) {
        return switch (currencyType) {
            case GOLD -> Text.literal(value).styled(s -> s.withColor(Formatting.YELLOW));
            case TICKETS -> Text.literal(value).styled(s -> s.withColor(COLOR_TICKET_VALUE));
            case PRESTIGE_TOKENS -> Text.literal(value).styled(s -> s.withColor(Formatting.AQUA));
            case ASCENSION_TOKENS -> Text.literal(value).styled(s -> s.withColor(Formatting.RED));
            case SILVER -> Text.literal(value).styled(s -> s.withColor(Formatting.GRAY));
            case BUBBLES -> Text.literal(value).styled(s -> s.withColor(COLOR_BUBBLES_VALUE));
            case SHEEP -> Text.literal(value).styled(s -> s.withColor(COLOR_SHEEP_VALUE));
            case ROCKET_FUEL, NATURAL_FUEL, FOSSIL_FUEL, HYDRO_FUEL ->
                    Text.literal(value).styled(s -> s.withColor(COLOR_FUEL_VALUE));
            case TRANSCENDENCE_TOKENS -> Text.literal(value).styled(s -> s.withColor(COLOR_TRANSCENDENCE_TOKENS));
            default -> Text.literal(value);
        };
    }

    /**
     * Builds Starbits text with gradient effect (one color per letter).
     */
    private static Text buildStarbitsGradientText(String name, String value) {
        MutableText result = Text.literal("");

        // Apply gradient to currency name (one color per letter)
        for (int i = 0; i < name.length() && i < STARBITS_GRADIENT.length; i++) {
            char c = name.charAt(i);
            int color = STARBITS_GRADIENT[i];
            result.append(Text.literal(String.valueOf(c)).styled(s -> s.withColor(color)));
        }

        // Add space and value (value uses the last gradient color)
        result.append(" ");
        int valueColor = STARBITS_GRADIENT[Math.min(STARBITS_GRADIENT.length - 1, name.length() - 1)];
        result.append(Text.literal(value).styled(s -> s.withColor(valueColor)));

        return result;
    }
}
