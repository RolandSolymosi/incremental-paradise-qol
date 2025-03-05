package com.incrementalqol.common.data.Skills;


import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;

public enum NightmareFarmingSkill implements NameableEnum {
    Landscaper("Landscaper",0),

    BiggerRake("Bigger Rake",1),
    FurtherRake("Further Rake",1),
    FasterRake("Faster Rake",1),

    TwoForOne("Two for One",0),
    ScarletLeaf("Scarlet Leaf",0),
    PenPals("Pen Pals",0),
    HeavyLeaves("Heavy Leaves",0),
    AutumnLeaves("Autumn Leaves",0),
    AutoCollect("Auto Collect",0),
    LeafPile("Leaf Pile",0),
    MoreLeaves("More Leaves",0),
    RedForNone("Red for None",0),
    BlownWithTheWind("Blown with the Wind",0),
    LongerLeaves("Longer Leaves",0),
    ;

    private final String name;
    private final int requiredPoints;

    NightmareFarmingSkill(String name, int requiredPoints) {
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
