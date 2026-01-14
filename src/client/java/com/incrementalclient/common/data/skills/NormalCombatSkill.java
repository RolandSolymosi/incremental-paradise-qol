package com.incrementalclient.common.data.skills;


import com.incrementalclient.common.data.World;
import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public enum NormalCombatSkill implements Skill {
    SweepingStrike("Sweeping Strike", true),
    RhinoCharge("Rhino Charge", true),
    BeeStorm("Bee Storm", true),
    KeySeeker("Key Seeker"),
    CrushingBlow("Crushing Blow"),
    Surge("Surge"),
    HeartyHeart("Hearty Heart"),
    CombatCapacity("Combat Capacity"),
    Toughness("Toughness"),
    Regen("Regen"),
    SuckerPunch("Sucker Punch"),
    RapidRecovery("Rapid Recovery"),
    CorpseLooter("Corpse Looter"),
    Revenge("Revenge"),
    PowerfulBeats("Powerful Beats"),
    UnscathedSpoils("Unscathed Spoils"),
    ;

    private final String name;
    private final boolean active;

    NormalCombatSkill(String name, boolean active) {
        this.name = name;
        this.active = active;
    }

    NormalCombatSkill(String name) {
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
        return SkillCategory.Combat;
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
