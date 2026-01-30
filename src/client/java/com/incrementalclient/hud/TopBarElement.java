package com.incrementalclient.hud;

import com.incrementalclient.common.utils.Vector2f;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.services.HudManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

public class TopBarElement extends BarElement {
    
    public TopBarElement(MinecraftClientAccessor mcAccessor,
                            HudManager hudManager) {
        super(mcAccessor, hudManager);
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
        
        // Draw top bar background (full width)
        renderBarBackground(context, 0, y, screenWidth, HudConstants.BAR_ELEMENT_HEIGHT, editMode);
        
        // Draw border for angular look
        drawAngularBorder(context, 0, y, screenWidth, HudConstants.BAR_ELEMENT_HEIGHT);
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

        mcAccessor.getTextRenderer().ifPresent(renderer -> context.drawText(renderer, Text.literal("Top Bar"), x + 10, y + 6, 0xFFFFFFFF, false));
    }

    @Override
    public Vector2f getDefaultPosition() {
        return new Vector2f(0, 0);
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
