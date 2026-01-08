package com.incrementalclient.hud.internals;

import com.incrementalclient.abstractions.HudElement;
import com.incrementalclient.common.utils.Vector2f;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class HudElementToggleWidget extends ClickableWidget {
    private final HudElement element;
    
    public HudElementToggleWidget(HudElement element, int x, int y) {
        super(x, y, 60, 12, Text.empty());
        this.element = element;
    }
    
    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        Vector2f pos = element.getCurrentPosition();
        Vector2f bounds = element.getBoundingBox();
        
        // Position toggle button at top-right of element
        this.setX((int) (pos.x + bounds.x - 60));
        this.setY((int) pos.y);
        
        // Ensure we're within screen bounds
        if (getX() < 0) this.setX((int) pos.x);
        if (getY() < 0) this.setY((int) pos.y);
        
        // Draw toggle button
        int bgColor = element.isEnabled() ? HudElement.HudConstants.TOGGLE_ENABLED : HudElement.HudConstants.TOGGLE_DISABLED;
        int borderColor = isHovered() ? HudElement.HudConstants.WIDGET_BORDER_DRAGGING : HudElement.HudConstants.WIDGET_BORDER_HOVER;
        
        context.fill(getX(), getY(), getX() + width, getY() + height, bgColor);
        context.drawBorder(getX(), getY(), width, height, borderColor);
        
        // Draw text
        String text = element.isEnabled() ? "ON" : "OFF";
        var textRenderer = net.minecraft.client.MinecraftClient.getInstance().textRenderer;
        int textX = getX() + (width - textRenderer.getWidth(text)) / 2;
        int textY = getY() + (height - 8) / 2;
        context.drawText(textRenderer, text, textX, textY, HudElement.HudConstants.TEXT_WHITE, false);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Update position before checking
        Vector2f pos = element.getCurrentPosition();
        Vector2f bounds = element.getBoundingBox();
        this.setX((int) (pos.x + bounds.x - 60));
        this.setY((int) pos.y);
        
        if (isMouseOver(mouseX, mouseY) && button == 0) {
            element.setEnabled(!element.isEnabled());
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        // No narration needed
    }
}

