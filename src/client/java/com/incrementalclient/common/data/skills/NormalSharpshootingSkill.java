package com.incrementalclient.common.data.skills;


import com.incrementalclient.common.data.World;
import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public enum NormalSharpshootingSkill implements Skill {
    ExplosiveArrow("Explosive Arrow", true),
    SwarmSurfer("Swarm Surfer", true),
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
    private final boolean active;

    NormalSharpshootingSkill(String name, boolean active) {
        this.name = name;
        this.active = active;
    }

    NormalSharpshootingSkill(String name) {
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
        return SkillCategory.Sharpshooting;
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
