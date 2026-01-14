package com.incrementalclient.common.data.skills;


import com.incrementalclient.common.data.World;
import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public enum NormalForagingSkill implements Skill {
    Timberstrike("Timberstrike"),
    LuckyGathering("Lucky Gathering"),
    BuzzingAssault("Buzzing Assault"),
    KeySeeker("Key Seeker"),
    WideChop("Wide Chop"),
    Stockpile("Stockpile"),
    LumberjackMunchies("Lumberjack Munchies"),
    WoodcuttingCapacity("Woodcutting Capacity"),
    SugarRush("Sugar Rush"),
    FruitBasket("Fruit Basket"),
    BugBounty("Bug Bounty"),
    WeakSpot("Weak Spot"),
    TreeFeller("Tree Feller"),
    RipeFruit("Ripe Fruit"),
    ;

    private final String name;
    private final boolean active;

    NormalForagingSkill(String name, boolean active) {
        this.name = name;
        this.active = active;
    }

    NormalForagingSkill(String name) {
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
        return SkillCategory.Foraging;
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
