package com.incrementalclient.common.data.skills;


import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;

public enum NightmareSpearFishingSkill implements NameableEnum {
    FishSenses("Fish Senses",0),

    HomingHarpoon("Homing Harpoon",1),
    Firefighter("Firefighter",1),
    FreshFish("Fresh Fish",1),

    StackingChance("Stacking Chance",4),
    RainbowFish("Rainbow Fish",4),

    HotSpa("Hot Spa",5),
    GreenFish("GreenFish",5),
    TerminalVelocity("Terminal Velocity",5),

    StackingLuck("StackingLuck",10),
    BlueFish("BlueFish",10),

    PurpleFish("Purple Fish",12),
    GuaranteedCatch("Guaranteed Catch",12),
    ReturnToSender("Return to Sender",12),
    ;

    private final String name;
    private final int requiredPoints;

    NightmareSpearFishingSkill(String name, int requiredPoints) {
        this.name = name;

        this.requiredPoints = requiredPoints;
    }

    @Override
    public Text getDisplayName() {
        return Text.of(name+ " [Requires: "+requiredPoints+"]");
    }

    public String getName() {
        return name;
    }
}
