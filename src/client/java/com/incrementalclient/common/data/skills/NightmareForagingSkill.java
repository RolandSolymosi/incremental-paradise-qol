package com.incrementalclient.common.data.skills;


import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;

public enum NightmareForagingSkill implements NameableEnum {
    AxeJuggling("Axe Juggling",0),

    QuickFeet("Quick Feet",1),
    XMarksTheSpot("X Marks the Spot",1),
    FeatherWeight("Feather Weight",1),

    HotHands("Hot Hands",0),
    LuckySplinters("Lucky Splinters",0),
    SharperAxe("Sharper Axe",0),
    ShakeShakeShake("Shake Shake Shake",0),
    BugCatcher("Bug Catcher",0), // This really is the name ingame :D
    FeastOrFamine("Feast or Famine",0),
    SafetyNet("Safety Net",0),
    JuggleFever("Juggle Fever",0),
    Carnage("Carnage",0),
    Gluttony("Gluttony",0),
    ;

    private final String name;
    private final int requiredPoints;

    NightmareForagingSkill(String name, int requiredPoints) {
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
