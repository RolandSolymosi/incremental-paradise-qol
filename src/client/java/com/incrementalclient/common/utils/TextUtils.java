package com.incrementalclient.common.utils;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.net.URI;

public class TextUtils {

    public static Text textColor(String string, int rgb) {
        return Text.literal(string).styled(s -> s.withColor(rgb));
    }

    public static Text textColor(String string, int rgb, boolean underline, boolean bold) {
        return Text.literal(string).styled(s -> s.withColor(rgb).withUnderline(underline).withBold(bold));
    }

    public static Text textColor(String string, Formatting color) {
        return Text.literal(string).styled(s -> s.withColor(color));
    }

    public static Text textColor(String string, Formatting color, boolean underline, boolean bold) {
        return Text.literal(string).styled(s -> s.withColor(color).withUnderline(underline).withBold(bold));
    }

    public static Text textLink(String string, String clickLink, String hoverText) {
        return Text.literal(string).styled(s -> s
                .withClickEvent(new ClickEvent.OpenUrl(URI.create(clickLink)))
                .withHoverEvent(new HoverEvent.ShowText(Text.literal(hoverText)))
        );
    }

    public static MutableText mutableRecolor(Text text, int rgb) {
        return Text.literal(text.getString()).styled(s -> s.withColor(rgb));
    }
}
