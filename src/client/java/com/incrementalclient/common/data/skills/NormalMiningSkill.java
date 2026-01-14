package com.incrementalclient.common.data.skills;


import com.incrementalclient.common.data.World;
import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public enum NormalMiningSkill implements Skill {
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
    private final boolean active;

    NormalMiningSkill(String name, boolean active) {
        this.name = name;
        this.active = active;
    }

    NormalMiningSkill(String name) {
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
        return SkillCategory.Mining;
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
