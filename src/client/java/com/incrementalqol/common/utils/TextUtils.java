package com.incrementalqol.common.utils;

import net.minecraft.text.*;
import java.net.URI;

public class TextUtils {

    public static Text textColor(String string, int rgb) {
        return Text.literal(string).styled(s -> s.withColor(rgb));
    }

    public static Text textLink(String string, String clickLink, String hoverText) {
        return Text.literal(string).styled(s -> s
                .withClickEvent(new ClickEvent.OpenUrl(URI.create(clickLink)))
                .withHoverEvent(new HoverEvent.ShowText(Text.literal(hoverText)))
        );
    }

    public static Text textColorUnderline(String string, int rgb) {
        return Text.literal(string).styled(s -> s.withColor(rgb).withUnderline(true));
    }

    public static MutableText mutableRecolor(Text text, int rgb) {
        return Text.literal(text.getString()).styled(s -> s.withColor(rgb));
    }
}
