package com.incrementalclient.hud.internals;

import com.incrementalclient.abstractions.HudElement;
import net.minecraft.client.gui.widget.ClickableWidget;
import com.incrementalclient.common.utils.Vector2f;
import com.incrementalclient.internals.MinecraftClientAccessor;

public class Utils {

    public static void setScreenSideXPos(ClickableWidget widget, HudElement hudElement) {
        Vector2f pos = hudElement.getCurrentPosition();
        Vector2f bounds = hudElement.getBoundingBox();
        // NOTE: This is a weird way to get a singleton, maybe change?
        int screenWidth = new MinecraftClientAccessor().getClient().getWindow().getScaledWidth();
        if (pos.x < screenWidth / 2) {
            widget.setX((int) (pos.x + bounds.x + 2));
        } else {
            widget.setX((int) (pos.x - widget.getWidth() - 2));
        }
    }
}