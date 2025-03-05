package com.incrementalqol.common.data.Skills;


import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;

public enum NormalMiningSkill implements NameableEnum {
    Ricochet("Ricochet"),
    CondensedStrike("Condensed Strike"),
    WingsOfWealth("Wings of Wealth"),
    KeySeeker("Key Seeker"),
    Cascade("Cascade"),
    TreasureHunter("Treasure Hunter"),
    Oresplosion("Oresplosion"),
    MiningCapacity("Mining Capacity"),
    SparkstoneBoost("Sparkstone Boost"),
    QuickExtraction("Quick Extraction"),
    Nuke("Nuke"),
    DawnsBounty("Dawn's Bounty"),
    Prospector("Prospector"),
    PerfectStrike("Perfect Strike"),
    FoolsGold("Fools Gold"),
    ;

    private final String name;

    NormalMiningSkill(String name) {
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
