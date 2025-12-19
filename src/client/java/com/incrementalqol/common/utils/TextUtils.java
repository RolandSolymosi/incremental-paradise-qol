package com.incrementalqol.common.utils;

import net.minecraft.text.*;

public class TextUtils {

    public static Text textColor(String string, int rgb) {
        return Text.literal(string).styled(s -> s.withColor(rgb));
    }

    public static Text textColorUnderline(String string, int rgb) {
        return Text.literal(string).styled(s -> s.withColor(rgb).withUnderline(true));
    }

    public static MutableText mutableRecolor(Text text, int rgb) {
        return Text.literal(text.getString()).styled(s -> s.withColor(rgb));
    }
}
