package com.incrementalqol.common.data;

import java.util.List;
import java.util.regex.Pattern;

public class TaskDescriptor {
    private final String name;
    private final TaskType type;
    private final String command;
    private final String fallbackCommand;

    private static final List<Pattern> MISC_PATTERNS = List.of(
            Pattern.compile("Clean (?<amount>\\d+[km]?) (?<type>.+) \\(?(?<progress>\\d+[km]?)"),
            Pattern.compile("Repair (?<amount>\\d+[km]?) (?<type>.+) in .+\\((?<progress>\\d+[km]?)"),
            Pattern.compile("Sell (?<amount>\\d+\\,?\\d*[km]?) (?<type>.+) \\((?<progress>\\d+\\,?\\d*[km]?)"),
            Pattern.compile("Gain (?<amount>\\d+\\,?\\d*[km]?) (?<type>.+) from selling.+ \\(?(?<progress>\\d+\\,?\\d*[km]?)")
    );

    private static final List<Pattern> GAMES_PATTERNS = List.of(
            Pattern.compile("Play (?<amount>\\d+[km]?) (?<opt>.+) of (?<type>.+) \\(?(?<progress>\\d+[km]?)"),
            Pattern.compile("Earn (?<amount>\\d+[km]?) (?<opt>.+) in (?<type>.+) \\(?(?<progress>\\d+[km]?)"),
            Pattern.compile("Earn (?<amount>\\d+[km]?)\\s+(?<opt>.+) playing (?<type>.+) \\(?(?<progress>\\d+[km]?)"),
            Pattern.compile("Find (?<amount>\\d+[km]?) (?<opt>.+) while playing (?<type>.+) \\((?<progress>\\d+[km]?)")
    );

    private static final List<Pattern> FORAGING_PATTERNS = List.of(
            Pattern.compile("Collect (?<amount>\\d+\\,?\\d*[km]?) (?<type>.+) \\(?(?<progress>\\d+\\,?\\d*[km]?)")
    );

    private static final List<Pattern> COMBAT_PATTERNS = List.of(
            Pattern.compile("Slay ?(?:the)?(?<type>.+) \\((?<progress>\\d+[km]?)\\/(?<amount>\\d+[km]?)"),
            Pattern.compile("Collect (?<amount>\\d+\\,?\\d*[km]?) drops from (?<type>.+) \\(?(?<progress>\\d+\\,?\\d*[km]?)")
    );

    private static final List<Pattern> FISHING_PATTERNS = List.of(
            Pattern.compile("Spear (?<type>.+) without missing (?<amount>\\d+[km]?).+\\((?<progress>\\d+[km]?)"),
            Pattern.compile("Collect (?<amount>\\d+\\,?\\d*[km]?) (?<type>.+) \\(?(?<progress>\\d+\\,?\\d*[km]?)")
    );

    private static final List<Pattern> MINING_PATTERNS = List.of(
            Pattern.compile("Collect (?<amount>\\d+\\,?\\d*[km]?) (?<type>.+) \\(?(?<progress>\\d+\\,?\\d*[km]?)")
    );

    private static final List<Pattern> FARMING_PATTERNS = List.of(
            Pattern.compile("Harvest (?<amount>\\d+\\,?\\d*[km]?) (?<type>.+) \\(?(?<progress>\\d+\\,?\\d*[km]?)")
    );

    public TaskDescriptor(String name, TaskType type, String command, String fallbackCommand) {
        this.name = name;
        this.type = type;
        this.command = command;
        this.fallbackCommand = fallbackCommand;
    }

    public List<Pattern> getPatterns() {
        return switch (this.type) {
            case TaskType.Misc -> MISC_PATTERNS;
            case TaskType.Gaming -> GAMES_PATTERNS;
            case TaskType.Farming -> FARMING_PATTERNS;
            case TaskType.Combat -> COMBAT_PATTERNS;
            case TaskType.Fishing -> FISHING_PATTERNS;
            case TaskType.Mining -> MINING_PATTERNS;
            case TaskType.Foraging -> FORAGING_PATTERNS;

            default -> List.of();
        };
    }

    public String getName() {
        return name;
    }

    public String getCommand() {
        return command;
    }

    public String getFallbackCommand() {
        return fallbackCommand;
    }
}

