package com.incrementalclient.hud;

import com.incrementalclient.abstractions.HudElement;
import com.incrementalclient.common.utils.Vector2f;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.services.GameInfoMonitor;
import com.incrementalclient.services.HudManager;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

import java.util.List;

public class HPBarElement extends HudElement<HPBarElement.Configuration> {
    private static final int BASE_BAR_WIDTH = 100; // Base width before applying Config hpBarSizeScale

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
                Categories.Hud.HpBar.createConfig(0,
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
                Categories.Hud.HpBar.createConfig(1,
                        Option.<Integer>createBuilder()
                                .name(Text.of("HP Bar Render Scale"))
                                .description(OptionDescription.of(Text.of("Controls aliasing vs smoothness of the bar edges. 1 = more pixelated, higher = smoother. Very high values can cost FPS. Changes apply instantly.")))
                                .binding(2, () -> configuration.hpBarRenderScale, newVal -> configuration.hpBarRenderScale = newVal)
                                .controller(o -> IntegerSliderControllerBuilder.create(o).step(1).range(1, 32))
                                .build()),
                Categories.Hud.HpBar.createConfig(2,
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

        float totalHP = currentHealth + absorption;
        float hpPercentage = maxHealth > 0 ? (totalHP / maxHealth) * 100.0f : 0.0f;
        
        String hpText = String.format("%.0f%%", hpPercentage);
        Text hpTextComponent = Text.literal(hpText);

        if (displayMode == HPBarDisplayMode.BAR_AND_NUMBER) {
            int barY = y + (HudConstants.BAR_ELEMENT_HEIGHT - 1) / 2;
            int barStartX = x;

            float fillRatio = Math.max(0, Math.min(1.0f, hpPercentage / 100.0f));

            double sizeScale = Math.max(0.4, Math.min(1.0, configuration.hpBarSizeScale));
            int barWidth = Math.max(40, (int) Math.round(BASE_BAR_WIDTH * sizeScale));

            int filledWidth = (int) (barWidth * fillRatio);
            int shadowX = barStartX + 2;
            int shadowY = barY + 1;
            
            int darkGreenColor = ColorHelper.getArgb(255, 30, 100, 30);
            int shadowUnfilledColor = ColorHelper.getArgb(255, 50, 50, 50);
            
            if (filledWidth > 0) {
                context.fill(shadowX, shadowY, shadowX + filledWidth, shadowY + 1, darkGreenColor);
            }
            if (filledWidth < barWidth) {
                context.fill(shadowX + filledWidth, shadowY, shadowX + barWidth, shadowY + 1, shadowUnfilledColor);
            }
            
            int brightGreenColor = ColorHelper.getArgb(255, 40, 200, 40);
            int lightGrayColor = ColorHelper.getArgb(255, 80, 80, 80);
            
            if (filledWidth > 0) {
                context.fill(barStartX, barY, barStartX + filledWidth, barY + 1, brightGreenColor);
            }
            if (filledWidth < barWidth) {
                context.fill(barStartX + filledWidth, barY, barStartX + barWidth, barY + 1, lightGrayColor);
            }
            
            int textX = barStartX + barWidth + 4;
            int textY = y + (HudConstants.BAR_ELEMENT_HEIGHT - HudConstants.TEXT_HEIGHT) / 2;
            
            context.drawText(textRenderer.get(), hpTextComponent, textX, textY, 0xFFFFFFFF, true);
        } else {
            int textY = y + (HudConstants.BAR_ELEMENT_HEIGHT - HudConstants.TEXT_HEIGHT) / 2;
            context.drawText(textRenderer.get(), hpTextComponent, x, textY, 0xFFFFFFFF, true);
        }
    }


    private void renderEditModePlaceholder(DrawContext context) {
        Vector2f pos = getCurrentPosition();
        int x = (int) pos.x;
        int y = (int) pos.y;

        TextRenderer textRenderer = mcAccessor.getTextRenderer().get();
        int textY = y + (HudConstants.BAR_ELEMENT_HEIGHT - HudConstants.TEXT_HEIGHT) / 2;
        context.drawText(textRenderer, Text.literal("HP Bar"), x, textY, 0xFFFFFFFF, true);
    }

    @Override
    public Vector2f getAnchorPoint() {
        // Position within bottom bar (left side, above hotbar)
        // Bottom bar is at screenHeight - 22, HP bar should be just above it
        var window = mcAccessor.getWindow();
        if (window.isPresent()) {
            int screenHeight = window.get().getScaledHeight();
            int bottomBarY = screenHeight - HudConstants.BAR_ELEMENT_HEIGHT; // Bottom bar height
            // Position HP bar above the bottom bar with some spacing
            // Use constant BAR_ELEMENT_HEIGHT instead of calling getBoundingBox().y
            return new Vector2f(10, bottomBarY - HudConstants.BAR_ELEMENT_HEIGHT - 4);
        }
        return new Vector2f(10, 10);
    }

    @Override
    public Vector2f getBoundingBox() {
        // Get display mode to determine bounding box
        HPBarDisplayMode displayMode = configuration.hpBarDisplayMode;

        var textRenderer = mcAccessor.getTextRenderer();
        // Approximate text width for percentage (e.g., "100%")
        int textWidth = textRenderer.isPresent() ? textRenderer.get().getWidth(Text.literal("100%")) : 40;
        
        if (displayMode == HPBarDisplayMode.NUMBER_ONLY) {
            return new Vector2f(textWidth, HudConstants.BAR_ELEMENT_HEIGHT);
        } else {
            double sizeScale = Math.max(0.4, Math.min(1.0, configuration.hpBarSizeScale));
            int barWidth = Math.max(40, (int) Math.round(BASE_BAR_WIDTH * sizeScale));
            int width = barWidth + 4 + textWidth;
            return new Vector2f(width, HudConstants.BAR_ELEMENT_HEIGHT);
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

