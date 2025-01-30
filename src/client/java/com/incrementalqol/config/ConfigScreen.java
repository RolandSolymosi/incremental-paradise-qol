package com.incrementalqol.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.incrementalqol.modules.OptionsModule.MOD_ID;

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
    public CheckboxWidget button3;

    public SliderWidget scaleSlider;


    @Override
    protected void init() {

        button1 = ButtonWidget.builder(Text.literal("Change HUD Positions"), button -> {

                    MinecraftClient.getInstance().setScreen(new DraggableScreen(MinecraftClient.getInstance().currentScreen));

        })
                .dimensions(width/2 - 200,20,200,20)
                .position(15, 20)
                .tooltip(Tooltip.of(Text.literal("Tooltip of Button 1")))
                .build();


        button2 = CheckboxWidget.builder(Text.literal("HUD Background"),textRenderer)
                .checked(config.getHudBackground())
                .callback((button2,checked) -> {
                    config.setHudBackground(checked);
        }).build();
        button2.setPosition(15,50);

        scaleSlider = new ScaleWidget(15,80,100,30,Text.literal("Scale"),config.getHudScale(),0.5,2) {
            {
                this.updateMessage();
            }


            @Override
            protected void applyValue() {
            config.setHudScale(this.snapToStep(min + (this.value * (max - min))));
            }
        };

        button3 = CheckboxWidget.builder(Text.literal("Sort By Task Type"),textRenderer)
                .checked(config.getSortedByType())
                .callback((button3,checked) -> {
                    config.setSortedByType(checked);
                }).build();
        button3.setPosition(15,140);

        addDrawableChild(button1);
        addDrawableChild(button2);
        addDrawableChild(scaleSlider);
        addDrawableChild(button3);

    }
    @Override
    public void close() {
        ConfigHandler.saveOptions();
        client.setScreen(parent);
    }


}


