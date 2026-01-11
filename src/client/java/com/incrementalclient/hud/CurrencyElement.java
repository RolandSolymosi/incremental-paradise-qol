package com.incrementalclient.hud;

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
import net.minecraft.client.util.Window;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class CurrencyElement extends HudElement<CurrencyElement.Configuration> {
    private static final int SPACING = 8; // Spacing between currencies
    private final GameInfoMonitor gameInfoMonitor;
    private final WorldMonitor worldMonitor;

    private final CurrencyElement.Configuration configuration = new CurrencyElement.Configuration();

    private final List<OptionPiece> options;


    public CurrencyElement(
            GameInfoMonitor gameInfoMonitor,
            WorldMonitor worldMonitor,
            MinecraftClientAccessor uiAccessor,
            HudManager hudManager
    ) {
        super(uiAccessor, hudManager);
        this.gameInfoMonitor = gameInfoMonitor;
        this.worldMonitor = worldMonitor;
        // This element is part of the bottom bar group
        this.inBottomBarGroup = true;
        this.bottomBarAlignment = BottomBarAlignment.CENTER; // Center in bottom bar by default
        // Anchor point will be calculated relative to bottom bar
        this.anchorPoint = new Vector2f(0, 0);

        options = List.of(
                Categories.Hud.Currency.createConfig(0,
                        Option.<Double>createBuilder()
                                .name(Text.of("Currency HUD background opacity"))
                                .description(OptionDescription.of(Text.of("Set the opacity of the currency HUD background.")))
                                .binding(0.3, () -> configuration.currencyHudBackgroundOpacity, newVal -> configuration.currencyHudBackgroundOpacity = newVal)
                                .controller(o -> DoubleSliderControllerBuilder.create(o).step(0.01).range(0.0, 1.0))
                                .build())
        );
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

        // Get currency data from GameInfo
        var progressData = gameInfoMonitor.getPersistentProgressData();
        if (progressData == null) {
            if (editMode) {
                renderEditModePlaceholder(context);
            }
            return;
        }

        EnumMap<CurrencyType, CurrencyValue> currencies = progressData.getAllCurrencies();
        if (currencies.isEmpty()) {
            if (editMode) {
                renderEditModePlaceholder(context);
            }
            return;
        }

        // Collect currency texts (just the styled values, no names)
        List<Text> currencyTexts = new ArrayList<>();
        for (var entry : currencies.entrySet()) {
            CurrencyValue currencyValue = entry.getValue();
            if (currencyValue != null) {
                // Just use the styled text, no prefix
                currencyTexts.add(currencyValue.getStyledText().copy());
            }
        }

        if (currencyTexts.isEmpty()) {
            if (editMode) {
                renderEditModePlaceholder(context);
            }
            return;
        }

        var window = mcAccessor.getWindow();

        // Get bottom bar position and calculate our position relative to it
        Vector2f bottomBarPos = getBottomBarPosition(window.get());
        float animationOffset = getBottomBarAnimationOffset();

        // Calculate position based on alignment within bottom bar
        int x = (int) deltaPosition.x; // Use deltaPosition.x for X position (from dragging)
        int bottomBarY = (int) (bottomBarPos.y + animationOffset);
        int y = bottomBarY + (BottomBarElement.BAR_HEIGHT - 9) / 2; // Center vertically in bottom bar

        var textRenderer = mcAccessor.getTextRenderer();
        if (textRenderer.isEmpty()) {
            return;
        }

        // Calculate total width
        int totalWidth = 0;
        for (int i = 0; i < currencyTexts.size(); i++) {
            totalWidth += textRenderer.get().getWidth(currencyTexts.get(i));
            if (i < currencyTexts.size() - 1) {
                totalWidth += SPACING; // Add spacing between items
            }
        }

        int height = 9; // Single line height

        // Apply alignment
        if (window.isEmpty()) {
            return;
        }
        int screenWidth = window.get().getScaledWidth();
        int infoAreaWidth = screenWidth - BottomBarElement.HOTBAR_WIDTH - BottomBarElement.PADDING * 3;

        switch (bottomBarAlignment) {
            case CENTER:
                x = (int) (deltaPosition.x + infoAreaWidth / 2 - totalWidth / 2);
                break;
            case RIGHT:
                x = (int) (deltaPosition.x + infoAreaWidth - totalWidth - HudConstants.TEXT_PADDING_X);
                break;
            case LEFT:
            default:
                x = (int) (deltaPosition.x + HudConstants.TEXT_PADDING_X);
                break;
        }

        // Ensure we stay within bounds
        x = Math.max(HudConstants.TEXT_PADDING_X, Math.min(x, screenWidth - BottomBarElement.HOTBAR_WIDTH - totalWidth - HudConstants.TEXT_PADDING_X));

        // Draw background
        int bgOpacity = getHudBackgroundOpacity();
        if (bgOpacity != 0) {
            int color = ColorHelper.getArgb(bgOpacity, 0, 0, 0);
            context.fill(x, y, x + totalWidth + HudConstants.BACKGROUND_PADDING, y + height + HudConstants.TEXT_PADDING_Y, color);
        }

        // Draw currencies horizontally
        int currentX = x + HudConstants.TEXT_PADDING_X;
        for (Text currencyText : currencyTexts) {
            context.drawText(textRenderer.get(), currencyText, currentX, y + 1, 0xFFFFFFFF, true);
            currentX += textRenderer.get().getWidth(currencyText) + SPACING;
        }
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

    /**
     * Gets the bottom bar position for calculating relative positions.
     */
    private Vector2f getBottomBarPosition(Window window) {
        if (window == null) {
            return new Vector2f(0, 0);
        }
        int screenHeight = window.getScaledHeight();
        return new Vector2f(0, screenHeight - BottomBarElement.BAR_HEIGHT);
    }

    @Override
    public Vector2f getAnchorPoint() {
        // Anchor point is relative to bottom bar
        var window = mcAccessor.getWindow();
        if (window.isPresent()) {
            Vector2f bottomBarPos = getBottomBarPosition(window.get());
            return bottomBarPos.add(deltaPosition);
        }
        return new Vector2f(0, 0);
    }

    @Override
    public Vector2f getBoundingBox() {
        if (!isElementEnabled()) {
            return new Vector2f(HudConstants.PLACEHOLDER_WIDTH_MEDIUM, 15);
        }

        var window = mcAccessor.getWindow();
        if (window.isPresent()) {
            return new Vector2f(HudConstants.PLACEHOLDER_WIDTH_MEDIUM, 15);
        }

        var progressData = gameInfoMonitor.getPersistentProgressData();
        if (progressData == null) {
            return new Vector2f(HudConstants.PLACEHOLDER_WIDTH_MEDIUM, 15);
        }

        EnumMap<CurrencyType, CurrencyValue> currencies = progressData.getAllCurrencies();
        if (currencies.isEmpty()) {
            return new Vector2f(HudConstants.PLACEHOLDER_WIDTH_MEDIUM, 15);
        }

        var textRenderer = mcAccessor.getTextRenderer();
        int totalWidth = 0;
        int count = 0;

        for (var entry : currencies.entrySet()) {
            CurrencyValue currencyValue = entry.getValue();
            if (currencyValue != null) {
                totalWidth += textRenderer.get().getWidth(currencyValue.getStyledText());
                count++;
            }
        }

        if (count > 0) {
            totalWidth += SPACING * (count - 1); // Add spacing between items
        }

        int height = 9 + HudConstants.TEXT_PADDING_Y;

        return new Vector2f(totalWidth + HudConstants.BACKGROUND_PADDING, height);
    }

    @Override
    public String getDisplayName() {
        return "Currency Tracker";
    }

    @Override
    public Vector2f getCurrentPosition() {
        // Override to use bottom bar relative positioning
        var window = mcAccessor.getWindow();
        if (window.isEmpty()) {
            return new Vector2f(0, 0);
        }

        Vector2f bottomBarPos = getBottomBarPosition(window.get());
        float animationOffset = getBottomBarAnimationOffset();

        // Y position is always relative to bottom bar (centered vertically)
        int y = (int) (bottomBarPos.y + animationOffset + (BottomBarElement.BAR_HEIGHT - 9) / 2);

        // X position uses deltaPosition for horizontal offset
        int x = (int) deltaPosition.x;

        return new Vector2f(x, y);
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
        return options;
    }


    public static class Configuration extends HudElement.ConfigurationBase {
        @SerialEntry
        public double currencyHudBackgroundOpacity = 0.3;
    }
}

