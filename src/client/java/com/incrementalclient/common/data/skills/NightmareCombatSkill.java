package com.incrementalclient.common.data.skills;


import dev.isxander.yacl3.api.NameableEnum;
import net.minecraft.text.Text;

public enum NightmareCombatSkill implements NameableEnum {
    DevilsGambit("Devil's Gambit",0),

    FasterSpins("Faster Spins",1),
    StrongerSymbols("Stronger Symbols",1),
    LuckierSymbols("Luckier Symbols",1),

    Jackpot("Jackpot",3),
    DiceSymbol("Dice Symbol",3),

    HeartierSymbols("Heartier Symbols",5),
    BonusSymbol("Bonus Symbol",5),
    LongerSymbols("Longer Symbols",5),

    WitherRoseSymbol("Wither Rose Symbol",10),
    GuaranteedPayout("Guaranteed Payout",10),

    WitherInsurance("Wither Insurance",12),
    CookingThePot("Cooking the Pot",12),
    BigWinnings("Big Winnings",12),
    ;

    private final String name;
    private final int requiredPoints;

    NightmareCombatSkill(String name, int requiredPoints) {
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
