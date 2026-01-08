package com.incrementalclient.common;

import net.minecraft.text.Text;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Represents a currency value with decimal precision and formatting support.
 * Uses BigDecimal for precision with large numbers and decimals.
 * Preserves styled Text for display with original colors.
 */
public final class CurrencyValue {
    private final BigDecimal value;
    private final String originalFormat; // Original string format for display (e.g., "6,450.1", "77.98Qt")
    private final Text styledText; // Styled text with original colors preserved
    
    public CurrencyValue(BigDecimal value, String originalFormat) {
        this.value = value;
        this.originalFormat = originalFormat != null ? originalFormat : formatNumber(value);
        this.styledText = Text.literal(this.originalFormat);
    }
    
    public CurrencyValue(BigDecimal value, String originalFormat, Text styledText) {
        this.value = value;
        this.originalFormat = originalFormat != null ? originalFormat : formatNumber(value);
        this.styledText = styledText != null ? styledText : Text.literal(this.originalFormat);
    }
    
    public CurrencyValue(double value, String originalFormat) {
        this(BigDecimal.valueOf(value), originalFormat);
    }
    
    public CurrencyValue(double value, String originalFormat, Text styledText) {
        this(BigDecimal.valueOf(value), originalFormat, styledText);
    }
    
    public BigDecimal getValue() {
        return value;
    }
    
    public long getLongValue() {
        return value.longValue();
    }
    
    public double getDoubleValue() {
        return value.doubleValue();
    }
    
    /**
     * Gets the original formatted string, or formats the number if not available.
     */
    public String getFormattedString() {
        return originalFormat;
    }
    
    /**
     * Gets the styled Text with original colors preserved.
     */
    public Text getStyledText() {
        return styledText;
    }
    
    /**
     * Formats a BigDecimal number with commas (e.g., 6450.1 -> "6,450.1").
     */
    private static String formatNumber(BigDecimal num) {
        DecimalFormat formatter = new DecimalFormat("#,##0.##", DecimalFormatSymbols.getInstance(Locale.US));
        return formatter.format(num);
    }
    
    /**
     * Formats this value with commas.
     */
    public String formatWithCommas() {
        return formatNumber(value);
    }
    
    /**
     * Creates a CurrencyValue from a long.
     */
    public static CurrencyValue of(long value) {
        return new CurrencyValue(BigDecimal.valueOf(value), formatNumber(BigDecimal.valueOf(value)));
    }
    
    /**
     * Creates a CurrencyValue from a double with original format.
     */
    public static CurrencyValue of(double value, String originalFormat) {
        return new CurrencyValue(BigDecimal.valueOf(value), originalFormat);
    }
    
    /**
     * Creates a CurrencyValue from a double with original format and styled text.
     */
    public static CurrencyValue of(double value, String originalFormat, Text styledText) {
        return new CurrencyValue(BigDecimal.valueOf(value), originalFormat, styledText);
    }
    
    /**
     * Creates a CurrencyValue from a BigDecimal with original format.
     */
    public static CurrencyValue of(BigDecimal value, String originalFormat) {
        return new CurrencyValue(value, originalFormat);
    }
    
    /**
     * Creates a CurrencyValue from a BigDecimal with original format and styled text.
     */
    public static CurrencyValue of(BigDecimal value, String originalFormat, Text styledText) {
        return new CurrencyValue(value, originalFormat, styledText);
    }
    
    @Override
    public String toString() {
        return getFormattedString();
    }
}

