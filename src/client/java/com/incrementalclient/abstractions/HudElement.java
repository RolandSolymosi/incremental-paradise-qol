package com.incrementalclient.abstractions;

import com.incrementalclient.common.utils.Vector2f;
import com.incrementalclient.interfaces.Configurable;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.services.HudManager;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public abstract class HudElement<T extends HudElement.ConfigurationBase> implements Configurable<T> {
    protected Vector2f elementPosition = new Vector2f(0, 0);
    protected float scale = 1.0f;
    protected boolean enabled = true;
    protected boolean scalable = true;
    protected boolean draggable = true;
    
    // Sub-elements support (for complex elements)
    protected List<Vector2f> subElementDeltas = new ArrayList<>();
    protected final MinecraftClientAccessor mcAccessor;
    protected final HudManager hudManager;

    public HudElement(MinecraftClientAccessor mcAccessor, HudManager hudManager) {
        this.mcAccessor = mcAccessor;
        this.hudManager = hudManager;
        hudManager.subscribe(new HudManager.HudRender(this, hudManager));
    }
    
    public HudElement(MinecraftClientAccessor mcAccessor, HudManager hudManager, int subElementCount) {
        this(mcAccessor, hudManager);
        for (int i = 0; i < subElementCount; i++) {
            this.subElementDeltas.add(new Vector2f(0, 0));
        }
    }

    public abstract void render(RenderSettings renderSettings);

    public Vector2f getCurrentPosition() {
        return elementPosition;
    }
    
    public Vector2f getOffsetPoint(){
        return new Vector2f(0, 0);
    }

    public Vector2f getTopLeftCornerPosition() {
        return getCurrentPosition().subtract(getOffsetPoint());
    }
    
    public abstract Vector2f getBoundingBox();

    public abstract String getDisplayName();
    
    // Optional: called when customization screen opens
    public void onEditModeEnter() {}
    
    public Vector2f getElementPosition() {
        return elementPosition;
    }
    
    public void setElementPosition(Vector2f elementPosition) {
        this.elementPosition = elementPosition;
        this.getConfiguration().deltaX = elementPosition.x;
        this.getConfiguration().deltaY = elementPosition.y;
    }

    public abstract Vector2f getDefaultPosition();

    public void resetToDefaultPosition() {
        this.elementPosition = getDefaultPosition();
        this.getConfiguration().deltaX = elementPosition.x;
        this.getConfiguration().deltaY = elementPosition.y;
    }

    public float getScale() {
        return scale;
    }
    
    public void setScale(float scale) {
        this.getConfiguration().scale = scale;
        this.scale = scale;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        this.getConfiguration().enabled = enabled;
    }

    /**
     * Check if this element should be enabled based on config/runtime.
     */
    public boolean isElementEnabled(){
        return isEnabled();
    }
    
    public boolean isScalable() {
        return scalable;
    }
    
    public void setScalable(boolean scalable) {
        this.scalable = scalable;
    }
    
    public boolean isDraggable() {
        return draggable;
    }
    
    public void setDraggable(boolean draggable) {
        this.draggable = draggable;
    }
    
    public boolean hasSubComponents() {
        return !subElementDeltas.isEmpty();
    }
    
    public int subComponentCount() {
        return subElementDeltas.size();
    }
    
    public List<Vector2f> getSubElementDeltaPositions() {
        return subElementDeltas;
    }
    
    public Vector2f getSubElementDeltaPosition(int index) {
        if (index >= 0 && index < subElementDeltas.size()) {
            return subElementDeltas.get(index);
        }
        return new Vector2f(0, 0);
    }
    
    public void setSubElementDeltaPosition(int index, Vector2f delta) {
        if (index >= 0 && index < subElementDeltas.size()) {
            subElementDeltas.set(index, delta);
        }
    }
    
    public void resetDeltaPositions() {
        this.elementPosition = new Vector2f(0, 0);
        subElementDeltas.replaceAll(ignored -> new Vector2f(0, 0));
    }
    
    public Vector2f getCurrentBoundingPoint() {
        return getCurrentPosition().add(getBoundingBox());
    }
    
    /**
     * Helper method to get the actual rendered width of a Text object.
     * This accounts for font metrics, color codes, and formatting.
     */
    protected int getTextWidth(Text text) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        return textRenderer.getWidth(text);
    }
    
    /**
     * Helper method to calculate the maximum width from a list of Text objects.
     * Useful for elements that render multiple lines of text.
     */
    protected int getMaxTextWidth(List<Text> texts) {
        if (texts.isEmpty()) {
            return 0;
        }
        return texts.stream()
            .mapToInt(this::getTextWidth)
            .max()
            .orElse(0);
    }
    
    protected int getCenteredTextY(int y) {
        return y + (HudConstants.BAR_ELEMENT_HEIGHT - HudConstants.TEXT_HEIGHT) / 2;
    }
    
    protected void renderBarText(DrawContext context, TextRenderer textRenderer, Text text, int x, int y) {
        int textY = getCenteredTextY(y);
        context.drawText(textRenderer, text, x + 2, textY, 0xFFFFFFFF, true);
    }

    @Override
    public void optionChanged(){
        elementPosition = new Vector2f(getConfiguration().deltaX, getConfiguration().deltaY);
        scale = getConfiguration().scale;
        enabled = getConfiguration().enabled;
    }

    public static final class HudConstants {
        // Bar element constants
        public static final int BAR_ELEMENT_HEIGHT = 22;
        // The actual font is 9 however 6 looks more centered
        public static final int TEXT_HEIGHT = 6;
        
        // Padding and spacing
        public static final int TEXT_PADDING_X = 2;
        public static final int TEXT_PADDING_Y = 5;
        public static final int LINE_SPACING = 15;
        public static final int BACKGROUND_PADDING = 4; // 2px on each side

        // Edit mode placeholder sizes
        public static final int PLACEHOLDER_WIDTH_SMALL = 100;
        public static final int PLACEHOLDER_WIDTH_MEDIUM = 150;
        public static final int PLACEHOLDER_HEIGHT = 20;

        // Customization widget colors
        public static final int WIDGET_HOVER_COLOR = 0x3adddddd;
        public static final int WIDGET_NORMAL_COLOR = 0x2a888888;
        public static final int WIDGET_BORDER_DRAGGING = 0xFFFFFFFF;
        public static final int WIDGET_BORDER_HOVER = 0xFFCCCCCC;
        public static final int WIDGET_BORDER_NORMAL = 0xFF666666;

        // Scale widget colors
        public static final int SCALE_WIDGET_HOVER = 0xFF4A90E2;
        public static final int SCALE_WIDGET_NORMAL = 0xFF2E5C8A;

        // Toggle widget colors
        public static final int TOGGLE_ENABLED = 0xFF4CAF50;
        public static final int TOGGLE_DISABLED = 0xFF757575;

        // Text colors
        public static final int TEXT_WHITE = 0xFFFFFFFF;
        public static final int TEXT_BACKGROUND = 0x80000000;

        // Snap point threshold
        public static final float SNAP_THRESHOLD = 10.0f;

        // Scale limits
        public static final float MIN_SCALE = 0.5f;
        public static final float MAX_SCALE = 2.0f;
        public static final float SCALE_INCREMENT = 0.1f;
        public static final float SCALE_SENSITIVITY = 0.005f;

        private HudConstants() {
            // Utility class - prevent instantiation
        }
    }

    public record RenderSettings(DrawContext context, float delta, boolean editMode){

    }

    public static abstract class ConfigurationBase {
        @SerialEntry
        public float deltaX = 0;
        @SerialEntry
        public float deltaY = 0;
        @SerialEntry
        public float scale = 1;
        @SerialEntry
        public boolean enabled = true;
    }
}

