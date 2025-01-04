package com.incrementalqol.config;

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

public class ConfigScreen extends Screen {
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private final Screen parent;

    private final Config config = ConfigHandler.getConfig();

    public ConfigScreen(Screen parent) {
        super(Text.literal("Incremental Paradise QOL Options"));
        this.parent = parent;
    }

    public ButtonWidget button1;
    public CheckboxWidget button2;
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

        button1 = ButtonWidget.builder(Text.literal("Change HUD Positions"), button -> {

                    MinecraftClient.getInstance().setScreen(new DraggableScreen(MinecraftClient.getInstance().currentScreen));

        })
                .dimensions(width/2 - 200,20,200,20)
                .position(buttonXPosition, buttonYPosition)
                .tooltip(Tooltip.of(Text.literal("Tooltip of Button 1")))
                .build();

        button2 = CheckboxWidget.builder(Text.literal("HUD Background"),textRenderer)
                .checked(config.getHudBackground())
                .callback((button2,checked) -> {
                    config.setHudBackground(checked);
        }).build();
        button2.setPosition(10,10);

        addDrawableChild(button1);
        addDrawableChild(button2);

    }
    @Override
    public void close() {
        ConfigHandler.saveOptions();
        client.setScreen(parent);
    }


}


