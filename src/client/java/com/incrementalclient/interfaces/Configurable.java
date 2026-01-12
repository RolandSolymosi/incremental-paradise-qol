package com.incrementalclient.interfaces;

import dev.isxander.yacl3.api.Option;

import java.util.List;

public interface Configurable<TConfiguration> {
    String getJsonSection();

    TConfiguration getConfiguration();

    default List<OptionPiece> getOption() {
        return List.of();
    }

    default boolean hasOption() {
        return !getOption().isEmpty();
    }

    void optionChanged();

    default void copyFrom(Object other) {
        if (other == null) return;
        if (other.getClass() != getConfiguration().getClass()) {
            throw new IllegalArgumentException("Config Handler tries to load wrong type.");
        }
        TConfiguration target = getConfiguration();
        // Iterate through all fields of the configuration class
        var currentClass = target.getClass();
        while (currentClass != null && currentClass != Object.class) {
            for (java.lang.reflect.Field field : currentClass.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    // Copy the value from the 'other' (loaded) object to 'target' (live) object
                    var value = field.get(other);
                    field.set(target, value);
                } catch (IllegalAccessException e) {
                    System.err.println("Failed to copy field: " + field.getName());
                }
            }
            currentClass = currentClass.getSuperclass();
        }

        optionChanged();
    }

    record OptionPiece(
            String Category,
            String CategoryDescription,
            int CategoryOrder,
            String Group,
            String GroupDescription,
            int GroupOrder,
            int Order,
            Option<?> Option
    ) {}

    interface ConfigNamespace {
        String category();

        int categoryOrder();

        String categoryDescription();

        String group();

        int groupOrder();

        String groupDescription();

        default OptionPiece createConfig(int order, Option<?> option) {
            return createConfig(this.groupOrder(), order, option);
        }

        default OptionPiece createConfig(int groupOrder, int order, Option<?> option) {
            return new OptionPiece(
                    category(),
                    categoryDescription(),
                    categoryOrder(),
                    group(),
                    groupDescription(),
                    groupOrder,
                    order,
                    option
            );
        }
    }

    class Categories {

        public static abstract class BaseCategory implements ConfigNamespace {
            private final String name;
            private final String desc;
            private final int catOrder;
            private final int defaultGroupOrder;

            public BaseCategory(String name, String desc, int catOrder, int defaultGroupOrder) {
                this.name = name;
                this.desc = desc;
                this.catOrder = catOrder;
                this.defaultGroupOrder = defaultGroupOrder;
            }

            @Override
            public OptionPiece createConfig(int groupOrder, Option<?> option) {
                return createConfig(groupOrder, 0, option);
            }

            @Override public String category() { return name; }
            @Override public int categoryOrder() { return catOrder; }
            @Override public String categoryDescription() { return desc; }
            @Override public int groupOrder() { return defaultGroupOrder; }
            @Override public String group() { return ""; }
            @Override public String groupDescription() { return ""; }
        }

        public static final class Group implements ConfigNamespace {
            private final BaseCategory parent;
            private final String name;
            private final int order;
            private final String desc;

            public Group(BaseCategory parent, String name, int order, String desc) {
                this.parent = parent;
                this.name = name;
                this.order = order;
                this.desc = desc;
            }

            @Override
            public OptionPiece createConfig(int itemOrder, Option<?> option) {
                return createConfig(this.order, itemOrder, option);
            }

            @Override public String category() { return parent.category(); }
            @Override public int categoryOrder() { return parent.categoryOrder(); }
            @Override public String categoryDescription() { return parent.categoryDescription(); }
            @Override public String group() { return name; }
            @Override public int groupOrder() { return order; }
            @Override public String groupDescription() { return desc; }
        }

        public static final class HudCategory extends BaseCategory {
            private HudCategory() { super("HUD", "Hud related settings.", 0, 0); }

            public final Group General = new Group(this, "General", 0, "Combat settings.");
            public final Group Vanilla = new Group(this, "Vanilla", 100, "Hide vanilla Minecraft HUD elements to replace them with custom versions.");
            public final Group Consumable = new Group(this, "Consumable", 200, "Active Consumable tracker Hud related settings.");
            public final Group Currency = new Group(this, "Currency", 300, "Currency Hud related settings.");
            public final Group HpBar = new Group(this, "HpBar", 400, "HpBar related settings.");
            public final Group ItemTarget = new Group(this, "Item Target", 500, "Item Target tracker Hud element.");
        }
        public static final HudCategory Hud = new HudCategory();

        public static final class HotkeysCategory extends BaseCategory {
            private HotkeysCategory() { super("Hotkeys", "Hotkey related settings.", 1000, 0); }

            public final Group Bank = new Group(this, "Banking", 0, "Banking related hotkeys");
            public final Group BlueprintSwap = new Group(this, "Blueprint Swap", 100, "Blueprint swapping hotkeys (swap to the first blueprint in the list, for now)");
        }
        public static final HotkeysCategory Hotkeys = new HotkeysCategory();

        public static final class TaskingCategory extends BaseCategory {
            private TaskingCategory() { super("Tasking", "Tasking related settings.", 2500, 0); }

            public final Group General = new Group(this, "General", 0, "Tasking general settings, like hotkey.");
            public final Group Hud = new Group(this, "Hud", 500, "Task Tracker Hud related settings.");
            public final Group Wardrobe = new Group(this, "Wardrobe", 1000, "Auto swap settings of wardrobes.");
            public final Group Tools = new Group(this, "Tools", 2000, "Auto swap settings of tools.");
        }
        public static final TaskingCategory Tasking = new TaskingCategory();

        public static final class SkillLevelingCategory extends BaseCategory {
            private SkillLevelingCategory() { super("Skill Leveling", "Skill leveling related settings.", 5000, 0); }

            public final Group General = new Group(this, "General", 0, "The common base settings for all skill category");
        }
        public static final SkillLevelingCategory SkillLeveling = new SkillLevelingCategory();

        public static final class MiscCategory extends BaseCategory {
            private MiscCategory() { super("Misc", "Other small settings.", 10000, 0); }

            public final Group General = new Group(this, "General", 0, "Small features not fitting anywhere else.");
            public final Group PetXp = new Group(this, "Pet XP", 100, "Pet XP calculation settings");
        }
        public static final MiscCategory Misc = new MiscCategory();
    }
}
