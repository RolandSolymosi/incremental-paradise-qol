package com.incrementalclient.common.data.tasks;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public enum TaskType {
    Combat("Combat", List.of(
            Pattern.compile("Kill (?:the )?(?<type>.+) \\(?(?<progress>[0-9.,]+[kmbt]?)/(?<amount>[0-9.,]+[kmbt]?)"),
            Pattern.compile("Slay (?:the )?(?<type>.+) \\(?(?<progress>[0-9.,]+[kmbt]?)/(?<amount>[0-9.,]+[kmbt]?)"),
            Pattern.compile("Collect (?<amount>[0-9.,]+[kmbt]?) drops from (?<constraint>Elite)?\\s*(?<type>.+) with (?<constraint2>.+) \\(?(?<progress>[0-9.,]+[kmbt]?)"),
            Pattern.compile("Collect (?<amount>[0-9.,]+[kmbt]?) drops from (?<constraint>Elite)?\\s*(?<type>.+) \\(?(?<progress>[0-9.,]+[kmbt]?)"))),
    Mining("Mining", List.of(
            Pattern.compile("Collect (?<amount>[0-9.,]+[kmbt]?) (?<type>.+) from (?<constraint>Shiny) Ores \\(?(?<progress>[0-9.,]+[kmbt]?)"),
            Pattern.compile("Collect (?<amount>[0-9.,]+[kmbt]?) (?<constraint>Shiny)\\s*(?<type>.+) \\(?(?<progress>[0-9.,]+[kmbt]?)"),
            Pattern.compile("Collect (?<amount>[0-9.,]+[kmbt]?) (?<type>.+) with (?<constraint>.+) \\(?(?<progress>[0-9.,]+[kmbt]?)"),
            Pattern.compile("Collect (?<amount>[0-9.,]+[kmbt]?) (?<type>.+) \\(?(?<progress>[0-9.,]+[kmbt]?)"))),
    Foraging("Foraging", List.of(
            Pattern.compile("Collect (?<amount>[0-9.,]+[kmbt]?) (?<constraint>Large)?\\s*(?<type>.+) with (?<constraint2>.+) \\(?(?<progress>[0-9.,]+[kmbt]?)"),
            Pattern.compile("Collect (?<amount>[0-9.,]+[kmbt]?) (?<constraint>Large)?\\s*(?<type>.+) \\(?(?<progress>[0-9.,]+[kmbt]?)"))),
    Farming("Farming", List.of(
            Pattern.compile("Harvest (?<amount>[0-9.,]+[kmbt]?) (?<type>.+) with (?<parameter>[0-9])\\+ (?<constraint>.+) \\(?(?<progress>[0-9.,]+[kmbt]?)"),
            Pattern.compile("Harvest (?<amount>[0-9.,]+[kmbt]?) (?<type>.+) \\(?(?<progress>[0-9.,]+[kmbt]?)"))),
    Fishing("Fishing", List.of(
            Pattern.compile("Spear [0-9.,]+ (?<type>.+) (?<constraint>in a row without missing) (?<amount>[0-9.,]+[kmbt]?).+\\((?<progress>[0-9.,]+[kmbt]?)"),
            Pattern.compile("Collect (?<amount>[0-9.,]+[kmbt]?) (?<parameter>.+) (?<constraint>colored) (?<type>.+) using a Fishing Spear \\(?(?<progress>[0-9.,]+[kmbt]?)"),
            Pattern.compile("Collect (?<amount>[0-9.,]+[kmbt]?) (?<type>.+) using a Fishing Spear \\(?(?<progress>[0-9.,]+[kmbt]?)"),
            Pattern.compile("Collect (?<amount>[0-9.,]+[kmbt]?) drops from (?<type>.+) with (?<constraint>.+) \\(?(?<progress>[0-9.,]+[kmbt]?)"),
            Pattern.compile("Collect (?<amount>[0-9.,]+[kmbt]?) drops from (?<type>.+) \\(?(?<progress>[0-9.,]+[kmbt]?)"),
            Pattern.compile("Collect (?<amount>[0-9.,]+[kmbt]?) (?<type>.+) \\(?(?<progress>[0-9.,]+[kmbt]?)"))),
    Misc("Misc", List.of(
            Pattern.compile("Clean (?<amount>[0-9.,]+[kmbt]?) (?<type>.+) \\(?(?<progress>[0-9.,]+[kmbt]?)"),
            Pattern.compile("Repair (?<amount>[0-9.,]+[kmbt]?) (?<type>.+) in the Abyss \\(?(?<progress>[0-9.,]+[kmbt]?)"),
            Pattern.compile("Sell (?<amount>[0-9.,]+[kmbt]?) (?<type>.+) \\(?(?<progress>[0-9.,]+[kmbt]?)"),
            Pattern.compile("Gain (?<amount>[0-9.,]+[kmbt]?) (?<type>.+) \\(?(?<progress>[0-9.,]+[kmbt]?)"),
            Pattern.compile("Loot (?<amount>[0-9.,]+[kmbt]?) (?<type>.+) \\(?(?<progress>[0-9.,]+[kmbt]?)"))),
    Gaming("Gaming", List.of(
            Pattern.compile("Play (?<amount>[0-9.,]+[kmbt]?) (?<constraint>.+) of (?<type>.+) \\(?(?<progress>[0-9.,]+[kmbt]?)"),
            Pattern.compile("Earn (?<amount>[0-9.,]+[kmbt]?) (?<constraint>.+) in (?<type>.+) \\(?(?<progress>[0-9.,]+[kmbt]?)"),
            Pattern.compile("Earn (?<amount>[0-9.,]+[kmbt]?) (?<constraint>.+) playing (?<type>.+) \\(?(?<progress>[0-9.,]+[kmbt]?)"),
            Pattern.compile("Find (?<amount>[0-9.,]+[kmbt]?) (?<constraint>.+) while playing (?<type>.+) \\(?(?<progress>[0-9.,]+[kmbt]?)"))),
    Quest("Quest", List.of()),
    Tutorial("Tutorial", List.of());

    private final String name;
    private final List<Pattern> patterns;

    TaskType(String name, List<Pattern> patterns) {
        this.name = name;
        this.patterns = patterns;
    }

    public String getString() {
        return name;
    }

    public static Optional<TaskType> findByName(String name) {
        return Arrays.stream(values())
                .filter(e -> e.name.equals(name))
                .findFirst();
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }
}