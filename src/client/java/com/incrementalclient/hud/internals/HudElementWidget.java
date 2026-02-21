package com.incrementalclient.hud.internals;

import com.incrementalclient.abstractions.HudElement;
import com.incrementalclient.common.utils.Vector2f;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

import java.util.List;

public class HudElementWidget extends ClickableWidget {
    private final HudElement element;
    private final List<Integer> snapPointsX;
    private final List<Integer> snapPointsY;
    private final int screenWidth;
    private final int screenHeight;
    private boolean isDragging = false;
    private Vector2f dragStartOffset = new Vector2f(0, 0);
    
    public HudElementWidget(HudElement element, List<Integer> snapPointsX, List<Integer> snapPointsY, int screenWidth, int screenHeight) {
        super(0, 0, 0, 0, Text.empty());
        this.element = element;
        this.snapPointsX = snapPointsX;
        this.snapPointsY = snapPointsY;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        updateBounds();
    }
    
    private void updateBounds() {
        Vector2f pos = element.getTopLeftCornerPosition();
        Vector2f bounds = element.getBoundingBox();
        
        this.setX((int) pos.x);
        this.setY((int) pos.y);
        this.width = Math.max(1, (int) bounds.x);
        this.height = Math.max(1, (int) bounds.y);
    }
    
    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        updateBounds();
        
        if (!element.isDraggable()) {
            return;
        }
        
        int color = isHovered() ? HudElement.HudConstants.WIDGET_HOVER_COLOR : HudElement.HudConstants.WIDGET_NORMAL_COLOR;
        context.fill(getX(), getY(), getX() + width, getY() + height, color);
        
        int borderColor = isDragging ? HudElement.HudConstants.WIDGET_BORDER_DRAGGING 
            : (isHovered() ? HudElement.HudConstants.WIDGET_BORDER_HOVER : HudElement.HudConstants.WIDGET_BORDER_NORMAL);
        context.drawBorder(getX(), getY(), width, height, borderColor);
        
        if (isHovered() || isDragging) {
            String name = element.getDisplayName();
            var textRenderer = net.minecraft.client.MinecraftClient.getInstance().textRenderer;
            int nameWidth = textRenderer.getWidth(name);
            int labelX = getX() + (width - nameWidth) / 2;
            int labelY = getY() - 12;
            
            context.fill(labelX - 2, labelY - 1, labelX + nameWidth + 2, labelY + 9, HudElement.HudConstants.TEXT_BACKGROUND);
            context.drawText(textRenderer, name, labelX, labelY, HudElement.HudConstants.TEXT_WHITE, false);
        }
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!element.isDraggable()) {
            return false;
        }
        
        updateBounds();
        if (isMouseOver(mouseX, mouseY)) {
            if (button == 1) {
                reset();
                return true;
            }
            if (button == 0) {
                Vector2f currentDelta = element.getElementPosition();
                dragStartOffset = new Vector2f(
                    (float) (mouseX),
                    (float) (mouseY)
                ).subtract(currentDelta);
                isDragging = true;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDragging && element.isDraggable()) {
            Vector2f newDelta = new Vector2f(
                (float) (mouseX - dragStartOffset.x),
                (float) (mouseY - dragStartOffset.y)
            );
            
            // Apply snap points
            if (!Screen.hasShiftDown()) {
                newDelta = applySnapPoints(newDelta);
            }
            
            // Constrain to screen bounds
            newDelta = constrainToScreenBounds(newDelta);
            
            element.setElementPosition(newDelta);
            updateBounds();
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }
    
    /**
     * Applies snap points to the delta position.
     */
    private Vector2f applySnapPoints(Vector2f delta) {
        float currentX = delta.x;
        float currentY = delta.y;
        
        float snappedX = currentX;
        float snappedY = currentY;
        float snapThreshold = HudElement.HudConstants.SNAP_THRESHOLD;
        
        // Snap to X snap points
        for (int snapX : snapPointsX) {
            if (Math.abs(currentX - snapX) < snapThreshold) {
                snappedX = snapX;
                break;
            }
        }
        
        // Snap to Y snap points
        for (int snapY : snapPointsY) {
            if (Math.abs(currentY - snapY) < snapThreshold) {
                snappedY = snapY;
                break;
            }
        }


        return new Vector2f(snappedX, snappedY);
    }
    
    /**
     * Constrains the element position to stay within screen bounds.
     */
    private Vector2f constrainToScreenBounds(Vector2f delta) {
        Vector2f anchor = element.getOffsetPoint();
        Vector2f bounds = element.getBoundingBox();
        
        float newX = delta.x;
        float newY = delta.y;
        
        // Constrain X: element should not go outside screen (0 to screenWidth - bounds.x)
        float minX = anchor.x;
        float maxX = screenWidth - bounds.x + anchor.x;
        newX = Math.max(minX, Math.min(maxX, newX));
        
        // Constrain Y: element should not go outside screen (0 to screenHeight - bounds.y)
        float minY = anchor.y;
        float maxY = screenHeight - bounds.y + anchor.y;
        newY = Math.max(minY, Math.min(maxY, newY));
        
        return new Vector2f(newX, newY);
    }
    
    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        // No narration needed
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isDragging) {
            isDragging = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
    
    public void reset() {
        element.resetToDefaultPosition();
        element.setScale(1);
        updateBounds();
    }
}

