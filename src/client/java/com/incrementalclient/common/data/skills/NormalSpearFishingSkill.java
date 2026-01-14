package com.incrementalclient.common.data.skills;


import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public enum NormalSpearFishingSkill implements Skill {
    SpoonBender("Spoon Bender"),
    Wavestreak("Wavestreak"),
    Beenado("Beenado"),
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

    NormalSpearFishingSkill(String name) {
        this.name = name;
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
}
