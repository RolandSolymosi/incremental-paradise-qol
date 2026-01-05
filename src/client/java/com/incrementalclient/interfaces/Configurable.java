package com.incrementalclient.interfaces;

import dev.isxander.yacl3.api.Option;

public interface Configurable<TConfiguration, TOption extends Option<?>> {
    String getCategory();

    String getGroupName();

    String getJsonSection();

    int getOrder();

    TConfiguration getConfiguration();

    default TOption getOption() {
        return null;
    }

    default boolean hasOption() {
        return getOption() != null;
    }

    void optionChanged();

    default void copyFrom(Object other) {
        if (other == null) return;
        if (other.getClass() != getConfiguration().getClass()) {
            throw new IllegalArgumentException("Config Handler tries to load wrong type.");
        }
        TConfiguration target = getConfiguration();
        // Iterate through all fields of the configuration class
        for (java.lang.reflect.Field field : getConfiguration().getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                // Copy the value from the 'other' (loaded) object to 'target' (live) object
                Object value = field.get(other);
                field.set(target, value);
            } catch (IllegalAccessException e) {
                System.err.println("Failed to copy field: " + field.getName());
            }
        }
        optionChanged();
    }
}
