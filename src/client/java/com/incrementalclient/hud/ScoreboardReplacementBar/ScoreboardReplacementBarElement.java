package com.incrementalclient.hud.ScoreboardReplacementBar;

import com.google.common.base.Suppliers;
import com.incrementalclient.abstractions.HudElement;
import com.incrementalclient.common.utils.Vector2f;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.services.GameInfoMonitor;
import com.incrementalclient.services.HudManager;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ScoreboardReplacementBarElement extends HudElement<ScoreboardReplacementBarElement.Configuration> {

    // TODO: This file is a mess, clean up

    private final GameInfoMonitor gameInfoMonitor;
    private final Supplier<List<OptionPiece>> options;

    public static final int PADDING = 8;
    // This can be reduced later as techincally the bar is made up of top and bottom padding and text but padding between lines can be less than bottom padding
    public static final int LINE_SPACING = 4;

    private final ScoreboardReplacementBarElement.Configuration configuration = new Configuration();

    public int lineCount = 0;

    public ScoreboardReplacementBarElement(MinecraftClientAccessor mcAccessor,
                                           HudManager hudManager,
                                           GameInfoMonitor gameInfoMonitor) {
        super(mcAccessor, hudManager);
        this.gameInfoMonitor = gameInfoMonitor;
        this.scalable = false;
        this.draggable = false;

        options = Suppliers.memoize(() -> List.of(
                Categories.Hud.ScoreboardReplacementBar.createConfig(0,
                        Option.<Integer>createBuilder()
                                .name(Text.of("Top Bar Padding"))
                                .description(OptionDescription.of(Text.of("Changes the top bar padding")))
                                .binding(configuration.defaultTopPadding, () -> configuration.topPadding, newVal -> configuration.topPadding = newVal)
                                .controller(o -> IntegerSliderControllerBuilder.create(o).step(1).range(0, 32))
                                .build()),
                Categories.Hud.ScoreboardReplacementBar.createConfig(1,
                        Option.<Integer>createBuilder()
                                .name(Text.of("Bottom Bar Padding"))
                                .description(OptionDescription.of(Text.of("Changes the bottom bar padding")))
                                .binding(configuration.defaultBottomPadding, () -> configuration.bottomPadding, newVal -> configuration.bottomPadding = newVal)
                                .controller(o -> IntegerSliderControllerBuilder.create(o).step(1).range(0, 32))
                                .build())
        ));
    }

    @Override
    public void render(RenderSettings renderSettings) {
        var editMode = renderSettings.editMode();
        var context = renderSettings.context();

        if (mcAccessor.getWindow().isEmpty()) {
            if (editMode) {
                renderEditModePlaceholder(context);
            }
            return;
        }

        Vector2f pos = getElementPosition();
        int y = (int) pos.y;
        int screenWidth = mcAccessor.getWindow().get().getScaledWidth();

        var textRenderer = mcAccessor.getTextRenderer();
        if (textRenderer.isEmpty()) {
            return;
        }

        // Get the data from the game info monitor
        var currentData = gameInfoMonitor.getCurrentSnapshot();
        var persistentData = gameInfoMonitor.getPersistentProgressData();

        // Get all the texts and put them in a hashmap
        HashMap<String, List<Text>> texts = new HashMap<>();
        texts.put("playerName", List.of(PlayerNameText.getPlayerNameText(persistentData, mcAccessor)));
        texts.put("progressLayers", ProgressLayerText.getProgressLayerText(persistentData));
        texts.put("area", List.of(AreaText.getAreaText(persistentData)));
        texts.put("currencies", CurrencyText.getCurrencyText(currentData));

        // Render the area in the middle of the bar
        Text areaText = texts.get("area").get(0);
        int areaWidth = textRenderer.get().getWidth(areaText);
        context.drawText(textRenderer.get(), areaText, (screenWidth - areaWidth) / 2, configuration.topPadding, 0xFFFFFFFF, true);

        // Render the player name and progress layers
        int currentLeftWidth = PADDING;
        List<Text> leftTexts = Stream.concat(texts.get("playerName").stream(), texts.get("progressLayers").stream()).toList();
        MutableText currentLeftText = Text.literal("");
        int currentLeftLineCount = 0;
        for (Text text : leftTexts) {
            if (currentLeftWidth + textRenderer.get().getWidth(text) > (screenWidth - areaWidth) / 2 - PADDING) {
                context.drawText(textRenderer.get(), currentLeftText, PADDING, configuration.topPadding + (LINE_SPACING + HudConstants.TEXT_HEIGHT) * currentLeftLineCount, 0xFFFFFFFF, true);
//                renderBarText(context, textRenderer.get(), currentLeftText, PADDING, LINE_SPACING * currentLeftLineCount);
                currentLeftWidth = PADDING;
                currentLeftLineCount += 1;
                currentLeftText = Text.literal("");
            }
            currentLeftText.append(text);
            currentLeftText.append(" ");
            currentLeftWidth += textRenderer.get().getWidth(text);
            currentLeftWidth += textRenderer.get().getWidth(Text.literal(" "));
        }
//        renderBarText(context, textRenderer.get(), currentLeftText, PADDING, LINE_SPACING * currentLeftLineCount);
        context.drawText(textRenderer.get(), currentLeftText, PADDING, configuration.topPadding + (LINE_SPACING + HudConstants.TEXT_HEIGHT) * currentLeftLineCount, 0xFFFFFFFF, true);

        // Render the currencies
        int currentRightWidth = PADDING;
        List<Text> rghtTexts = texts.get("currencies");
        MutableText currentRightText = Text.literal("");
        int currentRightLineCount = 0;
        for (Text text : rghtTexts) {
            if (currentRightWidth + textRenderer.get().getWidth(text) > (screenWidth - areaWidth) / 2 - PADDING) {
                context.drawText(textRenderer.get(), currentRightText, screenWidth - currentRightWidth, configuration.topPadding + (LINE_SPACING + HudConstants.TEXT_HEIGHT) * currentRightLineCount, 0xFFFFFFFF, true);
//                renderBarText(context, textRenderer.get(), currentRightText, screenWidth - currentRightWidth - PADDING, LINE_SPACING * currentRightLineCount);
                currentRightWidth = PADDING;
                currentRightLineCount += 1;
                currentRightText = Text.literal("");
            }
            currentRightText.append(text);
            currentRightText.append(" ");
            currentRightWidth += textRenderer.get().getWidth(text);
            currentRightWidth += textRenderer.get().getWidth(Text.literal(" "));
        }
        context.drawText(textRenderer.get(), currentRightText, screenWidth - currentRightWidth, configuration.topPadding + (LINE_SPACING + HudConstants.TEXT_HEIGHT) * currentRightLineCount, 0xFFFFFFFF, true);

        lineCount = Math.max(currentLeftLineCount, currentRightLineCount) + 1;

        hudManager.setBarHeight(configuration.topPadding + (LINE_SPACING + HudConstants.TEXT_HEIGHT) * lineCount - LINE_SPACING + configuration.bottomPadding + 1);

        // Draw top bar background (full width)
        renderBarBackground(context, 0, y, screenWidth, hudManager.getBarHeight(), editMode);

        // Draw border for angular look
        drawAngularBorder(context, 0, y, screenWidth, hudManager.getBarHeight());
    }

    private void renderEditModePlaceholder(DrawContext context) {
        Vector2f pos = getElementPosition();
        int x = (int) pos.x;
        int y = (int) pos.y;

        int screenWidth = mcAccessor.getWindow().isPresent() ?
                mcAccessor.getWindow().get().getScaledWidth() : 400;

        Vector2f placeholderSize = getBoundingBox();
        int bgColor = ColorHelper.getArgb(200, 0, 0, 0);
        context.fill(0, y, screenWidth, y + (int) placeholderSize.y, bgColor);

        mcAccessor.getTextRenderer().ifPresent(renderer -> context.drawText(renderer, Text.literal("Scoreboard Replacement Bar"), x + 10, y + 6, 0xFFFFFFFF, false));
    }

    protected void drawAngularBorder(DrawContext context, int x, int y, int width, int height) {
        int borderColor = ColorHelper.getArgb(255, 0, 0, 0);
        int highlightColor = ColorHelper.getArgb(200, 150, 150, 150);

        // Top border (with highlight on top edge for 3D effect)
        context.fill(x, y - 1, x + width, y, borderColor);
        context.fill(x, y - 1, x + width, y, highlightColor);

        // Bottom border
        context.fill(x, y + height, x + width, y + height + 1, borderColor);

        // Left border
        context.fill(x - 1, y, x, y + height, borderColor);

        // Right border
        context.fill(x + width, y, x + width + 1, y + height, borderColor);
    }

    protected void renderBarBackground(DrawContext context, int x, int y, int width, int height, boolean editMode) {
        int bgOpacity = editMode ? 100 : 180;
        int bgColor = ColorHelper.getArgb(bgOpacity, 20, 20, 20);
        context.fill(x, y, x + width, y + height, bgColor);
    }

    @Override
    public Vector2f getBoundingBox() {
        var window = mcAccessor.getWindow();
        if (window.isPresent()) {
            int screenWidth = window.get().getScaledWidth();
            return new Vector2f(screenWidth, HudElement.HudConstants.BAR_ELEMENT_HEIGHT);
        }
        return new Vector2f(400, HudConstants.BAR_ELEMENT_HEIGHT);
    }

    @Override
    public Vector2f getDefaultPosition() {
        return new Vector2f(0, 0);
    }

    @Override
    public String getDisplayName() {
        return "Scoreboard Replacement Bar";
    }

    @Override
    public String getJsonSection() {
        return "scoreboardReplacementBar";
    }

    @Override
    public ScoreboardReplacementBarElement.Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public List<OptionPiece> getOption() {
        return options.get();
    }

    public static class Configuration extends HudElement.ConfigurationBase {

        private static final int defaultTopPadding = 6;
        private static final int defaultBottomPadding = 6;

        @SerialEntry
        public int topPadding = defaultTopPadding;

        @SerialEntry
        public int bottomPadding = defaultBottomPadding;
    }
}
