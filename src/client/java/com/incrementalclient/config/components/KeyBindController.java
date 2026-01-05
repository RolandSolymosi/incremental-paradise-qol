package com.incrementalclient.config.components;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import dev.isxander.yacl3.gui.controllers.ControllerWidget;
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
        return InputUtil.fromKeyCode(option().pendingValue(), 0).getLocalizedText();
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> dim) {
        return new ControllerWidget<KeyBindController>(this, screen, dim) {
            @Override
            protected int getHoveredControlWidth() {
                return 200;
            }

            private boolean listening = false;

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                if (!isMouseOver(mouseX, mouseY) || !isAvailable())
                    return false;

                listening = true;
                return true;
            }

            @Override
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
                if (!listening) return false;
                if (keyCode == GLFW.GLFW_KEY_ESCAPE) option.requestSet(-1);
                else option.requestSet(keyCode);
                listening = false;
                return true;
            }

            @Override
            public void setFocused(boolean focused) {

            }

            @Override
            public boolean isFocused() {
                return false;
            }
        };
    }
}
