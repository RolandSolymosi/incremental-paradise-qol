package com.incrementalclient.common.data.tasks.abstractions;

import com.google.common.collect.ImmutableList;
import com.incrementalclient.common.data.Region;
import com.incrementalclient.common.data.Warp;
import com.incrementalclient.common.data.tasks.Constraint;
import com.incrementalclient.common.data.tasks.TaskType;

import java.util.List;
import java.util.Optional;

public sealed interface ITask permits NormalTask, GamingTask {
    ImmutableList<String> names();
    List<Constraint> constraints();
    ImmutableList<Warp> warps();
    TaskType taskType();
    String displayName(String[] parameters);
}
