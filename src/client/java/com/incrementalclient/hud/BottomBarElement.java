package com.incrementalclient.hud;

import com.incrementalclient.abstractions.HudElement;
import com.incrementalclient.common.utils.Vector2f;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.services.HudManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Text;
import net.minecraft.util.math.ColorHelper;

public class BottomBarElement extends HudElement<BottomBarElement.Configuration> {
    public static final int BAR_HEIGHT = 22; // Height of the bottom bar (matches hotbar height)
    public static final int HOTBAR_WIDTH = 182; // Standard hotbar width
    public static final int PADDING = 4; // Padding around elements
    private static final int MAX_ANIMATION_OFFSET = 40; // Maximum pixels to slide down when typing
    
    private static float animationOffset = 0; // Current animation offset (0 to MAX_ANIMATION_OFFSET) - static so other elements can access it

    private final Configuration configuration = new Configuration();

    /**
     * Gets the current animation offset (for elements in bottom bar group).
     */
    public static float getAnimationOffset() {
        return animationOffset;
    }
    
    public BottomBarElement(MinecraftClientAccessor mcAccessor,
                            HudManager hudManager) {
        super(mcAccessor, hudManager);
        this.scalable = false; // Bottom bar should not be scalable (full width)
        this.draggable = false; // Bottom bar should not be draggable (fixed position)
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
        
        // Update animation based on chat state
        if (!editMode) {
            updateAnimation();
        }
        
        Vector2f pos = getCurrentPosition();
        // Add animation offset to Y position (slides down when typing)
        int y = (int) (pos.y + animationOffset);
        int screenWidth = mcAccessor.getWindow().get().getScaledWidth();
        
        // Draw bottom bar background (full width)
        int bgColor = ColorHelper.getArgb(180, 20, 20, 20);
        context.fill(0, y, screenWidth, y + BAR_HEIGHT, bgColor);
        
        // Draw border for angular look
        drawAngularBorder(context, 0, y, screenWidth, BAR_HEIGHT);
        
        // Draw left side information area (for HP bar, etc.)
        int infoAreaWidth = screenWidth - HOTBAR_WIDTH - PADDING * 3;
        if (infoAreaWidth > 0) {
            // Draw separator line between info area and hotbar area
            int separatorX = infoAreaWidth + PADDING;
            context.fill(separatorX, y + 2, separatorX + 1, y + BAR_HEIGHT - 2, 
                ColorHelper.getArgb(255, 100, 100, 100));
        }
        
        // The hotbar will be rendered by Minecraft in the right area via mixin
        // We just provide the space for it
    }
    
    /**
     * Draw angular border around the bar for a more defined, textured look
     */
    private void drawAngularBorder(DrawContext context, int x, int y, int width, int height) {
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
    
    private void renderEditModePlaceholder(DrawContext context) {
        Vector2f pos = getCurrentPosition();
        int x = (int) pos.x;
        int y = (int) pos.y;

        int screenWidth = mcAccessor.getWindow().isPresent() ?
                mcAccessor.getWindow().get().getScaledWidth() : 400;
        
        Vector2f placeholderSize = getBoundingBox();
        int bgColor = ColorHelper.getArgb(200, 0, 0, 0);
        context.fill(0, y, screenWidth, y + (int) placeholderSize.y, bgColor);

        mcAccessor.getTextRenderer().ifPresent(renderer -> context.drawText(renderer, Text.literal("Bottom Bar"), x + 10, y + 6, 0xFFFFFFFF, false));
    }
    
    /**
     * Update animation offset based on whether chat is open
     * Similar to APEC's implementation
     */
    private void updateAnimation() {
        boolean isInChat = mcAccessor.getScreen().orElse(null) instanceof ChatScreen;
        
        // Smooth animation: increase when chat opens, decrease when closed
        // Using fixed animation speed for smooth transitions
        float animationSpeed = 2.0f; // Pixels per frame
        
        if (isInChat && animationOffset < MAX_ANIMATION_OFFSET) {
            animationOffset = Math.min(MAX_ANIMATION_OFFSET, animationOffset + animationSpeed);
        } else if (!isInChat && animationOffset > 0) {
            animationOffset = Math.max(0, animationOffset - animationSpeed);
        }
    }
    
    @Override
    public Vector2f getAnchorPoint() {
        // Position at exact bottom of screen (always, regardless of delta position)
        var window = mcAccessor.getWindow();
        if (window.isPresent()) {
            int screenHeight = window.get().getScaledHeight();
            return new Vector2f(0, screenHeight - BAR_HEIGHT);
        }
        return new Vector2f(0, 0);
    }
    
    @Override
    public Vector2f getCurrentPosition() {
        // Override to ignore delta position for bottom bar (always at anchor)
        return getAnchorPoint();
    }
    
    @Override
    public Vector2f getBoundingBox() {
        var window = mcAccessor.getWindow();
        if (window.isPresent()) {
            int screenWidth = window.get().getScaledWidth();
            return new Vector2f(screenWidth, BAR_HEIGHT);
        }
        return new Vector2f(400, BAR_HEIGHT);
    }

    @Override
    public String getDisplayName() {
        return "Bottom Bar";
    }

    @Override
    public String getJsonSection() {
        return "bottomBarHud";
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    public static class Configuration extends HudElement.ConfigurationBase {

    }
}

