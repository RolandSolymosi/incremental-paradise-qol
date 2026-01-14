package com.incrementalclient.common.data.skills;


import com.incrementalclient.common.data.World;
import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public enum NormalExcavationSkill implements Skill {
    SeismicResonance("Seismic Resonance", true),
    FeatherDuster("Feather Duster"),
    ExperiencedManager("Experienced Manager"),
    AncientJackpot("Ancient Jackpot"),
    BuriedBounty("Buried Bounty"),
    Lucky7s("Lucky 7s")
    ;

    private final String name;
    private final boolean active;

    NormalExcavationSkill(String name, boolean active) {
        this.name = name;
        this.active = active;
    }

    NormalExcavationSkill(String name) {
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
        return SkillCategory.Excavation;
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
