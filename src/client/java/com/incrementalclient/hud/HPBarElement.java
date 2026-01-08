package com.incrementalclient.hud;

import com.incrementalclient.abstractions.HudElement;
import com.incrementalclient.common.utils.Vector2f;
import com.incrementalclient.interfaces.Configurable;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.services.GameInfoMonitor;
import com.incrementalclient.services.HudManager;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

import java.awt.*;
import java.util.List;

public class HPBarElement extends HudElement<HPBarElement.Configuration> {
    private static final int BASE_BAR_WIDTH = 200; // Base width before applying Config hpBarSizeScale
    private static final int BASE_BAR_HEIGHT = 8;  // Base height before applying Config hpBarSizeScale
    private static final int TEXT_HEIGHT = 9; // Height for text
    // Render scale is configurable in-game (Config -> Vanilla HUD Elements -> HP Bar Display).
    // Higher = smoother, lower = more pixelated.

    private final HPBarElement.Configuration configuration = new Configuration();

    private final List<OptionPiece> options;
    private final GameInfoMonitor gameInfoMonitor;

    public HPBarElement(
            GameInfoMonitor gameInfoMonitor,
            MinecraftClientAccessor mcAccessor,
            HudManager hudManager
    ) {
        super(mcAccessor, hudManager);
        this.gameInfoMonitor = gameInfoMonitor;

        options = List.of(
                new OptionPiece(
                        "HUD",
                        70,
                        "HP Bar Display",
                        "Configure how the HP bar is displayed.",
                        0,
                        Option.<HPBarDisplayMode>createBuilder()
                                .name(Text.of("HP Bar Display Mode"))
                                .description(OptionDescription.of(Text.of("Choose between 'Number Only' (like overlay) or 'Bar and Number'.")))
                                .binding(HPBarDisplayMode.BAR_AND_NUMBER,
                                        () -> configuration.hpBarDisplayMode,
                                        newVal -> configuration.hpBarDisplayMode = newVal)
                                .controller(o -> EnumDropdownControllerBuilder.create(o)
                                        .formatValue(mode -> Text.of(mode.getDisplayName()))
                                )
                                .build()),
                new OptionPiece(
                        "HUD",
                        70,
                        "Consumable Timer HUD configuration",
                        "These are the basic settings for the consumable timer HUD.",
                        1,
                        Option.<Integer>createBuilder()
                                .name(Text.of("HP Bar Render Scale"))
                                .description(OptionDescription.of(Text.of("Controls aliasing vs smoothness of the bar edges. 1 = more pixelated, higher = smoother. Very high values can cost FPS. Changes apply instantly.")))
                                .binding(2, () -> configuration.hpBarRenderScale, newVal -> configuration.hpBarRenderScale = newVal)
                                .controller(o -> IntegerSliderControllerBuilder.create(o).step(1).range(1, 32))
                                .build()),
                new OptionPiece(
                        "HUD",
                        70,
                        "Consumable Timer HUD configuration",
                        "Also allow you to set the colors of the consumable timer HUD.",
                        2,
                        Option.<Double>createBuilder()
                                .name(Text.of("HP Bar Size"))
                                .description(OptionDescription.of(Text.of("Scales the bar only (not the text). Changes apply instantly.")))
                                .binding(0.75, () -> configuration.hpBarSizeScale, newVal -> configuration.hpBarSizeScale = newVal)
                                .controller(o -> DoubleSliderControllerBuilder.create(o).step(0.05).range(0.4, 1.0))
                                .build())
        );
    }

    @Override
    public void render(RenderSettings renderSettings) {
        var editMode = renderSettings.editMode();
        var context = renderSettings.context();

        if (mcAccessor.getWindow().isEmpty()){
            return;
        }

        var player = mcAccessor.getPlayer();
        if (player.isEmpty()) {
            if (editMode) {
                renderEditModePlaceholder(context);
            }
            return;
        }

        // Get display mode from config
        HPBarDisplayMode displayMode = configuration.hpBarDisplayMode;

        // Use GameInfo's parsed HP (base HP from overlay messages) instead of actual health
        float currentHealth = gameInfoMonitor.getPlayerHealth();
        float maxHealth = gameInfoMonitor.getPlayerMaxHealth();

        // Fallback to player entity if GameInfo hasn't parsed yet
        if (currentHealth == 0 && maxHealth == 20) {
            currentHealth = player.get().getHealth();
            maxHealth = player.get().getMaxHealth();
        }
        float absorption = player.get().getAbsorptionAmount();

        Vector2f pos = getCurrentPosition();
        int x = (int) pos.x;
        int y = (int) pos.y;

        var textRenderer = mcAccessor.getTextRenderer();

        // Render HP text (e.g., "100/100")
        String hpText = String.format("%.2f/%.2f", currentHealth, maxHealth);
        if (absorption > 0) {
            hpText += String.format(" (+%.2f)", absorption);
        }
        Text hpTextComponent = Text.literal(hpText);

        // Draw background for text
        int textWidth = textRenderer.get().getWidth(hpTextComponent);
        int bgOpacity = 200; // Semi-transparent black background
        int bgColor = ColorHelper.getArgb(bgOpacity, 0, 0, 0);
        context.fill(x, y, x + textWidth + HudConstants.BACKGROUND_PADDING, y + TEXT_HEIGHT + 2, bgColor);

        // Draw HP text
        context.drawText(textRenderer.get(), hpTextComponent, x + 2, y + 2, 0xFFFFFFFF, false);

        // Only render bar if display mode is BAR_AND_NUMBER
        if (displayMode == HPBarDisplayMode.BAR_AND_NUMBER) {
            // Calculate bar position (below text)
            int barY = y + TEXT_HEIGHT + 4;

            float totalHP = currentHealth + absorption;
            float fillRatio = Math.max(0, Math.min(1.0f, totalHP / maxHealth));

            // Bar sizing (bar only, not text)
            double sizeScale = Math.max(0.4, Math.min(1.0, configuration.hpBarSizeScale));
            int barWidth = Math.max(40, (int) Math.round(BASE_BAR_WIDTH * sizeScale));
            int barHeight = Math.max(4, (int) Math.round(BASE_BAR_HEIGHT * sizeScale));
            // Keep pill perfect: radius = half height
            int barRadius = Math.max(2, barHeight / 2);

            // True supersampling: draw the bar at higher internal resolution and scale down.
            var matrices = context.getMatrices();
            matrices.push();
            int renderScale = Math.max(1, Math.min(32, configuration.hpBarRenderScale));
            float inv = 1.0f / renderScale;
            matrices.scale(inv, inv, 1.0f);

            int sx = x * renderScale;
            int sy = barY * renderScale;
            int sBarWidth = barWidth * renderScale;
            int sBarHeight = barHeight * renderScale;
            int sRadius = barRadius * renderScale;

            // Background: lighter grey (no borders anywhere)
            drawPillShapeSmooth(context, sx, sy, sBarWidth, sBarHeight, sRadius, ColorHelper.getArgb(255, 80, 80, 80));

            // Filled portion
            if (fillRatio > 0) {
                int sFilledWidth = (int) (sBarWidth * fillRatio);
                if (sFilledWidth > 0) {
                    int barColor = getColorForHP((int) totalHP);
                    if (fillRatio >= 1.0f) {
                        drawPillShapeSmooth(context, sx, sy, sFilledWidth, sBarHeight, sRadius, barColor);
                    } else {
                        drawPillShapePartialSmooth(context, sx, sy, sFilledWidth, sBarHeight, sRadius, barColor);
                    }
                }
            }

            matrices.pop();
        }
    }

    /**
     * Get color for HP range
     * Red: 0-50, Yellow: 51-100, Green: 101-150, Blue: 151-200, etc.
     */
    private int getColorForHP(int hp) {
        int range = hp / 50; // Which 50 HP range are we in?

        return switch (range % 4) {
            case 0 -> ColorHelper.getArgb(255, 255, 0, 0);     // Red (0-50, 200-250, etc.)
            case 1 -> ColorHelper.getArgb(255, 255, 255, 0);    // Yellow (51-100, 251-300, etc.)
            case 2 -> ColorHelper.getArgb(255, 0, 255, 0);     // Green (101-150, 301-350, etc.)
            case 3 -> ColorHelper.getArgb(255, 0, 150, 255);   // Blue (151-200, 351-400, etc.)
            default -> ColorHelper.getArgb(255, 255, 255, 255); // White fallback
        };
    }

    /**
     * Draw a pill-shaped rectangle (fully rounded ends) - high resolution smooth version
     */
    private void drawPillShapeSmooth(DrawContext context, int x, int y, int width, int height, int radius, int color) {
        // Draw main rectangle (excluding rounded end areas)
        if (width > radius * 2) {
            context.fill(x + radius, y, x + width - radius, y + height, color);
        }

        // Draw left rounded end
        drawCircleSmooth(context, x + radius, y + height / 2, radius, color);

        // Draw right rounded end
        drawCircleSmooth(context, x + width - radius, y + height / 2, radius, color);
    }

    /**
     * Draw a pill shape that's partially filled (rounded on left, square on right)
     */
    private void drawPillShapePartialSmooth(DrawContext context, int x, int y, int width, int height, int radius, int color) {
        // Draw main rectangle (excluding left rounded end)
        if (width > radius) {
            context.fill(x + radius, y, x + width, y + height, color);
        }

        // Draw left rounded end (high resolution circle) only if we have enough width
        if (width >= radius) {
            drawCircleSmooth(context, x + radius, y + height / 2, radius, color);
        }
    }

    /**
     * Draw a high-resolution filled circle using sub-pixel precision for smoother edges
     */
    private void drawCircleSmooth(DrawContext context, int centerX, int centerY, int radius, int color) {
        // With renderScale, we already have more pixels to work with.
        // A simple filled circle is sufficient and much cheaper than heavy CPU supersampling.
        int rSq = radius * radius;
        for (int py = centerY - radius; py <= centerY + radius; py++) {
            int dy = py - centerY;
            int dySq = dy * dy;
            for (int px = centerX - radius; px <= centerX + radius; px++) {
                int dx = px - centerX;
                if (dx * dx + dySq <= rSq) {
                    context.fill(px, py, px + 1, py + 1, color);
                }
            }
        }
    }

    private void renderEditModePlaceholder(DrawContext context) {
        Vector2f pos = getCurrentPosition();
        int x = (int) pos.x;
        int y = (int) pos.y;

        Vector2f placeholderSize = getBoundingBox();
        int bgColor = ColorHelper.getArgb(200, 0, 0, 0);
        context.fill(x, y, x + (int) placeholderSize.x, y + (int) placeholderSize.y, bgColor);

        TextRenderer textRenderer = mcAccessor.getTextRenderer().get();
        context.drawText(textRenderer, Text.literal("HP Bar"), x + 2, y + 2, 0xFFFFFFFF, false);
    }

    @Override
    public Vector2f getAnchorPoint() {
        // Position within bottom bar (left side, above hotbar)
        // Bottom bar is at screenHeight - 22, HP bar should be just above it
        var window = mcAccessor.getWindow();
        if (window.isPresent()) {
            int screenHeight = window.get().getScaledHeight();
            int bottomBarY = screenHeight - 22; // Bottom bar height
            // Position HP bar above the bottom bar with some spacing
            return new Vector2f(10, bottomBarY - getBoundingBox().y - 4);
        }
        return new Vector2f(10, 10);
    }

    @Override
    public Vector2f getBoundingBox() {
        // Get display mode to determine bounding box
        HPBarDisplayMode displayMode = configuration.hpBarDisplayMode;

        int textWidth = 100; // Approximate width for "100.00/100.00 (+10.00)"

        if (displayMode == HPBarDisplayMode.NUMBER_ONLY) {
            // Only text, no bar
            return new Vector2f(textWidth + HudConstants.BACKGROUND_PADDING, TEXT_HEIGHT + 2);
        } else {
            // Text + bar
            double sizeScale = Math.max(0.4, Math.min(1.0, configuration.hpBarSizeScale));
            int barWidth = Math.max(40, (int) Math.round(BASE_BAR_WIDTH * sizeScale));
            int barHeight = Math.max(4, (int) Math.round(BASE_BAR_HEIGHT * sizeScale));
            int width = Math.max(textWidth, barWidth) + HudConstants.BACKGROUND_PADDING;
            int height = TEXT_HEIGHT + 4 + barHeight + 2;
            return new Vector2f(width, height);
        }
    }

    @Override
    public String getDisplayName() {
        return "HP Bar";
    }

    public enum HPBarDisplayMode {
        NUMBER_ONLY("Number Only"),
        BAR_AND_NUMBER("Bar and Number");

        private final String displayName;

        HPBarDisplayMode(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @Override
    public String getJsonSection() {
        return "hpBarHud";
    }

    @Override
    public HPBarElement.Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public List<OptionPiece> getOption() {
        return options;
    }

    public static class Configuration extends HudElement.ConfigurationBase {

        @SerialEntry
        public HPBarDisplayMode hpBarDisplayMode = HPBarDisplayMode.BAR_AND_NUMBER; // "NUMBER_ONLY" or "BAR_AND_NUMBER"

        @SerialEntry
        public int hpBarRenderScale = 2; // 1 = pixelated, higher = smoother (via supersampled render + downscale)

        @SerialEntry
        public double hpBarSizeScale = 0.75; // Scales only the bar (not the text). 1.0 = current size.
    }
}

