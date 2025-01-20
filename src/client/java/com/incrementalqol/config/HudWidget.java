package com.incrementalqol.config;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.incrementalqol.EntryPointClient.MOD_ID;

public class HudWidget extends ClickableWidget {

    private static final int WINDOW_WIDTH = 200;
    private static final int WINDOW_HEIGHT = 100;
    private static final int HEADER_HEIGHT = 20;

    private int windowX = 100;
    private int windowY = 100;

    private int dragOffsetX = 0;
    private int dragOffsetY = 0;
    private boolean isDragging = false;
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private final Config config = ConfigHandler.getConfig();

    public HudWidget(int x, int y, int width, int height) {
        super(x,y,width,height, Text.empty());
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        MatrixStack matrixStack = context.getMatrices();
        matrixStack.push();

        float scaleFactor = (float) this.config.getHudScale();
        matrixStack.scale(scaleFactor, scaleFactor, scaleFactor); // Apply scale during rendering

        context.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0x00000000);
        matrixStack.pop();
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        return;
    }

    // Custom isHovered method that accepts mouseX and mouseY as parameters
    public boolean isHovered(double mouseX, double mouseY) {
        float scaleFactor = (float) this.config.getHudScale();

        // Get the unscaled widget coordinates
        int unscaledX = this.getX();
        int unscaledY = this.getY();
        int unscaledWidth = this.width;
        int unscaledHeight = this.height;

        // Adjust the mouse coordinates to unscaled space by dividing by the scale factor
        double adjustedMouseX = mouseX / scaleFactor;
        double adjustedMouseY = mouseY / scaleFactor;

        // Check if the mouse is within the unscaled bounds
        return adjustedMouseX >= unscaledX && adjustedMouseX <= (unscaledX + unscaledWidth) &&
                adjustedMouseY >= unscaledY && adjustedMouseY <= (unscaledY + unscaledHeight);
    }


}