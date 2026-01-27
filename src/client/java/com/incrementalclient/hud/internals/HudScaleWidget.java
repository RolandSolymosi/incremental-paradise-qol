package com.incrementalclient.hud.internals;

import com.incrementalclient.abstractions.HudElement;
import com.incrementalclient.common.utils.Vector2f;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public class HudScaleWidget extends ClickableWidget {
    private final HudElement<?> element;
    private boolean isDragging = false;
    private float dragStartScale = 1.0f;
    private double dragStartY = 0;
    private static final int WIDGET_WIDTH = 25;
    private static final int WIDGET_HEIGHT = 10;
    
    public HudScaleWidget(HudElement<?> element, int x, int y) {
        super(x, y, WIDGET_WIDTH, WIDGET_HEIGHT, Text.empty());
        this.element = element;
    }
    
    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        Vector2f pos = element.getTopLeftCornerPosition();
        Vector2f bounds = element.getBoundingBox();
        Utils.setScreenSideXPos(this, element);
        this.setY((int) (pos.y + WIDGET_HEIGHT + 2));
        
        if (getX() < 0) this.setX((int) pos.x);
        if (getY() < 0) this.setY((int) pos.y);
        
        int bgColor = isHovered() ? HudElement.HudConstants.SCALE_WIDGET_HOVER : HudElement.HudConstants.SCALE_WIDGET_NORMAL;
        int borderColor = isDragging ? HudElement.HudConstants.WIDGET_BORDER_DRAGGING : HudElement.HudConstants.WIDGET_BORDER_HOVER;
        
        context.fill(getX(), getY(), getX() + width, getY() + height, bgColor);
        context.drawBorder(getX(), getY(), width, height, borderColor);
        
        String scaleText = String.format("%.1f", element.getScale());
        var textRenderer = net.minecraft.client.MinecraftClient.getInstance().textRenderer;
        int textX = getX() + (width - textRenderer.getWidth(scaleText)) / 2;
        int textY = getY() + (height - 8) / 2;
        context.drawText(textRenderer, scaleText, textX, textY, HudElement.HudConstants.TEXT_WHITE, false);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Vector2f pos = element.getTopLeftCornerPosition();
        Vector2f bounds = element.getBoundingBox();
        Utils.setScreenSideXPos(this, element);
        this.setY((int) (pos.y + WIDGET_HEIGHT + 2));
        
        if (isMouseOver(mouseX, mouseY)) {
            if (button == 1) {
                resetScale();
                return true;
            }
            if (button == 0) {
                isDragging = true;
                dragStartScale = element.getScale();
                dragStartY = mouseY;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDragging) {
            double deltaYTotal = dragStartY - mouseY;
            float scaleDelta = (float) (deltaYTotal * HudElement.HudConstants.SCALE_SENSITIVITY);
            float newScale = Math.max(HudElement.HudConstants.MIN_SCALE, Math.min(HudElement.HudConstants.MAX_SCALE, dragStartScale + scaleDelta));
            newScale = Math.round(newScale / HudElement.HudConstants.SCALE_INCREMENT) * HudElement.HudConstants.SCALE_INCREMENT;
            element.setScale(newScale);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isDragging) {
            isDragging = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
    
    public void resetScale() {
        element.setScale(1.0f);
    }
}

