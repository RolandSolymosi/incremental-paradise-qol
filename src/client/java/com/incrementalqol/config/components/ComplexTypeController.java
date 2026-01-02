package com.incrementalqol.config.components;

import dev.isxander.yacl3.api.Controller;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.utils.Dimension;
import dev.isxander.yacl3.gui.AbstractWidget;
import dev.isxander.yacl3.gui.YACLScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.function.Function;

public final class ComplexTypeController<T> implements Controller<T> {
    private final Option<T> option;
    private final Function<T, Text> textProvider;
    private final Function<T, Screen> screenFactory;

    public ComplexTypeController(Option<T> option, Function<T, Text> textProvider, Function<T, Screen> screenFactory) {
        this.option = option;
        this.textProvider = textProvider;
        this.screenFactory = screenFactory;
    }

    @Override
    public Option<T> option() {
        return option;
    }

    @Override
    public Text formatValue() {
        return textProvider.apply(option.pendingValue());
    }

    @Override
    public AbstractWidget provideWidget(YACLScreen screen, Dimension<Integer> widgetDimension) {
        return new ComplexTypeWidget(this, widgetDimension);
    }

    public static class Builder<T> implements ControllerBuilder<T> {
        private final Option<T> option;
        private Function<T, Text> textProvider = t -> Text.of(t.toString());
        private Function<T, Screen> screenFactory;

        public Builder(Option<T> option) {
            this.option = option;
        }

        public Builder<T> textProvider(Function<T, Text> textProvider) {
            this.textProvider = textProvider;
            return this;
        }

        public Builder<T> screenFactory(Function<T, Screen> screenFactory) {
            this.screenFactory = screenFactory;
            return this;
        }

        @Override
        public Controller<T> build() {
            if (screenFactory == null) {
                throw new IllegalStateException("Screen factory must be provided");
            }
            return new ComplexTypeController<>(option, textProvider, screenFactory);
        }
    }

    public static <T> Builder<T> create(Option<T> option) {
        return new Builder<>(option);
    }

    private class ComplexTypeWidget extends AbstractWidget {
        private final ComplexTypeController<T> controller;

        public ComplexTypeWidget(ComplexTypeController<T> controller, Dimension<Integer> dim) {
            super(dim);
            this.controller = controller;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            MinecraftClient client = MinecraftClient.getInstance();
            TextRenderer textRenderer = client.textRenderer;

            int x = getDimension().x();
            int y = getDimension().y();
            int width = getDimension().width();
            int height = getDimension().height();

            // Draw button background
            context.fill(x, y, x + width, y + height, 0xFF444444);
            context.drawBorder(x, y, width, height, 0xFFFFFFFF);

            // Draw text
            Text text = controller.textProvider.apply(controller.option().pendingValue());
            int textWidth = textRenderer.getWidth(text);
            context.drawText(textRenderer, text, x + (width - textWidth) / 2, y + (height - 8) / 2, 0xFFFFFFFF, true);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isMouseOver(mouseX, mouseY)) {
                Screen editScreen = controller.screenFactory.apply(controller.option().pendingValue());
                MinecraftClient.getInstance().setScreen(editScreen);
                return true;
            }
            return false;
        }

        @Override
        public void setFocused(boolean focused) {
        }

        @Override
        public boolean isFocused() {
            return false;
        }
    }
}
