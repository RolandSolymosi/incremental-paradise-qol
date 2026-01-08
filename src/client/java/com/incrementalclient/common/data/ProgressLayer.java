package com.incrementalclient.common.data;

/**
 * Represents different progression layers in the game.
 * Each layer has a full name and a short/abbreviated name used in the scoreboard.
 */
public enum ProgressLayer {
    LEVEL("Level", "Lv"),
    PRESTIGE("Prestige", "Pr"),
    ASCENSION("Ascension", "Asc"),
    NIGHTMARE("Nightmare", "Nmr"),
    TRANSCENDENCE("Transcendence", "Tr");

    private final String fullName;
    private final String shortName;

    ProgressLayer(String fullName, String shortName) {
        this.fullName = fullName;
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getShortName() {
        return shortName;
    }

    /**
     * Gets all possible name variants for this layer (for regex matching).
     */
    public String[] getNameVariants() {
        return new String[]{fullName, shortName};
    }
}

