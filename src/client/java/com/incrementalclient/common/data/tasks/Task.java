package com.incrementalclient.common.data.tasks;

import com.incrementalclient.common.data.*;
import com.incrementalclient.common.data.targets.Target;
import com.incrementalclient.common.data.tasks.abstractions.GamingTask;
import com.incrementalclient.common.data.tasks.abstractions.ITask;
import com.incrementalclient.common.data.tasks.abstractions.NormalTask;

import java.util.*;
import java.util.stream.Collectors;

public enum Task {
    // Global
    SellItems(new NormalTask("Sell Items", Region.W1_Overworld, List.of("items"), null, TaskType.Misc, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Carrot), List.of(Warp.W1_Carrot, Warp.W1_Spawn))),

    // World 1
    Coal(new NormalTask("Coal", Region.W1_Overworld, List.of("coal", "coal ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Coal), List.of(Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    ShinyCoal(new NormalTask("Shiny coal", Region.W1_Overworld, List.of("coal", "coal ore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Coal), List.of(Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    Iron(new NormalTask("Iron", Region.W1_Overworld, List.of("iron", "iron ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Iron), List.of(Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    ShinyIron(new NormalTask("Shiny iron", Region.W1_Overworld, List.of("iron", "iron ore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Iron), List.of(Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    Copper(new NormalTask("Copper", Region.W1_Overworld, List.of("copper", "copper ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Copper), List.of(Warp.W1_Copper, Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    ShinyCopper(new NormalTask("Shiny copper", Region.W1_Overworld, List.of("copper", "copper ore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Copper), List.of(Warp.W1_Copper, Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    Gold(new NormalTask("Gold", Region.W1_Overworld, List.of("gold", "gold ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Gold), List.of(Warp.W1_Gold, Warp.W1_Copper, Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    ShinyGold(new NormalTask("Shiny gold", Region.W1_Overworld, List.of("gold", "gold ore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Gold), List.of(Warp.W1_Gold, Warp.W1_Copper, Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    Redstone(new NormalTask("Redstone", Region.W1_Overworld, List.of("redstone", "redstone ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Redstone), List.of(Warp.W1_RedStone, Warp.W1_Gold, Warp.W1_Copper, Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    ShinyRedstone(new NormalTask("Shiny redstone", Region.W1_Overworld, List.of("redstone", "redstone ore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Redstone), List.of(Warp.W1_RedStone, Warp.W1_Gold, Warp.W1_Copper, Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),

    Applewood(new NormalTask("Applewood", Region.W1_Overworld, List.of("applewood"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W1_AppleTree), List.of(Warp.W1_Spawn))),
    Apple(new NormalTask("Apple", Region.W1_Overworld, List.of("apple"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W1_AppleTree), List.of(Warp.W1_Spawn))),
    LargeApple(new NormalTask("Large apple", Region.W1_Overworld, List.of("apples"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W1_AppleTree), List.of(Warp.W1_Spawn))),
    Palm(new NormalTask("Palm", Region.W1_Overworld, List.of("palm"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W1_PalmTree), List.of(Warp.W1_Crab, Warp.W1_Spawn))),
    Coconut(new NormalTask("Coconut", Region.W1_Overworld, List.of("coconuts"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W1_PalmTree), List.of(Warp.W1_Crab, Warp.W1_Spawn))),
    LargeCoconut(new NormalTask("Large coconut", Region.W1_Overworld, List.of("coconuts"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W1_PalmTree), List.of(Warp.W1_Crab, Warp.W1_Spawn))),
    Ladybug(new NormalTask("Ladybug", Region.W1_Overworld, List.of("ladybugs"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W1_Ladybug), List.of(Warp.W1_Beetroot, Warp.W1_Spawn))),

    ScaredHog(new NormalTask("Scared hog", Region.W1_Overworld, List.of("scared hogs"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W1_ScaredHog), List.of(Warp.W1_Spawn))),
    MountainGoat(new NormalTask("Goat", Region.W1_Overworld, List.of("mountain goats"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W1_Goat), List.of(Warp.W1_Spawn))),
    WildBoar(new NormalTask("Wild hog", Region.W1_Overworld, List.of("wild boars"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W1_WildBoar), List.of(Warp.W1_Hoglin, Warp.W1_Spawn))),
    Hoglin(new NormalTask("Hoglin", Region.W1_Overworld, List.of("hoglin"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W1_Hoglin), List.of(Warp.W1_Hoglin, Warp.W1_Spawn))),
    W1Elite(new NormalTask("Elite mobs in World #1", Region.W1_Overworld, List.of("mobs in world #1"), List.of(Constraint.Elite), TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W1_ScaredHog, Target.W1_Goat, Target.W1_WildBoar), List.of(Warp.W1_Spawn))),

    Wheat(new NormalTask("Wheat", Region.W1_Overworld, List.of("wheat"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Wheat), List.of(Warp.W1_Wheat, Warp.W1_Spawn))),
    Carrot(new NormalTask("Carrot", Region.W1_Overworld, List.of("carrots"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Carrot), List.of(Warp.W1_Carrot, Warp.W1_Spawn))),
    Potato(new NormalTask("Potato", Region.W1_Overworld, List.of("potatoes"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Potato), List.of(Warp.W1_Potato, Warp.W1_Spawn))),
    Beetroot(new NormalTask("Beetroot", Region.W1_Overworld, List.of("beetroot"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Beetroot), List.of(Warp.W1_Beetroot, Warp.W1_Spawn))),
    Honeycomb(new NormalTask("Honeycomb", Region.W1_Overworld, List.of("honeycomb"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Honeycomb), List.of(Warp.W1_Spawn))),
    Crops(new NormalTask("Crops", Region.W1_Overworld, List.of("crops"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Wheat, Target.W1_Carrot, Target.W1_Potato, Target.W1_Beetroot), List.of(Warp.W1_Carrot, Warp.W1_Spawn))),

    Riverfish(new NormalTask("Riverfish", Region.W1_Overworld, List.of("riverfish"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W1_Riverfish), List.of(Warp.W1_Carrot, Warp.W1_Spawn))),
    ColoredRiverfish(new NormalTask("%s Colored Riverfish", Region.W1_Overworld, List.of("riverfish"), List.of(Constraint.Colored), TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W1_Riverfish), List.of(Warp.W1_Carrot, Warp.W1_Spawn))),
    ConsecutiveFish(new NormalTask("2 fish without missing", Region.W1_Overworld, List.of("fish"), List.of(Constraint.Consecutive), TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W1_Riverfish), List.of(Warp.W1_Carrot, Warp.W1_Spawn))),
    Crab(new NormalTask("Crab", Region.W1_Crab, List.of("crabs"), null, TaskType.Fishing, DefaultWardrobe.CombatFishing, Tool.Spear, List.of(Target.W1_Crab), List.of(Warp.W1_Crab, Warp.W1_Spawn))),
    HermitCrab(new NormalTask("Hermit crab", Region.W1_Crab, List.of("hermit crabs"), null, TaskType.Fishing, DefaultWardrobe.CombatFishing, Tool.Spear, List.of(Target.W1_HermitCrab), List.of(Warp.W1_Crab, Warp.W1_Spawn))),

    Coinflip(new GamingTask("Play coinflip", Region.W1_Overworld, List.of("coinflip"), GameKind.Coinflip, List.of(Constraint.Games), List.of(Warp.W1_Spawn))),
    Rps(new GamingTask("Play rps", Region.W1_Overworld, List.of("rps"), GameKind.Rps, List.of(Constraint.Games), List.of(Warp.W1_Spawn))),
    EarnScorePixelpop(new GamingTask("Score in Pixelpop", Region.W1_Overworld, List.of("pixel pop"), GameKind.Pixelpop, List.of(Constraint.Score), List.of(Warp.W1_Spawn))),
    EarnTicketPixelpop(new GamingTask("Ticket for Pixelpop", Region.W1_Overworld, List.of("pixel pop"), GameKind.Pixelpop, List.of(Constraint.Ticket), List.of(Warp.W1_Spawn))),

    GoldMoney(new NormalTask("Sell for gold", Region.W1_Overworld, List.of("gold from selling items"), null, TaskType.Misc, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Carrot), List.of(Warp.W1_Carrot, Warp.W1_Spawn))),
    GarbageCans(new NormalTask("Clean garbage", Region.W1_Overworld, List.of("garbage cans"), null, TaskType.Misc, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W1_GarbageCan), List.of(Warp.W1_Spawn))),

    // World 2
    Crimsonite(new NormalTask("Crimsonite", Region.W2_Lush, List.of("crimsonite", "crimsonite ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W2_Crimsonite), List.of(Warp.W2_Lush, Warp.W2_Spawn))),
    ShinyCrimsonite(new NormalTask("Shiny crimsonite", Region.W2_Lush, List.of("crimsonite", "crimsonite ore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W2_Crimsonite), List.of(Warp.W2_Lush, Warp.W2_Spawn))),
    Verdelith(new NormalTask("Verdelith", Region.W2_Veil, List.of("verdelith", "verdelith ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W2_Verdelith), List.of(Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    ShinyVerdelith(new NormalTask("Shiny verdelith", Region.W2_Veil, List.of("verdelith", "verdelith ore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W2_Verdelith), List.of(Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    Azuregem(new NormalTask("Azuregem", Region.W2_Infernal, List.of("azuregem", "azuregem ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W2_Azuregem), List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    ShinyAzuregem(new NormalTask("Shiny azuregem", Region.W2_Infernal, List.of("azuregem", "azuregem ore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W2_Azuregem), List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    Aurorium(new NormalTask("Aurorium", Region.W2_Abyss, List.of("aurorium", "aurorium ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W2_Aurorium), List.of(Warp.W2_Abyss, Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    ShinyAurorium(new NormalTask("Shiny aurorium", Region.W2_Abyss, List.of("aurorium", "aurorium ore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W2_Aurorium), List.of(Warp.W2_Abyss, Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),

    Bonsai(new NormalTask("Bonsai", Region.W2_Overworld, List.of("bonsai"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W2_Spawn))),
    Pomegranate(new NormalTask("Pomegranate", Region.W2_Overworld, List.of("pomegranates"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W2_Spawn))),
    LargePomegranate(new NormalTask("Large pomegranate", Region.W2_Overworld, List.of("pomegranates"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W2_Spawn))),
    Pine(new NormalTask("Pine", Region.W2_Overworld, List.of("pine"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W2_Shimmer, Warp.W2_Spawn))),
    Pinecone(new NormalTask("Pinecone", Region.W2_Overworld, List.of("pinecones"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W2_Shimmer, Warp.W2_Spawn))),
    LargePinecone(new NormalTask("Large pinecone", Region.W2_Overworld, List.of("pinecones"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W2_Shimmer, Warp.W2_Spawn))),
    Deadwood(new NormalTask("Deadwood", Region.W2_Overworld, List.of("deadwood"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W2_Shimmer, Warp.W2_Spawn))),
    Zephyr(new NormalTask("Zephyr", Region.W2_Abyss, List.of("zephyr"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W2_Abyss, Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    SkyBeetle(new NormalTask("Sky Beetle", Region.W2_Overworld, List.of("skybeetles"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W2_SkyBeetle), List.of(Warp.W2_Spawn))),

    Panda(new NormalTask("Panda", Region.W2_Overworld, List.of("pandas"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W2_Panda), List.of(Warp.W2_Sky, Warp.W2_Spawn))),
    Sniffer(new NormalTask("Sniffer", Region.W2_Overworld, List.of("sniffers"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W2_Sniffer), List.of(Warp.W2_Forge, Warp.W2_Spawn))),
    Lurker(new NormalTask("Lurker", Region.W2_Lush, List.of("lurkers"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W2_Lurker), List.of(Warp.W2_Lush, Warp.W2_Spawn))),
    CaveCrawler(new NormalTask("Cave crawler", Region.W2_Lush, List.of("cave crawlers"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, List.of(Target.W2_AncientLurker), List.of(Warp.W2_Lush, Warp.W2_Spawn))),
    PoisonSlime(new NormalTask("Poison slime", Region.W2_Lush, List.of("poison slimes"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W2_PoisonSlime), List.of(Warp.W2_Lush, Warp.W2_Spawn))),
    Spotter(new NormalTask("Spotter", Region.W2_Veil, List.of("spotters"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W2_Spotter), List.of(Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    Verdemite(new NormalTask("Verdemite", Region.W2_Veil, List.of("verdemites"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Pickaxe, List.of(Target.W2_Verdemite), List.of(Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    Endermen(new NormalTask("Endermen", Region.W2_Veil, List.of("endermen"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W2_Enderman), List.of(Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    Ghast(new NormalTask("Ghast", Region.W2_Infernal, List.of("ghasts"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, List.of(Target.W2_Ghast), List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    GhastSoul(new NormalTask("Ghast Soul", Region.W2_Infernal, List.of("ghast souls"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, List.of(Target.W2_GhastSoul), List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    Blaze(new NormalTask("Blaze", Region.W2_Infernal, List.of("blazes"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W2_Blaze), List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    Nrub(new NormalTask("Nrub", Region.W2_Infernal, List.of("nrubs"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W2_Nrub), List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    InfernalImp(new NormalTask("Infernal imp", Region.W2_Infernal, List.of("infernal imps"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W2_InfernalImp), List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    Wick(new NormalTask("Wick", Region.W2_Abyss, List.of("wicks"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W2_Wick), List.of(Warp.W2_Abyss, Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    GlowSquid(new NormalTask("Glow squid", Region.W2_Abyss, List.of("glow squids"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, List.of(Target.W2_GlowSquid), List.of(Warp.W2_Abyss, Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    Slinker(new NormalTask("Slinker", Region.W2_Abyss, List.of("slinkers"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W2_Slinker), List.of(Warp.W2_Abyss, Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    Rodrick(new NormalTask("Rodrick", Region.W2_Overworld, List.of("rodrick"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W2_Rodrick, Warp.W2_Garlic, Warp.W2_Spawn))),
    SkyBeetleQueen(new NormalTask("Sky Beetle Queen", Region.W2_Overworld, List.of("sky beetle queen"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.W2_Sky, Warp.W2_Shimmer, Warp.W2_Spawn))),
    W2Elite(new NormalTask("Elite mobs in World #2", Region.W2_Overworld, List.of("mobs in world #2"), List.of(Constraint.Elite), TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W2_Panda, Target.W2_Sniffer, Target.W2_CaveCrawler, Target.W2_PoisonSlime, Target.W2_Enderman, Target.W2_Blaze, Target.W2_Nrub, Target.W2_Ghast, Target.W2_GhastSoul, Target.W2_InfernalImp, Target.W2_Wick, Target.W2_Slinker, Target.W2_GlowSquid), List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),

    Shimmer(new NormalTask("Shimmer", Region.W2_Overworld, List.of("shimmer"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W2_Shimmer, Warp.W2_Spawn))),
    Garlic(new NormalTask("Garlic", Region.W2_Overworld, List.of("garlic"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W2_Garlic, Warp.W2_Spawn))),
    Corn(new NormalTask("Corn", Region.W2_Overworld, List.of("corn"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W2_Corn, Warp.W2_Spawn))),
    Shy(new NormalTask("Shy", Region.W2_Veil, List.of("shy"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    LavaFruit(new NormalTask("Lava fruit", Region.W2_Infernal, List.of("lavafruit"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    Twine(new NormalTask("Twine", Region.W2_Abyss, List.of("twine"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W2_Abyss, Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    AdvancedCrops(new NormalTask("Advanced Crops", Region.W2_Overworld, List.of("advanced crops"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W2_Garlic, Warp.W2_Spawn))),

    Salmon(new NormalTask("Salmon", Region.W2_Overworld, List.of("salmon"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W2_Salmon), List.of(Warp.W2_Spawn))),
    Koi(new NormalTask("Koi", Region.W2_Overworld, List.of("koi"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W2_Koi), List.of(Warp.W2_Spawn))),
    Axolotl(new NormalTask("Axolotl", Region.W2_Overworld, List.of("axolotl"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W2_Axolotl), List.of(Warp.W2_Spawn))),
    MagmaFish(new NormalTask("Magma fish", Region.W2_Infernal, List.of("magmafish"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W2_Magmafish), List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    MoltenJellyfish(new NormalTask("Molten jellyfish", Region.W2_Infernal, List.of("molten jellyfish"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W2_MoltenJellyfish), List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    Bubbler(new NormalTask("Bubbler", Region.W2_Infernal, List.of("bubbler"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W2_Bubbler), List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    AbyssalCrab(new NormalTask("Abyssal crab", Region.W2_Abyss, List.of("abyssal crabs"), null, TaskType.Fishing, DefaultWardrobe.CombatFishing, Tool.Spear, List.of(Target.W2_AbyssalCrab), List.of(Warp.W2_Abyss, Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),

    Blackjack(new GamingTask("Play 21", Region.W2_Overworld, List.of("21"), GameKind.Blackjack, List.of(Constraint.Games), List.of(Warp.W2_Spawn))),
    SilverMoney(new NormalTask("Sell for silver", Region.W2_Overworld, List.of("silver from selling items"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    AbyssLamp(new NormalTask("Repair Lamppost", Region.W2_Abyss, List.of("lampposts"), null, TaskType.Misc, null, Tool.Spear, null, List.of(Warp.W2_Abyss, Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),

    // World 3
    Brightstone(new NormalTask("Brightstone", Region.W3_Mine, List.of("brightstone"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W3_Brightstone), List.of(Warp.W3_Mines, Warp.W3_Spawn))),
    ShinyBrightstone(new NormalTask("Shiny brightstone", Region.W3_Mine, List.of("brightstone"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W3_Brightstone), List.of(Warp.W3_Mines, Warp.W3_Spawn))),
    Diamond(new NormalTask("Diamond", Region.W3_Mine, List.of("diamonds"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W3_Diamond), List.of(Warp.W3_Mines, Warp.W3_Spawn))),
    ShinyDiamond(new NormalTask("Shiny diamond", Region.W3_Mine, List.of("diamonds"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W3_Diamond), List.of(Warp.W3_Mines, Warp.W3_Spawn))),
    Emerald(new NormalTask("Emerald", Region.W3_Mine, List.of("emeralds"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W3_Emerald), List.of(Warp.W3_Mines, Warp.W3_Spawn))),
    ShinyEmerald(new NormalTask("Shiny emerald", Region.W3_Mine, List.of("emeralds"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W3_Emerald), List.of(Warp.W3_Mines, Warp.W3_Spawn))),

    Gorespore(new NormalTask("Gorespore", Region.W3_Sty, List.of("gorespore"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Sty, Warp.W3_Spawn))),
    GoresporeSpore(new NormalTask("Gorespore spore", Region.W3_Sty, List.of("gorespore spores"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Sty, Warp.W3_Spawn))),
    LargeGoresporeSpore(new NormalTask("Large gorespore spore", Region.W3_Sty, List.of("gorespore spores"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Sty, Warp.W3_Spawn))),
    DuneDweller(new NormalTask("Dunes dweller", Region.W3_Beach, List.of("dune dweller"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Beach, Warp.W3_Spawn))),
    DuneDwellerSpore(new NormalTask("Dune dweller spore", Region.W3_Beach, List.of("dune dweller spores"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Beach, Warp.W3_Spawn))),
    LargeDuneDwellerSpore(new NormalTask("Large dune dweller spore", Region.W3_Beach, List.of("dune dweller spores"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Beach, Warp.W3_Spawn))),
    HoneyShroom(new NormalTask("Honey shrooms", Region.W3_Topside, List.of("honey shrooms"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Topside, Warp.W3_Spawn))),
    HoneySpore(new NormalTask("Honey spore", Region.W3_Topside, List.of("honey shroom spores"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Topside, Warp.W3_Spawn))),
    LargeHoneySpore(new NormalTask("Large honey spore", Region.W3_Topside, List.of("honey shroom spores"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Topside, Warp.W3_Spawn))),
    DreamShroom(new NormalTask("Dream shroom", Region.W3_Overworld, List.of("dream shrooms"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Spawn))),
    DreamSpore(new NormalTask("Dream spore", Region.W3_Overworld, List.of("dream spores"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Spawn))),
    LargeDreamSpore(new NormalTask("Large dream spore", Region.W3_Overworld, List.of("dream spores"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Spawn))),
    Barky(new NormalTask("Barky", Region.W3_Canine, List.of("barky"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Canine, Warp.W3_Spawn))),
    Pinepoodle(new NormalTask("Pinepoodle", Region.W3_Canine, List.of("pinepoodles"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Canine, Warp.W3_Spawn))),
    LargePinepoodle(new NormalTask("Large pinepoodle", Region.W3_Canine, List.of("pinepoodles"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Canine, Warp.W3_Spawn))),
    Capsnapper(new NormalTask("Capsnapper", Region.W3_Overworld, List.of("capsnappers"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W3_Capsnapper), List.of(Warp.W3_Spawn))),

    Baconwing(new NormalTask("Baconwing", Region.W3_Sty, List.of("baconwings"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W3_Baconwing), List.of(Warp.W3_Sty, Warp.W3_Spawn))),
    Camel(new NormalTask("Camel", Region.W3_Beach, List.of("camels"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W3_Camel), List.of(Warp.W3_Beach, Warp.W3_Spawn))),
    Bee(new NormalTask("Bee", Region.W3_Topside, List.of("bees"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W3_Bee), List.of(Warp.W3_Topside, Warp.W3_Spawn))),
    RoyalGuard(new NormalTask("Royal Guard", Region.W3_Topside, List.of("royal guards"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W3_RoyalGuard), List.of(Warp.W3_Topside, Warp.W3_Spawn))),
    Breeze(new NormalTask("Breeze", Region.W3_Mine, List.of("breezes"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W3_Breeze), List.of(Warp.W3_Mines, Warp.W3_Spawn))),
    DireWolf(new NormalTask("Dire wolf", Region.W3_Canine, List.of("dire wolves"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W3_DireWolf), List.of(Warp.W3_Canine, Warp.W3_Spawn))),
    Dreadhorn(new NormalTask("Dreadhorn", Region.W3_Overworld, List.of("dreadhorn"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W3_Dreadhorn, Warp.W3_Spawn))),
    W3Elite(new NormalTask("Elite mobs in World #3", Region.W3_Overworld, List.of("mobs in world #3"), List.of(Constraint.Elite), TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W3_Baconwing, Target.W3_Camel, Target.W3_Bee, Target.W3_RoyalGuard, Target.W3_Breeze, Target.W3_DireWolf), List.of(Warp.W3_Topside, Warp.W3_Spawn))),

    Oinky(new NormalTask("Oinky", Region.W3_Sty, List.of("oinky"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W3_Sty, Warp.W3_Spawn))),
    Cattail(new NormalTask("Cattail", Region.W3_Beach, List.of("cattails"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W3_Beach, Warp.W3_Spawn))),
    Gloom(new NormalTask("Gloom", Region.W3_Underside, List.of("gloom"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W3_Underside, Warp.W3_Spawn))),
    CollieFlower(new NormalTask("Collie flower", Region.W3_Canine, List.of("collie-flowers"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W3_Canine, Warp.W3_Spawn))),

    Bettafly(new NormalTask("Bettafly", Region.W3_Sty, List.of("bettafly"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W3_Bettafly, Target.W3_LegendaryBettafly), List.of(Warp.W3_Sty, Warp.W3_Spawn))),
    Soarfish(new NormalTask("Soarfish", Region.W3_Beach, List.of("soarfish"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W3_Soarfish, Target.W3_LegendarySoarfish), List.of(Warp.W3_Beach, Warp.W3_Spawn))),
    GoldfishRetriever(new NormalTask("Goldfish retriever", Region.W3_Canine, List.of("goldfish retrievers"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W3_Goldfish), List.of(Warp.W3_Canine, Warp.W3_Spawn))),
    Guardian(new NormalTask("Guardian", Region.W3_Underside, List.of("guardians"), null, TaskType.Fishing, DefaultWardrobe.CombatFishing, Tool.Spear, List.of(Target.W3_Guardian), List.of(Warp.W3_Underside, Warp.W3_Spawn))),

    FindMatchesMatcher(new GamingTask("Score in matcher", Region.W3_Overworld, List.of("matcher"), GameKind.Matcher, List.of(Constraint.Matches), List.of(Warp.W3_Spawn))),
    EarnTicketMatcher(new GamingTask("Ticket for matcher", Region.W3_Overworld, List.of("matcher"), GameKind.Matcher, List.of(Constraint.Ticket), List.of(Warp.W3_Spawn))),

    // World 4
    Cheddore(new NormalTask("Cheddore", Region.W4_Homestead, List.of("cheddore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W4_Cheddore), List.of(Warp.W4_Spawn))),
    ShinyCheddore(new NormalTask("Shiny cheddore", Region.W4_Homestead, List.of("cheddore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W4_Cheddore), List.of(Warp.W4_Spawn))),
    BlueCheese(new NormalTask("Blue cheese", Region.W4_Homestead, List.of("blue cheese"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W4_BlueCheese), List.of(Warp.W4_Spawn))),
    ShinyBlueCheese(new NormalTask("Shiny blue cheese", Region.W4_Homestead, List.of("blue cheese"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W4_BlueCheese), List.of(Warp.W4_Spawn))),
    Glowdust(new NormalTask("Glowdust", Region.W4_Beta, List.of("glowdust"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W4_Glowdust), List.of(Warp.W4_Beta))),
    ShinyGlowdust(new NormalTask("Shiny glowdust", Region.W4_Beta, List.of("glowdust"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W4_Glowdust), List.of(Warp.W4_Beta))),
    Slimecrust(new NormalTask("Slimecrust", Region.W4_Beta, List.of("slimecrust"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W4_Slimecrust), List.of(Warp.W4_Beta))),
    ShinySlimecrust(new NormalTask("Shiny slimecrust", Region.W4_Beta, List.of("slimecrust"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W4_Slimecrust), List.of(Warp.W4_Beta))),
    Voidshard(new NormalTask("Voidshard", Region.W4_Beta, List.of("voidshard"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W4_Voidshard), List.of(Warp.W4_Beta))),
    ShinyVoidshard(new NormalTask("Shiny voidshard", Region.W4_Beta, List.of("voidshard"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W4_Voidshard), List.of(Warp.W4_Beta))),
    Petrafin(new NormalTask("Petrafin", Region.W4_Delta, List.of("petrafin"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W4_Petrafin), List.of(Warp.W4_Delta))),
    ShinyPetrafin(new NormalTask("Shiny petrafin", Region.W4_Delta, List.of("petrafin"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W4_Petrafin), List.of(Warp.W4_Delta))),

    NestingWood(new NormalTask("Nesting wood", Region.W4_Homestead, List.of("nesting wood"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Spawn))),
    Passionfruit(new NormalTask("Passionfruit", Region.W4_Homestead, List.of("passionfruit"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Spawn))),
    LargePassionfruit(new NormalTask("Large passionfruit", Region.W4_Homestead, List.of("passionfruit"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Spawn))),
    Cryoflora(new NormalTask("Cryoflora", Region.W4_Alpha, List.of("cryoflora"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    Chillfruit(new NormalTask("Chillfruit", Region.W4_Alpha, List.of("chillfruit"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    LargeChillfruit(new NormalTask("Large chillfruit", Region.W4_Alpha, List.of("chillfruit"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    Sulphoroot(new NormalTask("Sulphoroot", Region.W4_Alpha, List.of("sulphoroot"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    Jackfruit(new NormalTask("Jackfruit", Region.W4_Alpha, List.of("jackfruit"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    LargeJackfruit(new NormalTask("Large jackfruit", Region.W4_Alpha, List.of("jackfruit"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    Pyrospire(new NormalTask("Pyrospire", Region.W4_Alpha, List.of("pyrospire"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    Scorchberry(new NormalTask("Scorchberry", Region.W4_Alpha, List.of("scorchberries"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    LargeScorchberry(new NormalTask("Large scorchberry", Region.W4_Alpha, List.of("scorchberries"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    ThornBeetle(new NormalTask("Thorn beetle", Region.W4_Alpha, List.of("thorn beetles"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W4_Thornbeetle), List.of(Warp.W4_Alpha))),
    Worm(new NormalTask("Worm", Region.W4_Alpha, List.of("worms"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W4_Worm), List.of(Warp.W4_Root, Warp.W4_Alpha))),
    Driftwood(new NormalTask("Driftwood", Region.W4_Delta, List.of("driftwood"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Delta))),

    Bat(new NormalTask("Bat", Region.W4_Homestead, List.of("bats"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, List.of(Target.W4_Bat), List.of(Warp.W4_Spawn))),
    Rat(new NormalTask("Rat", Region.W4_Sewer, List.of("rats"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W4_Rat), List.of(Warp.W4_Sewer, Warp.W4_Spawn))),
    Rattus(new NormalTask("Rattus", Region.W4_Sewer, List.of("rattus"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W4_Rattus, Warp.W4_Spawn))),
    Frog(new NormalTask("Frog", Region.W4_Alpha, List.of("frogs"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W4_Frog), List.of(Warp.W4_Alpha))),
    Sniper(new NormalTask("Sniper", Region.W4_Beta, List.of("snipers"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, List.of(Target.W4_Sniper), List.of(Warp.W4_Beta))),
    AngryMiner(new NormalTask("Angry miner", Region.W4_Beta, List.of("angry miners"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W4_AngryMiner), List.of(Warp.W4_Beta))),
    Ravager(new NormalTask("Ravager", Region.W4_Beta, List.of("ravagers"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W4_Ravager), List.of(Warp.W4_Beta))),
    W4Elite(new NormalTask("Elite mobs in World #4", Region.W4_Homestead, List.of("mobs in world #4"), List.of(Constraint.Elite), TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W4_Bat, Target.W4_Rat, Target.W4_Frog, Target.W4_Sniper, Target.W4_AngryMiner, Target.W4_Ravager), List.of(Warp.W4_Alpha))),

    Grasshopper(new NormalTask("Grasshopper", Region.W4_Homestead, List.of("grasshoppers"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W4_Spawn))),
    AstromoldAndAstromite(new NormalTask("Astromold & Astromite", Region.W4_Homestead, List.of("astromold & astromites"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W4_Spawn))),
    Magmold(new NormalTask("Magmold", Region.W4_Homestead, List.of("magmold"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W4_Spawn))),
    Algae(new NormalTask("Algae", Region.W4_Alpha, List.of("algae"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W4_Alpha))),
    BoomShroom(new NormalTask("Boom shroom", Region.W4_Alpha, List.of("boom shrooms"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W4_Alpha))),
    Pearl(new NormalTask("Pearl", Region.W4_Delta, List.of("pearls"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W4_Delta))),

    Cat(new NormalTask("Cat", Region.W4_Sewer, List.of("cats"), null, TaskType.Fishing, DefaultWardrobe.CombatFishing, Tool.Spear, List.of(Target.W4_Cat), List.of(Warp.W4_Sewer, Warp.W4_Spawn))),
    Catfish(new NormalTask("Catfish", Region.W4_Sewer, List.of("catfish"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W4_Catfish), List.of(Warp.W4_Sewer, Warp.W4_Spawn))),
    Piranha(new NormalTask("Piranha", Region.W4_Alpha, List.of("piranhas"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W4_Piranha), List.of(Warp.W4_Alpha))),
    Clownfish(new NormalTask("Clownfish", Region.W4_Delta, List.of("clownfish"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W4_Clownfish), List.of(Warp.W4_Delta))),
    Cichild(new NormalTask("Cichild", Region.W4_Delta, List.of("cichlid"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W4_Cichlid), List.of(Warp.W4_Delta))),
    Parrotfish(new NormalTask("Parrotfish", Region.W4_Delta, List.of("parrotfish"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W4_Parrotfish), List.of(Warp.W4_Delta))),
    RedEmperor(new NormalTask("Red emperor", Region.W4_Delta, List.of("red emperor"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W4_RedEmperor), List.of(Warp.W4_Delta))),
    Pufferfish(new NormalTask("Pufferfish", Region.W4_Delta, List.of("pufferfish"), null, TaskType.Fishing, DefaultWardrobe.CombatFishing, Tool.Spear, List.of(Target.W4_Pufferfish), List.of(Warp.W4_Delta))),

    SewerChest(new NormalTask("Sewer chest", Region.W4_Sewer, List.of("sewer chests"), null, TaskType.Misc, null, Tool.Spear, List.of(Target.W4_SewerChest), List.of(Warp.W4_Sewer, Warp.W4_Spawn))),

    // World Nightmare 1
    Quartz(new NormalTask("Quartz", Region.WN1_Overworld, List.of("quartz"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.N1_Quartz), List.of(Warp.WN1_Quartz, Warp.WN1_Spawn))),
    ShinyQuartz(new NormalTask("Shiny quartz", Region.WN1_Overworld, List.of("quartz"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.N1_Quartz), List.of(Warp.WN1_Quartz, Warp.WN1_Spawn))),
    QuartzWithShatterpoint(new NormalTask("Quartz with shatterpoint", Region.WN1_Overworld, List.of("quartz"), List.of(Constraint.Shatterpoint), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.N1_Quartz), List.of(Warp.WN1_Quartz, Warp.WN1_Spawn))),
    Lapis(new NormalTask("Lapis", Region.WN1_Overworld, List.of("lapis"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.N1_Lapis), List.of(Warp.WN1_Lapis, Warp.WN1_Quartz, Warp.WN1_Spawn))),
    ShinyLapis(new NormalTask("Shiny lapis", Region.WN1_Overworld, List.of("lapis"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.N1_Lapis), List.of(Warp.WN1_Lapis, Warp.WN1_Quartz, Warp.WN1_Spawn))),
    LapisWithShatterpoint(new NormalTask("Lapis with shatterpoint", Region.WN1_Overworld, List.of("lapis"), List.of(Constraint.Shatterpoint), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.N1_Lapis), List.of(Warp.WN1_Lapis, Warp.WN1_Quartz, Warp.WN1_Spawn))),
    NetherGold(new NormalTask("Nether gold", Region.WN1_Overworld, List.of("nether gold"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.N1_NetherGold), List.of(Warp.WN1_NetherGold, Warp.WN1_Lapis, Warp.WN1_Quartz, Warp.WN1_Spawn))),
    ShinyNetherGold(new NormalTask("Shiny nether gold", Region.WN1_Overworld, List.of("nether gold"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.N1_NetherGold), List.of(Warp.WN1_NetherGold, Warp.WN1_Lapis, Warp.WN1_Quartz, Warp.WN1_Spawn))),
    NetherGoldWithShatterpoint(new NormalTask("Nether gold with shatterpoint", Region.WN1_Overworld, List.of("nether gold"), List.of(Constraint.Shatterpoint), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.N1_NetherGold), List.of(Warp.WN1_NetherGold, Warp.WN1_Lapis, Warp.WN1_Quartz, Warp.WN1_Spawn))),
    AncientDebris(new NormalTask("Ancient debris", Region.WN1_Overworld, List.of("ancient debris"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.N1_AncientDebris), List.of(Warp.WN1_Netherite, Warp.WN1_NetherGold, Warp.WN1_Lapis, Warp.WN1_Quartz, Warp.WN1_Spawn))),
    ShinyAncientDebris(new NormalTask("Shiny ancient debris", Region.WN1_Overworld, List.of("ancient debris"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.N1_AncientDebris), List.of(Warp.WN1_Netherite, Warp.WN1_NetherGold, Warp.WN1_Lapis, Warp.WN1_Quartz, Warp.WN1_Spawn))),
    AncientDebrisWithShatterpoint(new NormalTask("Ancient debris with shatterpoint", Region.WN1_Overworld, List.of("ancient debris"), List.of(Constraint.Shatterpoint), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.N1_AncientDebris), List.of(Warp.WN1_Netherite, Warp.WN1_NetherGold, Warp.WN1_Lapis, Warp.WN1_Quartz, Warp.WN1_Spawn))),

    Crimson(new NormalTask("Crimson", Region.WN1_Overworld, List.of("crimson"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.WN1_Spawn))),
    CrimsonWithJuggling(new NormalTask("Crimson with axe juggling", Region.WN1_Overworld, List.of("crimson"), List.of(Constraint.AxeJuggling), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.WN1_Spawn))),
    Toquoi(new NormalTask("Toquoi", Region.WN1_Overworld, List.of("toquoi"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.WN1_Spawn))),
    LargeToquoi(new NormalTask("Large toquoi", Region.WN1_Overworld, List.of("toquoi"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.WN1_Spawn))),
    Aqua(new NormalTask("Aqua", Region.WN1_Overworld, List.of("aqua"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),
    AquaWithJuggling(new NormalTask("Aqua with axe juggling", Region.WN1_Overworld, List.of("aqua"), List.of(Constraint.AxeJuggling), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),
    Cheruza(new NormalTask("Cheruza", Region.WN1_Overworld, List.of("cheruza"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),
    LargeCheruza(new NormalTask("Large cheruza", Region.WN1_Overworld, List.of("cheruza"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),
    Winkle(new NormalTask("Winkle", Region.WN1_Overworld, List.of("winkles"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.N1_Winkle), List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),

    Piglin(new NormalTask("Piglin", Region.WN1_Overworld, List.of("piglins"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.N1_Piglin), List.of(Warp.WN1_Spawn))),
    PiglinWithPeaShooter(new NormalTask("Piglin with ranged damage & pea shooter", Region.WN1_Overworld, List.of("piglins"), List.of(Constraint.PeaShooter), TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.N1_Piglin), List.of(Warp.WN1_Spawn))),
    PiglinWithDevilsGambit(new NormalTask("Piglin with davil's gambit", Region.WN1_Overworld, List.of("piglins"), List.of(Constraint.DevilsGambit), TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.N1_Piglin), List.of(Warp.WN1_Spawn))),
    Bamboodle(new NormalTask("Bamboodle", Region.WN1_Overworld, List.of("bamboodles"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, List.of(Target.N1_Bamboodle), List.of(Warp.WN1_Bamboodle, Warp.WN1_Zoglin, Warp.WN1_Decay, Warp.WN1_Spawn))),
    BamboodleWithPeaShooter(new NormalTask("Bamboodle with ranged damage & pea shooter", Region.WN1_Overworld, List.of("bamboodles"), List.of(Constraint.PeaShooter), TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, List.of(Target.N1_Bamboodle), List.of(Warp.WN1_Bamboodle, Warp.WN1_Zoglin, Warp.WN1_Decay, Warp.WN1_Spawn))),
    BamboodleWithDevilsGambit(new NormalTask("Bamboodle with davil's gambit", Region.WN1_Overworld, List.of("bamboodles"), List.of(Constraint.DevilsGambit), TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, List.of(Target.N1_Bamboodle), List.of(Warp.WN1_Bamboodle, Warp.WN1_Zoglin, Warp.WN1_Decay, Warp.WN1_Spawn))),
    Firefox(new NormalTask("Firefox", Region.WN1_Overworld, List.of("firefoxes"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, List.of(Target.N1_Firefox), List.of(Warp.WN1_Firefox, Warp.WN1_Spawn))),
    FirefoxWithPeaShooter(new NormalTask("Firefox with ranged damage & pea shooter", Region.WN1_Overworld, List.of("firefoxes"), List.of(Constraint.PeaShooter), TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, List.of(Target.N1_Firefox), List.of(Warp.WN1_Firefox, Warp.WN1_Spawn))),
    FirefoxWithDevilsGambit(new NormalTask("Firefox with davil's gambit", Region.WN1_Overworld, List.of("firefoxes"), List.of(Constraint.DevilsGambit), TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, List.of(Target.N1_Firefox), List.of(Warp.WN1_Firefox, Warp.WN1_Spawn))),
    Marshmallow(new NormalTask("Marshmallow", Region.WN1_Overworld, List.of("marshmallows"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, List.of(Target.N1_Marshmallow), List.of(Warp.WN1_Zoglin, Warp.WN1_Decay, Warp.WN1_Spawn))),
    N1Elite(new NormalTask("Elite mobs in Nightmare #1", Region.WN1_Overworld, List.of("mobs in nightmare #1"), List.of(Constraint.Elite), TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.N1_Piglin, Target.N1_Bamboodle, Target.N1_Firefox, Target.N1_Marshmallow), List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),
    Zoglin(new NormalTask("Zoglin", Region.WN1_Overworld, List.of("zoglin"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.WN1_Zoglin, Warp.WN1_Bamboodle, Warp.WN1_Decay, Warp.WN1_Spawn))),

    Decay(new NormalTask("Decay", Region.WN1_Overworld, List.of("decay"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),
    DecayWithLandscaping(new NormalTask("Decay with %s+ landscaping", Region.WN1_Overworld, List.of("decay"), List.of(Constraint.Landscaping), TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),
    IcebergLettuceOrTorchFlower(new NormalTask("Iceberg lettuce or torchflower", Region.WN1_Overworld, List.of("torchflowers or iceberg lettuce"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.WN1_Torchflower, Warp.WN1_Spawn))),
    IcebergLettuceOrTorchFlowerWithLandscaping(new NormalTask("Iceberg lettuce or torchflower with %s+ landscaping", Region.WN1_Overworld, List.of("torchflowers or iceberg lettuce"), List.of(Constraint.Landscaping), TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.WN1_Torchflower, Warp.WN1_Spawn))),
    Mandrake(new NormalTask("Mandrake", Region.WN1_Overworld, List.of("mandrakes"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.WN1_Mandrake, Warp.WN1_Spawn))),
    MandrakeWithLandscaping(new NormalTask("Mandrake with %s+ landscaping", Region.WN1_Overworld, List.of("mandrakes"), List.of(Constraint.Landscaping), TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.WN1_Mandrake, Warp.WN1_Spawn))),
    Splinterseed(new NormalTask("Splinterseed", Region.WN1_Overworld, List.of("splinterseed"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.WN1_Splinterseed, Warp.WN1_Stable, Warp.WN1_Spawn))),
    SplinterseedWithLandscaping(new NormalTask("Splinterseed with %s+ landscaping", Region.WN1_Overworld, List.of("splinterseed"), List.of(Constraint.Landscaping), TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.WN1_Splinterseed, Warp.WN1_Stable, Warp.WN1_Spawn))),

    Charred(new NormalTask("Charred", Region.WN1_Overworld, List.of("charred"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.N1_Charred), List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),
    SmokedSalmon(new NormalTask("Smoked salmon", Region.WN1_Overworld, List.of("smoked salmon"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.N1_SmokedSalmon), List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),
    Strider(new NormalTask("Strider", Region.WN1_Overworld, List.of("striders"), null, TaskType.Fishing, DefaultWardrobe.CombatFishing, Tool.Spear, List.of(Target.N1_Strider), List.of(Warp.WN1_Spawn))),

    EarnTicketDice(new GamingTask("Ticker for pairdice", Region.WN1_Overworld, List.of("pairadice"), GameKind.Pairdice, List.of(Constraint.Ticket), List.of(Warp.WN1_Spawn))),
    EarnPointDice(new GamingTask("Score in pairdice", Region.WN1_Overworld, List.of("pairadice"), GameKind.Pairdice, null /* TODO: Missing constraint name*/, List.of(Warp.WN1_Spawn))),
    ShiverMoney(new NormalTask("Sell for shiver", Region.WN1_Overworld, List.of("shiver from selling items"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.WN1_Quartz, Warp.WN1_Spawn))),
    ;

    private final ITask task;

    Task(ITask task) {
        this.task = task;
    }

    private static final Map<String, Map<Set<Constraint>, Task>> tasks =
            Collections.unmodifiableMap(Arrays.stream(Task.values())
                    .flatMap(t -> t.task.names().stream().map(name -> Map.entry(name.toLowerCase(), t)))
                    .collect(Collectors.groupingBy(
                            Map.Entry::getKey,
                            Collectors.toMap(
                                    e -> {
                                        List<Constraint> c = e.getValue().task.constraints();
                                        return c == null ? Collections.emptySet() : new HashSet<>(c);
                                    },
                                    Map.Entry::getValue,
                                    (v1, v2) -> {
                                        throw new IllegalStateException(String.format(
                                                "Duplicate task detected for name/constraints: %s vs %s", v1, v2));
                                    }
                            )
                    )));

    public static Task tryGetTask(String target, Set<Constraint> constraints) {
        if (target == null) return null;

        var constraintMap = tasks.get(target.toLowerCase());
        if (constraintMap != null) {
            return constraintMap.get(constraints == null ? Collections.emptySet() : constraints);
        }
        return null;
    }

    public ITask getDescriptor(){
        return task;
    }
}

