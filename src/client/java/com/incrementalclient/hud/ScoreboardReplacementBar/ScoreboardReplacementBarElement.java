package com.incrementalclient.hud.ScoreboardReplacementBar;

import com.incrementalclient.common.utils.Vector2f;
import com.incrementalclient.hud.BarElement;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.services.GameInfoMonitor;
import com.incrementalclient.services.HudManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class ScoreboardReplacementBarElement extends BarElement {

    private final GameInfoMonitor gameInfoMonitor;

    public static final int PADDING = 4;
    // This can be reduced later as techincally the bar is made up of top and bottom padding and text but padding between lines can be less than bottom padding
    public static final int LINE_SPACING = HudConstants.BAR_ELEMENT_HEIGHT;

    public int lineCount = 0;

    public ScoreboardReplacementBarElement(MinecraftClientAccessor mcAccessor,
                                           HudManager hudManager,
                                           GameInfoMonitor gameInfoMonitor) {
        super(mcAccessor, hudManager);
        this.gameInfoMonitor = gameInfoMonitor;
        this.draggable = false;
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

        Vector2f pos = getCurrentPosition();
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
        renderBarText(context, textRenderer.get(), areaText, (screenWidth - areaWidth) / 2, 0);

        // Render the player name and progress layers
        int currentLeftWidth = PADDING;
        List<Text> leftTexts = Stream.concat(texts.get("playerName").stream(), texts.get("progressLayers").stream()).toList();
        MutableText currentLeftText = Text.literal("");
        int currentLeftLineCount = 0;
        for (Text text : leftTexts) {
            if (currentLeftWidth + textRenderer.get().getWidth(text) > (screenWidth - areaWidth) / 2 - PADDING) {
                renderBarText(context, textRenderer.get(), currentLeftText, PADDING, LINE_SPACING * currentLeftLineCount);
                currentLeftWidth = PADDING;
                currentLeftLineCount += 1;
                currentLeftText = Text.literal("");
            }
            currentLeftText.append(text);
            currentLeftText.append(" ");
            currentLeftWidth += textRenderer.get().getWidth(text);
            currentLeftWidth += textRenderer.get().getWidth(Text.literal(" "));
        }
        renderBarText(context, textRenderer.get(), currentLeftText, PADDING, LINE_SPACING * currentLeftLineCount);

        // Render the currencies
        int currentRightWidth = PADDING;
        List<Text> rghtTexts = texts.get("currencies");
        MutableText currentRightText = Text.literal("");
        int currentRightLineCount = 0;
        for (Text text : rghtTexts) {
            if (currentRightWidth + textRenderer.get().getWidth(text) > (screenWidth - areaWidth) / 2 - PADDING) {
                renderBarText(context, textRenderer.get(), currentRightText, screenWidth - currentRightWidth - PADDING, LINE_SPACING * currentRightLineCount);
                currentRightWidth = PADDING;
                currentRightLineCount += 1;
                currentRightText = Text.literal("");
            }
            currentRightText.append(text);
            currentRightText.append(" ");
            currentRightWidth += textRenderer.get().getWidth(text);
            currentRightWidth += textRenderer.get().getWidth(Text.literal(" "));
        }
        renderBarText(context, textRenderer.get(), currentRightText, screenWidth - currentRightWidth - PADDING, LINE_SPACING * currentRightLineCount);

        lineCount = Math.max(currentLeftLineCount, currentRightLineCount) + 1;

        hudManager.setBarHeight(22 * lineCount);

        // Draw top bar background (full width)
        renderBarBackground(context, 0, y, screenWidth, HudConstants.BAR_ELEMENT_HEIGHT * lineCount, editMode);

        // Draw border for angular look
        drawAngularBorder(context, 0, y, screenWidth, HudConstants.BAR_ELEMENT_HEIGHT * lineCount);
    }

    private void renderEditModePlaceholder(DrawContext context) {
        Vector2f pos = getCurrentPosition();
        int x = (int) pos.x;
        int y = (int) pos.y;

        int screenWidth = mcAccessor.getWindow().isPresent() ?
                mcAccessor.getWindow().get().getScaledWidth() : 400;

        Vector2f placeholderSize = getBoundingBox();
        int bgColor = ColorHelper.getArgb(200, 0, 0, 0);
        context.fill(0, y, screenWidth, y + (int) placeholderSize.y, bgColor);

        mcAccessor.getTextRenderer().ifPresent(renderer -> context.drawText(renderer, Text.literal("Top Bar"), x + 10, y + 6, 0xFFFFFFFF, false));
    }

    @Override
    public String getDisplayName() {
        return "Top Bar";
    }

    @Override
    public String getJsonSection() {
        return "topBarHud";
    }
}
