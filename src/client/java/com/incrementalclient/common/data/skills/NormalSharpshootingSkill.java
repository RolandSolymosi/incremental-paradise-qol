package com.incrementalclient.common.data.skills;


import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;

public enum NormalSharpshootingSkill implements NameableEnum {
    ExplosiveArrow("Explosive Arrow"),
    SwarmSurfer("Swarm Surfer"),
    Hawkeye("Hawkeye"),
    Assassin("Assassin"),
    FullForce("Full Force"),
    Sniper("Sniper"),
    Revitalize("Revitalize"),
    HotStreak("Hot Streak"),
    CleanKill("Clean Kill"),
    PowerfulShot("Powerful Shot"),
    QuickQuiver("Quick Quiver"),
    FullOfHoles("Full of Holes"),
    ;

    private final String name;

    NormalSharpshootingSkill(String name) {
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
