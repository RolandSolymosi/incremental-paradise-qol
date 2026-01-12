package com.incrementalclient.common.data;

/**
 * Represents different currency types in the game.
 * Each currency may have multiple aliases used in the scoreboard.
 */
public enum CurrencyType {
    GOLD("Gold"),
    SILVER("Silver"),
    BUBBLES("Bubbles"),
    SHIVERS("Shivers"),
    STARBITS("Starbits"),
    SHEEP("Sheep"),
    ROCKET_FUEL("Rocket Fuel"),
    NATURAL_FUEL("Natural Fuel"),
    FOSSIL_FUEL("Fossil Fuel"),
    HYDRO_FUEL("Hydro Fuel"),
    PRESTIGE_TOKENS("Prestige Tokens", "Pr-T"),
    ASCENSION_TOKENS("Ascension Tokens", "Asc-T"),
    TRANSCENDENCE_TOKENS("Tr-Tokens", "Tr-T"),
    TICKETS("Tickets");

    private final String[] aliases;

    CurrencyType(String... aliases) {
        this.aliases = aliases;
    }

    public String[] getAliases() {
        return aliases;
    }
}

