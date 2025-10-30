package com.incrementalqol.common.data;

import java.util.Arrays;
import java.util.Optional;

public enum SkillType {
    Combat("Combat"),
    Mining("Mining"),
    Foraging("Foraging"),
    Farming("Farming"),
    SpearFishing("Spear Fishing"),
    Sharpshooting("Sharpshooting"),
    Excavation("Excavation");

    private final String name;

    SkillType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Optional<SkillType> findByName(String name) {
        return Arrays.stream(values())
                .filter(e -> e.name.equals(name))
                .findFirst();
    }
}

