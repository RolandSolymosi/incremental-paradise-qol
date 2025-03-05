package com.incrementalqol.config;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class ScaleWidget extends SliderWidget {

    public final double min;
    public final double max;

    public ScaleWidget(int x, int y, int width, int height, Text message, double value, double min, double max) {
        super(x, y, width, height, message, (value - min) / (max - min));
        this.min = min;
        this.max = max;
        updateMessage();
    }

    @Override
    protected void applyValue() {
        // Calculate the actual value and snap it to the nearest step
        double actualValue = snapToStep(min + (this.value * (max - min)));


    }

    @Override
    protected void updateMessage() {
        // Display the value snapped to the nearest step
        double displayValue = snapToStep(min + (this.value * (max - min)));

        this.setMessage(Text.literal(String.format("HUD Scale : %.1fx", displayValue)));
    }

    public double snapToStep(double value) {
        // Snap the value to the nearest step increment
        return Math.round(value / 0.1) * 0.1;
    }

    // Optionally, you can add a method to get the clamped and snapped value
    public double getClampedValue() {
        return snapToStep(min + (this.value * (max - min)));
    }
}
