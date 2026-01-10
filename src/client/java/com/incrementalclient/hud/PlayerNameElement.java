package com.incrementalclient.hud;

import com.incrementalclient.abstractions.HudElement;
import com.incrementalclient.common.utils.Vector2f;
import com.incrementalclient.interfaces.Configurable;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.services.GameInfoMonitor;
import com.incrementalclient.services.HudManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

import java.util.List;

public class PlayerNameElement extends HudElement<PlayerNameElement.Configuration> {
    private static final int TEXT_HEIGHT = 9; // Height for text

    private final PlayerNameElement.Configuration configuration = new Configuration();
    private final GameInfoMonitor gameInfoMonitor;

    public PlayerNameElement(
            GameInfoMonitor gameInfoMonitor,
            MinecraftClientAccessor mcAccessor,
            HudManager hudManager
    ) {
        super(mcAccessor, hudManager);
        this.gameInfoMonitor = gameInfoMonitor;
    }

    @Override
    public void render(RenderSettings renderSettings) {
        var editMode = renderSettings.editMode();
        var context = renderSettings.context();

        if (mcAccessor.getWindow().isEmpty()) {
            return;
        }

        var player = mcAccessor.getPlayer();
        if (player.isEmpty()) {
            if (editMode) {
                renderEditModePlaceholder(context);
            }
            return;
        }

        // Get player name
        String playerName = player.get().getName().getString();

        // Get rank from GameInfo (with preserved formatting from scoreboard)
        var progressData = gameInfoMonitor.getPersistentProgressData();
        Text rankText = Text.empty();
        if (progressData != null) {
            rankText = progressData.getRank();
        }

        Vector2f pos = getCurrentPosition();
        int x = (int) pos.x;
        int y = (int) pos.y;

        var textRenderer = mcAccessor.getTextRenderer();
        if (textRenderer.isEmpty()) {
            return;
        }

        // Build display text: [Rank] PlayerName
        // Rank is already extracted as just the name (e.g., "Explorer") by the parser
        Text displayText;
        if (rankText != null && !rankText.getString().isEmpty()) {
            // Build text with just the rank name: [Explorer] PlayerName
            // Rank text now contains just the rank name with preserved styling
            displayText = Text.literal("[")
                    .append(rankText.copy())
                    .append(Text.literal("] "))
                    .append(Text.literal(playerName));
        } else {
            // No rank, just show player name
            displayText = Text.literal(playerName);
        }

        // Draw background for text with 30% opacity
        int textWidth = textRenderer.get().getWidth(displayText);
        int bgOpacity = (int) (255 * 0.3); // 30% opacity
        int bgColor = ColorHelper.getArgb(bgOpacity, 0, 0, 0);
        context.fill(x, y, x + textWidth + HudConstants.BACKGROUND_PADDING, y + TEXT_HEIGHT + 2, bgColor);

        // Draw text
        context.drawText(textRenderer.get(), displayText, x + 2, y + 2, 0xFFFFFFFF, false);
    }

    private void renderEditModePlaceholder(DrawContext context) {
        Vector2f pos = getCurrentPosition();
        int x = (int) pos.x;
        int y = (int) pos.y;

        Vector2f placeholderSize = getBoundingBox();
        int bgOpacity = (int) (255 * 0.3); // 30% opacity
        int bgColor = ColorHelper.getArgb(bgOpacity, 0, 0, 0);
        context.fill(x, y, x + (int) placeholderSize.x, y + (int) placeholderSize.y, bgColor);

        TextRenderer textRenderer = mcAccessor.getTextRenderer().get();
        context.drawText(textRenderer, Text.literal("Player Name"), x + 2, y + 2, 0xFFFFFFFF, false);
    }

    @Override
    public Vector2f getAnchorPoint() {
        // Position at top left with some spacing
        var window = mcAccessor.getWindow();
        if (window.isPresent()) {
            return new Vector2f(10, 10);
        }
        return new Vector2f(10, 10);
    }

    @Override
    public Vector2f getBoundingBox() {
        var textRenderer = mcAccessor.getTextRenderer();
        if (textRenderer.isEmpty()) {
            return new Vector2f(150 + HudConstants.BACKGROUND_PADDING, TEXT_HEIGHT + 2);
        }

        // Calculate approximate width: [Rank] PlayerName
        // Use a reasonable estimate for rank length
        var player = mcAccessor.getPlayer();
        String playerName = player.map(p -> p.getName().getString()).orElse("PlayerName");
        
        var progressData = gameInfoMonitor.getPersistentProgressData();
        int rankWidth = 0;
        if (progressData != null) {
            Text rankText = progressData.getRank();
            if (rankText != null && !rankText.getString().isEmpty()) {
                rankWidth = textRenderer.get().getWidth(rankText) + textRenderer.get().getWidth("[] ");
            }
        }
        
        int nameWidth = textRenderer.get().getWidth(playerName);
        int totalWidth = Math.max(150, rankWidth + nameWidth); // Minimum width for placeholder

        return new Vector2f(totalWidth + HudConstants.BACKGROUND_PADDING, TEXT_HEIGHT + 2);
    }

    @Override
    public String getDisplayName() {
        return "Player Name";
    }

    @Override
    public String getJsonSection() {
        return "playerNameHud";
    }

    @Override
    public PlayerNameElement.Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public List<Configurable.OptionPiece> getOption() {
        return List.of(); // No options for now
    }

    public static class Configuration extends HudElement.ConfigurationBase {
        // No configuration options yet
    }
}
