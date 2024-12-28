package com.incrementalqol;

public class Task{
    private final String name;
    private final String description;
    private final String warp;
    private final int strWidth;
    private boolean completed;

    public Task (String name, String description, String warp, int strWidth, boolean completed) {
        this.name=name;
        this.description=description;
        this.warp=warp;
        this.strWidth = strWidth;
        this.completed=completed;
    }

    public boolean isCompleted() {
        return completed;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    public String getWarp() {
        return warp;
    }
    public int getStrWidth() {
        return strWidth;
    }

    public void setCompleted() {
        this.completed = true;
    }
}
