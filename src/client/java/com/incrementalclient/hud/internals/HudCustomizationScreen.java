package com.incrementalclient.hud.internals;

import com.incrementalclient.abstractions.HudElement;
import com.incrementalclient.hud.BottomBarElement;
import com.incrementalclient.hud.TopBarElement;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.services.ConfigHandler;
import com.incrementalclient.services.HudManager;
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
    private final HudManager hudManager;
    private final List<Integer> snapPointsX = new ArrayList<>();
    private final List<Integer> snapPointsY = new ArrayList<>();

    public HudCustomizationScreen(MinecraftClientAccessor mcAccessor, HudElement<?>[] hudElements, ConfigHandler configHandler, HudManager hudManager) {
        super(Text.literal("HUD Customization"));
        this.parent = mcAccessor.getScreen().orElse(null);
        this.hudElements = hudElements;
        this.configHandler = configHandler;
        this.hudManager = hudManager;
    }

    @Override
    protected void init() {
        HudManager.Configuration.ActiveBarMode activeBarMode = hudManager.getConfiguration().getActiveBarMode();
        
        for (HudElement<?> element : hudElements) {
            element.onEditModeEnter();
        }

        refreshSnapPoints();

        List<HudElement<?>> barsToAdd = new ArrayList<>();
        List<HudElement<?>> otherElementsToAdd = new ArrayList<>();
        
        for (HudElement<?> element : hudElements) {
            if (!HudManager.shouldRenderBar(element, hudManager.getConfiguration())) {
                continue;
            }
            
            if (element instanceof BottomBarElement || element instanceof TopBarElement) {
                barsToAdd.add(element);
            } else {
                otherElementsToAdd.add(element);
            }
        }
        
        for (HudElement<?> element : otherElementsToAdd) {
            addDrawableChild(new HudElementWidget(element, snapPointsX, snapPointsY, width, height));
        }
        
        for (HudElement<?> element : otherElementsToAdd) {
            addDrawableChild(new HudElementToggleWidget(element, 0, 0));
            if (element.isScalable()) {
                Vector2f pos = element.getCurrentPosition();
                addDrawableChild(new HudScaleWidget(element, (int) pos.x + 7, (int) pos.y + 7));
            }
        }

        int buttonY = 5;
        if (hudManager.getConfiguration().getBarScoreboardReplacement()) {
            buttonY += hudManager.getBarHeight();
        }
        
        addDrawableChild(ButtonWidget.builder(
            Text.literal("Reset All"),
            button -> {
                for (HudElement<?> element : hudElements) {
                    element.resetDeltaPositions();
                    element.setScale(1.0f);
                }
            }
        ).dimensions(width / 2 - 105, buttonY, 100, 20).build());

        addDrawableChild(ButtonWidget.builder(
            Text.literal("Done"),
            button -> {
                configHandler.save();
                client.setScreen(parent);
            }
        ).dimensions(width / 2 + 5, buttonY, 100, 20).build());
    }

    private void refreshSnapPoints() {
        snapPointsX.clear();
        snapPointsY.clear();

        snapPointsX.add(0);
        snapPointsX.add(width);
        snapPointsY.add(0);
        snapPointsY.add(height);

        for (HudElement<?> element : hudElements) {
            if (element instanceof BottomBarElement || element instanceof TopBarElement) {
                continue;
            }
            
            Vector2f pos = element.getCurrentPosition();
            Vector2f bounds = element.getBoundingBox();
            int centerX = (int) (pos.x + bounds.x / 2);
            int centerY = (int) (pos.y + bounds.y / 2);
            
            snapPointsX.add(centerX);
            snapPointsY.add(centerY);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);

        String instructions = "Drag elements to move | Right-click to reset | Toggle ON/OFF to show/hide";
        var textRenderer = client.textRenderer;
        int textWidth = textRenderer.getWidth(instructions);
        int textX = (width - textWidth) / 2;
        context.fill(textX - 5, height - 35, textX + textWidth + 5, height - 15, HudElement.HudConstants.TEXT_BACKGROUND);
        context.drawText(textRenderer, instructions, textX, height - 30, HudElement.HudConstants.TEXT_WHITE, false);

        List<HudElement<?>> bars = new ArrayList<>();
        List<HudElement<?>> otherElements = new ArrayList<>();
        for (var element : hudElements) {
            if (element instanceof BottomBarElement || element instanceof TopBarElement) {
                if (HudManager.shouldRenderBar(element, hudManager.getConfiguration())) {
                    bars.add(element);
                }
            } else {
                otherElements.add(element);
            }
        }
        
        renderElements(bars, context, delta);
        renderElements(otherElements, context, delta);

        super.render(context, mouseX, mouseY, delta);
    }
    
    private void renderElements(List<HudElement<?>> elements, DrawContext context, float delta) {
        for (var element : elements) {
            if (!element.isEnabled()) continue;

            net.minecraft.client.util.math.MatrixStack matrixStack = context.getMatrices();
            matrixStack.push();
            Vector2f pos = element.getDeltaPosition();
            matrixStack.translate(pos.x, pos.y, 0);
            matrixStack.scale(element.getScale(), element.getScale(), element.getScale());
            matrixStack.translate(-pos.x, -pos.y, 0);

            element.render(new HudElement.RenderSettings(context, delta, true));

            matrixStack.pop();
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
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

