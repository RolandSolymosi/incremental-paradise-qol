package com.incrementalqol.common.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static boolean isPlayerHead(ItemStack stack) {
        Item currentItem = stack.getItem();
        return currentItem.getName().getString().contains("Head");
    }

    public static List<String> parseLoreLines(List<Text> text) {
        List<String> blocks = new ArrayList<>();
        StringBuilder blockBuilder = new StringBuilder();
        for (Text line : text) {
            if (line.getString().equals(" ") || line.getString().equals("")) {
                blocks.add(blockBuilder.toString());
                blockBuilder.setLength(0);
            } else {
                blockBuilder.append(line.getString());
            }
        }
        if (!blockBuilder.isEmpty()) {
            blocks.add(blockBuilder.toString());
        }
        return blocks;
    }
}