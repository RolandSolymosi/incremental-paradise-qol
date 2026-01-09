package com.incrementalclient.common.data.skills;

import java.util.Arrays;
import java.util.Optional;

public enum SkillCategory {
    Combat("Combat"),
    Mining("Mining"),
    Foraging("Foraging"),
    Farming("Farming"),
    SpearFishing("Spear Fishing"),
    Sharpshooting("Sharpshooting"),
    Excavation("Excavation");

    private final String name;

    SkillCategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Optional<SkillCategory> findByName(String name) {
        return Arrays.stream(values())
                .filter(e -> e.name.equals(name))
                .findFirst();
    }
}

