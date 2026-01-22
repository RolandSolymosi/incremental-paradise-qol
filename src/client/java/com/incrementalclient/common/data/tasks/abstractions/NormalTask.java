package com.incrementalclient.common.data.tasks.abstractions;

import com.google.common.collect.ImmutableList;
import com.incrementalclient.common.data.DefaultWardrobe;
import com.incrementalclient.common.data.Region;
import com.incrementalclient.common.data.Tool;
import com.incrementalclient.common.data.Warp;
import com.incrementalclient.common.data.targets.Target;
import com.incrementalclient.common.data.tasks.Constraint;
import com.incrementalclient.common.data.tasks.TaskType;

import java.util.List;
import java.util.Optional;

public final class NormalTask implements ITask {

    private final String displayName;
    private final Region region;
    private final ImmutableList<String> names;
    private final ImmutableList<Constraint> constraints;
    private final ImmutableList<com.incrementalclient.common.data.targets.Target> targets;
    private final TaskType taskType;
    private final ImmutableList<Warp> warps;
    private final DefaultWardrobe wardrobe;
    private final Tool tool;

    public NormalTask(String displayName, Region region, List<String> names, List<Constraint> constraints, TaskType taskType, DefaultWardrobe wardrobe, Tool tool, List<Target> targets, List<Warp> warps) {
        this.displayName = displayName;
        this.region = region;
        this.names = ImmutableList.copyOf(names);
        this.constraints = constraints != null ?  ImmutableList.copyOf(constraints) : ImmutableList.of();
        this.targets = targets != null ? ImmutableList.copyOf(targets) : ImmutableList.of();
        this.taskType = taskType;
        this.warps = warps != null ? ImmutableList.copyOf(warps) : ImmutableList.of();
        this.wardrobe = wardrobe;
        this.tool = tool;
    }

    @Override
    public String displayName(String[] parameters) {
        if (parameters != null && parameters.length > 0){
            return String.format(displayName, (Object[]) parameters);
        }
        return displayName;
    }

    @Override
    public String displayName() {
        return displayName;
    }

    @Override
    public ImmutableList<String> names() {
        return names;
    }

    @Override
    public ImmutableList<Constraint> constraints() {
        return constraints;
    }

    @Override
    public ImmutableList<Warp> warps() {
        return warps;
    }

    @Override
    public TaskType taskType() {
        return taskType;
    }

    public Tool tool() {
        return tool;
    }

    public DefaultWardrobe wardrobe() {
        return wardrobe;
    }

    public ImmutableList<Target> targets(){
        return targets;
    }

    public Region getRegion() {
        return region;
    }
}