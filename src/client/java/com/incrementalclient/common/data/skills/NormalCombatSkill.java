package com.incrementalclient.common.data.skills;


import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;

public enum NormalCombatSkill implements NameableEnum {
    SweepingStrike("Sweeping Strike"),
    RhinoCharge("Rhino Charge"),
    BeeStorm("Bee Storm"),
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

    NormalCombatSkill(String name) {
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
