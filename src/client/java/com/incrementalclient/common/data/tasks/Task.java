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
    W1_Coal(new NormalTask("Coal", Region.W1_Overworld, List.of("coal", "coal ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Coal), List.of(Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    W1_ShinyCoal(new NormalTask("Shiny coal", Region.W1_Overworld, List.of("coal", "coal ore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Coal), List.of(Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    W1_Iron(new NormalTask("Iron", Region.W1_Overworld, List.of("iron", "iron ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Iron), List.of(Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    W1_ShinyIron(new NormalTask("Shiny iron", Region.W1_Overworld, List.of("iron", "iron ore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Iron), List.of(Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    W1_Copper(new NormalTask("Copper", Region.W1_Overworld, List.of("copper", "copper ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Copper), List.of(Warp.W1_Copper, Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    W1_ShinyCopper(new NormalTask("Shiny copper", Region.W1_Overworld, List.of("copper", "copper ore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Copper), List.of(Warp.W1_Copper, Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    W1_Gold(new NormalTask("Gold", Region.W1_Overworld, List.of("gold", "gold ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Gold), List.of(Warp.W1_Gold, Warp.W1_Copper, Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    W1_ShinyGold(new NormalTask("Shiny gold", Region.W1_Overworld, List.of("gold", "gold ore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Gold), List.of(Warp.W1_Gold, Warp.W1_Copper, Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    W1_Redstone(new NormalTask("Redstone", Region.W1_Overworld, List.of("redstone", "redstone ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Redstone), List.of(Warp.W1_RedStone, Warp.W1_Gold, Warp.W1_Copper, Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    W1_ShinyRedstone(new NormalTask("Shiny redstone", Region.W1_Overworld, List.of("redstone", "redstone ore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Redstone), List.of(Warp.W1_RedStone, Warp.W1_Gold, Warp.W1_Copper, Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),

    W1_Applewood(new NormalTask("Applewood", Region.W1_Overworld, List.of("applewood"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W1_AppleTree), List.of(Warp.W1_Spawn))),
    W1_Apple(new NormalTask("Apple", Region.W1_Overworld, List.of("apple"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W1_AppleTree), List.of(Warp.W1_Spawn))),
    W1_LargeApple(new NormalTask("Large apple", Region.W1_Overworld, List.of("apples"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W1_AppleTree), List.of(Warp.W1_Spawn))),
    W1_Palm(new NormalTask("", Region.W1_Overworld, List.of("palm"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W1_PalmTree), List.of(Warp.W1_Crab, Warp.W1_Spawn))),
    W1_Coconut(new NormalTask("Coconut", Region.W1_Overworld, List.of("coconuts"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W1_PalmTree), List.of(Warp.W1_Crab, Warp.W1_Spawn))),
    W1_LargeCoconut(new NormalTask("Large coconut", Region.W1_Overworld, List.of("coconuts"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W1_PalmTree), List.of(Warp.W1_Crab, Warp.W1_Spawn))),
    W1_Ladybug(new NormalTask("Ladybug", Region.W1_Overworld, List.of("ladybugs"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W1_Ladybug), List.of(Warp.W1_Beetroot, Warp.W1_Spawn))),

    W1_ScaredHog(new NormalTask("Scared hog", Region.W1_Overworld, List.of("scared hogs"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W1_ScaredHog), List.of(Warp.W1_Spawn))),
    W1_Goat(new NormalTask("Goat", Region.W1_Overworld, List.of("mountain goats"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W1_Goat), List.of(Warp.W1_Spawn))),
    W1_WildHog(new NormalTask("Wild hog", Region.W1_Overworld, List.of("wild boars"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W1_WildBoar), List.of(Warp.W1_Hoglin, Warp.W1_Spawn))),
    W1_Hoglin(new NormalTask("Hoglin", Region.W1_Overworld, List.of("hoglin"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W1_Hoglin), List.of(Warp.W1_Hoglin, Warp.W1_Spawn))),
    W1_Elite(new NormalTask("Elite mobs in World #1", Region.W1_Overworld, List.of("mobs in world #1"), List.of(Constraint.Elite), TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W1_ScaredHog, Target.W1_Goat, Target.W1_WildBoar), List.of(Warp.W1_Spawn))),

    W1_Wheat(new NormalTask("Wheat", Region.W1_Overworld, List.of("wheat"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Wheat), List.of(Warp.W1_Wheat, Warp.W1_Spawn))),
    W1_Carrot(new NormalTask("Carrot", Region.W1_Overworld, List.of("carrots"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Carrot), List.of(Warp.W1_Carrot, Warp.W1_Spawn))),
    W1_Potato(new NormalTask("Potato", Region.W1_Overworld, List.of("potatoes"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Potato), List.of(Warp.W1_Potato, Warp.W1_Spawn))),
    W1_Beetroot(new NormalTask("Beetroot", Region.W1_Overworld, List.of("beetroot"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Beetroot), List.of(Warp.W1_Beetroot, Warp.W1_Spawn))),
    W1_Honeycomb(new NormalTask("Honeycomb", Region.W1_Overworld, List.of("honeycomb"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Honeycomb), List.of(Warp.W1_Spawn))),
    W1_Crops(new NormalTask("Crops", Region.W1_Overworld, List.of("crops"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Wheat, Target.W1_Carrot, Target.W1_Potato, Target.W1_Beetroot), List.of(Warp.W1_Carrot, Warp.W1_Spawn))),

    W1_Riverfish(new NormalTask("Riverfish", Region.W1_Overworld, List.of("riverfish"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W1_Riverfish), List.of(Warp.W1_Carrot, Warp.W1_Spawn))),
    // TODO: Separate colored fish to each its own task
    W1_ColoredRiverfish(new NormalTask("(TODO) Colored Riverfish", Region.W1_Overworld, List.of("riverfish"), List.of(Constraint.Colored), TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W1_Riverfish), List.of(Warp.W1_Carrot, Warp.W1_Spawn))),
    W1_ConsecutiveFish(new NormalTask("2 fish without missing", Region.W1_Overworld, List.of("fish"), List.of(Constraint.Consecutive), TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W1_Riverfish), List.of(Warp.W1_Carrot, Warp.W1_Spawn))),
    W1_Crab(new NormalTask("Crab", Region.W1_Crab, List.of("crabs"), null, TaskType.Fishing, DefaultWardrobe.CombatFishing, Tool.Spear, List.of(Target.W1_Crab), List.of(Warp.W1_Crab, Warp.W1_Spawn))),
    W1_HermitCrab(new NormalTask("Hermit crab", Region.W1_Crab, List.of("hermit crabs"), null, TaskType.Fishing, DefaultWardrobe.CombatFishing, Tool.Spear, List.of(Target.W1_HermitCrab), List.of(Warp.W1_Crab, Warp.W1_Spawn))),

    W1_PlayCoinflip(new GamingTask("Play coinflip", List.of("coinflip"), GameKind.Rps, List.of(Constraint.Games), List.of(Warp.W1_Spawn))),
    W1_PlayRps(new GamingTask("Play rps", List.of("rps"), GameKind.Rps, List.of(Constraint.Games), List.of(Warp.W1_Spawn))),
    W1_EarnScorePixelpop(new GamingTask("Score in Pixelpop", List.of("pixel pop"), GameKind.Pixelpop, List.of(Constraint.Score), List.of(Warp.W1_Spawn))),
    W1_EarnTicketPixelpop(new GamingTask("Ticket for Pixelpop", List.of("rps"), GameKind.Pixelpop, List.of(Constraint.Ticket), List.of(Warp.W1_Spawn))),

    W1_EarnGold(new NormalTask("Sell for gold", Region.W1_Overworld, List.of("gold from selling items"), null, TaskType.Misc, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Carrot), List.of(Warp.W1_Carrot, Warp.W1_Spawn))),
    W1_CleanGarbage(new NormalTask("Clean garbage", Region.W1_Overworld, List.of("garbage cans"), null, TaskType.Misc, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W1_GarbageCan), List.of(Warp.W1_Spawn))),

    // World 2
    W2_Crimsonite(new NormalTask("Crimsonite", Region.W2_Lush, List.of("crimsonite", "crimsonite ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W2_Lush, Warp.W2_Spawn))),
    W2_ShinyCrimsonite(new NormalTask("Shiny crimsonite", Region.W2_Lush, List.of("crimsonite", "crimsonite ore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W2_Lush, Warp.W2_Spawn))),
    W2_Verdelith(new NormalTask("Verdelith", Region.W2_Veil, List.of("verdelith", "verdelith ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_ShinyVerdelith(new NormalTask("Shiny verdelith", Region.W2_Veil, List.of("verdelith", "verdelith ore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_Azuregem(new NormalTask("Azuregem", Region.W2_Infernal, List.of("azuregem", "azuregem ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_ShinyAzuregem(new NormalTask("Shiny azuregem", Region.W2_Infernal, List.of("azuregem", "azuregem ore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_Aurorium(new NormalTask("Aurorium", Region.W2_Abyss, List.of("aurorium", "aurorium ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W2_Abyss, Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_ShinyAurorium(new NormalTask("Shiny aurorium", Region.W2_Abyss, List.of("aurorium", "aurorium ore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W2_Abyss, Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),

    W2_Bonsai(new NormalTask("Bonsai", Region.W1_Overworld, List.of("bonsai"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W2_Spawn))),
    W2_Pomegranate(new NormalTask("Pomegranate", Region.W1_Overworld, List.of("pomegranates"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W2_Spawn))),
    W2_LargePomegranate(new NormalTask("Large pomegranate", Region.W1_Overworld, List.of("pomegranates"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W2_Spawn))),
    W2_Pine(new NormalTask("Pine", Region.W1_Overworld, List.of("pine"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W2_Shimmer, Warp.W2_Spawn))),
    W2_Pinecone(new NormalTask("Pinecone", Region.W1_Overworld, List.of("pinecones"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W2_Shimmer, Warp.W2_Spawn))),
    W2_LargePinecone(new NormalTask("Large pinecone", Region.W1_Overworld, List.of("pinecones"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W2_Shimmer, Warp.W2_Spawn))),
    W2_Deadwood(new NormalTask("Deadwood", Region.W1_Overworld, List.of("deadwood"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W2_Shimmer, Warp.W2_Spawn))),
    W2_Zephyr(new NormalTask("Zephyr", Region.W2_Abyss, List.of("zephyr"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W2_Abyss, Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_SkyBeetle(new NormalTask("Sky Beetle", Region.W1_Overworld, List.of("skybeetles"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W2_Spawn))),

    W2_Panda(new NormalTask("Panda", Region.W1_Overworld, List.of("pandas"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W2_Sky, Warp.W2_Spawn))),
    W2_Sniffer(new NormalTask("Sniffer", Region.W1_Overworld, List.of("sniffers"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W2_Forge, Warp.W2_Spawn))),
    W2_Lurker(new NormalTask("Lurker", Region.W2_Lush, List.of("lurkers"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W2_Lush, Warp.W2_Spawn))),
    W2_CaveCrawler(new NormalTask("Cave crawler", Region.W2_Lush, List.of("cave crawlers"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.W2_Lush, Warp.W2_Spawn))),
    W2_PoisonSlime(new NormalTask("Poison slime", Region.W2_Lush, List.of("poison slimes"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W2_Lush, Warp.W2_Spawn))),
    W2_Spotter(new NormalTask("Spotter", Region.W2_Veil, List.of("spotters"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_Verdemite(new NormalTask("Verdemite", Region.W2_Veil, List.of("verdemites"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Pickaxe, null, List.of(Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_Endermen(new NormalTask("Endermen", Region.W2_Veil, List.of("endermen"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_Ghast(new NormalTask("Ghast", Region.W2_Infernal, List.of("ghasts"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_GhastSoul(new NormalTask("Ghast Soul", Region.W2_Infernal, List.of("ghast souls"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_Blaze(new NormalTask("Blaze", Region.W2_Infernal, List.of("blazes"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_Nrub(new NormalTask("Nrub", Region.W2_Infernal, List.of("nrubs"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_InfernalImp(new NormalTask("Infernal imp", Region.W2_Infernal, List.of("infernal imps"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_Wick(new NormalTask("Wick", Region.W2_Abyss, List.of("wicks"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W2_Abyss, Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_GlowSquid(new NormalTask("Glow squid", Region.W2_Abyss, List.of("glow squids"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.W2_Abyss, Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_Slinker(new NormalTask("Slinker", Region.W2_Abyss, List.of("slinkers"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W2_Abyss, Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_Rodrick(new NormalTask("Rodrick", Region.W1_Overworld, List.of("rodrick"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W2_Rodrick, Warp.W2_Garlic, Warp.W2_Spawn))),
    W2_SkyBeetleQueen(new NormalTask("Sky Beetle Queen", Region.W1_Overworld, List.of("sky beetle queen"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.W2_Sky, Warp.W2_Shimmer, Warp.W2_Spawn))),
    W2_W2Elite(new NormalTask("Elite mobs in World #2", Region.W1_Overworld, List.of("mobs in world #2"), List.of(Constraint.Elite), TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),

    W2_Shimmer(new NormalTask("Shimmer", Region.W1_Overworld, List.of("shimmer"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W2_Shimmer, Warp.W2_Spawn))),
    W2_Garlic(new NormalTask("Garlic", Region.W1_Overworld, List.of("garlic"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W2_Garlic, Warp.W2_Spawn))),
    W2_Corn(new NormalTask("Corn", Region.W1_Overworld, List.of("corn"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W2_Corn, Warp.W2_Spawn))),
    W2_Shy(new NormalTask("Shy", Region.W2_Veil, List.of("shy"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_LavaFruit(new NormalTask("Lava fruit", Region.W2_Infernal, List.of("lavafruit"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_Twine(new NormalTask("Twine", Region.W2_Abyss, List.of("twine"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W2_Abyss, Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_AdvancedCrops(new NormalTask("Advanced Crops", Region.W1_Overworld, List.of("advanced crops"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W2_Garlic, Warp.W2_Spawn))),

    W2_Salmon(new NormalTask("Salmon", Region.W1_Overworld, List.of("salmon"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W2_Spawn))),
    W2_Koi(new NormalTask("Koi", Region.W1_Overworld, List.of("koi"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W2_Spawn))),
    W2_Axolotl(new NormalTask("Axolotl", Region.W1_Overworld, List.of("axolotl"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W2_Spawn))),
    W2_MagmaFish(new NormalTask("Magma fish", Region.W2_Infernal, List.of("magmafish"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_MoltenJellyfish(new NormalTask("", Region.W2_Infernal, List.of("molten jellyfish"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_Bubbler(new NormalTask("Bubbler", Region.W2_Infernal, List.of("bubbler"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_AbyssalCrab(new NormalTask("Abyssal crab", Region.W2_Abyss, List.of("abyssal crabs"), null, TaskType.Fishing, DefaultWardrobe.CombatFishing, Tool.Spear, null, List.of(Warp.W2_Abyss, Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),

    W2_PlayBlackjack(new GamingTask("Play 21", List.of("21"), GameKind.Blackjack, List.of(Constraint.Games), List.of(Warp.W2_Spawn))),
    W2_SilverMoney(new NormalTask("Sell for silver", Region.W1_Overworld, List.of("silver from selling items"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_AbyssLamp(new NormalTask("Repair Lamppost", Region.W2_Abyss, List.of("lampposts"), null, TaskType.Misc, null, Tool.Spear, null, List.of(Warp.W2_Abyss, Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),

    // World 3
    W3_Brightstone(new NormalTask("Brightstone", Region.W3_Mine, List.of("brightstone"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W3_Mines, Warp.W3_Spawn))),
    W3_ShinyBrightstone(new NormalTask("Shiny brightstone", Region.W3_Mine, List.of("brightstone"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W3_Mines, Warp.W3_Spawn))),
    W3_Diamond(new NormalTask("Diamond", Region.W3_Mine, List.of("diamonds"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W3_Mines, Warp.W3_Spawn))),
    W3_ShinyDiamond(new NormalTask("Shiny diamond", Region.W3_Mine, List.of("diamonds"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W3_Mines, Warp.W3_Spawn))),
    W3_Emerald(new NormalTask("Emerald", Region.W3_Mine, List.of("emeralds"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W3_Mines, Warp.W3_Spawn))),
    W3_ShinyEmerald(new NormalTask("Shiny emerald", Region.W3_Mine, List.of("emeralds"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W3_Mines, Warp.W3_Spawn))),

    W3_Gorespore(new NormalTask("Gorespore", Region.W3_Sty, List.of("gorespore"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Sty, Warp.W3_Spawn))),
    W3_GoresporeSpore(new NormalTask("Gorespore spore", Region.W3_Sty, List.of("gorespore spores"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Sty, Warp.W3_Spawn))),
    W3_LargeGoresporeSpore(new NormalTask("Large gorespore spore", Region.W3_Sty, List.of("gorespore spores"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Sty, Warp.W3_Spawn))),
    W3_DuneDweller(new NormalTask("Dunes dweller", Region.W3_Beach, List.of("dune dweller"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Beach, Warp.W3_Spawn))),
    W3_DuneDwellerSpore(new NormalTask("Dune dweller spore", Region.W3_Beach, List.of("dune dweller spores"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Beach, Warp.W3_Spawn))),
    W3_LargeDuneDwellerSpore(new NormalTask("Large dune dweller spore", Region.W3_Beach, List.of("dune dweller spores"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Beach, Warp.W3_Spawn))),
    W3_HoneyShroom(new NormalTask("Honey shrooms", Region.W3_Topside, List.of("honey shrooms"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Topside, Warp.W3_Spawn))),
    W3_HoneySpore(new NormalTask("Honey spore", Region.W3_Topside, List.of("honey shroom spores"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Topside, Warp.W3_Spawn))),
    W3_LargeHoneySpore(new NormalTask("Large honey spore", Region.W3_Topside, List.of("honey shroom spores"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Topside, Warp.W3_Spawn))),
    W3_DreamShroom(new NormalTask("Dream shroom", Region.W3_Overworld, List.of("dream shrooms"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Spawn))),
    W3_DreamSpore(new NormalTask("Dream spore", Region.W3_Overworld, List.of("dream spores"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Spawn))),
    W3_LargeDreamSpore(new NormalTask("Large dream spore", Region.W3_Overworld, List.of("dream spores"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Spawn))),
    W3_Barky(new NormalTask("Barky", Region.W3_Canine, List.of("barky"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Canine, Warp.W3_Spawn))),
    W3_Pinepoodle(new NormalTask("Pinepoodle", Region.W3_Canine, List.of("pinepoodles"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Canine, Warp.W3_Spawn))),
    W3_LargePinepoodle(new NormalTask("Large pinepoodle", Region.W3_Canine, List.of("pinepoodles"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Canine, Warp.W3_Spawn))),
    W3_Capsnapper(new NormalTask("Capsnapper", Region.W3_Overworld, List.of("capsnappers"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Spawn))),

    W3_Baconwing(new NormalTask("Baconwing", Region.W3_Sty, List.of("baconwings"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W3_Sty, Warp.W3_Spawn))),
    W3_Camel(new NormalTask("Camel", Region.W3_Beach, List.of("camels"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W3_Beach, Warp.W3_Spawn))),
    W3_Bee(new NormalTask("Bee", Region.W3_Topside, List.of("bees"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W3_Topside, Warp.W3_Spawn))),
    W3_RoyalGuard(new NormalTask("Royal Guard", Region.W3_Topside, List.of("royal guards"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W3_Topside, Warp.W3_Spawn))),
    W3_Breeze(new NormalTask("Breeze", Region.W3_Mine, List.of("breezes"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W3_Mines, Warp.W3_Spawn))),
    W3_DireWolf(new NormalTask("Dire wolf", Region.W3_Canine, List.of("dire wolves"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W3_Canine, Warp.W3_Spawn))),
    W3_Dreadhorn(new NormalTask("Dreadhorn", Region.W3_Overworld, List.of("dreadhorn"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W3_Dreadhorn, Warp.W3_Spawn))),
    W3_W3Elite(new NormalTask("Elite mobs in World #3", Region.W3_Overworld, List.of("mobs in world #3"), List.of(Constraint.Elite), TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W3_Topside, Warp.W3_Spawn))),

    W3_Oinky(new NormalTask("Oinky", Region.W3_Sty, List.of("oinky"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W3_Sty, Warp.W3_Spawn))),
    W3_Cattail(new NormalTask("Cattail", Region.W3_Beach, List.of("cattails"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W3_Beach, Warp.W3_Spawn))),
    W3_Gloom(new NormalTask("Gloom", Region.W3_Underside, List.of("gloom"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W3_Underside, Warp.W3_Spawn))),
    W3_CollieFlower(new NormalTask("Collie flower", Region.W3_Canine, List.of("collie-flowers"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W3_Canine, Warp.W3_Spawn))),

    W3_Bettafly(new NormalTask("Bettafly", Region.W3_Sty, List.of("bettafly"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W3_Sty, Warp.W3_Spawn))),
    W3_Soarfish(new NormalTask("Soarfish", Region.W3_Beach, List.of("soarfish"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W3_Beach, Warp.W3_Spawn))),
    W3_GoldfishRetriever(new NormalTask("Goldfish retriever", Region.W3_Canine, List.of("goldfish retrievers"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W3_Canine, Warp.W3_Spawn))),
    W3_Guardian(new NormalTask("Guardian", Region.W3_Underside, List.of("guardians"), null, TaskType.Fishing, DefaultWardrobe.CombatFishing, Tool.Spear, null, List.of(Warp.W3_Underside, Warp.W3_Spawn))),

    W3_FindMatchesMatcher(new GamingTask("Score in matcher", List.of("matcher"), GameKind.Matcher, List.of(Constraint.Matches), List.of(Warp.W3_Spawn))),
    W3_EarnTicketMatcher(new GamingTask("Ticket for matcher", List.of("matcher"), GameKind.Matcher, List.of(Constraint.Ticket), List.of(Warp.W3_Spawn))),

    // World 4
    W4_Cheddore(new NormalTask("Cheddore", Region.W4_Homestead, List.of("cheddore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W4_Spawn))),
    W4_ShinyCheddore(new NormalTask("Shiny cheddore", Region.W4_Homestead, List.of("cheddore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W4_Spawn))),
    W4_BlueCheese(new NormalTask("Blue cheese", Region.W4_Homestead, List.of("blue cheese"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W4_Spawn))),
    W4_ShinyBlueCheese(new NormalTask("Shiny blue cheese", Region.W4_Homestead, List.of("blue cheese"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W4_Spawn))),
    W4_Glowdust(new NormalTask("Glowdust", Region.W4_Beta, List.of("glowdust"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W4_Beta))),
    W4_ShinyGlowdust(new NormalTask("Shiny glowdust", Region.W4_Beta, List.of("glowdust"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W4_Beta))),
    W4_Slimecrust(new NormalTask("Slimecrust", Region.W4_Beta, List.of("slimecrust"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W4_Beta))),
    W4_ShinySlimecrust(new NormalTask("Shiny slimecrust", Region.W4_Beta, List.of("slimecrust"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W4_Beta))),
    W4_Voidshard(new NormalTask("Voidshard", Region.W4_Beta, List.of("voidshard"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W4_Beta))),
    W4_ShinyVoidshard(new NormalTask("Shiny voidshard", Region.W4_Beta, List.of("voidshard"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W4_Beta))),
    W4_Petrafin(new NormalTask("Petrafin", Region.W4_Delta, List.of("petrafin"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W4_Delta))),
    W4_ShinyPetrafin(new NormalTask("Shiny petrafin", Region.W4_Delta, List.of("petrafin"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W4_Delta))),

    W4_NestingWood(new NormalTask("Nesting wood", Region.W4_Homestead, List.of("nesting wood"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Spawn))),
    W4_Passionfruit(new NormalTask("Passionfruit", Region.W4_Homestead, List.of("passionfruit"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Spawn))),
    W4_LargePassionfruit(new NormalTask("Large passionfruit", Region.W4_Homestead, List.of("passionfruit"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Spawn))),
    W4_Cryoflora(new NormalTask("Cryoflora", Region.W4_Alpha, List.of("cryoflora"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    W4_Chillfruit(new NormalTask("Chillfruit", Region.W4_Alpha, List.of("chillfruit"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    W4_LargeChillfruit(new NormalTask("Large chillfruit", Region.W4_Alpha, List.of("chillfruit"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    W4_Sulphoroot(new NormalTask("Sulphoroot", Region.W4_Alpha, List.of("sulphoroot"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    W4_Jackfruit(new NormalTask("Jackfruit", Region.W4_Alpha, List.of("jackfruit"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    W4_LargeJackfruit(new NormalTask("Large jackfruit", Region.W4_Alpha, List.of("jackfruit"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    W4_Pyrospire(new NormalTask("Pyrospire", Region.W4_Alpha, List.of("pyrospire"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    W4_Scorchberry(new NormalTask("Scorchberry", Region.W4_Alpha, List.of("scorchberries"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    W4_LargeScorchberry(new NormalTask("Large scorchberry", Region.W4_Alpha, List.of("scorchberries"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    W4_ThornBeetle(new NormalTask("Thorn beetle", Region.W4_Alpha, List.of("thorn beetles"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    W4_Worm(new NormalTask("Worm", Region.W4_Alpha, List.of("worms"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Root, Warp.W4_Alpha))),
    W4_Driftwood(new NormalTask("Driftwood", Region.W4_Delta, List.of("driftwood"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Delta))),

    W4_Bat(new NormalTask("Bat", Region.W4_Homestead, List.of("bats"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.W4_Spawn))),
    W4_Rat(new NormalTask("Rat", Region.W4_Sewer, List.of("rats"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W4_Sewer, Warp.W4_Spawn))),
    W4_Rattus(new NormalTask("Rattus", Region.W4_Sewer, List.of("rattus"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W4_Rattus, Warp.W4_Spawn))),
    W4_Frog(new NormalTask("Frog", Region.W4_Alpha, List.of("frogs"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W4_Alpha))),
    W4_Sniper(new NormalTask("Sniper", Region.W4_Beta, List.of("snipers"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.W4_Beta))),
    W4_AngryMiner(new NormalTask("Angry miner", Region.W4_Beta, List.of("angry miners"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W4_Beta))),
    W4_Ravager(new NormalTask("Ravager", Region.W4_Beta, List.of("ravagers"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W4_Beta))),
    W4_Elite(new NormalTask("Elite mobs in World #4", Region.W4_Homestead, List.of("mobs in world #4"), List.of(Constraint.Elite), TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W4_Alpha))),

    W4_Grasshopper(new NormalTask("Grasshopper", Region.W4_Homestead, List.of("grasshoppers"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W4_Spawn))),
    W4_AstromoldAndAstromite(new NormalTask("Astromold & Astromite", Region.W4_Homestead, List.of("astromold & astromites"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W4_Spawn))),
    W4_Magmold(new NormalTask("Magmold", Region.W4_Homestead, List.of("magmold"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W4_Spawn))),
    W4_Algae(new NormalTask("Algae", Region.W4_Alpha, List.of("algae"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W4_Alpha))),
    W4_BoomShroom(new NormalTask("Boom shroom", Region.W4_Alpha, List.of("boom shrooms"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W4_Alpha))),
    W4_Pearl(new NormalTask("Pearl", Region.W4_Delta, List.of("pearls"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W4_Delta))),

    W4_Cat(new NormalTask("Cat", Region.W4_Sewer, List.of("cats"), null, TaskType.Fishing, DefaultWardrobe.CombatFishing, Tool.Spear, null, List.of(Warp.W4_Sewer, Warp.W4_Spawn))),
    W4_Catfish(new NormalTask("Catfish", Region.W4_Sewer, List.of("catfish"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W4_Sewer, Warp.W4_Spawn))),
    W4_Piranha(new NormalTask("Piranha", Region.W4_Alpha, List.of("piranhas"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W4_Alpha))),
    W4_Clownfish(new NormalTask("Clownfish", Region.W4_Delta, List.of("clownfish"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W4_Delta))),
    W4_Cichild(new NormalTask("Cichild", Region.W4_Delta, List.of("cichlid"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W4_Delta))),
    W4_Parrotfish(new NormalTask("Parrotfish", Region.W4_Delta, List.of("parrotfish"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W4_Delta))),
    W4_RedEmperor(new NormalTask("Red emperor", Region.W4_Delta, List.of("red emperor"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W4_Delta))),
    W4_Pufferfish(new NormalTask("Pufferfish", Region.W4_Delta, List.of("pufferfish"), null, TaskType.Fishing, DefaultWardrobe.CombatFishing, Tool.Spear, null, List.of(Warp.W4_Delta))),

    W4_SewerChest(new NormalTask("Sewer chest", Region.W4_Sewer, List.of("sewer chests"), null, TaskType.Misc, null, Tool.Spear, null, List.of(Warp.W4_Sewer, Warp.W4_Spawn))),

    // World Nightmare 1
    WN1_Quartz(new NormalTask("Quartz", Region.WN1_Overworld, List.of("quartz"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.WN1_Quartz, Warp.WN1_Spawn))),
    WN1_ShinyQuartz(new NormalTask("Shiny quartz", Region.WN1_Overworld, List.of("quartz"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.WN1_Quartz, Warp.WN1_Spawn))),
    WN1_QuartzWithShatterpoint(new NormalTask("Quartz with shatterpoint", Region.WN1_Overworld, List.of("quartz"), List.of(Constraint.Shatterpoint), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.WN1_Quartz, Warp.WN1_Spawn))),
    WN1_Lapis(new NormalTask("Lapis", Region.WN1_Overworld, List.of("lapis"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.WN1_Lapis, Warp.WN1_Quartz, Warp.WN1_Spawn))),
    WN1_ShinyLapis(new NormalTask("Shiny lapis", Region.WN1_Overworld, List.of("lapis"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.WN1_Lapis, Warp.WN1_Quartz, Warp.WN1_Spawn))),
    WN1_LapisWithShatterpoint(new NormalTask("Lapis with shatterpoint", Region.WN1_Overworld, List.of("lapis"), List.of(Constraint.Shatterpoint), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.WN1_Lapis, Warp.WN1_Quartz, Warp.WN1_Spawn))),
    WN1_NetherGold(new NormalTask("Nether gold", Region.WN1_Overworld, List.of("nether gold"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.WN1_NetherGold, Warp.WN1_Lapis, Warp.WN1_Quartz, Warp.WN1_Spawn))),
    WN1_ShinyNetherGold(new NormalTask("Shiny nether gold", Region.WN1_Overworld, List.of("nether gold"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.WN1_NetherGold, Warp.WN1_Lapis, Warp.WN1_Quartz, Warp.WN1_Spawn))),
    WN1_NetherGoldWithShatterpoint(new NormalTask("Nether gold with shatterpoint", Region.WN1_Overworld, List.of("nether gold"), List.of(Constraint.Shatterpoint), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.WN1_NetherGold, Warp.WN1_Lapis, Warp.WN1_Quartz, Warp.WN1_Spawn))),
    WN1_AncientDebris(new NormalTask("Ancient debris", Region.WN1_Overworld, List.of("ancient debris"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.WN1_Netherite, Warp.WN1_NetherGold, Warp.WN1_Lapis, Warp.WN1_Quartz, Warp.WN1_Spawn))),
    WN1_ShinyAncientDebris(new NormalTask("Shiny ancient debris", Region.WN1_Overworld, List.of("ancient debris"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.WN1_Netherite, Warp.WN1_NetherGold, Warp.WN1_Lapis, Warp.WN1_Quartz, Warp.WN1_Spawn))),
    WN1_AncientDebrisWithShatterpoint(new NormalTask("Ancient debris with shatterpoint", Region.WN1_Overworld, List.of("ancient debris"), List.of(Constraint.Shatterpoint), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.WN1_Netherite, Warp.WN1_NetherGold, Warp.WN1_Lapis, Warp.WN1_Quartz, Warp.WN1_Spawn))),

    WN1_Crimson(new NormalTask("Crimson", Region.WN1_Overworld, List.of("crimson"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.WN1_Spawn))),
    WN1_CrimsonWithJuggling(new NormalTask("Crimson with axe juggling", Region.WN1_Overworld, List.of("crimson"), List.of(Constraint.AxeJuggling), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.WN1_Spawn))),
    WN1_Toquoi(new NormalTask("Toquoi", Region.WN1_Overworld, List.of("toquoi"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.WN1_Spawn))),
    WN1_LargeToquoi(new NormalTask("Large toquoi", Region.WN1_Overworld, List.of("toquoi"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.WN1_Spawn))),
    WN1_Aqua(new NormalTask("Aqua", Region.WN1_Overworld, List.of("aqua"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),
    WN1_AquaWithJuggling(new NormalTask("Aqua with axe juggling", Region.WN1_Overworld, List.of("aqua"), List.of(Constraint.AxeJuggling), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),
    WN1_Cheruza(new NormalTask("Cheruza", Region.WN1_Overworld, List.of("cheruza"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),
    WN1_LargeCheruza(new NormalTask("Large cheruza", Region.WN1_Overworld, List.of("cheruza"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),
    WN1_Winkle(new NormalTask("Winkle", Region.WN1_Overworld, List.of("winkles"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),

    WN1_Piglin(new NormalTask("Piglin", Region.WN1_Overworld, List.of("piglins"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.WN1_Spawn))),
    WN1_PiglinWithPeaShooter(new NormalTask("Piglin with ranged damage & pea shooter", Region.WN1_Overworld, List.of("piglins"), List.of(Constraint.PeaShooter), TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.WN1_Spawn))),
    WN1_PiglinWithDevilsGambit(new NormalTask("Piglin with davil's gambit", Region.WN1_Overworld, List.of("piglins"), List.of(Constraint.DevilsGambit), TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.WN1_Spawn))),
    WN1_Bamboodle(new NormalTask("Bamboodle", Region.WN1_Overworld, List.of("bamboodles"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.WN1_Bamboodle, Warp.WN1_Zoglin, Warp.WN1_Decay, Warp.WN1_Spawn))),
    WN1_BamboodleWithPeaShooter(new NormalTask("Bamboodle with ranged damage & pea shooter", Region.WN1_Overworld, List.of("bamboodles"), List.of(Constraint.PeaShooter), TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.WN1_Bamboodle, Warp.WN1_Zoglin, Warp.WN1_Decay, Warp.WN1_Spawn))),
    WN1_BamboodleWithDevilsGambit(new NormalTask("Bamboodle with davil's gambit", Region.WN1_Overworld, List.of("bamboodles"), List.of(Constraint.DevilsGambit), TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.WN1_Bamboodle, Warp.WN1_Zoglin, Warp.WN1_Decay, Warp.WN1_Spawn))),
    WN1_Firefox(new NormalTask("Firefox", Region.WN1_Overworld, List.of("firefoxes"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.WN1_Firefox, Warp.WN1_Spawn))),
    WN1_FirefoxWithPeaShooter(new NormalTask("Firefox with ranged damage & pea shooter", Region.WN1_Overworld, List.of("firefoxes"), List.of(Constraint.PeaShooter), TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.WN1_Firefox, Warp.WN1_Spawn))),
    WN1_FirefoxWithDevilsGambit(new NormalTask("Firefox with davil's gambit", Region.WN1_Overworld, List.of("firefoxes"), List.of(Constraint.DevilsGambit), TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.WN1_Firefox, Warp.WN1_Spawn))),
    WN1_Marshmallow(new NormalTask("Marshmallow", Region.WN1_Overworld, List.of("marshmallows"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.WN1_Zoglin, Warp.WN1_Decay, Warp.WN1_Spawn))),
    WN1_N1Elite(new NormalTask("Elite mobs in Nightmare #1", Region.WN1_Overworld, List.of("mobs in nightmare #1"), List.of(Constraint.Elite), TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),
    WN1_Zoglin(new NormalTask("Zoglin", Region.WN1_Overworld, List.of("zoglin"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.WN1_Zoglin, Warp.WN1_Bamboodle, Warp.WN1_Decay, Warp.WN1_Spawn))),

    WN1_Decay(new NormalTask("Decay", Region.WN1_Overworld, List.of("decay"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),
    WN1_DecayWithLandscaping(new NormalTask("Decay with 4+ landscaping", Region.WN1_Overworld, List.of("decay"), List.of(Constraint.Landscaping), TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),
    WN1_IcebergLettuceOrTorchFlower(new NormalTask("Iceberg lettuce or torchflower", Region.WN1_Overworld, List.of("torchflowers or iceberg lettuce"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.WN1_Torchflower, Warp.WN1_Spawn))),
    WN1_IcebergLettuceOrTorchFlowerWithLandscaping(new NormalTask("Iceberg lettuce or torchflower with 4+ landscaping", Region.WN1_Overworld, List.of("torchflowers or iceberg lettuce"), List.of(Constraint.Landscaping), TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.WN1_Torchflower, Warp.WN1_Spawn))),
    WN1_Mandrake(new NormalTask("Mandrake", Region.WN1_Overworld, List.of("mandrakes"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.WN1_Mandrake, Warp.WN1_Spawn))),
    WN1_MandrakeWithLandscaping(new NormalTask("Mandrake with 4+ landscaping", Region.WN1_Overworld, List.of("mandrakes"), List.of(Constraint.Landscaping), TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.WN1_Mandrake, Warp.WN1_Spawn))),
    WN1_Splinterseed(new NormalTask("Splinterseed", Region.WN1_Overworld, List.of("splinterseed"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.WN1_Splinterseed, Warp.WN1_Stable, Warp.WN1_Spawn))),
    WN1_SplinterseedWithLandscaping(new NormalTask("Splinterseed with %s+ landscaping", Region.WN1_Overworld, List.of("splinterseed"), List.of(Constraint.Landscaping), TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.WN1_Splinterseed, Warp.WN1_Stable, Warp.WN1_Spawn))),

    WN1_Charred(new NormalTask("Charred", Region.WN1_Overworld, List.of("charred"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),
    WN1_SmokedSalmon(new NormalTask("Smoked salmon", Region.WN1_Overworld, List.of("smoked salmon"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),
    WN1_Strider(new NormalTask("Strider", Region.WN1_Overworld, List.of("striders"), null, TaskType.Fishing, DefaultWardrobe.CombatFishing, Tool.Spear, null, List.of(Warp.WN1_Spawn))),

    WN1_EarnTicketDice(new GamingTask("Ticker for pairdice", List.of("pairadice"), GameKind.Pairdice, List.of(Constraint.Ticket), List.of(Warp.WN1_Spawn))),
    WN1_EarnPointDice(new GamingTask("Score in pairdice", List.of("pairadice"), GameKind.Pairdice, null /* TODO: Missing constraint name*/, List.of(Warp.WN1_Spawn))),
    WN1_ShiverMoney(new NormalTask("Sell for shiver", Region.WN1_Overworld, List.of("shiver from selling items"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.WN1_Quartz, Warp.WN1_Spawn))),
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

