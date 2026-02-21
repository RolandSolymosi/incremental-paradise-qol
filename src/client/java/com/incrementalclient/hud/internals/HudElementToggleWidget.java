package com.incrementalclient.hud.internals;

import com.incrementalclient.abstractions.HudElement;
import com.incrementalclient.common.utils.Vector2f;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;

public class HudElementToggleWidget extends HudElementOptionWidget {
    private static final int WIDGET_WIDTH = 30;
    private static final int WIDGET_HEIGHT = 10;

    public HudElementToggleWidget(HudElement element) {
        super(element, WIDGET_WIDTH, WIDGET_HEIGHT);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
               if (isMouseOver(mouseX, mouseY) && button == 0) {
            element.setEnabled(!element.isEnabled());
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }

    @Override
    protected int getBorderColor(){
        return isHovered() ? HudElement.HudConstants.WIDGET_BORDER_DRAGGING : HudElement.HudConstants.WIDGET_BORDER_HOVER;
    }

    @Override
    protected int getBackgroundColor(){
        return element.isEnabled() ? HudElement.HudConstants.TOGGLE_ENABLED : HudElement.HudConstants.TOGGLE_DISABLED;
    }

    @Override
    protected String getText(){
        return element.isEnabled() ? "ON" : "OFF";
    }
}

