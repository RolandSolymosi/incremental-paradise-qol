package com.incrementalclient.common.data.skills;


import com.incrementalclient.common.data.World;
import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public enum NightmareSharpshootingSkill implements Skill {
    PeaShooter("Pea Shooter", 0),

    TurretAccuracy("Turret Accuracy", 1),
    Overclocked("Overclocked", 1),
    BackupBattery("Backup Battery", 1),

    RallyShot("Rally Shot", 0),
    GhostArrows("Ghost Arrows", 0),
    ConcussiveArrows("Concussive Arrows", 0),
    SniperScopes("Sniper Scopes", 0),
    MorePower("More Power", 0),
    SpareParts("Spare Parts", 0),
    PocketHoles("Pocket Holes", 0),
    RepeatedTrauma("Repeated Trauma", 0),
    HeatSeekingArrows("Heat Seeking Arrows", 0),
    LiquidCooling("Liquid Cooling", 0),
    ;

    private final String name;
    private final int requiredPoints;

    NightmareSharpshootingSkill(String name, int requiredPoints) {
        this.name = name;

        this.requiredPoints = requiredPoints;
    }

    @Override
    public Text getDisplayName() {
        return Text.of(name + " [Requires: " + requiredPoints + "]");
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
        return World.Realm.Nightmare;
    }

    @Override
    public boolean isActiveUpgrade() {
        return this == PeaShooter;
    }
}
