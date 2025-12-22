package com.incrementalqol.common.data;

import com.incrementalqol.modules.TaskTracker.Task;

import java.util.*;

public class TaskCollection {

    private static final Map<TaskTarget, TaskDescriptor> TaskDescriptors = new HashMap<>();

    static {
        // World 1
        TaskDescriptors.put(TaskTarget.Coal, new TaskDescriptor(TaskType.Mining, "coal", new ArrayList<>(List.of("mine")), TaskType.Mining, ToolType.Pickaxe));
        TaskDescriptors.put(TaskTarget.Iron, new TaskDescriptor(TaskType.Mining, "iron", new ArrayList<>(List.of("coal", "mine")), TaskType.Mining, ToolType.Pickaxe));
        TaskDescriptors.put(TaskTarget.Copper, new TaskDescriptor(TaskType.Mining, "copper", new ArrayList<>(List.of("iron", "coal", "mine")), TaskType.Mining, ToolType.Pickaxe));
        TaskDescriptors.put(TaskTarget.Gold, new TaskDescriptor(TaskType.Mining, "gold", new ArrayList<>(List.of("copper", "iron", "coal", "mine")), TaskType.Mining, ToolType.Pickaxe));
        TaskDescriptors.put(TaskTarget.Redstone, new TaskDescriptor(TaskType.Mining, "redstone", new ArrayList<>(List.of("gold", "copper", "iron", "coal", "mine")), TaskType.Mining, ToolType.Pickaxe));
        TaskDescriptors.put(TaskTarget.Applewood, new TaskDescriptor(TaskType.Foraging, "w1", new ArrayList<>(List.of("w1")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Apple, new TaskDescriptor(TaskType.Foraging, "w1", new ArrayList<>(List.of("w1")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Palm, new TaskDescriptor(TaskType.Foraging, "crab", new ArrayList<>(List.of("w1")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Coconut, new TaskDescriptor(TaskType.Foraging, "crab", new ArrayList<>(List.of("w1")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Ladybug, new TaskDescriptor(TaskType.Foraging, "beetroot", new ArrayList<>(List.of("w1")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.ScaredHog, new TaskDescriptor(TaskType.Combat, "w1", new ArrayList<>(List.of("w1")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.WildBoar, new TaskDescriptor(TaskType.Combat, "hoglin", new ArrayList<>(List.of("carrot")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.MountainGoat, new TaskDescriptor(TaskType.Combat, "w1", new ArrayList<>(List.of("w1")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.Hoglin, new TaskDescriptor(TaskType.Combat, "hoglin", new ArrayList<>(List.of("carrot")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.W1Elite, new TaskDescriptor(TaskType.Combat, "hoglin", new ArrayList<>(List.of("carrot")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.Wheat, new TaskDescriptor(TaskType.Farming, "wheat", new ArrayList<>(List.of("w1")), TaskType.Farming, ToolType.Hoe));
        TaskDescriptors.put(TaskTarget.Carrot, new TaskDescriptor(TaskType.Farming, "carrot", new ArrayList<>(List.of("w1")), TaskType.Farming, ToolType.Hoe));
        TaskDescriptors.put(TaskTarget.Potato, new TaskDescriptor(TaskType.Farming, "potato", new ArrayList<>(List.of("w1")), TaskType.Farming, ToolType.Hoe));
        TaskDescriptors.put(TaskTarget.Beetroot, new TaskDescriptor(TaskType.Farming, "beetroot", new ArrayList<>(List.of("w1")), TaskType.Farming, ToolType.Hoe));
        TaskDescriptors.put(TaskTarget.Honeycomb, new TaskDescriptor(TaskType.Farming, "w1", new ArrayList<>(List.of("w1")), TaskType.Farming, ToolType.Hoe));
        TaskDescriptors.put(TaskTarget.Crops, new TaskDescriptor(TaskType.Farming, "carrot", new ArrayList<>(List.of("w1")), TaskType.Farming, ToolType.Hoe));
        TaskDescriptors.put(TaskTarget.Riverfish, new TaskDescriptor(TaskType.Fishing, "carrot", new ArrayList<>(List.of("w1")), TaskType.Fishing, ToolType.FishingRod));
        TaskDescriptors.put(TaskTarget.Crab, new TaskDescriptor(TaskType.Fishing, "wheat", new ArrayList<>(List.of("w1")), TaskType.CombatFishing, ToolType.FishingRod));
        TaskDescriptors.put(TaskTarget.HermitCrab, new TaskDescriptor(TaskType.Fishing, "crab", new ArrayList<>(List.of("w1")), TaskType.CombatFishing, ToolType.FishingRod));
        TaskDescriptors.put(TaskTarget.ConsecutiveFish, new TaskDescriptor(TaskType.Fishing, "carrot", new ArrayList<>(List.of("w1")), TaskType.Fishing, ToolType.FishingRod));
        TaskDescriptors.put(TaskTarget.Rps, new TaskDescriptor(TaskType.Gaming, "rps", new ArrayList<>(List.of("rps")), null, null));
        TaskDescriptors.put(TaskTarget.Coinflip, new TaskDescriptor(TaskType.Gaming, "cf", new ArrayList<>(List.of("cf")), null, null));
        TaskDescriptors.put(TaskTarget.Pixelpop, new TaskDescriptor(TaskType.Gaming, "pixelpop", new ArrayList<>(List.of("pixelpop")), null, null));
        TaskDescriptors.put(TaskTarget.Items, new TaskDescriptor(TaskType.Misc, "carrot", new ArrayList<>(List.of("w1")), TaskType.Farming, ToolType.Hoe));
        TaskDescriptors.put(TaskTarget.GoldMoney, new TaskDescriptor(TaskType.Misc, "redstone", new ArrayList<>(List.of("gold", "copper", "iron", "coal", "mine")), TaskType.Mining, ToolType.Pickaxe));
        TaskDescriptors.put(TaskTarget.GarbageCans, new TaskDescriptor(TaskType.Misc, "w1", new ArrayList<>(List.of("w1")), null, null));

        // World 2
        TaskDescriptors.put(TaskTarget.Crimsonite, new TaskDescriptor(TaskType.Mining, "lush", new ArrayList<>(List.of("w2")), TaskType.Mining, ToolType.Pickaxe));
        TaskDescriptors.put(TaskTarget.Verdelith, new TaskDescriptor(TaskType.Mining, "veil", new ArrayList<>(List.of("lush", "w2")), TaskType.Mining, ToolType.Pickaxe));
        TaskDescriptors.put(TaskTarget.Azuregem, new TaskDescriptor(TaskType.Mining, "infernal", new ArrayList<>(List.of("veil", "lush", "w2")), TaskType.Mining, ToolType.Pickaxe));
        TaskDescriptors.put(TaskTarget.Aurorium, new TaskDescriptor(TaskType.Mining, "abyss", new ArrayList<>(List.of("infernal", "veil", "lush", "w2")), TaskType.Mining, ToolType.Pickaxe));
        TaskDescriptors.put(TaskTarget.Bonsai, new TaskDescriptor(TaskType.Foraging, "w2", new ArrayList<>(List.of("w2")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Pomegranate, new TaskDescriptor(TaskType.Foraging, "w2", new ArrayList<>(List.of("w2")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Pine, new TaskDescriptor(TaskType.Foraging, "shimmer", new ArrayList<>(List.of("w2")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Pinecone, new TaskDescriptor(TaskType.Foraging, "shimmer", new ArrayList<>(List.of("w2")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Deadwood, new TaskDescriptor(TaskType.Foraging, "shimmer", new ArrayList<>(List.of("w2")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Zephyr, new TaskDescriptor(TaskType.Foraging, "abyss", new ArrayList<>(List.of("infernal", "veil", "lush", "w2")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.SkyBeetle, new TaskDescriptor(TaskType.Foraging, "w2", new ArrayList<>(List.of("w2")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Panda, new TaskDescriptor(TaskType.Combat, "sky", new ArrayList<>(List.of("w2")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.Sniffer, new TaskDescriptor(TaskType.Combat, "forge", new ArrayList<>(List.of("w2")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.Lurker, new TaskDescriptor(TaskType.Combat, "lush", new ArrayList<>(List.of("w2")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.CaveCrawler, new TaskDescriptor(TaskType.Combat, "lush", new ArrayList<>(List.of("w2")), TaskType.Combat, ToolType.Bow));
        TaskDescriptors.put(TaskTarget.PoisonSlime, new TaskDescriptor(TaskType.Combat, "lush", new ArrayList<>(List.of("w2")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.Spotter, new TaskDescriptor(TaskType.Combat, "veil", new ArrayList<>(List.of("lush", "w2")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.Verdemite, new TaskDescriptor(TaskType.Combat, "veil", new ArrayList<>(List.of("lush", "w2")), TaskType.Combat, ToolType.Pickaxe));
        TaskDescriptors.put(TaskTarget.Endermen, new TaskDescriptor(TaskType.Combat, "veil", new ArrayList<>(List.of("lush", "w2")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.Ghast, new TaskDescriptor(TaskType.Combat, "infernal", new ArrayList<>(List.of("veil", "lush", "w2")), TaskType.Combat, ToolType.Bow));
        TaskDescriptors.put(TaskTarget.GhastSoul, new TaskDescriptor(TaskType.Combat, "infernal", new ArrayList<>(List.of("veil", "lush", "w2")), TaskType.Combat, ToolType.Bow));
        TaskDescriptors.put(TaskTarget.Blaze, new TaskDescriptor(TaskType.Combat, "infernal", new ArrayList<>(List.of("veil", "lush", "w2")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.Nrub, new TaskDescriptor(TaskType.Combat, "infernal", new ArrayList<>(List.of("veil", "lush", "w2")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.InfernalImp, new TaskDescriptor(TaskType.Combat, "infernal", new ArrayList<>(List.of("veil", "lush", "w2")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.Wick, new TaskDescriptor(TaskType.Combat, "abyss", new ArrayList<>(List.of("infernal", "veil", "lush", "w2")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.GlowSquid, new TaskDescriptor(TaskType.Combat, "abyss", new ArrayList<>(List.of("infernal", "veil", "lush", "w2")), TaskType.Combat, ToolType.Bow));
        TaskDescriptors.put(TaskTarget.Slinker, new TaskDescriptor(TaskType.Combat, "abyss", new ArrayList<>(List.of("infernal", "veil", "lush", "w2")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.Rodrick, new TaskDescriptor(TaskType.Combat, "rodrick", new ArrayList<>(List.of("garlic")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.SkyBeetleQueen, new TaskDescriptor(TaskType.Combat, "sky", new ArrayList<>(List.of("shimmer")), TaskType.Combat, ToolType.Bow));
        TaskDescriptors.put(TaskTarget.W2Elite, new TaskDescriptor(TaskType.Combat, "infernal", new ArrayList<>(List.of("veil", "lush", "w2")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.Shimmer, new TaskDescriptor(TaskType.Farming, "shimmer", new ArrayList<>(List.of("w2")), TaskType.Farming, ToolType.Hoe));
        TaskDescriptors.put(TaskTarget.Garlic, new TaskDescriptor(TaskType.Farming, "garlic", new ArrayList<>(List.of("w2")), TaskType.Farming, ToolType.Hoe));
        TaskDescriptors.put(TaskTarget.Corn, new TaskDescriptor(TaskType.Farming, "corn", new ArrayList<>(List.of("w2")), TaskType.Farming, ToolType.Hoe));
        TaskDescriptors.put(TaskTarget.Shy, new TaskDescriptor(TaskType.Farming, "veil", new ArrayList<>(List.of("lush", "w2")), TaskType.Farming, ToolType.Hoe));
        TaskDescriptors.put(TaskTarget.LavaFruit, new TaskDescriptor(TaskType.Farming, "infernal", new ArrayList<>(List.of("veil", "lush", "w2")), TaskType.Farming, ToolType.Hoe));
        TaskDescriptors.put(TaskTarget.Twine, new TaskDescriptor(TaskType.Farming, "abyss", new ArrayList<>(List.of("infernal", "veil", "lush", "w2")), TaskType.Farming, ToolType.Hoe));
        TaskDescriptors.put(TaskTarget.AdvancedCrops, new TaskDescriptor(TaskType.Farming, "garlic", new ArrayList<>(List.of("w2")), TaskType.Farming, ToolType.Hoe));
        TaskDescriptors.put(TaskTarget.Salmon, new TaskDescriptor(TaskType.Fishing, "w2", new ArrayList<>(List.of("w2")), TaskType.Fishing, ToolType.FishingRod));
        TaskDescriptors.put(TaskTarget.Koi, new TaskDescriptor(TaskType.Fishing, "w2", new ArrayList<>(List.of("w2")), TaskType.Fishing, ToolType.FishingRod));
        TaskDescriptors.put(TaskTarget.Axolotl, new TaskDescriptor(TaskType.Fishing, "w2", new ArrayList<>(List.of("w2")), TaskType.Fishing, ToolType.FishingRod));
        TaskDescriptors.put(TaskTarget.MagmaFish, new TaskDescriptor(TaskType.Fishing, "infernal", new ArrayList<>(List.of("veil", "lush", "w2")), TaskType.Fishing, ToolType.FishingRod));
        TaskDescriptors.put(TaskTarget.MoltenJellyfish, new TaskDescriptor(TaskType.Fishing, "infernal", new ArrayList<>(List.of("veil", "lush", "w2")), TaskType.Fishing, ToolType.FishingRod));
        TaskDescriptors.put(TaskTarget.Bubbler, new TaskDescriptor(TaskType.Fishing, "infernal", new ArrayList<>(List.of("veil", "lush", "w2")), TaskType.Fishing, ToolType.FishingRod));
        TaskDescriptors.put(TaskTarget.AbyssalCrab, new TaskDescriptor(TaskType.Fishing, "abyss", new ArrayList<>(List.of("infernal", "veil", "lush", "w2")), TaskType.CombatFishing, ToolType.FishingRod));
        TaskDescriptors.put(TaskTarget.Blackjack, new TaskDescriptor(TaskType.Gaming, "21", new ArrayList<>(List.of("21")), null, null));
        TaskDescriptors.put(TaskTarget.SilverMoney, new TaskDescriptor(TaskType.Misc, "infernal", new ArrayList<>(List.of("veil", "lush", "w2")), TaskType.Mining, ToolType.Pickaxe));
        TaskDescriptors.put(TaskTarget.AbyssLamp, new TaskDescriptor(TaskType.Misc, "abyss", new ArrayList<>(List.of("infernal", "veil", "lush", "w2")), null, null));

        // World 3
        TaskDescriptors.put(TaskTarget.Brightstone, new TaskDescriptor(TaskType.Mining, "mines3", new ArrayList<>(List.of("w3")), TaskType.Mining, ToolType.Pickaxe));
        TaskDescriptors.put(TaskTarget.Diamond, new TaskDescriptor(TaskType.Mining, "mines3", new ArrayList<>(List.of("w3")), TaskType.Mining, ToolType.Pickaxe));
        TaskDescriptors.put(TaskTarget.Emerald, new TaskDescriptor(TaskType.Mining, "mines3", new ArrayList<>(List.of("w3")), TaskType.Mining, ToolType.Pickaxe));
        TaskDescriptors.put(TaskTarget.Gorespore, new TaskDescriptor(TaskType.Foraging, "sty", new ArrayList<>(List.of("w3")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.GoresporeSpore, new TaskDescriptor(TaskType.Foraging, "sty", new ArrayList<>(List.of("w3")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.DuneDweller, new TaskDescriptor(TaskType.Foraging, "beach", new ArrayList<>(List.of("w3")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.DuneDwellerSpore, new TaskDescriptor(TaskType.Foraging, "beach", new ArrayList<>(List.of("w3")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.HoneyShroom, new TaskDescriptor(TaskType.Foraging, "topside", new ArrayList<>(List.of("w3")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.HoneySpore, new TaskDescriptor(TaskType.Foraging, "topside", new ArrayList<>(List.of("w3")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.DreamShroom, new TaskDescriptor(TaskType.Foraging, "w3", new ArrayList<>(List.of("w3")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.DreamSpore, new TaskDescriptor(TaskType.Foraging, "w3", new ArrayList<>(List.of("w3")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Barky, new TaskDescriptor(TaskType.Foraging, "canine", new ArrayList<>(List.of("w3")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Pinepoodle, new TaskDescriptor(TaskType.Foraging, "canine", new ArrayList<>(List.of("w3")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Capsnapper, new TaskDescriptor(TaskType.Foraging, "w3", new ArrayList<>(List.of("w3")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Baconwing, new TaskDescriptor(TaskType.Combat, "sty", new ArrayList<>(List.of("w3")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.Camel, new TaskDescriptor(TaskType.Combat, "beach", new ArrayList<>(List.of("w3")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.Bee, new TaskDescriptor(TaskType.Combat, "topside", new ArrayList<>(List.of("w3")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.RoyalGuard, new TaskDescriptor(TaskType.Combat, "topside", new ArrayList<>(List.of("w3")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.Breeze, new TaskDescriptor(TaskType.Combat, "mines3", new ArrayList<>(List.of("w3")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.DireWolf, new TaskDescriptor(TaskType.Combat, "canine", new ArrayList<>(List.of("w3")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.Dreadhorn, new TaskDescriptor(TaskType.Combat, "dreadhorn", new ArrayList<>(List.of("canine", "w3")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.W3Elite, new TaskDescriptor(TaskType.Combat, "topside", new ArrayList<>(List.of("w3")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.Oinky, new TaskDescriptor(TaskType.Farming, "sty", new ArrayList<>(List.of("w3")), TaskType.Farming, ToolType.Hoe));
        TaskDescriptors.put(TaskTarget.Cattail, new TaskDescriptor(TaskType.Farming, "beach", new ArrayList<>(List.of("w3")), TaskType.Farming, ToolType.Hoe));
        TaskDescriptors.put(TaskTarget.Gloom, new TaskDescriptor(TaskType.Farming, "underside", new ArrayList<>(List.of("w3")), TaskType.Farming, ToolType.Hoe));
        TaskDescriptors.put(TaskTarget.CollieFlower, new TaskDescriptor(TaskType.Farming, "canine", new ArrayList<>(List.of("w3")), TaskType.Farming, ToolType.Hoe));
        TaskDescriptors.put(TaskTarget.GoldfishRetriever, new TaskDescriptor(TaskType.Fishing, "canine", new ArrayList<>(List.of("w3")), TaskType.Fishing, ToolType.FishingRod));
        TaskDescriptors.put(TaskTarget.Bettafly, new TaskDescriptor(TaskType.Fishing, "sty", new ArrayList<>(List.of("w3")), TaskType.Fishing, ToolType.FishingRod));
        TaskDescriptors.put(TaskTarget.Soarfish, new TaskDescriptor(TaskType.Fishing, "beach", new ArrayList<>(List.of("w3")), TaskType.Fishing, ToolType.FishingRod));
        TaskDescriptors.put(TaskTarget.Guardian, new TaskDescriptor(TaskType.Fishing, "underside", new ArrayList<>(List.of("w3")), TaskType.CombatFishing, ToolType.FishingRod));
        TaskDescriptors.put(TaskTarget.Matcher, new TaskDescriptor(TaskType.Gaming, "matcher", new ArrayList<>(List.of("matcher")), null, null));

        // World 4
        TaskDescriptors.put(TaskTarget.Cheddore, new TaskDescriptor(TaskType.Mining, "w4", new ArrayList<>(List.of("w4")), TaskType.Mining, ToolType.Pickaxe));
        TaskDescriptors.put(TaskTarget.BlueCheese, new TaskDescriptor(TaskType.Mining, "w4", new ArrayList<>(List.of("w4")), TaskType.Mining, ToolType.Pickaxe));
        TaskDescriptors.put(TaskTarget.Glowdust, new TaskDescriptor(TaskType.Mining, "beta", new ArrayList<>(List.of("beta")), TaskType.Mining, ToolType.Pickaxe));
        TaskDescriptors.put(TaskTarget.Slimecrust, new TaskDescriptor(TaskType.Mining, "beta", new ArrayList<>(List.of("beta")), TaskType.Mining, ToolType.Pickaxe));
        TaskDescriptors.put(TaskTarget.Voidshard, new TaskDescriptor(TaskType.Mining, "beta", new ArrayList<>(List.of("beta")), TaskType.Mining, ToolType.Pickaxe));
        TaskDescriptors.put(TaskTarget.Petrafin, new TaskDescriptor(TaskType.Mining, "delta", new ArrayList<>(List.of("delta")), TaskType.Mining, ToolType.Pickaxe));
        TaskDescriptors.put(TaskTarget.NestingWood, new TaskDescriptor(TaskType.Foraging, "w4", new ArrayList<>(List.of("w4")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Passionfruit, new TaskDescriptor(TaskType.Foraging, "w4", new ArrayList<>(List.of("w4")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Cryoflora, new TaskDescriptor(TaskType.Foraging, "alpha", new ArrayList<>(List.of("alpha")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Chillfruit, new TaskDescriptor(TaskType.Foraging, "alpha", new ArrayList<>(List.of("alpha")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Sulphoroot, new TaskDescriptor(TaskType.Foraging, "alpha", new ArrayList<>(List.of("alpha")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Jackfruit, new TaskDescriptor(TaskType.Foraging, "alpha", new ArrayList<>(List.of("alpha")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Pyrospire, new TaskDescriptor(TaskType.Foraging, "alpha", new ArrayList<>(List.of("alpha")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Scorchberry, new TaskDescriptor(TaskType.Foraging, "alpha", new ArrayList<>(List.of("alpha")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.ThornBeetle, new TaskDescriptor(TaskType.Foraging, "alpha", new ArrayList<>(List.of("alpha")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Worm, new TaskDescriptor(TaskType.Foraging, "alpha", new ArrayList<>(List.of("alpha")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Driftwood, new TaskDescriptor(TaskType.Foraging, "delta", new ArrayList<>(List.of("delta")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Bat, new TaskDescriptor(TaskType.Combat, "w4", new ArrayList<>(List.of("w4")), TaskType.Combat, ToolType.Bow));
        TaskDescriptors.put(TaskTarget.Rat, new TaskDescriptor(TaskType.Combat, "sewer", new ArrayList<>(List.of("w4")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.Rattus, new TaskDescriptor(TaskType.Combat, "rattus", new ArrayList<>(List.of("w4")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.Frog, new TaskDescriptor(TaskType.Combat, "alpha", new ArrayList<>(List.of("alpha")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.Sniper, new TaskDescriptor(TaskType.Combat, "beta", new ArrayList<>(List.of("beta")), TaskType.Combat, ToolType.Bow));
        TaskDescriptors.put(TaskTarget.AngryMiner, new TaskDescriptor(TaskType.Combat, "beta", new ArrayList<>(List.of("beta")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.Ravager, new TaskDescriptor(TaskType.Combat, "beta", new ArrayList<>(List.of("beta")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.W4Elite, new TaskDescriptor(TaskType.Combat, "alpha", new ArrayList<>(List.of("w4")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.Grasshopper, new TaskDescriptor(TaskType.Farming, "w4", new ArrayList<>(List.of("w4")), TaskType.Farming, ToolType.Hoe));
        TaskDescriptors.put(TaskTarget.AstromoldAndAstromite, new TaskDescriptor(TaskType.Farming, "w4", new ArrayList<>(List.of("w4")), TaskType.Farming, ToolType.Hoe));
        TaskDescriptors.put(TaskTarget.Magmold, new TaskDescriptor(TaskType.Farming, "w4", new ArrayList<>(List.of("w4")), TaskType.Farming, ToolType.Hoe));
        TaskDescriptors.put(TaskTarget.Algae, new TaskDescriptor(TaskType.Farming, "alpha", new ArrayList<>(List.of("alpha")), TaskType.Farming, ToolType.Hoe));
        TaskDescriptors.put(TaskTarget.BoomShroom, new TaskDescriptor(TaskType.Farming, "alpha", new ArrayList<>(List.of("alpha")), TaskType.Farming, ToolType.Hoe));
        TaskDescriptors.put(TaskTarget.Pearl, new TaskDescriptor(TaskType.Farming, "delta", new ArrayList<>(List.of("delta")), TaskType.Farming, ToolType.Hoe));
        TaskDescriptors.put(TaskTarget.Cat, new TaskDescriptor(TaskType.Fishing, "sewer", new ArrayList<>(List.of("w4")), TaskType.CombatFishing, ToolType.FishingRod));
        TaskDescriptors.put(TaskTarget.Catfish, new TaskDescriptor(TaskType.Fishing, "sewer", new ArrayList<>(List.of("w4")), TaskType.Fishing, ToolType.FishingRod));
        TaskDescriptors.put(TaskTarget.Piranha, new TaskDescriptor(TaskType.Fishing, "alpha", new ArrayList<>(List.of("alpha")), TaskType.Fishing, ToolType.FishingRod));
        TaskDescriptors.put(TaskTarget.Clownfish, new TaskDescriptor(TaskType.Fishing, "delta", new ArrayList<>(List.of("delta")), TaskType.Fishing, ToolType.FishingRod));
        TaskDescriptors.put(TaskTarget.Cichild, new TaskDescriptor(TaskType.Fishing, "delta", new ArrayList<>(List.of("delta")), TaskType.Fishing, ToolType.FishingRod));
        TaskDescriptors.put(TaskTarget.Parrotfish, new TaskDescriptor(TaskType.Fishing, "delta", new ArrayList<>(List.of("delta")), TaskType.Fishing, ToolType.FishingRod));
        TaskDescriptors.put(TaskTarget.RedEmperor, new TaskDescriptor(TaskType.Fishing, "delta", new ArrayList<>(List.of("delta")), TaskType.Fishing, ToolType.FishingRod));
        TaskDescriptors.put(TaskTarget.Pufferfish, new TaskDescriptor(TaskType.Fishing, "delta", new ArrayList<>(List.of("delta")), TaskType.CombatFishing, ToolType.FishingRod));
        TaskDescriptors.put(TaskTarget.SewerChest, new TaskDescriptor(TaskType.Misc, "sewer", new ArrayList<>(List.of("w4")), null, null));

        // World Nightmare 1
        TaskDescriptors.put(TaskTarget.Quartz, new TaskDescriptor(TaskType.Mining, "quartz", new ArrayList<>(List.of("n1")), TaskType.Mining, ToolType.Pickaxe));
        TaskDescriptors.put(TaskTarget.Lapis, new TaskDescriptor(TaskType.Mining, "lapis", new ArrayList<>(List.of("quartz", "n1")), TaskType.Mining, ToolType.Pickaxe));
        TaskDescriptors.put(TaskTarget.NetherGold, new TaskDescriptor(TaskType.Mining, "ngold", new ArrayList<>(List.of("lapis", "quartz", "n1")), TaskType.Mining, ToolType.Pickaxe));
        TaskDescriptors.put(TaskTarget.AncientDebris, new TaskDescriptor(TaskType.Mining, "netherite", new ArrayList<>(List.of("netherite", "ngold", "lapis", "quartz", "n1")), TaskType.Mining, ToolType.Pickaxe));
        TaskDescriptors.put(TaskTarget.Crimson, new TaskDescriptor(TaskType.Foraging, "n1", new ArrayList<>(List.of("n1")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Toquoi, new TaskDescriptor(TaskType.Foraging, "n1", new ArrayList<>(List.of("n1")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Aqua, new TaskDescriptor(TaskType.Foraging, "decay", new ArrayList<>(List.of("n1")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Cheruza, new TaskDescriptor(TaskType.Foraging, "decay", new ArrayList<>(List.of("n1")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Winkle, new TaskDescriptor(TaskType.Foraging, "decay", new ArrayList<>(List.of("n1")), TaskType.Foraging, ToolType.Axe));
        TaskDescriptors.put(TaskTarget.Piglin, new TaskDescriptor(TaskType.Combat, "n1", new ArrayList<>(List.of("n1")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.Bamboodle, new TaskDescriptor(TaskType.Combat, "bamboodle", new ArrayList<>(List.of("zoglin","decay","n1")), TaskType.Combat, ToolType.Bow));
        TaskDescriptors.put(TaskTarget.Firefox, new TaskDescriptor(TaskType.Combat, "firefox", new ArrayList<>(List.of("n1")), TaskType.Combat, ToolType.Bow));
        TaskDescriptors.put(TaskTarget.Marshmallow, new TaskDescriptor(TaskType.Combat, "zoglin", new ArrayList<>(List.of("decay","n1")), TaskType.Combat, ToolType.Bow));
        TaskDescriptors.put(TaskTarget.N1Elite, new TaskDescriptor(TaskType.Combat, "decay", new ArrayList<>(List.of("n1")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.Zoglin, new TaskDescriptor(TaskType.Combat, "zoglin", new ArrayList<>(List.of("bamboodle","decay","n1")), TaskType.Combat, ToolType.Melee));
        TaskDescriptors.put(TaskTarget.Decay, new TaskDescriptor(TaskType.Farming, "decay", new ArrayList<>(List.of("n1")), TaskType.Farming, ToolType.Hoe));
        TaskDescriptors.put(TaskTarget.IcebergLettuceOrTorchFlower, new TaskDescriptor(TaskType.Farming, "torch", new ArrayList<>(List.of("n1")), TaskType.Farming, ToolType.Hoe));
        TaskDescriptors.put(TaskTarget.Mandrake, new TaskDescriptor(TaskType.Farming, "mandrake", new ArrayList<>(List.of("n1")), TaskType.Farming, ToolType.Hoe));
        TaskDescriptors.put(TaskTarget.Splinterseed, new TaskDescriptor(TaskType.Farming, "splinter", new ArrayList<>(List.of("stable","n1")), TaskType.Farming, ToolType.Hoe));
        TaskDescriptors.put(TaskTarget.Charred, new TaskDescriptor(TaskType.Fishing, "decay", new ArrayList<>(List.of("n1")), TaskType.Fishing, ToolType.FishingRod));
        TaskDescriptors.put(TaskTarget.SmokedSalmon, new TaskDescriptor(TaskType.Fishing, "decay", new ArrayList<>(List.of("n1")), TaskType.Fishing, ToolType.FishingRod));
        TaskDescriptors.put(TaskTarget.Strider, new TaskDescriptor(TaskType.Fishing, "n1", new ArrayList<>(List.of("n1")), TaskType.CombatFishing, ToolType.FishingRod));
        TaskDescriptors.put(TaskTarget.Dice, new TaskDescriptor(TaskType.Gaming, "dice", new ArrayList<>(List.of("dice")), null, null));
        TaskDescriptors.put(TaskTarget.ShiverMoney, new TaskDescriptor(TaskType.Misc, "quartz", new ArrayList<>(List.of("n1")), TaskType.Mining, ToolType.Pickaxe));
    }

    private static final Map<String, TaskTarget> TaskMap = new HashMap<>();

    static {
        // World 1
        TaskMap.put("coal ore", TaskTarget.Coal);
        TaskMap.put("coal", TaskTarget.Coal);
        TaskMap.put("iron ore", TaskTarget.Iron);
        TaskMap.put("iron", TaskTarget.Iron);
        TaskMap.put("copper ore", TaskTarget.Copper);
        TaskMap.put("copper", TaskTarget.Copper);
        TaskMap.put("gold ore", TaskTarget.Gold);
        TaskMap.put("gold", TaskTarget.Gold);
        TaskMap.put("redstone ore", TaskTarget.Redstone);
        TaskMap.put("redstone", TaskTarget.Redstone);
        TaskMap.put("applewood", TaskTarget.Applewood);
        TaskMap.put("apples", TaskTarget.Apple);
        TaskMap.put("palm", TaskTarget.Palm);
        TaskMap.put("coconuts", TaskTarget.Coconut);
        TaskMap.put("ladybugs", TaskTarget.Ladybug);
        TaskMap.put("scared hogs", TaskTarget.ScaredHog);
        TaskMap.put("wild boars", TaskTarget.WildBoar);
        TaskMap.put("mountain goats", TaskTarget.MountainGoat);
        TaskMap.put("hoglin", TaskTarget.Hoglin);
        TaskMap.put("elite mobs in world #1", TaskTarget.W1Elite);
        TaskMap.put("wheat", TaskTarget.Wheat);
        TaskMap.put("carrots", TaskTarget.Carrot);
        TaskMap.put("potatoes", TaskTarget.Potato);
        TaskMap.put("beetroot", TaskTarget.Beetroot);
        TaskMap.put("honeycomb", TaskTarget.Honeycomb);
        TaskMap.put("crops", TaskTarget.Crops);
        TaskMap.put("riverfish", TaskTarget.Riverfish);
        TaskMap.put("fish", TaskTarget.ConsecutiveFish);
        TaskMap.put("crabs", TaskTarget.Crab);
        TaskMap.put("hermit crabs", TaskTarget.HermitCrab);
        TaskMap.put("rps", TaskTarget.Rps);
        TaskMap.put("coinflip", TaskTarget.Coinflip);
        TaskMap.put("pixel pop", TaskTarget.Pixelpop);
        TaskMap.put("items", TaskTarget.Items);
        TaskMap.put("gold from selling items", TaskTarget.GoldMoney);
        TaskMap.put("garbage cans", TaskTarget.GarbageCans);

        // World 2
        TaskMap.put("crimsonite", TaskTarget.Crimsonite);
        TaskMap.put("verdelith", TaskTarget.Verdelith);
        TaskMap.put("azuregem", TaskTarget.Azuregem);
        TaskMap.put("aurorium", TaskTarget.Aurorium);
        TaskMap.put("bonsai", TaskTarget.Bonsai);
        TaskMap.put("pomegranates", TaskTarget.Pomegranate);
        TaskMap.put("pine", TaskTarget.Pine);
        TaskMap.put("pinecones", TaskTarget.Pinecone);
        TaskMap.put("deadwood", TaskTarget.Deadwood);
        TaskMap.put("zephyr", TaskTarget.Zephyr);
        TaskMap.put("skybeetles", TaskTarget.SkyBeetle);
        TaskMap.put("pandas", TaskTarget.Panda);
        TaskMap.put("sniffers", TaskTarget.Sniffer);
        TaskMap.put("lurkers", TaskTarget.Lurker);
        TaskMap.put("cave crawlers", TaskTarget.CaveCrawler);
        TaskMap.put("poison slimes", TaskTarget.PoisonSlime);
        TaskMap.put("spotters", TaskTarget.Spotter);
        TaskMap.put("verdemites", TaskTarget.Verdemite);
        TaskMap.put("endermen", TaskTarget.Endermen);
        TaskMap.put("ghasts", TaskTarget.Ghast);
        TaskMap.put("ghast souls", TaskTarget.GhastSoul);
        TaskMap.put("blazes", TaskTarget.Blaze);
        TaskMap.put("nrubs", TaskTarget.Nrub);
        TaskMap.put("infernal imps", TaskTarget.InfernalImp);
        TaskMap.put("wicks", TaskTarget.Wick);
        TaskMap.put("glow squids", TaskTarget.GlowSquid);
        TaskMap.put("slinkers", TaskTarget.Slinker);
        TaskMap.put("rodrick", TaskTarget.Rodrick);
        TaskMap.put("sky beetle queen", TaskTarget.SkyBeetleQueen);
        TaskMap.put("elite mobs in world #2", TaskTarget.W2Elite);
        TaskMap.put("shimmer", TaskTarget.Shimmer);
        TaskMap.put("garlic", TaskTarget.Garlic);
        TaskMap.put("corn", TaskTarget.Corn);
        TaskMap.put("shy", TaskTarget.Shy);
        TaskMap.put("lavafruit", TaskTarget.LavaFruit);
        TaskMap.put("twine", TaskTarget.Twine);
        TaskMap.put("advanced crops", TaskTarget.AdvancedCrops);
        TaskMap.put("salmon", TaskTarget.Salmon);
        TaskMap.put("koi", TaskTarget.Koi);
        TaskMap.put("axolotl", TaskTarget.Axolotl);
        TaskMap.put("magmafish", TaskTarget.MagmaFish);
        TaskMap.put("molten jellyfish", TaskTarget.MoltenJellyfish);
        TaskMap.put("bubbler", TaskTarget.Bubbler);
        TaskMap.put("abyssal crabs", TaskTarget.AbyssalCrab);
        TaskMap.put("21", TaskTarget.Blackjack);
        TaskMap.put("silver from selling items", TaskTarget.SilverMoney);
        TaskMap.put("lampposts", TaskTarget.AbyssLamp);

        // World 3
        TaskMap.put("brightstone", TaskTarget.Brightstone);
        TaskMap.put("diamonds", TaskTarget.Diamond);
        TaskMap.put("emeralds", TaskTarget.Emerald);
        TaskMap.put("gorespore", TaskTarget.Gorespore);
        TaskMap.put("gorespore spores", TaskTarget.GoresporeSpore);
        TaskMap.put("dune dweller", TaskTarget.DuneDweller);
        TaskMap.put("dune dweller spores", TaskTarget.DuneDwellerSpore);
        TaskMap.put("honey shrooms", TaskTarget.HoneyShroom);
        TaskMap.put("honey shroom spores", TaskTarget.HoneySpore);
        TaskMap.put("dream shrooms", TaskTarget.DreamShroom);
        TaskMap.put("dream spores", TaskTarget.DreamSpore);
        TaskMap.put("barky", TaskTarget.Barky);
        TaskMap.put("pinepoodles", TaskTarget.Pinepoodle);
        TaskMap.put("capsnappers", TaskTarget.Capsnapper);
        TaskMap.put("baconwings", TaskTarget.Baconwing);
        TaskMap.put("camels", TaskTarget.Camel);
        TaskMap.put("bees", TaskTarget.Bee);
        TaskMap.put("royal guards", TaskTarget.RoyalGuard);
        TaskMap.put("breezes", TaskTarget.Breeze);
        TaskMap.put("dire wolves", TaskTarget.DireWolf);
        TaskMap.put("dreadhorn", TaskTarget.Dreadhorn);
        TaskMap.put("elite mobs in world #3", TaskTarget.W3Elite);
        TaskMap.put("oinky", TaskTarget.Oinky);
        TaskMap.put("cattails", TaskTarget.Cattail);
        TaskMap.put("gloom", TaskTarget.Gloom);
        TaskMap.put("collie-flowers", TaskTarget.CollieFlower);
        TaskMap.put("goldfish retrievers", TaskTarget.GoldfishRetriever);
        TaskMap.put("bettafly", TaskTarget.Bettafly);
        TaskMap.put("soarfish", TaskTarget.Soarfish);
        TaskMap.put("guardians", TaskTarget.Guardian);
        TaskMap.put("matcher", TaskTarget.Matcher);

        // World 4
        TaskMap.put("cheddore", TaskTarget.Cheddore);
        TaskMap.put("blue cheese", TaskTarget.BlueCheese);
        TaskMap.put("glowdust", TaskTarget.Glowdust);
        TaskMap.put("slimecrust", TaskTarget.Slimecrust);
        TaskMap.put("voidshard", TaskTarget.Voidshard);
        TaskMap.put("petrafin", TaskTarget.Petrafin);
        TaskMap.put("nesting wood", TaskTarget.NestingWood);
        TaskMap.put("passionfruit", TaskTarget.Passionfruit);
        TaskMap.put("cryoflora", TaskTarget.Cryoflora);
        TaskMap.put("chillfruit", TaskTarget.Chillfruit);
        TaskMap.put("sulphoroot", TaskTarget.Sulphoroot);
        TaskMap.put("jackfruit", TaskTarget.Jackfruit);
        TaskMap.put("pyrospire", TaskTarget.Pyrospire);
        TaskMap.put("scorchberries", TaskTarget.Scorchberry);
        TaskMap.put("thorn beetles", TaskTarget.ThornBeetle);
        TaskMap.put("worms", TaskTarget.Worm);
        TaskMap.put("driftwood", TaskTarget.Driftwood);
        TaskMap.put("bats", TaskTarget.Bat);
        TaskMap.put("rats", TaskTarget.Rat);
        TaskMap.put("rattus", TaskTarget.Rattus);
        TaskMap.put("frogs", TaskTarget.Frog);
        TaskMap.put("snipers", TaskTarget.Sniper);
        TaskMap.put("angry miners", TaskTarget.AngryMiner);
        TaskMap.put("ravagers", TaskTarget.Ravager);
        TaskMap.put("elite mobs in world #4", TaskTarget.W4Elite);
        TaskMap.put("grasshoppers", TaskTarget.Grasshopper);
        TaskMap.put("astromold & astromites", TaskTarget.AstromoldAndAstromite);
        TaskMap.put("magmold", TaskTarget.Magmold);
        TaskMap.put("algae", TaskTarget.Algae);
        TaskMap.put("boom shrooms", TaskTarget.BoomShroom);
        TaskMap.put("pearls", TaskTarget.Pearl);
        TaskMap.put("cats", TaskTarget.Cat);
        TaskMap.put("catfish", TaskTarget.Catfish);
        TaskMap.put("piranhas", TaskTarget.Piranha);
        TaskMap.put("clownfish", TaskTarget.Clownfish);
        TaskMap.put("cichlid", TaskTarget.Cichild);
        TaskMap.put("parrotfish", TaskTarget.Parrotfish);
        TaskMap.put("red emperor", TaskTarget.RedEmperor);
        TaskMap.put("pufferfish", TaskTarget.Pufferfish);
        TaskMap.put("sewer chests", TaskTarget.SewerChest);


        // World Nightmare 1
        TaskMap.put("quartz", TaskTarget.Quartz);
        TaskMap.put("lapis", TaskTarget.Lapis);
        TaskMap.put("nether gold", TaskTarget.NetherGold);
        TaskMap.put("ancient debris", TaskTarget.AncientDebris);
        TaskMap.put("crimson", TaskTarget.Crimson);
        TaskMap.put("toquoi", TaskTarget.Toquoi);
        TaskMap.put("aqua", TaskTarget.Aqua);
        TaskMap.put("cheruza", TaskTarget.Cheruza);
        TaskMap.put("winkles", TaskTarget.Winkle);
        TaskMap.put("piglins", TaskTarget.Piglin);
        TaskMap.put("bamboodles", TaskTarget.Bamboodle);
        TaskMap.put("firefoxes", TaskTarget.Firefox);
        TaskMap.put("marshmallows", TaskTarget.Marshmallow);
        TaskMap.put("elite mobs in nightmare #1", TaskTarget.N1Elite);
        TaskMap.put("zoglin", TaskTarget.Zoglin);
        TaskMap.put("decay", TaskTarget.Decay);
        TaskMap.put("torchflowers or iceberg lettuce", TaskTarget.IcebergLettuceOrTorchFlower);
        TaskMap.put("mandrakes", TaskTarget.Mandrake);
        TaskMap.put("splinterseed", TaskTarget.Splinterseed);
        TaskMap.put("charred", TaskTarget.Charred);
        TaskMap.put("smoked salmon", TaskTarget.SmokedSalmon);
        TaskMap.put("striders", TaskTarget.Strider);
        TaskMap.put("pairadice", TaskTarget.Dice);
        TaskMap.put("shiver from selling items", TaskTarget.ShiverMoney);
    }

    public static TaskTarget TryGetTaskTarget(String target) {
        return TaskMap.getOrDefault(target.toLowerCase(), null);
    }

    public static TaskDescriptor TryGetDescriptor(String target) {
        return TaskDescriptors.getOrDefault(TryGetTaskTarget(target), null);
    }

    public static TaskDescriptor GetDescriptor(TaskTarget target) {
        return TaskDescriptors.get(target);
    }

    public static class TaskDescriptor {
        private final TaskType type;
        private final String command;
        private final ArrayList<String> fallbackCommands;
        private final TaskType defaultWardrobe;
        private final ToolType defaultHotBarSlot;

        public TaskDescriptor(TaskType type, String command, ArrayList<String> fallbackCommands, TaskType wardrobe, ToolType hotBarSlotId) {
            this.type = type;
            this.command = command;
            this.fallbackCommands = fallbackCommands;
            this.defaultWardrobe = wardrobe;
            this.defaultHotBarSlot = hotBarSlotId;
        }

        public TaskType getDefaultWardrobe() {
            return defaultWardrobe;
        }

        public ToolType getDefaultHotBarSlot() {
            return defaultHotBarSlot;
        }

        public String getCommand() {
            return type != TaskType.Gaming ? "warp " + command : command;
        }

        public String getFallbackCommand(int index) {
            if (fallbackCommands.size() <= index) {
                return null;
            }
            return type != TaskType.Gaming ? "warp " + fallbackCommands.get(index) : fallbackCommands.get(index);
        }
    }

    public enum TaskType {
        Combat("1"),
        Mining("2"),
        Foraging("3"),
        Farming("4"),
        Fishing("5"),
        CombatFishing("6"),
        Misc(null),
        Gaming(null);

        private final String value;

        TaskType(String value) {
            this.value = value;
        }

        public String getString() {
            return value;
        }
    }

    public enum TaskTarget {
        // WORLD 1
        // Mining
        Coal, Iron, Copper, Gold, Redstone,

        // Foraging
        Applewood, Apple, Palm, Coconut, Ladybug,

        // Combat
        ScaredHog, WildBoar, MountainGoat, Hoglin,
        W1Elite,

        // Farming
        Wheat, Carrot, Potato, Beetroot, Honeycomb, Crops,

        // Fishing
        Riverfish, Crab, HermitCrab, ConsecutiveFish,

        // Gaming
        Rps, Coinflip, Pixelpop,

        // Misc
        Items, GoldMoney, GarbageCans,

        // WORLD 2
        // Mining
        Crimsonite, Verdelith, Azuregem, Aurorium,

        // Foraging
        Bonsai, Pomegranate, Pine, Pinecone, Deadwood, Zephyr, SkyBeetle,

        // Combat
        Panda, Sniffer, Lurker, CaveCrawler, PoisonSlime, Spotter, Verdemite, Endermen, Ghast, GhastSoul, Blaze, Nrub, InfernalImp, Wick, GlowSquid, Slinker, Rodrick, SkyBeetleQueen,
        W2Elite,

        // Farming
        Shimmer, Garlic, Corn, Shy, LavaFruit, Twine,
        AdvancedCrops,

        // Fishing
        Salmon, Koi, Axolotl, MagmaFish, MoltenJellyfish, Bubbler, AbyssalCrab,

        // Gaming
        Blackjack,

        // Misc
        SilverMoney, AbyssLamp,

        // WORLD 3
        // Mining
        Brightstone, Diamond, Emerald,

        // Foraging
        Gorespore, GoresporeSpore, DuneDweller, DuneDwellerSpore, HoneyShroom, HoneySpore, DreamShroom, DreamSpore, Barky, Pinepoodle, Capsnapper,

        // Combat
        Baconwing, Camel, Bee, RoyalGuard, Breeze, DireWolf, Dreadhorn,
        W3Elite,

        // Farming
        Oinky, Cattail, Gloom, CollieFlower,

        // Fishing
        GoldfishRetriever, Bettafly, Soarfish, Guardian,

        // Gaming
        Matcher,

        // WORLD 4
        // Mining
        Cheddore, BlueCheese, Glowdust, Slimecrust, Voidshard, Petrafin,

        // Foraging
        NestingWood, Passionfruit, Cryoflora, Chillfruit, Sulphoroot, Jackfruit, Pyrospire, Scorchberry, ThornBeetle, Worm, Driftwood,

        // Combat
        Bat, Rat, Rattus, Frog, Sniper, AngryMiner, Ravager,
        W4Elite,

        // Farming
        Grasshopper, AstromoldAndAstromite, Magmold, Algae, BoomShroom, Pearl,

        // Fishing
        Cat, Catfish, Piranha, Clownfish, Cichild, Parrotfish, RedEmperor, Pufferfish,

        // Gaming

        // Misc
        SewerChest,

        // WORLD Nightmare 1
        // Mining
        Quartz, Lapis, NetherGold, AncientDebris,

        // Foraging
        Crimson, Toquoi, Aqua, Cheruza, Winkle,

        // Combat
        Piglin, Bamboodle, Firefox, Marshmallow,
        N1Elite, Zoglin,

        // Farming
        Decay, IcebergLettuceOrTorchFlower, Mandrake, Splinterseed,

        // Fishing
        Charred, SmokedSalmon, Strider,

        // Gaming
        Dice,

        // Misc
        ShiverMoney,

        // Legacy?
        Astromold, Astromite,
    }
}

