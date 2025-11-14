package com.incrementalclient.common.data.skills;

import java.util.Arrays;
import java.util.Optional;

public enum Category {
    Combat("Combat"),
    Mining("Mining"),
    Foraging("Foraging"),
    Farming("Farming"),
    SpearFishing("Spear Fishing"),
    Sharpshooting("Sharpshooting"),
    Excavation("Excavation");

    private final String name;

    Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Optional<Category> findByName(String name) {
        return Arrays.stream(values())
                .filter(e -> e.name.equals(name))
                .findFirst();
    }
}

