package com.incrementalclient.common.data;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum Warp {
    W1_Spawn("w1", World.World1, new BlockPos(343,109,235)),
    W1_Stable("stable", World.World1, new BlockPos(343,109,235)),
    W1_Wheat("wheat", World.World1, new BlockPos(343,109,235)),
    W1_Carrot("carrot", World.World1, new BlockPos(343,109,235)),
    W1_Potato("potato", World.World1, new BlockPos(343,109,235)),
    W1_Beetroot("beetroot", World.World1, new BlockPos(343,109,235)),
    W1_Mines("mines", World.World1, new BlockPos(343,109,235)),
    W1_Coal("coal", World.World1, new BlockPos(343,109,235)),
    W1_Iron("iron", World.World1, new BlockPos(343,109,235)),
    W1_Copper("copper", World.World1, new BlockPos(343,109,235)),
    W1_Gold("gold", World.World1, new BlockPos(343,109,235)),
    W1_RedStone("redstone", World.World1, new BlockPos(343,109,235)),
    W1_Crab("crab", World.World1, new BlockPos(343,109,235)),
    W1_Hoglin("hoglin", World.World1, new BlockPos(343,109,235)),

    W2_Spawn("w2", World.World1, new BlockPos(343,109,235)),
    W2_Lush("lush", World.World2, new BlockPos(343,109,235)),
    W2_Veil("veil", World.World2, new BlockPos(343,109,235)),
    W2_Infernal("infernal", World.World2, new BlockPos(343,109,235)),
    W2_Abyss("abyss", World.World2, new BlockPos(343,109,235)),
    W2_Shimmer("shimmer", World.World2, new BlockPos(343,109,235)),
    W2_Garlic("garlic", World.World2, new BlockPos(343,109,235)),
    W2_Corn("corn", World.World2, new BlockPos(343,109,235)),
    W2_Forge("forge", World.World2, new BlockPos(343,109,235)),
    W2_Rodrick("rodrick", World.World2, new BlockPos(343,109,235)),
    W2_Sky("sky", World.World2, new BlockPos(343,109,235)),
    W2_Bakery("bakery", World.World2, new BlockPos(343,109,235)),

    W3_Spawn("w3", World.World3, new BlockPos(343,109,235)),
    W3_Sheep("sheep", World.World3, new BlockPos(343,109,235)),
    W3_Sty("sty", World.World3, new BlockPos(343,109,235)),
    W3_Beach("beach", World.World3, new BlockPos(343,109,235)),
    W3_Underside("underside", World.World3, new BlockPos(343,109,235)),
    W3_Topside("topside", World.World3, new BlockPos(343,109,235)),
    W3_Canine("canine", World.World3, new BlockPos(343,109,235)),
    W3_Mines("mines3", World.World3, new BlockPos(343,109,235)),
    W3_Dreadhorn("dreadhorn", World.World3, new BlockPos(343,109,235)),

    W4_Spawn("w4", World.World4, new BlockPos(343,109,235)),
    W4_Sewer("sewer", World.World4, new BlockPos(343,109,235)),
    W4_Rattus("sewer", World.World4, new BlockPos(343,109,235)),
    W4_Alpha("alpha", World.World4, new BlockPos(343,109,235)),
    W4_Root("root", World.World4, new BlockPos(343,109,235)),
    W4_Beta("beta", World.World4, new BlockPos(343,109,235)),
    W4_Delta("delta", World.World4, new BlockPos(343,109,235)),

    WN1_Spawn("n1", World.WorldNightmare1, new BlockPos(343,109,235)),
    WN1_Stable("stable", World.WorldNightmare1, new BlockPos(343,109,235)),
    WN1_Decay("decay", World.WorldNightmare1, new BlockPos(343,109,235)),
    WN1_Torchflower("torchflower", World.WorldNightmare1, new BlockPos(343,109,235)),
    WN1_Mandrake("mandrake", World.WorldNightmare1, new BlockPos(343,109,235)),
    WN1_Splinterseed("splinterseed", World.WorldNightmare1, new BlockPos(343,109,235)),
    WN1_Quartz("quartz", World.WorldNightmare1, new BlockPos(343,109,235)),
    WN1_Lapis("lapis", World.WorldNightmare1, new BlockPos(343,109,235)),
    WN1_NetherGold("ngold", World.WorldNightmare1, new BlockPos(343,109,235)),
    WN1_Netherite("netherite", World.WorldNightmare1, new BlockPos(343,109,235)),
    WN1_Bamboodle("bamboodle", World.WorldNightmare1, new BlockPos(343,109,235)),
    WN1_Firefox("firefox", World.WorldNightmare1, new BlockPos(343,109,235)),
    WN1_Zoglin("zoglin", World.WorldNightmare1, new BlockPos(343,109,235)),
    ;

    private static final Map<String, Warp> WarpsByCommand = Arrays.stream(Warp.values()).collect(Collectors.toMap(Warp::getCommand, e -> e));
    private static final double TOLERANCE = 5;
    private final String command;
    private final World world;
    private final BlockPos position;

    Warp(String command, World world, BlockPos position) {
        this.command = command;
        this.world = world;
        this.position = position;
    }

    public String getCommand() {
        return command;
    }

    public boolean isAtPosition(ClientWorld clientWorld, BlockPos blockPos){
        return isAtPosition(clientWorld, blockPos, TOLERANCE);
    }

    public boolean isAtPosition(ClientWorld clientWorld, BlockPos blockPos, double tolerance){
        var currentWorld = World.findById(clientWorld.getRegistryKey().getValue());
        if (currentWorld.isPresent() && currentWorld.get() == world) {
            position.isWithinDistance(blockPos, tolerance);
        }
        return false;
    }

    public static boolean isAtPosition(String target, ClientWorld clientWorld, BlockPos position){
        return isAtPosition(target, clientWorld, position, TOLERANCE);
    }

    public static boolean isAtPosition(String target, ClientWorld clientWorld, BlockPos position, double tolerance) {
        var currentWarp = WarpsByCommand.get(target);
        if (currentWarp != null){
            return currentWarp.isAtPosition(clientWorld, position, tolerance);
        }

        return false;
    }
}