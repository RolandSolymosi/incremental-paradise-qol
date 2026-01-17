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
import net.minecraft.util.Formatting;

import java.util.List;

public class PlayerNameElement extends HudElement<PlayerNameElement.Configuration> {

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

        String playerName = player.get().getName().getString();

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

        Text displayText;
        if (rankText != null && !rankText.getString().isEmpty()) {
            String rankName = rankText.getString();
            Formatting rankColor = getRankColor(rankName);
            
            displayText = Text.literal("[")
                    .styled(style -> style.withColor(Formatting.GRAY))
                    .append(Text.literal(rankName)
                            .styled(style -> style.withColor(rankColor).withBold(true)))
                    .append(Text.literal("] ")
                            .styled(style -> style.withColor(Formatting.GRAY)))
                    .append(Text.literal(playerName)
                            .styled(style -> style.withColor(Formatting.GRAY)));
        } else {
            displayText = Text.literal(playerName)
                    .styled(style -> style.withColor(Formatting.GRAY));
        }

        renderBarText(context, textRenderer.get(), displayText, x, y);
    }

    private Formatting getRankColor(String rankName) {
        String rankLower = rankName.toLowerCase();
        return switch (rankLower) {
            case "explorer" -> Formatting.YELLOW;
            case "navigator" -> Formatting.GREEN;
            case "adventurer" -> Formatting.AQUA;
            case "voyager" -> Formatting.LIGHT_PURPLE;
            case "outlander" -> Formatting.DARK_PURPLE;
            case "trailblazer" -> Formatting.GOLD;
            case "qa" -> Formatting.BLUE;
            case "mod" -> Formatting.DARK_GREEN;
            default -> Formatting.WHITE;
        };
    }

    private void renderEditModePlaceholder(DrawContext context) {
        Vector2f pos = getCurrentPosition();
        int x = (int) pos.x;
        int y = (int) pos.y;

        TextRenderer textRenderer = mcAccessor.getTextRenderer().get();
        renderBarText(context, textRenderer, Text.literal("Player Name"), x, y);
    }

    @Override
    public Vector2f getAnchorPoint() {
        return new Vector2f(10, 10);
    }

    @Override
    public Vector2f getBoundingBox() {
        var textRenderer = mcAccessor.getTextRenderer();
        if (textRenderer.isEmpty()) {
            return new Vector2f(100, HudConstants.BAR_ELEMENT_HEIGHT); // Placeholder width
        }

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
        int totalWidth = rankWidth + nameWidth;

        return new Vector2f(totalWidth, HudConstants.BAR_ELEMENT_HEIGHT);
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

    public static class Configuration extends HudElement.ConfigurationBase {
    }
}
