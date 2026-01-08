package com.incrementalclient.hud.internals;

import com.incrementalclient.abstractions.HudElement;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.services.ConfigHandler;
import com.incrementalclient.common.utils.Vector2f;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class HudCustomizationScreen extends Screen {
    private final Screen parent;
    private final HudElement<?>[] hudElements;
    private final ConfigHandler configHandler;
    private final List<Integer> snapPointsX = new ArrayList<>();
    private final List<Integer> snapPointsY = new ArrayList<>();

    public HudCustomizationScreen(MinecraftClientAccessor mcAccessor, HudElement<?>[] hudElements, ConfigHandler configHandler) {
        super(Text.literal("HUD Customization"));
        this.parent = mcAccessor.getScreen().orElse(null);
        this.hudElements = hudElements;
        this.configHandler = configHandler;
    }

    @Override
    protected void init() {
        // Notify elements that edit mode is entering
        for (HudElement<?> element : hudElements) {
            element.onEditModeEnter();
        }

        refreshSnapPoints();

        // Create widgets for each element
        // Order matters: toggle and scale widgets should be added AFTER element widget
        // so they render on top and can be clicked
        for (HudElement<?> element : hudElements) {
            // Add toggle widget first (will be rendered last, so on top)
            addDrawableChild(new HudElementToggleWidget(element, 0, 0));
            // Add scale widget
            if (element.isScalable()) {
                Vector2f pos = element.getCurrentPosition();
                addDrawableChild(new HudScaleWidget(element, (int) pos.x + 7, (int) pos.y + 7));
            }
            // Add element widget last (will be rendered first, so behind other widgets)
            addDrawableChild(new HudElementWidget(element, snapPointsX, snapPointsY));
        }

        // Reset all button
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Reset All"),
            button -> {
                for (HudElement<?> element : hudElements) {
                    element.resetDeltaPositions();
                    element.setScale(1.0f);
                }
                refreshSnapPoints();
            }
        ).dimensions(width / 2 - 105, 5, 100, 20).build());

        // Done button
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Done"),
            button -> {
                configHandler.save();
                client.setScreen(parent);
            }
        ).dimensions(width / 2 + 5, 5, 100, 20).build());

        // Instructions text (rendered separately)
    }

    private void refreshSnapPoints() {
        snapPointsX.clear();
        snapPointsY.clear();

        // Add screen edges
        snapPointsX.add(0);
        snapPointsX.add(width);
        snapPointsY.add(0);
        snapPointsY.add(height);

        // Add screen center
        snapPointsX.add(width / 2);
        snapPointsY.add(height / 2);

        // Add quarter points
        snapPointsX.add(width / 4);
        snapPointsX.add(width * 3 / 4);
        snapPointsY.add(height / 4);
        snapPointsY.add(height * 3 / 4);

        // Add element positions
        for (HudElement<?> element : hudElements) {
            Vector2f pos = element.getCurrentPosition();
            Vector2f bounds = element.getCurrentBoundingPoint();

            snapPointsX.add((int) pos.x);
            snapPointsY.add((int) pos.y);
            snapPointsX.add((int) bounds.x);
            snapPointsY.add((int) bounds.y);
            snapPointsX.add((int) (pos.x + bounds.x / 2)); // Center of element
            snapPointsY.add((int) (pos.y + bounds.y / 2));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Render background
        renderBackground(context, mouseX, mouseY, delta);

        // Render instructions
        String instructions = "Drag elements to move | Right-click to reset | Toggle ON/OFF to show/hide";
        var textRenderer = client.textRenderer;
        int textWidth = textRenderer.getWidth(instructions);
        int textX = (width - textWidth) / 2;
        context.fill(textX - 5, height - 35, textX + textWidth + 5, height - 15, HudElement.HudConstants.TEXT_BACKGROUND);
        context.drawText(textRenderer, instructions, textX, height - 30, HudElement.HudConstants.TEXT_WHITE, false);

        // Render HUD elements in edit mode
        for (var element : hudElements) {
            if (!element.isEnabled()) continue;

            net.minecraft.client.util.math.MatrixStack matrixStack = context.getMatrices();
            matrixStack.push();
            matrixStack.scale(element.getScale(), element.getScale(), element.getScale());

            element.render(new HudElement.RenderSettings(context, delta, true));

            matrixStack.pop();
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        // Semi-transparent background
        context.fill(0, 0, width, height, HudElement.HudConstants.TEXT_BACKGROUND);
    }
    
    @Override
    public void close() {
        configHandler.save();
        client.setScreen(parent);
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        refreshSnapPoints();
        return super.mouseReleased(mouseX, mouseY, button);
    }
}

