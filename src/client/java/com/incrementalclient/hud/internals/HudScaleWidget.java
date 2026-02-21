package com.incrementalclient.hud.internals;

import com.incrementalclient.abstractions.HudElement;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;

public class HudScaleWidget extends HudElementOptionWidget {
    private boolean isDragging = false;
    private float dragStartScale = 1.0f;
    private double dragStartY = 0;
    private static final int WIDGET_WIDTH = 25;
    private static final int WIDGET_HEIGHT = 10;

    public HudScaleWidget(HudElement<?> element) {
        super(element, WIDGET_WIDTH, WIDGET_HEIGHT, WIDGET_HEIGHT + 2);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
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

    @Override
    protected int getBorderColor(){
        return isDragging ? HudElement.HudConstants.WIDGET_BORDER_DRAGGING : HudElement.HudConstants.WIDGET_BORDER_HOVER;
    }

    @Override
    protected int getBackgroundColor(){
        return isHovered() ? HudElement.HudConstants.SCALE_WIDGET_HOVER : HudElement.HudConstants.SCALE_WIDGET_NORMAL;
    }

    @Override
    protected String getText(){
        return String.format("%.1f", element.getScale());
    }
}

