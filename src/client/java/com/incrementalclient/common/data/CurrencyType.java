package com.incrementalclient.common.data;

/**
 * Represents different currency types in the game.
 * Each currency may have multiple aliases used in the scoreboard.
 */
public enum CurrencyType {
    GOLD("Gold"),
    TICKETS("Tickets"),
    PRESTIGE_TOKENS("Prestige Tokens", "Pr-T"),
    ASCENSION_TOKENS("Ascension Tokens", "Asc-T"),
    TRANSCENDENCE_TOKENS("Tr-Tokens", "Tr-T"),
    SILVER("Silver"),
    BUBBLES("Bubbles"),
    SHEEP("Sheep"),
    SHIVERS("Shivers"),
    STARBITS("Starbits"),
    ROCKET_FUEL("Rocket Fuel"),
    NATURAL_FUEL("Natural Fuel"),
    FOSSIL_FUEL("Fossil Fuel"),
    HYDRO_FUEL("Hydro Fuel");

    private final String[] aliases;

    CurrencyType(String... aliases) {
        this.aliases = aliases;
    }

    public String[] getAliases() {
        return aliases;
    }
}

