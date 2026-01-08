package com.incrementalclient.config.controllers;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class KeyBindController implements Controller<Integer> {
    private final Option<Integer> option;

    public KeyBindController(Option<Integer> option) {
        this.option = option;
    }

    @Override
    public Option<Integer> option() {
        return option;
    }

    @Override
    public Text formatValue() {
        int code = option().pendingValue();
        if (code == GLFW.GLFW_KEY_UNKNOWN) {
            return Text.literal("None");
        }
        return InputUtil.fromKeyCode(code, 0).getLocalizedText();
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> dim) {
        return new ControllerWidget<>(this, screen, dim) {
            @Override
            protected int getHoveredControlWidth() {
                return 200;
            }

            private boolean listening = false;

            @Override
            public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
                super.render(graphics, mouseX, mouseY, delta);
                if (!listening) {
                    return;
                }

                Text listeningText = Text.literal("> ___ <");

                graphics.drawText(textRenderer, listeningText, this.getDimension().xLimit() - this.textRenderer.getWidth(listeningText) - this.getXPadding(), getTextY(), 0xFFFFFFFF, true);
            }

            @Override
            protected void drawValueText(DrawContext graphics, int mouseX, int mouseY, float delta) {
                if (!listening){
                    Text valueText = this.getValueText();
                    graphics.drawText(this.textRenderer, valueText, this.getDimension().xLimit() - this.textRenderer.getWidth(valueText) - this.getXPadding(), this.getTextY(), this.getValueColor(), true);
                }
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                boolean mouseOver = isMouseOver(mouseX, mouseY);

                if (mouseOver && isAvailable()) {
                    listening = !listening;
                    if (listening) playDownSound();
                    return true;
                }

                if (listening) {
                    listening = false;
                    return true;
                }

                return super.mouseClicked(mouseX, mouseY, button);
            }

            @Override
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                if (!listening) return super.keyPressed(keyCode, scanCode, modifiers);
                if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                    option.requestSet(-1);
                } else {
                    option.requestSet(keyCode);
                }
                listening = false;
                return true;
            }

            @Override
            public void setFocused(boolean focused) {
                listening = focused;
            }

            @Override
            public boolean isFocused() {
                return listening;
            }

            @Override
            public void unfocus(){
                listening = false;
            }
        };
    }
}
