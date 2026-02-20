package com.incrementalclient.common.data.skills;


import com.incrementalclient.common.data.World;
import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public enum NormalSpearFishingSkill implements Skill {
    SpoonBender("Spoon Bender", true),
    Wavestreak("Wavestreak", true),
    Beenado("Beenado", true),
    KeySeeker("Key Seeker"),
    HomingHarpoon("Homing Harpoon"),
    PiercingBlow("Piercing Blow"),
    StealthyStingray("Stealthy Stingray"),
    SpearFishingCapacity("Spear Fishing Capacity"),
    Accuracy("Accuracy"),
    HittingVitals("Hitting Vitals"),
    TheBigCatch("The Big Catch"),
    FishGameHunter("Fish Game Hunter"),
    GuaranteedCatch("Guaranteed Catch"),
    Scales("Scales"),
    ScubaGear("Scuba Gear"),
    ;

    private final String name;
    private final boolean active;

    NormalSpearFishingSkill(String name, boolean active) {
        this.name = name;
        this.active = active;
    }

    NormalSpearFishingSkill(String name) {
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
        return SkillCategory.SpearFishing;
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
