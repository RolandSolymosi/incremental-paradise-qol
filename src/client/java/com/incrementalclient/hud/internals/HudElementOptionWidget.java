package com.incrementalclient.hud.internals;

import com.incrementalclient.abstractions.HudElement;
import com.incrementalclient.common.utils.Vector2f;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;

public abstract class HudElementOptionWidget extends ClickableWidget {
    protected final HudElement element;
    protected final int yOffset;

    public HudElementOptionWidget(HudElement element, int width, int height) {
        super(0, 0, width, height, Text.empty());
        this.element = element;
        this.yOffset = 0;
    }

    public HudElementOptionWidget(HudElement element, int width, int height, int yOffset) {
        super(0, 0, width, height, Text.empty());
        this.element = element;
        this.yOffset = yOffset;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        Vector2f pos = element.getTopLeftCornerPosition();

        setX((int) pos.x);
        setY((int) pos.y);

        if (getX() < 0) this.setX((int) pos.x);
        if (getY() < 0) this.setY((int) pos.y);

        int bgColor = getBackgroundColor();
        int borderColor = getBorderColor();

        context.fill(getX(), getY(), getX() + width, getY() + height, bgColor);
        context.drawBorder(getX(), getY(), width, height, borderColor);

        String text = getText();
        var textRenderer = net.minecraft.client.MinecraftClient.getInstance().textRenderer;
        int textX = getX() + (width - textRenderer.getWidth(text)) / 2;
        int textY = getY() + (height - 8) / 2;
        context.drawText(textRenderer, text, textX, textY, HudElement.HudConstants.TEXT_WHITE, false);
    }

    @Override
    public void setX(int x){
        int screenWidth = element.getScreenWidth();
        if (x < screenWidth / 2) {
            super.setX((int) (x + element.getBoundingBox().x + 2));
        } else {
            super.setX((x - this.getWidth() - 2));
        }
    }

    @Override
    public void setY(int y){
        super.setY(y + yOffset);
    }

    protected abstract int getBorderColor();

    protected abstract int getBackgroundColor();

    protected abstract String getText();
}
