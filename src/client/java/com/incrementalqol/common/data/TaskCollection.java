package com.incrementalqol.common.data;

import java.util.HashMap;
import java.util.Map;

public class TaskCollection {

    public static final Map<Tasks, TaskDescriptor> TaskDescriptors = new HashMap<>();

    static {
        TaskDescriptors.put(Tasks.Gold, new TaskDescriptor("Gold", TaskType.Misc, "warp redstone", "warp mine"));
        TaskDescriptors.put(Tasks.GoldOre, new TaskDescriptor("Gold Ore", TaskType.Misc, "warp redstone", "warp mine"));
    }

    public enum Tasks{
        Gold,
        GoldOre
    }

    public TaskDescriptor TryGetDescriptor(String name){
        return switch (name.toLowerCase()){
            case "gold" -> TaskDescriptors.get(Tasks.Gold);
            case "gold ore" -> TaskDescriptors.get(Tasks.GoldOre);

            default -> null;
        };
    }
}

