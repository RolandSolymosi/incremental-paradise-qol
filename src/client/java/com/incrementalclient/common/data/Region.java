package com.incrementalclient.common.data;

import com.incrementalqol.common.data.World;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;

public enum Region {
    W1_Overworld("", World.World1, Warp.W1_Spawn, List.of()),
    W1_Crab("Crab", World.World1, Warp.W1_Crab, List.of()),
    W1_CoalMine("Coal", World.World1, Warp.W1_Coal, List.of()),
    W1_IronMine("Iron", World.World1, Warp.W1_Iron, List.of()),
    W1_CopperMine("Copper", World.World1, Warp.W1_Copper, List.of()),
    W1_GoldMine("Gold", World.World1, Warp.W1_Gold, List.of()),
    W1_RedstoneMine("Redstone", World.World1, Warp.W1_RedStone, List.of()),

    W2_Overworld("", World.World2, Warp.W2_Spawn, List.of()),
    W2_Lush("Lush", World.World2, Warp.W2_Lush, List.of()),
    W2_Veil("Veil", World.World2, Warp.W2_Veil, List.of()),
    W2_Infernal("Infernal", World.World2, Warp.W2_Infernal, List.of()),
    W2_Abyss("Abyss", World.World2, Warp.W2_Abyss, List.of()),

    W3_Overworld("", World.World3, Warp.W3_Spawn, List.of()),
    W3_Mine("Mine", World.World3, Warp.W3_Mines, List.of()),
    W3_Beach("Beach", World.World3, Warp.W3_Beach, List.of()),
    W3_Sty("Sty", World.World3, Warp.W3_Sty, List.of()),
    W3_Canine("Canine", World.World3, Warp.W3_Canine, List.of()),
    W3_Underside("Underside", World.World3, Warp.W3_Underside, List.of()),
    W3_Topside("Topside", World.World3, Warp.W3_Topside, List.of()),

    W4_Homestead("Homestead", World.World4, Warp.W4_Spawn, List.of()),
    W4_CityOutskirt("", World.World4, Warp.W4_Spawn, List.of()),
    W4_Sewer("Sewer", World.World4, Warp.W4_Sewer, List.of()),
    W4_Alpha("Alpha", World.World4, Warp.W4_Alpha, List.of()),
    W4_Beta("Beta", World.World4, Warp.W4_Beta, List.of()),
    W4_Delta("Delta", World.World4, Warp.W4_Beta, List.of()),

    WN1_Overworld("", World.WorldNightmare1, Warp.WN1_Spawn, List.of());

    private final String name;
    private final World world;
    private final Warp warp;
    private final List<Box> boundingBoxes;

    Region(String name, World world, Warp warp, List<Box> boundingBoxes) {
        this.name = name;
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

    public Warp getWarp() {
        return warp;
    }

    public String getName() {
        return name;
    }
}
