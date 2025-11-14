package com.incrementalclient.common.data;

import com.incrementalqol.common.data.World;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

public enum Region {
    W1_Overworld(World.World1, Warp.W1_Spawn, new Box(0, 0, 0, 0, 0, 0)),
    W1_Crab(World.World1, Warp.W1_Crab, new Box(0, 0, 0, 0, 0, 0)),
    W1_CoalMine(World.World1, Warp.W1_Coal, new Box(0, 0, 0, 0, 0, 0)),
    W1_IronMine(World.World1, Warp.W1_Iron, new Box(0, 0, 0, 0, 0, 0)),
    W1_CopperMine(World.World1, Warp.W1_Copper, new Box(0, 0, 0, 0, 0, 0)),
    W1_GoldMine(World.World1, Warp.W1_Gold, new Box(0, 0, 0, 0, 0, 0)),
    W1_RedstoneMine(World.World1, Warp.W1_RedStone, new Box(0, 0, 0, 0, 0, 0)),

    W2_Overworld(World.World2, Warp.W2_Spawn, new Box(0, 0, 0, 0, 0, 0)),
    W2_Lush(World.World2, Warp.W2_Lush, new Box(0, 0, 0, 0, 0, 0)),
    W2_Veil(World.World2, Warp.W2_Veil, new Box(0, 0, 0, 0, 0, 0)),
    W2_Infernal(World.World2, Warp.W2_Infernal, new Box(0, 0, 0, 0, 0, 0)),
    W2_Abyss(World.World2, Warp.W2_Abyss, new Box(0, 0, 0, 0, 0, 0)),

    W3_Center(World.World3, Warp.W3_Spawn, new Box(0, 0, 0, 0, 0, 0)),
    W3_Beach(World.World3, Warp.W3_Beach, new Box(0, 0, 0, 0, 0, 0)),
    W3_Sty(World.World3, Warp.W3_Sty, new Box(0, 0, 0, 0, 0, 0)),
    W3_Canine(World.World3, Warp.W3_Canine, new Box(0, 0, 0, 0, 0, 0)),
    W3_Underside(World.World3, Warp.W3_Underside, new Box(0, 0, 0, 0, 0, 0)),
    W3_Topside(World.World3, Warp.W3_Topside, new Box(0, 0, 0, 0, 0, 0)),

    W4_City(World.World4, Warp.W4_Spawn, new Box(0, 0, 0, 0, 0, 0)),
    W4_Sewer(World.World4, Warp.W4_Sewer, new Box(0, 0, 0, 0, 0, 0)),
    W4_Alpha(World.World4, Warp.W4_Alpha, new Box(0, 0, 0, 0, 0, 0)),
    W4_Beta(World.World4, Warp.W4_Beta, new Box(0, 0, 0, 0, 0, 0));

    private final World world;
    private final Warp warp;
    private final Box box;

    Region(World world, Warp warp, Box box) {
        this.world = world;
        this.warp = warp;
        this.box = box;
    }

    public boolean isInRegion(net.minecraft.world.World clientWorld, BlockPos blockPos) {
        var currentWorld = World.findById(clientWorld.getRegistryKey().getValue());
        if (currentWorld.isPresent() && currentWorld.get() == world) {
            return box.contains(blockPos.toCenterPos());
        }
        return false;
    }

    public Warp getWarp(){
        return warp;
    }
}
