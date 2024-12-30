package com.incrementalqol;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.incrementalqol.EntryPointClient.MOD_ID;

public class OptionsScreen extends Screen {
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private final Screen parent;

    protected OptionsScreen(Screen parent) {
        super(Text.literal("Incremental Paradise QOL Options"));
        this.parent = parent;
    }

    public ButtonWidget button1;
    public ButtonWidget button2;
    public CheckboxWidget button3;
    private static final int WINDOW_WIDTH = 200;
    private static final int WINDOW_HEIGHT = 100;
    private static final int HEADER_HEIGHT = 20;

    private int windowX = 100;
    private int windowY = 100;
    private boolean isDragging = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;
    private static int buttonXPosition = 100;
    private static int buttonYPosition = 100;

    @Override
    protected void init() {

        button1 = ButtonWidget.builder(Text.literal("Button 1"), button -> {
            LOGGER.warn("Button 1 clicked");
        })
                .dimensions(width/2 - 200,20,200,20)
                .position(buttonXPosition, buttonYPosition)
                .tooltip(Tooltip.of(Text.literal("Tooltip of Button 1")))
                .build();

        button2 = ButtonWidget.builder(Text.literal("Button 4"), button -> {
                    LOGGER.warn("Button 2 clicked");
                })
                .dimensions(width / 2 + 5, 20, 200, 20)
                .tooltip(Tooltip.of(Text.literal("Tooltip of Button 2")))
                .build();


        addDrawableChild(button1);
        addDrawableChild(button2);

    }
    @Override
    public void close() {
        client.setScreen(parent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("You must see me"), width / 2, height / 2, 0xffffff);

        super.render(context, mouseX, mouseY, delta);

        // Draw the window background (50% transparent black)
        context.fill(windowX, windowY + HEADER_HEIGHT, windowX + WINDOW_WIDTH, windowY + WINDOW_HEIGHT, 0x80000000);

        // Draw the header (blue)
        context.fill(windowX, windowY, windowX + WINDOW_WIDTH, windowY + HEADER_HEIGHT, 0xFF0000FF);

        // holloh text(:
        context.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, "Holloh!", windowX + WINDOW_WIDTH / 2, windowY + 5, 0xFFFFFF);
    }


    private boolean isMouseOverButton(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        ButtonWidget buttonWidget = button1;

        if (button1.isHovered()) {
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
        if (isDragging) {
            ButtonWidget buttonWidget = button1;

            // Update button position relative to the drag offset
            int newX = (int) (mouseX - dragOffsetX);
            int newY = (int) (mouseY - dragOffsetY);
            buttonXPosition = newX;
            buttonYPosition = newY;
            buttonWidget.setPosition(newX, newY);

            return true; // Indicate that this event was handled
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
