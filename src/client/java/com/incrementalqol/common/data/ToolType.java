package com.incrementalqol.common.data;

public enum ToolType {
    Melee(0),
    Pickaxe(1),
    Axe(2),
    Hoe(3),
    FishingRod(4),
    Bow(5);

    private final int value;

    ToolType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}