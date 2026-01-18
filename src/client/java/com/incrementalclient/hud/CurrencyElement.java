package com.incrementalclient.hud;

import com.google.common.base.Suppliers;
import com.incrementalclient.abstractions.HudElement;
import com.incrementalclient.common.CurrencyValue;
import com.incrementalclient.common.data.CurrencyType;
import com.incrementalclient.common.data.World;
import com.incrementalclient.common.utils.Vector2f;
import com.incrementalclient.interfaces.Configurable;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.services.GameInfoMonitor;
import com.incrementalclient.services.HudManager;
import com.incrementalclient.services.WorldMonitor;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ColorHelper;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class CurrencyElement extends HudElement<CurrencyElement.Configuration> {
    private static final int LINE_SPACING = 2; // Spacing between currency rows
    private static final int LINE_HEIGHT = 9; // Height of each line
    
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
    
    private final GameInfoMonitor gameInfoMonitor;
    private final WorldMonitor worldMonitor;

    private final CurrencyElement.Configuration configuration = new CurrencyElement.Configuration();

    private final Supplier<List<OptionPiece>> options;


    public CurrencyElement(
            GameInfoMonitor gameInfoMonitor,
            WorldMonitor worldMonitor,
            MinecraftClientAccessor uiAccessor,
            HudManager hudManager
    ) {
        super(uiAccessor, hudManager);
        this.gameInfoMonitor = gameInfoMonitor;
        this.worldMonitor = worldMonitor;
        // Default anchor point (top-left corner)
        this.anchorPoint = new Vector2f(10, 10);

        options = Suppliers.memoize(() -> List.of(
                Categories.Hud.Currency.createConfig(0,
                        Option.<Double>createBuilder()
                                .name(Text.of("Currency HUD background opacity"))
                                .description(OptionDescription.of(Text.of("Set the opacity of the currency HUD background.")))
                                .binding(configuration.currencyHudBackgroundOpacity, () -> configuration.currencyHudBackgroundOpacity, newVal -> configuration.currencyHudBackgroundOpacity = newVal)
                                .controller(o -> DoubleSliderControllerBuilder.create(o).step(0.01).range(0.0, 1.0))
                                .build())
        ));
    }

    @Override
    public void  render(RenderSettings renderSettings) {
        var editMode = renderSettings.editMode();
        var context = renderSettings.context();
        if (!isElementEnabled()) {
            return;
        }

        if (mcAccessor.getWindow().isEmpty()) {
            if (editMode) {
                renderEditModePlaceholder(context);
            }
            return;
        }

        // Get current currency data from scoreboard (only currencies currently on scoreboard)
        var currentSnapshot = gameInfoMonitor.getCurrentSnapshot();
        if (currentSnapshot == null || currentSnapshot.currencies.isEmpty()) {
            if (editMode) {
                renderEditModePlaceholder(context);
            }
            return;
        }

        EnumMap<CurrencyType, CurrencyValue> currencies = currentSnapshot.currencies;

        // Collect currency entries and sort by CurrencyType enum order
        List<Map.Entry<CurrencyType, CurrencyValue>> currencyEntries = new ArrayList<>(currencies.entrySet());
        currencyEntries.sort(Comparator.comparing(entry -> entry.getKey()));
        Collections.reverse(currencyEntries);

        if (currencyEntries.isEmpty()) {
            if (editMode) {
                renderEditModePlaceholder(context);
            }
            return;
        }

        var window = mcAccessor.getWindow();
        if (window.isEmpty()) {
            return;
        }

        var textRenderer = mcAccessor.getTextRenderer();
        if (textRenderer.isEmpty()) {
            return;
        }

        // Build styled currency texts with colors
        List<Text> currencyTexts = new ArrayList<>();
        int maxWidth = 0;
        for (var entry : currencyEntries) {
            CurrencyType currencyType = entry.getKey();
            CurrencyValue currencyValue = entry.getValue();
            if (currencyValue != null) {
                Text currencyText = buildCurrencyText(currencyType, currencyValue);
                currencyTexts.add(currencyText);
                int width = textRenderer.get().getWidth(currencyText);
                maxWidth = Math.max(maxWidth, width);
            }
        }

        if (currencyTexts.isEmpty()) {
            if (editMode) {
                renderEditModePlaceholder(context);
            }
            return;
        }

        // Calculate layout: vertical list (one currency per row)
        int totalHeight = currencyTexts.size() * LINE_HEIGHT + (currencyTexts.size() - 1) * LINE_SPACING;

        // Get position from anchor point and delta position
        Vector2f pos = getCurrentPosition();
        int x = (int) pos.x;
        int y = (int) pos.y;

        // Draw background
        int bgOpacity = getHudBackgroundOpacity();
        if (bgOpacity != 0) {
            int color = ColorHelper.getArgb(bgOpacity, 0, 0, 0);
            context.fill(x, y, x + maxWidth + HudConstants.BACKGROUND_PADDING, y + totalHeight + HudConstants.TEXT_PADDING_Y, color);
        }

//        // Draw currencies in vertical list
//        int startX = x + HudConstants.TEXT_PADDING_X;
//        int currentY = y + 1;
//        for (Text currencyText : currencyTexts) {
//            context.drawText(textRenderer.get(), currencyText, startX, currentY, 0xFFFFFFFF, true);
//            currentY += LINE_HEIGHT + LINE_SPACING;
//        }
        MutableText currencyText = Text.literal("");

        for (int i = 0; i < currencyTexts.size(); i++) {
            Text currentCurrencyText = currencyTexts.get(i);
            currencyText.append(currentCurrencyText).append(" ");
        }

        renderBarText(context, textRenderer.get(), currencyText, x, y);
    }

    /**
     * Builds a styled Text for a currency with appropriate colors for name and value.
     */
    private Text buildCurrencyText(CurrencyType currencyType, CurrencyValue currencyValue) {
        String currencyName = currencyType.getAliases()[currencyType.getAliases().length-1];
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
    private Text getCurrencyNameText(CurrencyType currencyType, String name) {
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
    private Text getCurrencyValueText(CurrencyType currencyType, String value) {
        return switch (currencyType) {
            case GOLD -> Text.literal(value).styled(s -> s.withColor(Formatting.YELLOW));
            case TICKETS -> Text.literal(value).styled(s -> s.withColor(COLOR_TICKET_VALUE));
            case PRESTIGE_TOKENS -> Text.literal(value).styled(s -> s.withColor(Formatting.AQUA));
            case ASCENSION_TOKENS -> Text.literal(value).styled(s -> s.withColor(Formatting.RED));
            case SILVER -> Text.literal(value).styled(s -> s.withColor(Formatting.GRAY));
            case BUBBLES -> Text.literal(value).styled(s -> s.withColor(COLOR_BUBBLES_VALUE));
            case SHEEP -> Text.literal(value).styled(s -> s.withColor(COLOR_SHEEP_VALUE));
            case ROCKET_FUEL, NATURAL_FUEL, FOSSIL_FUEL, HYDRO_FUEL -> Text.literal(value).styled(s -> s.withColor(COLOR_FUEL_VALUE));
            case TRANSCENDENCE_TOKENS -> Text.literal(value).styled(s -> s.withColor(COLOR_TRANSCENDENCE_TOKENS));
            default -> Text.literal(value);
        };
    }
    
    /**
     * Builds Starbits text with gradient effect (one color per letter).
     */
    private Text buildStarbitsGradientText(String name, String value) {
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

    private int getHudBackgroundOpacity(){
        return (int) (getConfiguration().currencyHudBackgroundOpacity * 255);
    }

    private void renderEditModePlaceholder(DrawContext context) {
        Vector2f pos = getCurrentPosition();
        int x = (int) pos.x;
        int y = (int) pos.y;

        int placeholderWidth = HudConstants.PLACEHOLDER_WIDTH_MEDIUM;
        int placeholderHeight = 15;

        int bgOpacity = getHudBackgroundOpacity();
        if (bgOpacity != 0) {
            int color = ColorHelper.getArgb(bgOpacity, 0, 0, 0);
            context.fill(x, y, x + placeholderWidth, y + placeholderHeight, color);
        }

        var textRenderer = mcAccessor.getTextRenderer();
        if (textRenderer.isEmpty()) {
            return;
        }
        context.drawText(textRenderer.get(), "Currency Tracker (Preview)",
                x + HudConstants.TEXT_PADDING_X,
                y + 3,
                ColorHelper.getArgb(255, 255, 255, 255),
                true);
    }

    @Override
    public boolean isElementEnabled() {
        return worldMonitor.currentWorld() != World.BossArenas;
    }

    @Override
    public Vector2f getAnchorPoint() {
        // Default anchor point (top-left corner)
        return new Vector2f(10, 10);
    }

    @Override
    public Vector2f getBoundingBox() {
        if (!isElementEnabled()) {
            return new Vector2f(HudConstants.PLACEHOLDER_WIDTH_MEDIUM, 15);
        }

        var window = mcAccessor.getWindow();
        if (window.isEmpty()) {
            return new Vector2f(HudConstants.PLACEHOLDER_WIDTH_MEDIUM, 15);
        }

        var currentSnapshot = gameInfoMonitor.getCurrentSnapshot();
        if (currentSnapshot == null || currentSnapshot.currencies.isEmpty()) {
            return new Vector2f(HudConstants.PLACEHOLDER_WIDTH_MEDIUM, 15);
        }

        EnumMap<CurrencyType, CurrencyValue> currencies = currentSnapshot.currencies;

        var textRenderer = mcAccessor.getTextRenderer();
        if (textRenderer.isEmpty()) {
            return new Vector2f(HudConstants.PLACEHOLDER_WIDTH_MEDIUM, 15);
        }

        // Collect currency entries and sort by CurrencyType enum order
        List<Map.Entry<CurrencyType, CurrencyValue>> currencyEntries = new ArrayList<>(currencies.entrySet());
        currencyEntries.sort(Comparator.comparing(entry -> entry.getKey()));

        // Calculate layout: vertical list (one currency per row)
        int maxWidth = 0;
        for (var entry : currencyEntries) {
            CurrencyType currencyType = entry.getKey();
            CurrencyValue currencyValue = entry.getValue();
            if (currencyValue != null) {
                Text currencyText = buildCurrencyText(currencyType, currencyValue);
                int width = textRenderer.get().getWidth(currencyText);
                maxWidth = Math.max(maxWidth, width);
            }
        }

        int totalHeight = currencyEntries.size() * LINE_HEIGHT + (currencyEntries.size() - 1) * LINE_SPACING;

        return new Vector2f(maxWidth + HudConstants.BACKGROUND_PADDING, totalHeight + HudConstants.TEXT_PADDING_Y);
    }

    @Override
    public String getDisplayName() {
        return "Currency Tracker";
    }

    @Override
    public Vector2f getCurrentPosition() {
        // Use anchor point + delta position for regular positioning
        return anchorPoint.add(deltaPosition);
    }

    @Override
    public String getJsonSection() {
        return "currencyHud";
    }

    @Override
    public CurrencyElement.Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public List<Configurable.OptionPiece> getOption() {
        return options.get();
    }


    public static class Configuration extends HudElement.ConfigurationBase {
        @SerialEntry
        public double currencyHudBackgroundOpacity = 0.3;
    }
}

