package com.incrementalclient.common.utils;

import com.incrementalclient.common.data.skills.SkillCategory;
import com.incrementalclient.internals.ScreenCapture;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public static short getSkillSlotId(String nameOfSlot25, SkillCategory skillType) {
        // Current three variations (1. No Sharpshooting or Excavation, 2. Only Sharpshooting, 3. Sharpshooting and Excavation)
        // These can be differentiated by the item in slot 25
        short slotId = 0;
        switch (nameOfSlot25) {
            case "Excavation": {
                slotId = switch (skillType) {
                    case SkillCategory.Combat -> 21;
                    case SkillCategory.Mining -> 19;
                    case SkillCategory.Foraging -> 20;
                    case SkillCategory.Farming -> 22;
                    case SkillCategory.SpearFishing -> 23;
                    case SkillCategory.Sharpshooting -> 24;
                    case SkillCategory.Excavation -> 25;
                };
                break;
            }
            case "Sharpshooting": {
                slotId = switch (skillType) {
                    case SkillCategory.Combat -> 21;
                    case SkillCategory.Mining -> 19;
                    case SkillCategory.Foraging -> 20;
                    case SkillCategory.Farming -> 23;
                    case SkillCategory.SpearFishing -> 24;
                    case SkillCategory.Sharpshooting -> 25;
                    case Excavation -> 0;
                };
                break;
            }
            case " ": {
                slotId = switch (skillType) {
                    case SkillCategory.Combat -> 22;
                    case SkillCategory.Mining -> 20;
                    case SkillCategory.Foraging -> 21;
                    case SkillCategory.Farming -> 23;
                    case SkillCategory.SpearFishing -> 24;
                    case Sharpshooting -> 0;
                    case Excavation -> 0;
                };
                break;
            }
        }
        return slotId;
    }

    public static String formatDurationSeconds(Duration duration) {
        // there isn't an ofSeconds, and either way I want this more.
        var millis = duration.toMillis();
        // typecast to ensure floating point division is used
        // instead of integer division (will round)
        var seconds = ((double) millis) / 1000d;
        return String.format("%.2f", seconds);
    }
}