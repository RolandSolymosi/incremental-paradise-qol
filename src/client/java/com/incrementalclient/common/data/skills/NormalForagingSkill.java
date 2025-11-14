package com.incrementalclient.common.data.skills;


import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;

public enum NormalForagingSkill implements NameableEnum {
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

    NormalForagingSkill(String name) {
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
