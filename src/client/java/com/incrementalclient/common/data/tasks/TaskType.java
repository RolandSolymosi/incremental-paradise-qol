package com.incrementalclient.common.data.tasks;

public enum TaskType {
    Combat("1"),
    Mining("2"),
    Foraging("3"),
    Farming("4"),
    Fishing("5"),
    CombatFishing("6"),
    Misc(null),
    Gaming(null);

    private final String value;

    TaskType(String value) {
        this.value = value;
    }

    public String getString() {
        return value;
    }
}