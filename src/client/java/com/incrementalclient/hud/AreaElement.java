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

public class AreaElement extends HudElement<AreaElement.Configuration> {

    private final AreaElement.Configuration configuration = new Configuration();
    private final GameInfoMonitor gameInfoMonitor;

    public AreaElement(
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

        var progressData = gameInfoMonitor.getPersistentProgressData();
        Text areaText = Text.empty();
        if (progressData != null) {
            areaText = progressData.getArea();
        }

        Vector2f pos = getCurrentPosition();
        int x = (int) pos.x;
        int y = (int) pos.y;

        var textRenderer = mcAccessor.getTextRenderer();
        if (textRenderer.isEmpty()) {
            if (editMode) {
                renderEditModePlaceholder(context);
            }
            return;
        }

        Text displayText;
        if (areaText != null && !areaText.getString().isEmpty()) {
            displayText = Text.literal(areaText.getString())
                    .styled(style -> style.withColor(Formatting.YELLOW));
        } else {
            if (editMode) {
                renderEditModePlaceholder(context);
                return;
            }
            displayText = Text.literal("");
        }

        renderBarText(context, textRenderer.get(), displayText, x, y);
    }

    private void renderEditModePlaceholder(DrawContext context) {
        Vector2f pos = getCurrentPosition();
        int x = (int) pos.x;
        int y = (int) pos.y;

        TextRenderer textRenderer = mcAccessor.getTextRenderer().get();
        Text placeholderText = Text.literal("Area")
                .styled(style -> style.withColor(Formatting.YELLOW));
        renderBarText(context, textRenderer, placeholderText, x, y);
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

        var progressData = gameInfoMonitor.getPersistentProgressData();
        int textWidth = 100; // Default minimum width for placeholder
        if (progressData != null) {
            Text areaText = progressData.getArea();
            if (areaText != null && !areaText.getString().isEmpty()) {
                textWidth = textRenderer.get().getWidth(areaText);
            }
        }

        return new Vector2f(textWidth, HudConstants.BAR_ELEMENT_HEIGHT);
    }

    @Override
    public String getDisplayName() {
        return "Area";
    }

    @Override
    public String getJsonSection() {
        return "areaHud";
    }

    @Override
    public AreaElement.Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public List<Configurable.OptionPiece> getOption() {
        return List.of();
    }

    public static class Configuration extends HudElement.ConfigurationBase {
    }
}