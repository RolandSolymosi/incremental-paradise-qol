package com.incrementalclient.common.data.skills;


import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;

public enum NormalFarmingSkill implements NameableEnum {
    Harvester("Harvester"),
    CropChomp("Crop Chomp"),
    Pollinate("Pollinate"),
    KeySeeker("Key Seeker"),
    ChainReaction("Chain Reaction"),
    FarmingCapacity("Farming Capacity"),
    YieldSharing("Yield Sharing"),
    BlessedSoil("Blessed Soil"),
    NeatureWalk("Neature Walk"), // Thats the name.
    SpiritsBlessing("Spirit's Blessing"),
    CluckinCrops("Cluckin' Crops"),
    BountifulHarvest("Bountiful Harvest"),
    JuicyJackpot("Juicy Jackpot"),
    BlessedHarvest("Blessed Harvest"),
    GreenThumb("Green Thumb"),
    ;

    private final String name;

    NormalFarmingSkill(String name) {
        this.name = name;
    }

    @Override
    public Text getDisplayName() {
        return Text.of(name);
    }

    public String getName() {
        return name;
    }
}
