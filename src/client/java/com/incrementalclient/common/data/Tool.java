package com.incrementalclient.common.data;

public enum Tool {
    Melee(0),
    Pickaxe(1),
    Axe(2),
    Hoe(3),
    Spear(4),
    Bow(5),
    Brush(6);;

    private final int value;

    Tool(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}