package com.incrementalclient.common.data;

public enum DefaultWardrobe {
    Combat("1"),
    Mining("2"),
    Foraging("3"),
    Farming("4"),
    Fishing("5"),
    CombatFishing("6");

    private final String value;

    DefaultWardrobe(String value) {
        this.value = value;
    }

    public String getString() {
        return value;
    }
}

