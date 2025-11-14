package com.incrementalclient.common.data.targets.abstractions;

import com.incrementalclient.common.data.Region;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;
import java.util.function.Predicate;

public final class BlockTarget implements ITarget {
    private final String name;
    private final Predicate<BlockState> condition;
    private final Set<net.minecraft.block.Block> blocks;
    private final Region region;

    public BlockTarget(String name, Region region, Predicate<BlockState> condition, net.minecraft.block.Block... blocks) {
        this.name = name;
        this.condition = condition;
        this.blocks = Set.of(blocks);
        this.region = region;
    }

    @Override public String name() { return name; }

    @Override
    public boolean matches(World world, BlockPos pos) {
        var blockState = world.getBlockState(pos);
        if (blockState != null){
            return (region != null && region.isInRegion(world, pos)) && blocks.stream().anyMatch(blockState::isOf) && condition.test(blockState);
        }

        return false;
    }

    @Override
    public void highlight(MinecraftClient client) {
        // Example: trigger your block outline/ghost render system
    }

    public Set<net.minecraft.block.Block> getBlocks() { return blocks; }
}