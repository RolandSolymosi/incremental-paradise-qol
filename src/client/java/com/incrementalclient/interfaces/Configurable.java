package com.incrementalclient.interfaces;

import dev.isxander.yacl3.api.Option;

import java.util.List;

public interface Configurable<TConfiguration> {
    String getJsonSection();

    TConfiguration getConfiguration();

    default List<OptionPiece> getOption() {
        return List.of();
    }

    default boolean hasOption() {
        return !getOption().isEmpty();
    }

    void optionChanged();

    default void copyFrom(Object other) {
        if (other == null) return;
        if (other.getClass() != getConfiguration().getClass()) {
            throw new IllegalArgumentException("Config Handler tries to load wrong type.");
        }
        TConfiguration target = getConfiguration();
        // Iterate through all fields of the configuration class
        var currentClass = target.getClass();
        while (currentClass != null && currentClass != Object.class) {
            for (java.lang.reflect.Field field : currentClass.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    // Copy the value from the 'other' (loaded) object to 'target' (live) object
                    var value = field.get(other);
                    field.set(target, value);
                } catch (IllegalAccessException e) {
                    System.err.println("Failed to copy field: " + field.getName());
                }
            }
            currentClass = currentClass.getSuperclass();
        }

        optionChanged();
    }

    record OptionPiece(String Category, int GroupOrder, String Group, String GroupDescription, int Order, Option<?> Option) {
    }
}
