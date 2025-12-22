package com.incrementalqol.config;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.joml.Matrix3x2fStack; // Use JOML directly
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.client.gui.Click; // The new Click object

import static com.incrementalqol.modules.OptionsModule.MOD_ID;

public class DraggableScreen extends Screen {
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private final Screen parent;
    private final Config config = Config.HANDLER.instance();

    public HudWidget hud1;

    private boolean isDragging = false;
    private double dragOffsetX = 0;
    private double dragOffsetY = 0;

    public DraggableScreen(Screen parent) {
        super(Text.literal("Incremental Paradise QOL Options"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        hud1 = new HudWidget(config.getHudPosX(), config.getHudPosY(), 100, 50);
        // addSelectableChild allows the screen to process clicks for the widget 
        // without drawing it automatically (so we can handle the scaling manually).
        this.addSelectableChild(hud1);
    }

    @Override
    public void close() {
        Config.HANDLER.save();
        if (this.client != null) {
            this.client.setScreen(parent);
        }
    }

    // Signature updated to use Click click, boolean bl
    @Override
    public boolean mouseClicked(Click click, boolean bl) {
        float scale = (float) config.getHudScale();
        
        // Adjust mouse coordinates from the Click object to match the widget's scale
        double scaledX = click.x() / scale;
        double scaledY = click.y() / scale;

        if (hud1.isMouseOver(scaledX, scaledY)) {
            isDragging = true;
            dragOffsetX = scaledX - hud1.getX();
            dragOffsetY = scaledY - hud1.getY();
            return true; 
        }
        return super.mouseClicked(click, bl);
    }

    // Signature updated to use Click click, double deltaX, double deltaY
    @Override
    public boolean mouseDragged(Click click, double deltaX, double deltaY) {
        if (isDragging) {
            float scaleFactor = (float) config.getHudScale();

            int newX = (int) (click.x() / scaleFactor - dragOffsetX);
            int newY = (int) (click.y() / scaleFactor - dragOffsetY);

            hud1.setPosition(newX, newY);
            config.setHudPosX(newX);
            config.setHudPosY(newY);

            return true;
        }
        return super.mouseDragged(click, deltaX, deltaY);
    }

    // Signature updated to use Click click
    @Override
    public boolean mouseReleased(Click click) {
        if (isDragging) {
            isDragging = false;
            return true;
        }
        return super.mouseReleased(click);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        // Standard background rendering for 1.21.x
        this.renderInGameBackground(context);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);

        float scale = (float) config.getHudScale();
        
        // Use Matrix3x2fStack (JOML) instead of MatrixStack
        Matrix3x2fStack matrices = context.getMatrices();
        
        matrices.pushMatrix();
        matrices.scale(scale, scale);

        // Adjust mouse coords for widget hover state
        int scaledMouseX = (int) (mouseX / scale);
        int scaledMouseY = (int) (mouseY / scale);
        
        hud1.renderWidget(context, scaledMouseX, scaledMouseY, delta);

        matrices.popMatrix();

        super.render(context, mouseX, mouseY, delta);
    }
}