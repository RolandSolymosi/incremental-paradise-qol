package com.incrementalclient.common.data.targets.abstractions;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public sealed interface ITarget permits BlockTarget, EntityTarget {
    String displayName();
    String name();

    default boolean matches(World world, BlockPos pos) {
        return false;
    }

    default boolean matches(net.minecraft.entity.Entity entity) {
        return false;
    }
}
