package com.incrementalqol.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.incrementalqol.EntryPointClient.MOD_ID;

public class DraggableScreen extends Screen {
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private final Screen parent;

    private final Config config = ConfigHandler.getConfig();

    public DraggableScreen(Screen parent) {
        super(Text.literal("Incremental Paradise QOL Options"));
        this.parent = parent;
    }

    public HudWidget hud1;

    private boolean isDragging = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;

    @Override
    protected void init() {
        hud1 = new HudWidget(config.getHudPosX(), config.getHudPosY(), 100, 50);
        this.addDrawableChild(hud1);
    }

    @Override
    public void close() {
        ConfigHandler.saveOptions();
        client.setScreen(parent);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float scale = (float) config.getHudScale();
        if (hud1.isHovered(mouseX, mouseY)) {
            // Start dragging
            isDragging = true;
            dragOffsetX = (int) (mouseX / scale - hud1.getX()); // Adjust by scale
            dragOffsetY = (int) (mouseY / scale - hud1.getY()); // Adjust by scale
            return true; // Indicate that this event was handled
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDragging) {
            // Apply scaling to the mouse position to match the scaled rendering.
            float scaleFactor = (float) config.getHudScale(); // Make sure this matches your rendering scale

            // Calculate the new position based on the scaled mouse position and the initial drag offset
            int newX = (int) (mouseX / scaleFactor) - dragOffsetX;
            int newY = (int) (mouseY / scaleFactor) - dragOffsetY;

            // Set the new position for the widget
            hud1.setPosition(newX, newY);

            // Save the updated position without scaling (so the configuration reflects the original scale)
            config.setHudPosX(newX);
            config.setHudPosY(newY);

            return true; // Indicate that this event was handled
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isDragging) {
            isDragging = false;
            return true; // Indicate that this event was handled
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        return;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        float scale = (float) config.getHudScale();
        MatrixStack matrixStack = context.getMatrices();
        matrixStack.push();

        // Apply the same scaling as for the widget
        matrixStack.scale(scale, scale, scale);

        // Render the HUD and hitbox
        hud1.render(context, mouseX, mouseY, delta);

        matrixStack.pop();
    }
}
