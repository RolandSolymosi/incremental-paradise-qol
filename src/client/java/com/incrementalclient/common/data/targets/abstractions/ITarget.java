package com.incrementalclient.common.data.targets.abstractions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public sealed interface ITarget permits BlockTarget, EntityTarget {
    String name();

    default boolean matches(World world, BlockPos pos) {
        return false;
    }

    default boolean matches(net.minecraft.entity.Entity entity) {
        return false;
    }

    default void highlight(MinecraftClient client) {
        // optional no-op, subclasses can override
    }
}
