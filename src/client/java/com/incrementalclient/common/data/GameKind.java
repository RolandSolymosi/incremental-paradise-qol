package com.incrementalclient.common.data;

public enum GameKind {
    Coinflip("Coinflip", "cf"),
    Rps("Rps", "rps"),
    Pixelpop("Pixel Pop", "pixelpop"),
    Blackjack("Blackjack", "21"),
    Matcher("Matcher", "matcher"),
    Pairdice("Pairdice", "pairdice");

    private final String name;
    private final String command;

    GameKind(String name, String command) {
        this.name = name;
        this.command = command;
    }

    public String getName() {
        return name;
    }

    public String getCommand() {
        return command;
    }
}