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

public class AggroElement extends HudElement<AggroElement.Configuration> {

    private final AggroElement.Configuration configuration = new Configuration();
    private final GameInfoMonitor gameInfoMonitor;

    public AggroElement(
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

        int aggroLevel = gameInfoMonitor.getPlayerExperienceLevel();
        float experienceProgress = gameInfoMonitor.getPlayerExperienceProgress();
        int aggroProgressPercent = Math.round(experienceProgress * 100.0f);

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

        Text displayText = buildAggroText(aggroLevel, aggroProgressPercent);
        renderBarText(context, textRenderer.get(), displayText, x, y);
    }

    private void renderEditModePlaceholder(DrawContext context) {
        Vector2f pos = getCurrentPosition();
        int x = (int) pos.x;
        int y = (int) pos.y;

        TextRenderer textRenderer = mcAccessor.getTextRenderer().get();
        Text placeholderText = buildAggroText(0, 0);
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
            return new Vector2f(120, HudConstants.BAR_ELEMENT_HEIGHT); // Placeholder width
        }

        int aggroLevel = gameInfoMonitor.getPlayerExperienceLevel();
        float experienceProgress = gameInfoMonitor.getPlayerExperienceProgress();
        int aggroProgressPercent = Math.round(experienceProgress * 100.0f);
        String displayTextString = String.format("Aggro : %d | %d%%", aggroLevel, aggroProgressPercent);
        Text displayText = Text.literal(displayTextString);
        int textWidth = textRenderer.get().getWidth(displayText);

        return new Vector2f(textWidth, HudConstants.BAR_ELEMENT_HEIGHT);
    }

    @Override
    public String getDisplayName() {
        return "Aggro";
    }

    @Override
    public String getJsonSection() {
        return "aggroHud";
    }

    @Override
    public AggroElement.Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public List<Configurable.OptionPiece> getOption() {
        return List.of();
    }

    private Text buildAggroText(int level, int progressPercent) {
        return Text.literal("Aggro")
                .styled(style -> style.withColor(Formatting.RED))
                .append(Text.literal(" : ").styled(style -> style.withColor(Formatting.GRAY)))
                .append(Text.literal(String.valueOf(level)).styled(style -> style.withColor(Formatting.DARK_GREEN)))
                .append(Text.literal(" | ").styled(style -> style.withColor(Formatting.GRAY)))
                .append(Text.literal(progressPercent + "%").styled(style -> style.withColor(Formatting.DARK_GREEN)));
    }

    public static class Configuration extends HudElement.ConfigurationBase {
    }
}
