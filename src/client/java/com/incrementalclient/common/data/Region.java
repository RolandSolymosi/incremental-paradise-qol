package com.incrementalclient.common.data;

import com.incrementalqol.common.data.World;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;

public enum Region {
    W1_Overworld(World.World1, Warp.W1_Spawn, List.of()),
    W1_Crab(World.World1, Warp.W1_Crab, List.of()),
    W1_CoalMine(World.World1, Warp.W1_Coal, List.of()),
    W1_IronMine(World.World1, Warp.W1_Iron, List.of()),
    W1_CopperMine(World.World1, Warp.W1_Copper, List.of()),
    W1_GoldMine(World.World1, Warp.W1_Gold, List.of()),
    W1_RedstoneMine(World.World1, Warp.W1_RedStone, List.of()),

    W2_Overworld(World.World2, Warp.W2_Spawn, List.of()),
    W2_Lush(World.World2, Warp.W2_Lush, List.of()),
    W2_Veil(World.World2, Warp.W2_Veil, List.of()),
    W2_Infernal(World.World2, Warp.W2_Infernal, List.of()),
    W2_Abyss(World.World2, Warp.W2_Abyss, List.of()),

    W3_Center(World.World3, Warp.W3_Spawn, List.of()),
    W3_Beach(World.World3, Warp.W3_Beach, List.of()),
    W3_Sty(World.World3, Warp.W3_Sty, List.of()),
    W3_Canine(World.World3, Warp.W3_Canine, List.of()),
    W3_Underside(World.World3, Warp.W3_Underside, List.of()),
    W3_Topside(World.World3, Warp.W3_Topside, List.of()),

    W4_City(World.World4, Warp.W4_Spawn, List.of()),
    W4_CityOutskirt(World.World4, Warp.W4_Spawn, List.of()),
    W4_Sewer(World.World4, Warp.W4_Sewer, List.of()),
    W4_Alpha(World.World4, Warp.W4_Alpha, List.of()),
    W4_Beta(World.World4, Warp.W4_Beta, List.of());

    private final World world;
    private final Warp warp;
    private final List<Box> boundingBoxes;

    Region(World world, Warp warp, List<Box> boundingBoxes) {
        this.world = world;
        this.warp = warp;
        this.boundingBoxes = boundingBoxes;
    }

    public boolean isInRegion(net.minecraft.world.World clientWorld, BlockPos blockPos) {
        var currentWorld = World.findById(clientWorld.getRegistryKey().getValue());
        if (currentWorld.isPresent() && currentWorld.get() == world) {
            return boundingBoxes.stream().anyMatch(b -> b.contains(blockPos.toCenterPos()));
        }
        return false;
    }

    public Warp getWarp(){
        return warp;
    }
}
