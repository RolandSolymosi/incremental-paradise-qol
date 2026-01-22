package com.incrementalclient.featues.Tasking;

public enum TicketTaskOverride {
    Skipped,
    NotSkipped,
    Default;

    public static TicketTaskOverride booleanToEnum(boolean bool) {
        if (bool) {
            return Skipped;
        } else {
            return NotSkipped;
        }
    }

    public String getName(){
        return String.join(" ",name().split("(?<=[a-z])(?=[A-Z0-9])|(?<=[A-Z])(?=[A-Z][a-z])|(?<=[0-9])(?=[A-Za-z])"));
    }

}
