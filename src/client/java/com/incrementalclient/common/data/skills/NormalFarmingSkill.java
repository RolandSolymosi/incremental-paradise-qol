package com.incrementalclient.common.data.skills;


import com.incrementalclient.common.data.World;
import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public enum NormalFarmingSkill implements Skill {
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
    private final boolean active;

    NormalFarmingSkill(String name, boolean active) {
        this.name = name;
        this.active = active;
    }

    NormalFarmingSkill(String name) {
        this(name, false);
    }

    @Override
    public Text getDisplayName() {
        return Text.of(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public @NotNull SkillCategory getCategory() {
        return SkillCategory.Farming;
    }

    @Override
    public @NotNull World.Realm getRealm() {
        return World.Realm.Normal;
    }

    @Override
    public boolean isActiveUpgrade() {
        return active;
    }
}
