package com.incrementalclient.common.utils;

import com.incrementalqol.common.data.SkillType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;

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

    public static short getSkillSlotId(Pair<Integer, List<ItemStack>> content, SkillType skillType) {
        // Current three variations (1. No Sharpshooting or Excavation, 2. Only Sharpshooting, 3. Sharpshooting and Excavation)
        // These can be differentiated by the item in slot 25
        short slotId = 0;
        var customName = Objects.requireNonNull(content.getRight().get(25).getCustomName()).getString();
        switch (customName) {
            case "Excavation": {
                slotId = switch (skillType) {
                    case SkillType.Combat -> 21;
                    case SkillType.Mining -> 19;
                    case SkillType.Foraging -> 20;
                    case SkillType.Farming -> 22;
                    case SkillType.SpearFishing -> 23;
                    case SkillType.Sharpshooting -> 24;
                    case SkillType.Excavation -> 25;
                };
                break;
            }
            case "Sharpshooting": {
                slotId = switch (skillType) {
                    case SkillType.Combat -> 21;
                    case SkillType.Mining -> 19;
                    case SkillType.Foraging -> 20;
                    case SkillType.Farming -> 23;
                    case SkillType.SpearFishing -> 24;
                    case SkillType.Sharpshooting -> 25;
                    case Excavation -> 0;
                };
                break;
            }
            case " ": {
                slotId = switch (skillType) {
                    case SkillType.Combat -> 22;
                    case SkillType.Mining -> 20;
                    case SkillType.Foraging -> 21;
                    case SkillType.Farming -> 23;
                    case SkillType.SpearFishing -> 24;
                    case Sharpshooting -> 0;
                    case Excavation -> 0;
                };
                break;
            }
        }
        return slotId;
    }
}