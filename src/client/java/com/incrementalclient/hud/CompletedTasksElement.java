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

import java.util.List;

public class CompletedTasksElement extends HudElement<CompletedTasksElement.Configuration> {

    private final CompletedTasksElement.Configuration configuration = new Configuration();
    private final GameInfoMonitor gameInfoMonitor;

    public CompletedTasksElement(
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
        int completedTasks = 0;
        int totalTasks = 0;
        if (progressData != null) {
            completedTasks = progressData.getCompletedTasks();
            totalTasks = progressData.getTotalTasks();
        }

        Vector2f pos = getCurrentPosition();
        int x = (int) pos.x;
        int y = (int) pos.y;

        var textRenderer = mcAccessor.getTextRenderer();
        if (textRenderer.isEmpty()) {
            return;
        }

        Text displayText = Text.literal("Completed Tasks " + completedTasks + "/" + totalTasks);
        renderBarText(context, textRenderer.get(), displayText, x, y);
    }

    private void renderEditModePlaceholder(DrawContext context) {
        Vector2f pos = getCurrentPosition();
        int x = (int) pos.x;
        int y = (int) pos.y;

        TextRenderer textRenderer = mcAccessor.getTextRenderer().get();
        renderBarText(context, textRenderer, Text.literal("Completed Tasks X/Y"), x, y);
    }

    @Override
    public Vector2f getAnchorPoint() {
        return new Vector2f(10, 10);
    }

    @Override
    public Vector2f getBoundingBox() {
        var textRenderer = mcAccessor.getTextRenderer();
        if (textRenderer.isEmpty()) {
            return new Vector2f(150, HudConstants.BAR_ELEMENT_HEIGHT);
        }

        var progressData = gameInfoMonitor.getPersistentProgressData();
        int completedTasks = 0;
        int totalTasks = 0;
        if (progressData != null) {
            completedTasks = progressData.getCompletedTasks();
            totalTasks = progressData.getTotalTasks();
        }
        
        Text displayText = Text.literal("Completed Tasks " + completedTasks + "/" + totalTasks);
        int textWidth = textRenderer.get().getWidth(displayText);

        return new Vector2f(Math.max(150, textWidth), HudConstants.BAR_ELEMENT_HEIGHT);
    }

    @Override
    public String getDisplayName() {
        return "Completed Tasks";
    }

    @Override
    public String getJsonSection() {
        return "completedTasksHud";
    }

    @Override
    public CompletedTasksElement.Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public List<Configurable.OptionPiece> getOption() {
        return List.of();
    }

    public static class Configuration extends HudElement.ConfigurationBase {
    }
}
