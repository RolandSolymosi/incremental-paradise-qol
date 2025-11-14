package com.incrementalclient.common.data.targets;

import com.incrementalclient.common.data.Region;
import com.incrementalclient.common.data.targets.abstractions.BlockTarget;
import com.incrementalclient.common.data.targets.abstractions.EntityTarget;
import com.incrementalclient.common.data.targets.abstractions.ITarget;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;

public enum Target {
    Fossil(new BlockTarget("fossil", null, t -> true, Blocks.SUSPICIOUS_SAND)),

    W1_Coal(new BlockTarget("coalore", Region.W1_CoalMine, t -> true, Blocks.DEEPSLATE_COAL_ORE)),
    W1_Iron(new BlockTarget("ironore", Region.W1_IronMine, t -> true, Blocks.DEEPSLATE_IRON_ORE)),
    W1_Copper(new BlockTarget("copperore", Region.W1_CopperMine, t -> true, Blocks.DEEPSLATE_COPPER_ORE)),
    W1_Gold(new BlockTarget("goldore", Region.W1_GoldMine, t -> true, Blocks.DEEPSLATE_GOLD_ORE)),
    W1_Redstone(new BlockTarget("redstoneore", Region.W1_RedstoneMine, t -> true, Blocks.DEEPSLATE_REDSTONE_ORE)),
    W1_AppleTree(new BlockTarget("applewood", Region.W1_Overworld, t -> true, Blocks.JUNGLE_WOOD, Blocks.SPRUCE_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.BROWN_MUSHROOM_BLOCK)),
    W1_PalmTree(new BlockTarget("palm", Region.W1_Crab, t -> true, Blocks.JUNGLE_WOOD, Blocks.SPRUCE_SLAB, Blocks.BROWN_MUSHROOM_BLOCK)),
    W1_Wheat(new BlockTarget("wheat", Region.W1_Overworld, t -> true, Blocks.WHEAT)),
    W1_Carrot(new BlockTarget("carrot", Region.W1_Overworld, t -> true, Blocks.CARROTS)),
    W1_Potato(new BlockTarget("potato", Region.W1_Overworld, t -> true, Blocks.POTATOES)),
    W1_Beetroot(new BlockTarget("beetroot", Region.W1_Overworld, t -> true, Blocks.BEETROOTS)),
    W1_Honeycomb(new BlockTarget("honeycomb", Region.W1_Overworld, t -> t.get(Properties.HONEY_LEVEL) > 0, Blocks.BEE_NEST)),
    W1_Ladybug(new EntityTarget("ladybug", Region.W1_Overworld, t -> true, EntityType.ARMOR_STAND)),
    W1_ScaredHog(new EntityTarget("scaredhog", Region.W1_Overworld, t -> true, EntityType.PIG)),
    W1_WildBoar(new EntityTarget("wildboar", Region.W1_Overworld, t -> true, EntityType.HOGLIN)),
    W1_Goat(new EntityTarget("mountaingoat", Region.W1_Overworld, t -> true, EntityType.GOAT)),
    W1_Riverfish(new EntityTarget("riverfish", Region.W1_Overworld, t -> true, EntityType.TROPICAL_FISH)),
    W1_Crab(new EntityTarget("crab", Region.W1_Overworld, t -> true, EntityType.PIG)),
    W1_HermitCrab(new EntityTarget("hermitcrab", Region.W1_Overworld, t -> true, EntityType.PIG)),
    W1_Hoglin(new EntityTarget("hoglin", Region.W1_Overworld, t -> true, EntityType.HOGLIN)),
    W1_GarbageCan(new EntityTarget("garbagecan", Region.W1_Overworld, t -> true, EntityType.ARMOR_STAND)),

    ;

    private static final Map<String, ITarget> BY_NAME = Collections.unmodifiableMap(Arrays
            .stream(Target.values())
            .map(e -> e.target)
            .collect(Collectors.toUnmodifiableMap(ITarget::name, e -> e)));

    private static final Map<net.minecraft.block.Block, List<BlockTarget>> BY_TYPE_BLOCK = Collections.unmodifiableMap(Arrays
            .stream(Target.values()).map(e -> e.target)
            .filter(e -> e instanceof BlockTarget)
            .map(e -> (BlockTarget) e)
            .flatMap(bt -> bt.getBlocks().stream().map(b -> Map.entry(b, bt)))
            .collect(Collectors.groupingBy(Map.Entry::getKey,
                    Collectors.mapping(Map.Entry::getValue, Collectors.toList()))));

    private static final Map<EntityType<?>, List<EntityTarget>> BY_TYPE_ENTITY = Collections.unmodifiableMap(Arrays
            .stream(Target.values()).map(e -> e.target)
            .filter(e -> e instanceof EntityTarget)
            .map(e -> (EntityTarget) e)
            .collect(Collectors.groupingBy(
                    EntityTarget::getType,
                    Collectors.toUnmodifiableList()
            )));

    private final ITarget target;

    Target(ITarget Target) {
        this.target = Target;
    }

    public static ITarget byName(String name) {
        return BY_NAME.get(name);
    }

    public static List<BlockTarget> find(World world, BlockPos pos) {
        var block = world.getBlockState(pos);
        if (block != null) {
            var possibleTargets = BY_TYPE_BLOCK.get(block.getBlock());
            if (possibleTargets != null) {
                return possibleTargets.stream().filter(x -> x.matches(world, pos)).toList();
            }
        }
        return List.of();
    }

    public static List<EntityTarget> find(net.minecraft.entity.Entity entity) {
        var possibleTargets = BY_TYPE_ENTITY.get(entity.getType());
        if (possibleTargets != null) {
            return possibleTargets.stream().filter(x -> x.matches(entity)).toList();
        }
        return List.of();
    }
}