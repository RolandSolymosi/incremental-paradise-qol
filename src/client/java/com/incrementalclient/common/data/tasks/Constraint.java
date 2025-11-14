package com.incrementalclient.common.data.tasks;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Constraint {
    Shiny("shiny"),
    Elite("elite"),
    Large("large"),
    Color("colored"),
    Consecutive("in a row without missing"),
    Ticket("Tickets"),
    Games("games"),
    Score("score"),
    Matches("matches"),
    PeaShooter("pea shooter"),
    Gambit("devils gambit");

    private static final Map<String, Constraint> Constraints = Arrays.stream(Constraint.values()).collect(Collectors.toMap(Constraint::name, e -> e));

    private final String name;

    Constraint(String name){
        this.name = name;
    }

    public static Constraint find(String name){
        return Constraints.get(name);
    }
}
