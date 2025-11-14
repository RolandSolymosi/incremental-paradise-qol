package com.incrementalclient.common.data.skills;


import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;

public enum NightmareMiningSkill implements NameableEnum {
    Shatterpoint("Shatterpoint",0),

    HeavyStrikes("Heavy Strikes",1),
    SnailOil("Snail Oil",1),
    ExpandedZone("Expanded Zone",1),

    CriticalStrikes("Critical Strikes",0),
    OnARoll("On a Roll",0),
    LuckyCrits("Lucky Crits",0),
    IncreasedDuration("Increased Duration",0),
    MitiagedLosees("Mitiaged Losees",0), // This really is the name ingame :D
    SparkingStrikes("Sparking Strikes",0),
    SpreadingCracks("SpreadingCracks",0),
    AllTheTime("All the Time",0),
    RapidRefunds("Rapid Refunds",0),
    ShorterTrack("Shorter Track",0),
    ;

    private final String name;
    private final int requiredPoints;

    NightmareMiningSkill(String name, int requiredPoints) {
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
