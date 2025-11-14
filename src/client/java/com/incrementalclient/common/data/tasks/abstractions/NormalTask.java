package com.incrementalclient.common.data.tasks.abstractions;

import com.google.common.collect.ImmutableList;
import com.incrementalclient.common.data.DefaultWardrobe;
import com.incrementalclient.common.data.Tool;
import com.incrementalclient.common.data.Warp;
import com.incrementalclient.common.data.targets.Target;
import com.incrementalclient.common.data.tasks.Constraint;
import com.incrementalclient.common.data.tasks.TaskType;

import java.util.List;
import java.util.Optional;

public final class NormalTask implements ITask {

    private final ImmutableList<String> names;
    private final Optional<Constraint> constraint;
    private final ImmutableList<com.incrementalclient.common.data.targets.Target> targets;
    private final TaskType taskType;
    private final ImmutableList<Warp> warps;
    private final DefaultWardrobe wardrobe;
    private final Tool tool;

    public NormalTask(List<String> names, Constraint constraint, TaskType taskType, DefaultWardrobe wardrobe, Tool tool, List<Target> targets, List<Warp> warps) {
        this.names = ImmutableList.copyOf(names);
        this.constraint = Optional.ofNullable(constraint);
        this.targets = targets != null ? ImmutableList.copyOf(targets) : ImmutableList.of();
        this.taskType = taskType;
        this.warps = warps != null ? ImmutableList.copyOf(warps) : ImmutableList.of();
        this.wardrobe = wardrobe;
        this.tool = tool;
    }

    @Override
    public ImmutableList<String> names() {
        return names;
    }

    @Override
    public Optional<Constraint> constraint() {
        return constraint;
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
}