package com.incrementalclient.abstractions;

import com.incrementalclient.common.utils.Vector2f;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.services.HudManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

import java.util.List;

/**
 * Base class for HUD elements that render a list of text items.
 * Handles common rendering logic like background, padding, and text positioning.
 */
public abstract class TextListHudElement<T extends TextListHudElement.ConfigurationBase> extends HudElement<T> {
    protected TextListHudElement(MinecraftClientAccessor uiAccessor,
                                 HudManager hudManager) {
        super(uiAccessor, hudManager);
    }

    /**
     * Get the list of Text objects to render.
     *
     * @param editMode Whether we're in edit mode
     * @return List of Text objects to render
     */
    protected abstract List<Text> getTextsToRender(boolean editMode);

    /**
     * Get the background opacity from config.
     */
    protected abstract int getBackgroundOpacity();

    /**
     * Get the default placeholder size.
     */
    protected int getPlaceholderWidth() {
        return HudConstants.PLACEHOLDER_WIDTH_MEDIUM;
    }

    @Override
    public void render(RenderSettings renderSettings) {
        var editMode = renderSettings.editMode();
        var context = renderSettings.context();
        if (!isElementEnabled()) {
            return;
        }
        var textRenderer = mcAccessor.getTextRenderer();
        if (textRenderer.isEmpty()){
            return;
        }

        List<Text> texts = getTextsToRender(editMode);
        if (texts.isEmpty() && !editMode) {
            return;
        }

        Vector2f pos = getCurrentPosition();
        int x = (int) pos.x;
        int y = (int) pos.y;


        // Handle edit mode placeholder
        if (texts.isEmpty() && editMode) {
            renderEditModePlaceholder(context, textRenderer.get(), x, y);
            return;
        }

        // Calculate dimensions
        int maxWidth = texts.stream()
                .mapToInt(this::getTextWidth)
                .max()
                .orElse(0);
        int height = HudConstants.TEXT_PADDING_Y + (HudConstants.LINE_SPACING * texts.size());

        // Draw background
        int bgOpacity = getBackgroundOpacity();
        if (bgOpacity != 0) {
            int color = ColorHelper.getArgb(bgOpacity, 0, 0, 0);
            context.fill(x, y, x + maxWidth + HudConstants.BACKGROUND_PADDING, y + height, color);
        }

        // Draw texts
        int textColor = ColorHelper.getArgb(255, 255, 255, 255);
        for (int i = 0; i < texts.size(); i++) {
            context.drawText(textRenderer.get(), texts.get(i),
                    x + HudConstants.TEXT_PADDING_X,
                    y + HudConstants.TEXT_PADDING_Y + (HudConstants.LINE_SPACING * i),
                    textColor, true);
        }
    }

    private void renderEditModePlaceholder(DrawContext context, TextRenderer textRenderer, int x, int y) {
        int placeholderWidth = getPlaceholderWidth();
        int placeholderHeight = HudConstants.PLACEHOLDER_HEIGHT;

        int bgOpacity = getBackgroundOpacity();
        if (bgOpacity != 0) {
            int color = ColorHelper.getArgb(bgOpacity, 0, 0, 0);
            context.fill(x, y, x + placeholderWidth, y + placeholderHeight, color);
        }

        String previewText = getDisplayName() + " (Preview)";
        context.drawText(textRenderer, previewText,
                x + HudConstants.TEXT_PADDING_X,
                y + HudConstants.TEXT_PADDING_Y,
                ColorHelper.getArgb(255, 255, 255, 255), true);
    }

    @Override
    public Vector2f getBoundingBox() {
        if (!isElementEnabled()) {
            return new Vector2f(0, 0);
        }

        List<Text> texts = getTextsToRender(false);
        if (texts.isEmpty()) {
            return new Vector2f(getPlaceholderWidth(), HudConstants.PLACEHOLDER_HEIGHT);
        }

        int maxWidth = texts.stream()
                .mapToInt(this::getTextWidth)
                .max()
                .orElse(0);
        int height = HudConstants.TEXT_PADDING_Y + (HudConstants.LINE_SPACING * texts.size());

        return new Vector2f(maxWidth + HudConstants.BACKGROUND_PADDING, height);
    }
}

