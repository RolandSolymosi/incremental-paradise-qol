package com.incrementalclient.common.data.skills;


import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;

public enum NormalExcavationSkill implements NameableEnum {
    SeismicResonance("Seismic Resonance"),
    FeatherDuster("Feather Duster"),
    ExperiencedManager("Experienced Manager"),
    AncientJackpot("Ancient Jackpot"),
    BuriedBounty("Buried Bounty"),
    Lucky7s("Lucky 7s")
    ;

    private final String name;

    NormalExcavationSkill(String name) {
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
