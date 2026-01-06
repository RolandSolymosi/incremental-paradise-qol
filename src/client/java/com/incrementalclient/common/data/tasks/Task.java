package com.incrementalclient.common.data.tasks;

import com.incrementalclient.common.data.DefaultWardrobe;
import com.incrementalclient.common.data.GameKind;
import com.incrementalclient.common.data.Tool;
import com.incrementalclient.common.data.Warp;
import com.incrementalclient.common.data.targets.Target;
import com.incrementalclient.common.data.tasks.abstractions.GamingTask;
import com.incrementalclient.common.data.tasks.abstractions.ITask;
import com.incrementalclient.common.data.tasks.abstractions.NormalTask;

import java.util.*;
import java.util.stream.Collectors;

public enum Task {
    // Global
    SellItems(new NormalTask(List.of("items"), null, TaskType.Misc, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Carrot), List.of(Warp.W1_Carrot, Warp.W1_Spawn))),

    // World 1
    W1_Coal(new NormalTask(List.of("coal", "coal ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Coal), List.of(Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    W1_ShinyCoal(new NormalTask(List.of("coal", "coal ore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Coal), List.of(Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    W1_Iron(new NormalTask(List.of("iron", "iron ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Iron), List.of(Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    W1_ShinyIron(new NormalTask(List.of("iron", "iron ore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Iron), List.of(Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    W1_Copper(new NormalTask(List.of("copper", "copper ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Copper), List.of(Warp.W1_Copper, Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    W1_ShinyCopper(new NormalTask(List.of("copper", "copper ore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Copper), List.of(Warp.W1_Copper, Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    W1_Gold(new NormalTask(List.of("gold", "gold ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Gold), List.of(Warp.W1_Gold, Warp.W1_Copper, Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    W1_ShinyGold(new NormalTask(List.of("gold", "gold ore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Gold), List.of(Warp.W1_Gold, Warp.W1_Copper, Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    W1_Redstone(new NormalTask(List.of("redstone", "redstone ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Redstone), List.of(Warp.W1_RedStone, Warp.W1_Gold, Warp.W1_Copper, Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    W1_ShinyRedstone(new NormalTask(List.of("redstone", "redstone ore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Redstone), List.of(Warp.W1_RedStone, Warp.W1_Gold, Warp.W1_Copper, Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),

    W1_Applewood(new NormalTask(List.of("applewood"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W1_AppleTree), List.of(Warp.W1_Spawn))),
    W1_Apple(new NormalTask(List.of("apple"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W1_AppleTree), List.of(Warp.W1_Spawn))),
    W1_LargeApple(new NormalTask(List.of("apples"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W1_AppleTree), List.of(Warp.W1_Spawn))),
    W1_Palm(new NormalTask(List.of("palm"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W1_PalmTree), List.of(Warp.W1_Crab, Warp.W1_Spawn))),
    W1_Coconut(new NormalTask(List.of("coconuts"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W1_PalmTree), List.of(Warp.W1_Crab, Warp.W1_Spawn))),
    W1_LargeCoconut(new NormalTask(List.of("coconuts"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W1_PalmTree), List.of(Warp.W1_Crab, Warp.W1_Spawn))),
    W1_Ladybug(new NormalTask(List.of("ladybugs"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W1_Ladybug), List.of(Warp.W1_Beetroot, Warp.W1_Spawn))),

    W1_ScaredHog(new NormalTask(List.of("scared hogs"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W1_ScaredHog), List.of(Warp.W1_Spawn))),
    W1_Goat(new NormalTask(List.of("mountain goats"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W1_Goat), List.of(Warp.W1_Spawn))),
    W1_WildHog(new NormalTask(List.of("wild boars"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W1_WildBoar), List.of(Warp.W1_Hoglin, Warp.W1_Spawn))),
    W1_Hoglin(new NormalTask(List.of("hoglin"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W1_Hoglin), List.of(Warp.W1_Hoglin, Warp.W1_Spawn))),
    W1_Elite(new NormalTask(List.of("mobs in world #1"), List.of(Constraint.Elite), TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W1_ScaredHog, Target.W1_Goat, Target.W1_WildBoar), List.of(Warp.W1_Spawn))),

    W1_Wheat(new NormalTask(List.of("wheat"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Wheat), List.of(Warp.W1_Wheat, Warp.W1_Spawn))),
    W1_Carrot(new NormalTask(List.of("carrots"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Carrot), List.of(Warp.W1_Carrot, Warp.W1_Spawn))),
    W1_Potato(new NormalTask(List.of("potatoes"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Potato), List.of(Warp.W1_Potato, Warp.W1_Spawn))),
    W1_Beetroot(new NormalTask(List.of("beetroot"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Beetroot), List.of(Warp.W1_Beetroot, Warp.W1_Spawn))),
    W1_HoneyComb(new NormalTask(List.of("honeycomb"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Honeycomb), List.of(Warp.W1_Spawn))),
    W1_Crops(new NormalTask(List.of("crops"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Wheat, Target.W1_Carrot, Target.W1_Potato, Target.W1_Beetroot), List.of(Warp.W1_Carrot, Warp.W1_Spawn))),

    W1_Riverfish(new NormalTask(List.of("riverfish"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W1_Riverfish), List.of(Warp.W1_Carrot, Warp.W1_Spawn))),
    // TODO: Separate colored fish to each its own task
    W1_ColoredRiverfish(new NormalTask(List.of("riverfish"), List.of(Constraint.Colored), TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W1_Riverfish), List.of(Warp.W1_Carrot, Warp.W1_Spawn))),
    W1_ConsecutiveFish(new NormalTask(List.of("fish"), List.of(Constraint.Consecutive), TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W1_Riverfish), List.of(Warp.W1_Carrot, Warp.W1_Spawn))),
    W1_Crab(new NormalTask(List.of("crabs"), null, TaskType.Fishing, DefaultWardrobe.CombatFishing, Tool.Spear, List.of(Target.W1_Crab), List.of(Warp.W1_Crab, Warp.W1_Spawn))),
    W1_HermitCrab(new NormalTask(List.of("hermit crabs"), null, TaskType.Fishing, DefaultWardrobe.CombatFishing, Tool.Spear, List.of(Target.W1_HermitCrab), List.of(Warp.W1_Crab, Warp.W1_Spawn))),

    W1_PlayCoinflip(new GamingTask(List.of("coinflip"), GameKind.Rps, List.of(Constraint.Games), List.of(Warp.W1_Spawn))),
    W1_PlayRps(new GamingTask(List.of("rps"), GameKind.Rps, List.of(Constraint.Games), List.of(Warp.W1_Spawn))),
    W1_EarnScorePixelpop(new GamingTask(List.of("pixel pop"), GameKind.Pixelpop, List.of(Constraint.Score), List.of(Warp.W1_Spawn))),
    W1_EarnTicketPixelpop(new GamingTask(List.of("rps"), GameKind.Pixelpop, List.of(Constraint.Ticket), List.of(Warp.W1_Spawn))),

    W1_EarnGold(new NormalTask(List.of("gold from selling items"), null, TaskType.Misc, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Carrot), List.of(Warp.W1_Carrot, Warp.W1_Spawn))),
    W1_CleanGarbage(new NormalTask(List.of("garbage cans"), null, TaskType.Misc, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W1_GarbageCan), List.of(Warp.W1_Spawn))),

    // World 2
    W2_Crimsonite(new NormalTask(List.of("crimsonite", "crimsonite ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W2_Lush, Warp.W2_Spawn))),
    W2_ShinyCrimsonite(new NormalTask(List.of("crimsonite", "crimsonite ore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W2_Lush, Warp.W2_Spawn))),
    W2_Verdelith(new NormalTask(List.of("verdelith", "verdelith ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_ShinyVerdelith(new NormalTask(List.of("verdelith", "verdelith ore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_Azuregem(new NormalTask(List.of("azuregem", "azuregem ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_ShinyAzuregem(new NormalTask(List.of("azuregem", "azuregem ore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_Aurorium(new NormalTask(List.of("aurorium", "aurorium ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W2_Abyss, Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_ShinyAurorium(new NormalTask(List.of("aurorium", "aurorium ore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W2_Abyss, Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),

    W2_Bonsai(new NormalTask(List.of("bonsai"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W2_Spawn))),
    W2_Pomegranate(new NormalTask(List.of("pomegranates"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W2_Spawn))),
    W2_LargePomegranate(new NormalTask(List.of("pomegranates"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W2_Spawn))),
    W2_Pine(new NormalTask(List.of("pine"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W2_Shimmer, Warp.W2_Spawn))),
    W2_Pinecone(new NormalTask(List.of("pinecones"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W2_Shimmer, Warp.W2_Spawn))),
    W2_LargePinecone(new NormalTask(List.of("pinecones"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W2_Shimmer, Warp.W2_Spawn))),
    W2_Deadwood(new NormalTask(List.of("deadwood"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W2_Shimmer, Warp.W2_Spawn))),
    W2_Zephyr(new NormalTask(List.of("zephyr"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W2_Abyss, Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_SkyBeetle(new NormalTask(List.of("skybeetles"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W2_Spawn))),

    W2_Panda(new NormalTask(List.of("pandas"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W2_Sky, Warp.W2_Spawn))),
    W2_Sniffer(new NormalTask(List.of("sniffers"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W2_Forge, Warp.W2_Spawn))),
    W2_Lurker(new NormalTask(List.of("lurkers"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W2_Lush, Warp.W2_Spawn))),
    W2_CaveCrawler(new NormalTask(List.of("cave crawlers"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.W2_Lush, Warp.W2_Spawn))),
    W2_PoisonSlime(new NormalTask(List.of("poison slimes"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W2_Lush, Warp.W2_Spawn))),
    W2_Spotter(new NormalTask(List.of("spotters"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_Verdemite(new NormalTask(List.of("verdemites"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Pickaxe, null, List.of(Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_Endermen(new NormalTask(List.of("endermen"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_Ghast(new NormalTask(List.of("ghasts"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_GhastSoul(new NormalTask(List.of("ghast souls"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_Blaze(new NormalTask(List.of("blazes"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_Nrub(new NormalTask(List.of("nrubs"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_InfernalImp(new NormalTask(List.of("infernal imps"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_Wick(new NormalTask(List.of("wicks"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W2_Abyss, Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_GlowSquid(new NormalTask(List.of("glow squids"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.W2_Abyss, Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_Slinker(new NormalTask(List.of("slinkers"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W2_Abyss, Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_Rodrick(new NormalTask(List.of("rodrick"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W2_Rodrick, Warp.W2_Garlic, Warp.W2_Spawn))),
    W2_SkyBeetleQueen(new NormalTask(List.of("sky beetle queen"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.W2_Sky, Warp.W2_Shimmer, Warp.W2_Spawn))),
    W2_W2Elite(new NormalTask(List.of("mobs in world #2"), List.of(Constraint.Elite), TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),

    W2_Shimmer(new NormalTask(List.of("shimmer"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W2_Shimmer, Warp.W2_Spawn))),
    W2_Garlic(new NormalTask(List.of("garlic"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W2_Garlic, Warp.W2_Spawn))),
    W2_Corn(new NormalTask(List.of("corn"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W2_Corn, Warp.W2_Spawn))),
    W2_Shy(new NormalTask(List.of("shy"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_LavaFruit(new NormalTask(List.of("lavafruit"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_Twine(new NormalTask(List.of("twine"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W2_Abyss, Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_AdvancedCrops(new NormalTask(List.of("advanced crops"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W2_Garlic, Warp.W2_Spawn))),

    W2_Salmon(new NormalTask(List.of("salmon"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W2_Spawn))),
    W2_Koi(new NormalTask(List.of("koi"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W2_Spawn))),
    W2_Axolotl(new NormalTask(List.of("axolotl"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W2_Spawn))),
    W2_MagmaFish(new NormalTask(List.of("magmafish"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_MoltenJellyfish(new NormalTask(List.of("molten jellyfish"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_Bubbler(new NormalTask(List.of("bubbler"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_AbyssalCrab(new NormalTask(List.of("abyssal crabs"), null, TaskType.Fishing, DefaultWardrobe.CombatFishing, Tool.Spear, null, List.of(Warp.W2_Abyss, Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),

    W2_Blackjack(new GamingTask(List.of("21"), GameKind.Blackjack, null, List.of(Warp.W2_Spawn))),
    W2_SilverMoney(new NormalTask(List.of("silver from selling items"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),
    W2_AbyssLamp(new NormalTask(List.of("lampposts"), null, TaskType.Misc, null, Tool.Spear, null, List.of(Warp.W2_Abyss, Warp.W2_Infernal, Warp.W2_Veil, Warp.W2_Lush, Warp.W2_Spawn))),

    // World 3
    W3_Brightstone(new NormalTask(List.of("brightstone"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W3_Mines, Warp.W3_Spawn))),
    W3_ShinyBrightstone(new NormalTask(List.of("brightstone"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W3_Mines, Warp.W3_Spawn))),
    W3_Diamond(new NormalTask(List.of("diamonds"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W3_Mines, Warp.W3_Spawn))),
    W3_ShinyDiamond(new NormalTask(List.of("diamonds"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W3_Mines, Warp.W3_Spawn))),
    W3_Emerald(new NormalTask(List.of("emeralds"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W3_Mines, Warp.W3_Spawn))),
    W3_ShinyEmerald(new NormalTask(List.of("emeralds"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W3_Mines, Warp.W3_Spawn))),

    W3_Gorespore(new NormalTask(List.of("gorespore"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Sty, Warp.W3_Spawn))),
    W3_GoresporeSpore(new NormalTask(List.of("gorespore spores"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Sty, Warp.W3_Spawn))),
    W3_LargeGoresporeSpore(new NormalTask(List.of("gorespore spores"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Sty, Warp.W3_Spawn))),
    W3_DuneDweller(new NormalTask(List.of("dune dweller"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Beach, Warp.W3_Spawn))),
    W3_DuneDwellerSpore(new NormalTask(List.of("dune dweller spores"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Beach, Warp.W3_Spawn))),
    W3_LargeDuneDwellerSpore(new NormalTask(List.of("dune dweller spores"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Beach, Warp.W3_Spawn))),
    W3_HoneyShroom(new NormalTask(List.of("honey shrooms"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Topside, Warp.W3_Spawn))),
    W3_HoneySpore(new NormalTask(List.of("honey shroom spores"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Topside, Warp.W3_Spawn))),
    W3_LargeHoneySpore(new NormalTask(List.of("honey shroom spores"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Topside, Warp.W3_Spawn))),
    W3_DreamShroom(new NormalTask(List.of("dream shrooms"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Spawn))),
    W3_DreamSpore(new NormalTask(List.of("dream spores"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Spawn))),
    W3_LargeDreamSpore(new NormalTask(List.of("dream spores"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Spawn))),
    W3_Barky(new NormalTask(List.of("barky"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Canine, Warp.W3_Spawn))),
    W3_Pinepoodle(new NormalTask(List.of("pinepoodles"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Canine, Warp.W3_Spawn))),
    W3_LargePinepoodle(new NormalTask(List.of("pinepoodles"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Canine, Warp.W3_Spawn))),
    W3_Capsnapper(new NormalTask(List.of("capsnappers"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W3_Spawn))),

    W3_Baconwing(new NormalTask(List.of("baconwings"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W3_Sty, Warp.W3_Spawn))),
    W3_Camel(new NormalTask(List.of("camels"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W3_Beach, Warp.W3_Spawn))),
    W3_Bee(new NormalTask(List.of("bees"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W3_Topside, Warp.W3_Spawn))),
    W3_RoyalGuard(new NormalTask(List.of("royal guards"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W3_Topside, Warp.W3_Spawn))),
    W3_Breeze(new NormalTask(List.of("breezes"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W3_Mines, Warp.W3_Spawn))),
    W3_DireWolf(new NormalTask(List.of("dire wolves"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W3_Canine, Warp.W3_Spawn))),
    W3_Dreadhorn(new NormalTask(List.of("dreadhorn"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W3_Dreadhorn, Warp.W3_Spawn))),
    W3_W3Elite(new NormalTask(List.of("mobs in world #3"), List.of(Constraint.Elite), TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W3_Topside, Warp.W3_Spawn))),

    W3_Oinky(new NormalTask(List.of("oinky"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W3_Sty, Warp.W3_Spawn))),
    W3_Cattail(new NormalTask(List.of("cattails"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W3_Beach, Warp.W3_Spawn))),
    W3_Gloom(new NormalTask(List.of("gloom"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W3_Underside, Warp.W3_Spawn))),
    W3_CollieFlower(new NormalTask(List.of("collie-flowers"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W3_Canine, Warp.W3_Spawn))),

    W3_GoldfishRetriever(new NormalTask(List.of("goldfish retrievers"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W3_Canine, Warp.W3_Spawn))),
    W3_Bettafly(new NormalTask(List.of("bettafly"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W3_Sty, Warp.W3_Spawn))),
    W3_Soarfish(new NormalTask(List.of("soarfish"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W3_Beach, Warp.W3_Spawn))),
    W3_Guardian(new NormalTask(List.of("guardians"), null, TaskType.Fishing, DefaultWardrobe.CombatFishing, Tool.Spear, null, List.of(Warp.W3_Underside, Warp.W3_Spawn))),

    W3_Matcher(new GamingTask(List.of("matcher"), GameKind.Matcher, null, List.of(Warp.W3_Spawn))),

    // World 4
    W4_Cheddore(new NormalTask(List.of("cheddore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W4_Spawn))),
    W4_ShinyCheddore(new NormalTask(List.of("cheddore"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W4_Spawn))),
    W4_BlueCheese(new NormalTask(List.of("blue cheese"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W4_Spawn))),
    W4_ShinyBlueCheese(new NormalTask(List.of("blue cheese"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W4_Spawn))),
    W4_Glowdust(new NormalTask(List.of("glowdust"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W4_Beta))),
    W4_ShinyGlowdust(new NormalTask(List.of("glowdust"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W4_Beta))),
    W4_Slimecrust(new NormalTask(List.of("slimecrust"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W4_Beta))),
    W4_ShinySlimecrust(new NormalTask(List.of("slimecrust"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W4_Beta))),
    W4_Voidshard(new NormalTask(List.of("voidshard"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W4_Beta))),
    W4_ShinyVoidshard(new NormalTask(List.of("voidshard"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W4_Beta))),
    W4_Petrafin(new NormalTask(List.of("petrafin"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W4_Delta))),
    W4_ShinyPetrafin(new NormalTask(List.of("petrafin"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.W4_Delta))),

    W4_NestingWood(new NormalTask(List.of("nesting wood"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Spawn))),
    W4_Passionfruit(new NormalTask(List.of("passionfruit"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Spawn))),
    W4_LargePassionfruit(new NormalTask(List.of("passionfruit"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Spawn))),
    W4_Cryoflora(new NormalTask(List.of("cryoflora"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    W4_Chillfruit(new NormalTask(List.of("chillfruit"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    W4_LargeChillfruit(new NormalTask(List.of("chillfruit"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    W4_Sulphoroot(new NormalTask(List.of("sulphoroot"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    W4_Jackfruit(new NormalTask(List.of("jackfruit"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    W4_LargeJackfruit(new NormalTask(List.of("jackfruit"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    W4_Pyrospire(new NormalTask(List.of("pyrospire"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    W4_Scorchberry(new NormalTask(List.of("scorchberries"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    W4_LargeScorchberry(new NormalTask(List.of("scorchberries"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    W4_ThornBeetle(new NormalTask(List.of("thorn beetles"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Alpha))),
    W4_Worm(new NormalTask(List.of("worms"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Root, Warp.W4_Alpha))),
    W4_Driftwood(new NormalTask(List.of("driftwood"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.W4_Delta))),

    W4_Bat(new NormalTask(List.of("bats"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.W4_Spawn))),
    W4_Rat(new NormalTask(List.of("rats"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W4_Sewer, Warp.W4_Spawn))),
    W4_Rattus(new NormalTask(List.of("rattus"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W4_Rattus, Warp.W4_Spawn))),
    W4_Frog(new NormalTask(List.of("frogs"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W4_Alpha))),
    W4_Sniper(new NormalTask(List.of("snipers"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.W4_Beta))),
    W4_AngryMiner(new NormalTask(List.of("angry miners"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W4_Beta))),
    W4_Ravager(new NormalTask(List.of("ravagers"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W4_Beta))),
    W4_Elite(new NormalTask(List.of("mobs in world #4"), List.of(Constraint.Elite), TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.W4_Alpha))),

    W4_Grasshopper(new NormalTask(List.of("grasshoppers"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W4_Spawn))),
    W4_AstromoldAndAstromite(new NormalTask(List.of("astromold & astromites"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W4_Spawn))),
    W4_Magmold(new NormalTask(List.of("magmold"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W4_Spawn))),
    W4_Algae(new NormalTask(List.of("algae"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W4_Alpha))),
    W4_BoomShroom(new NormalTask(List.of("boom shrooms"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W4_Alpha))),
    W4_Pearl(new NormalTask(List.of("pearls"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.W4_Delta))),

    W4_Cat(new NormalTask(List.of("cats"), null, TaskType.Fishing, DefaultWardrobe.CombatFishing, Tool.Spear, null, List.of(Warp.W4_Sewer, Warp.W4_Spawn))),
    W4_Catfish(new NormalTask(List.of("catfish"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W4_Sewer, Warp.W4_Spawn))),
    W4_Piranha(new NormalTask(List.of("piranhas"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W4_Alpha))),
    W4_Clownfish(new NormalTask(List.of("clownfish"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W4_Delta))),
    W4_Cichild(new NormalTask(List.of("cichlid"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W4_Delta))),
    W4_Parrotfish(new NormalTask(List.of("parrotfish"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W4_Delta))),
    W4_RedEmperor(new NormalTask(List.of("red emperor"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.W4_Delta))),
    W4_Pufferfish(new NormalTask(List.of("pufferfish"), null, TaskType.Fishing, DefaultWardrobe.CombatFishing, Tool.Spear, null, List.of(Warp.W4_Delta))),

    W4_SewerChest(new NormalTask(List.of("sewer chests"), null, TaskType.Misc, null, Tool.Spear, null, List.of(Warp.W4_Sewer, Warp.W4_Spawn))),

    // World Nightmare 1
    WN1_Quartz(new NormalTask(List.of("quartz"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.WN1_Quartz, Warp.WN1_Spawn))),
    WN1_ShinyQuartz(new NormalTask(List.of("quartz"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.WN1_Quartz, Warp.WN1_Spawn))),
    WN1_Lapis(new NormalTask(List.of("lapis"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.WN1_Lapis, Warp.WN1_Quartz, Warp.WN1_Spawn))),
    WN1_ShinyLapis(new NormalTask(List.of("lapis"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.WN1_Lapis, Warp.WN1_Quartz, Warp.WN1_Spawn))),
    WN1_NetherGold(new NormalTask(List.of("nether gold"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.WN1_NetherGold, Warp.WN1_Lapis, Warp.WN1_Quartz, Warp.WN1_Spawn))),
    WN1_ShinyNetherGold(new NormalTask(List.of("nether gold"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.WN1_NetherGold, Warp.WN1_Lapis, Warp.WN1_Quartz, Warp.WN1_Spawn))),
    WN1_AncientDebris(new NormalTask(List.of("ancient debris"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.WN1_Netherite, Warp.WN1_NetherGold, Warp.WN1_Lapis, Warp.WN1_Quartz, Warp.WN1_Spawn))),
    WN1_ShinyAncientDebris(new NormalTask(List.of("ancient debris"), List.of(Constraint.Shiny), TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.WN1_Netherite, Warp.WN1_NetherGold, Warp.WN1_Lapis, Warp.WN1_Quartz, Warp.WN1_Spawn))),

    WN1_Crimson(new NormalTask(List.of("crimson"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.WN1_Spawn))),
    WN1_CrimsonWithJuggling(new NormalTask(List.of("crimson"), List.of(Constraint.AxeJuggling), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.WN1_Spawn))),
    WN1_Toquoi(new NormalTask(List.of("toquoi"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.WN1_Spawn))),
    WN1_LargeToquoi(new NormalTask(List.of("toquoi"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.WN1_Spawn))),
    WN1_Aqua(new NormalTask(List.of("aqua"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),
    WN1_AquaWithJuggling(new NormalTask(List.of("aqua"), List.of(Constraint.AxeJuggling), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),
    WN1_Cheruza(new NormalTask(List.of("cheruza"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),
    WN1_LargeCheruza(new NormalTask(List.of("cheruza"), List.of(Constraint.Large), TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),
    WN1_Winkle(new NormalTask(List.of("winkles"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, null, List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),

    WN1_Piglin(new NormalTask(List.of("piglins"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.WN1_Spawn))),
    WN1_PiglinWithPeaShooter(new NormalTask(List.of("piglins"), List.of(Constraint.PeaShooter), TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.WN1_Spawn))),
    WN1_PiglinWithDevilsGambit(new NormalTask(List.of("piglins"), List.of(Constraint.DevilsGambit), TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.WN1_Spawn))),
    WN1_Bamboodle(new NormalTask(List.of("bamboodles"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.WN1_Bamboodle, Warp.WN1_Zoglin, Warp.WN1_Decay, Warp.WN1_Spawn))),
    WN1_BamboodleWithPeaShooter(new NormalTask(List.of("bamboodles"), List.of(Constraint.PeaShooter), TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.WN1_Bamboodle, Warp.WN1_Zoglin, Warp.WN1_Decay, Warp.WN1_Spawn))),
    WN1_BamboodleWithDevilsGambit(new NormalTask(List.of("bamboodles"), List.of(Constraint.DevilsGambit), TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.WN1_Bamboodle, Warp.WN1_Zoglin, Warp.WN1_Decay, Warp.WN1_Spawn))),
    WN1_Firefox(new NormalTask(List.of("firefoxes"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.WN1_Firefox, Warp.WN1_Spawn))),
    WN1_FirefoxWithPeaShooter(new NormalTask(List.of("firefoxes"), List.of(Constraint.PeaShooter), TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.WN1_Firefox, Warp.WN1_Spawn))),
    WN1_FirefoxWithDevilsGambit(new NormalTask(List.of("firefoxes"), List.of(Constraint.DevilsGambit), TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.WN1_Firefox, Warp.WN1_Spawn))),
    WN1_Marshmallow(new NormalTask(List.of("marshmallows"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Bow, null, List.of(Warp.WN1_Zoglin, Warp.WN1_Decay, Warp.WN1_Spawn))),
    WN1_N1Elite(new NormalTask(List.of("mobs in nightmare #1"), List.of(Constraint.Elite), TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),
    WN1_Zoglin(new NormalTask(List.of("zoglin"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, null, List.of(Warp.WN1_Zoglin, Warp.WN1_Bamboodle, Warp.WN1_Decay, Warp.WN1_Spawn))),

    WN1_Decay(new NormalTask(List.of("decay"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),
    WN1_DecayWithLandscaping(new NormalTask(List.of("decay"), List.of(Constraint.Landscaping), TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),
    WN1_IcebergLettuceOrTorchFlower(new NormalTask(List.of("torchflowers or iceberg lettuce"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.WN1_Torchflower, Warp.WN1_Spawn))),
    WN1_IcebergLettuceOrTorchFlowerWithLandscaping(new NormalTask(List.of("torchflowers or iceberg lettuce"), List.of(Constraint.Landscaping), TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.WN1_Torchflower, Warp.WN1_Spawn))),
    WN1_Mandrake(new NormalTask(List.of("mandrakes"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.WN1_Mandrake, Warp.WN1_Spawn))),
    WN1_MandrakeWithLandscaping(new NormalTask(List.of("mandrakes"), List.of(Constraint.Landscaping), TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.WN1_Mandrake, Warp.WN1_Spawn))),
    WN1_Splinterseed(new NormalTask(List.of("splinterseed"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.WN1_Splinterseed, Warp.WN1_Stable, Warp.WN1_Spawn))),
    WN1_SplinterseedWithLandscaping(new NormalTask(List.of("splinterseed"), List.of(Constraint.Landscaping), TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, null, List.of(Warp.WN1_Splinterseed, Warp.WN1_Stable, Warp.WN1_Spawn))),

    WN1_Charred(new NormalTask(List.of("charred"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),
    WN1_SmokedSalmon(new NormalTask(List.of("smoked salmon"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, null, List.of(Warp.WN1_Decay, Warp.WN1_Spawn))),
    WN1_Strider(new NormalTask(List.of("striders"), null, TaskType.Fishing, DefaultWardrobe.CombatFishing, Tool.Spear, null, List.of(Warp.WN1_Spawn))),

    WN1_Dice(new GamingTask(List.of("pairadice"), GameKind.Pairdice, null, List.of(Warp.WN1_Spawn))),
    WN1_ShiverMoney(new NormalTask(List.of("shiver from selling items"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, null, List.of(Warp.WN1_Quartz, Warp.WN1_Spawn))),
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

