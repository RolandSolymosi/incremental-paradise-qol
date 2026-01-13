package com.incrementalclient.common.data.targets.abstractions;

import com.incrementalclient.common.data.Region;
import com.incrementalclient.common.data.World;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public final class BlockTarget implements ITarget {
    private final String name;
    private final String displayName;
    private final Predicate<BlockState> condition;
    private final Set<net.minecraft.block.Block> blocks;
    private final List<Region> regions;

    public BlockTarget(String name, String displayName, List<Region> regions, Predicate<BlockState> condition, net.minecraft.block.Block... blocks) {
        this.name = name;
        this.displayName = displayName;
        this.condition = condition;
        this.blocks = Set.of(blocks);
        this.regions = regions == null ? List.of() : regions;
    }

    @Override public String name() { return name; }

    @Override
    public boolean matches(net.minecraft.world.World world, BlockPos pos) {
        var blockState = world.getBlockState(pos);
        if (blockState != null){
            return (!regions.isEmpty() && regions.stream().anyMatch(r -> r.isInRegion(world, pos))) && blocks.stream().anyMatch(blockState::isOf) && condition.test(blockState);
        }

        return false;
    }

    public boolean matches(World world, Block block){
        return blocks.stream().anyMatch(b -> b.getTranslationKey().equals(block.getTranslationKey()) && this.regions.stream().anyMatch(r -> r.getWorld() == world));
    }

    public Set<net.minecraft.block.Block> getBlocks() { return blocks; }

    @Override
    public String displayName() {
        return displayName;
    }
}