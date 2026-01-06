package com.incrementalclient.common.data.tasks.abstractions;

import com.google.common.collect.ImmutableList;
import com.incrementalclient.common.data.GameKind;
import com.incrementalclient.common.data.Warp;
import com.incrementalclient.common.data.tasks.Constraint;
import com.incrementalclient.common.data.tasks.TaskType;

import java.util.List;
import java.util.Optional;

public final class GamingTask implements ITask {

    private final ImmutableList<String> names;
    private final ImmutableList<Constraint> constraints;
    private final GameKind game;
    private final ImmutableList<Warp> warps;

    public GamingTask(List<String> names, GameKind game, List<Constraint> constraints, List<Warp> warps) {
        this.names = ImmutableList.copyOf(names);
        this.constraints = constraints != null ? ImmutableList.copyOf(constraints) : ImmutableList.of();
        this.game = game;
        this.warps = warps != null ? ImmutableList.copyOf(warps) : ImmutableList.of();
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
        return TaskType.Gaming;
    }

    public GameKind game() {
        return game;
    }
}