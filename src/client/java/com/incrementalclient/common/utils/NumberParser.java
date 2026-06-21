package com.incrementalclient.common.utils;

import java.text.NumberFormat;
import java.util.regex.Pattern;

public class NumberParser {
    public static final Pattern NumberPattern = Pattern.compile("\\d+(?:[.,]\\d+)?[kmb]?");

    public static long parseSuffixedNumber(String input) {
        if (input == null || input.isEmpty()) return 0L;

        String cleanInput = input.toLowerCase().trim();
        // Decide multiplier
        var multiplier = switch (cleanInput.charAt(cleanInput.length()-1)) {
            case 'k' -> 1_000L;
            case 'm' -> 1_000_000L;
            case 'b' -> 1_000_000_000L;
            case 't' -> 1_000_000_000_000L;
            default -> 1;
        };
        if (multiplier > 1){
            cleanInput = cleanInput.substring(0, cleanInput.length() - 1);
        }

        // Remove a thousand separator, no need for cultural as number is provided by the server which is not localised.
        cleanInput = cleanInput.replace(",", "");


        try {
            double baseValue = Double.parseDouble(cleanInput);
            // Round up numbers to the nearest int in the case of a float.
            return (long) Math.ceil((baseValue * multiplier));
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    public static String formatSuffixedNumber(long value) {
        if (value < 1000) return String.valueOf(value);

        var val = (double) value;
        String suffix;

        if (value >= 1_000_000_000_000L) {
            val /= 1_000_000_000_000L;
            suffix = "t";
        } else if (value >= 1_000_000_000L) {
            val /= 1_000_000_000L;
            suffix = "b";
        } else if (value >= 1_000_000L) {
            val /= 1_000_000L;
            suffix = "m";
        } else {
            if (value < 100_000L) {
                return NumberFormat.getInstance().format(value);
            }
            val /= 1000L;
            suffix = "k";
        }

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(0);

        return nf.format(val) + suffix;
    }
}
