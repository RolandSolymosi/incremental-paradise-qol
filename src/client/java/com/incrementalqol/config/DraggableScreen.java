package com.incrementalqol.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
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

    hud1 = new HudWidget(config.getHudPosX(), config.getHudPosY(), 100,50);
    this.addDrawableChild(hud1);

    }
    @Override
    public void close() {
        ConfigHandler.saveOptions();
        client.setScreen(parent);
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        HudWidget buttonWidget = hud1;

        if (hud1.isHovered()) {
            // Start dragging
            isDragging = true;
            dragOffsetX = (int) (mouseX - buttonWidget.getX());
            dragOffsetY = (int) (mouseY - buttonWidget.getY());
            return true; // Indicate that this event was handled
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (hud1.isHovered() && isDragging) {
            int newX = (int) mouseX - dragOffsetX;
            int newY = (int) mouseY - dragOffsetY;
            hud1.setPosition(newX, newY);
            config.setHudPosX(newX);
            config.setHudPosY(newY);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        // Stop dragging
        isDragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

}


