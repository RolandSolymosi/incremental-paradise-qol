package com.incrementalclient.hud.unused;

import com.incrementalclient.abstractions.HudElement;
import com.incrementalclient.common.utils.Vector2f;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.services.HudManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.ColorHelper;
BIG FUCKING ERROR
public abstract class BarElement extends HudElement<BarElement.Configuration> {
    public static final int HOTBAR_WIDTH = 182; // Standard hotbar width
    public static final int PADDING = 4; // Padding around elements
    
    protected final Configuration configuration = new Configuration();
    
    public BarElement(MinecraftClientAccessor mcAccessor, HudManager hudManager) {
        super(mcAccessor, hudManager);
        this.scalable = false; // Bars should not be scalable (full width)
        this.draggable = false; // Bars should not be draggable (fixed position)
    }
    
    @Override
    public Configuration getConfiguration() {
        return configuration;
    }
    
    protected void drawAngularBorder(DrawContext context, int x, int y, int width, int height) {
        int borderColor = ColorHelper.getArgb(255, 0, 0, 0);
        int highlightColor = ColorHelper.getArgb(200, 150, 150, 150);
        
        // Top border (with highlight on top edge for 3D effect)
        context.fill(x, y - 1, x + width, y, borderColor);
        context.fill(x, y - 1, x + width, y, highlightColor);
        
        // Bottom border
        context.fill(x, y + height, x + width, y + height + 1, borderColor);
        
        // Left border
        context.fill(x - 1, y, x, y + height, borderColor);
        
        // Right border
        context.fill(x + width, y, x + width + 1, y + height, borderColor);
    }
    
    protected void renderBarBackground(DrawContext context, int x, int y, int width, int height, boolean editMode) {
        int bgOpacity = editMode ? 100 : 180;
        int bgColor = ColorHelper.getArgb(bgOpacity, 20, 20, 20);
        context.fill(x, y, x + width, y + height, bgColor);
    }
    
    @Override
    public Vector2f getBoundingBox() {
        var window = mcAccessor.getWindow();
        if (window.isPresent()) {
            int screenWidth = window.get().getScaledWidth();
            return new Vector2f(screenWidth, HudConstants.BAR_ELEMENT_HEIGHT);
        }
        return new Vector2f(400, HudConstants.BAR_ELEMENT_HEIGHT);
    }
    
    public static class Configuration extends HudElement.ConfigurationBase {
    }
}
