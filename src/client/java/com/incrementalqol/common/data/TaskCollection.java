package com.incrementalqol.common.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TaskCollection {

    private static final Map<TaskTarget, TaskDescriptor> TaskDescriptors = new HashMap<>();

    static {
        // World 1
        TaskDescriptors.put(TaskTarget.Coal, new TaskDescriptor(TaskType.Mining, "coal", "mine", "2", 1));
        TaskDescriptors.put(TaskTarget.Iron, new TaskDescriptor(TaskType.Mining, "iron", "mine", "2", 1));
        TaskDescriptors.put(TaskTarget.Copper, new TaskDescriptor(TaskType.Mining, "copper", "mine", "2", 1));
        TaskDescriptors.put(TaskTarget.Gold, new TaskDescriptor(TaskType.Mining, "gold", "mine", "2", 1));
        TaskDescriptors.put(TaskTarget.Redstone, new TaskDescriptor(TaskType.Mining, "redstone", "mine", "2", 1));
        TaskDescriptors.put(TaskTarget.Applewood, new TaskDescriptor(TaskType.Foraging, "w1", "w1", "3", 2));
        TaskDescriptors.put(TaskTarget.Apple, new TaskDescriptor(TaskType.Foraging, "w1", "w1", "3", 2));
        TaskDescriptors.put(TaskTarget.Palm, new TaskDescriptor(TaskType.Foraging, "crab", "w1", "3", 2));
        TaskDescriptors.put(TaskTarget.Coconut, new TaskDescriptor(TaskType.Foraging, "crab", "w1", "3", 2));
        TaskDescriptors.put(TaskTarget.Ladybug, new TaskDescriptor(TaskType.Foraging, "beetroot", "w1", "3", 2));
        TaskDescriptors.put(TaskTarget.ScaredHog, new TaskDescriptor(TaskType.Combat, "w1", "w1", "1", 0));
        TaskDescriptors.put(TaskTarget.WildBoar, new TaskDescriptor(TaskType.Combat, "carrot", "w1", "1", 0));
        TaskDescriptors.put(TaskTarget.MountainGoat, new TaskDescriptor(TaskType.Combat, "w1", "w1", "1", 0));
        TaskDescriptors.put(TaskTarget.Hoglin, new TaskDescriptor(TaskType.Combat, "hoglin", "w1", "1", 0));
        TaskDescriptors.put(TaskTarget.W1_Elite, new TaskDescriptor(TaskType.Combat, "hoglin", "w1", "1", 0));
        TaskDescriptors.put(TaskTarget.Wheat, new TaskDescriptor(TaskType.Farming, "wheat", "w1", "4", 3));
        TaskDescriptors.put(TaskTarget.Carrot, new TaskDescriptor(TaskType.Farming, "carrot", "w1", "4", 3));
        TaskDescriptors.put(TaskTarget.Potato, new TaskDescriptor(TaskType.Farming, "potato", "w1", "4", 3));
        TaskDescriptors.put(TaskTarget.Beetroot, new TaskDescriptor(TaskType.Farming, "beetroot", "w1", "4", 3));
        TaskDescriptors.put(TaskTarget.Honeycomb, new TaskDescriptor(TaskType.Farming, "w1", "w1", "4", 3));
        TaskDescriptors.put(TaskTarget.Riverfish, new TaskDescriptor(TaskType.Fishing, "carrot", "w1", "5", 4));
        TaskDescriptors.put(TaskTarget.Crab, new TaskDescriptor(TaskType.Fishing, "wheat", "w1", "5", 4));
        TaskDescriptors.put(TaskTarget.HermitCrab, new TaskDescriptor(TaskType.Fishing, "crab", "w1", "5", 4));
        TaskDescriptors.put(TaskTarget.Rps, new TaskDescriptor(TaskType.Gaming, "rps", "w1", null, null));
        TaskDescriptors.put(TaskTarget.Coinflip, new TaskDescriptor(TaskType.Gaming, "cf", "w1", null, null));
        TaskDescriptors.put(TaskTarget.Pixelpop, new TaskDescriptor(TaskType.Gaming, "pixelpop", "w1", null, null));
        TaskDescriptors.put(TaskTarget.Items, new TaskDescriptor(TaskType.Misc, "carrot", "w1", "4", 3));
        TaskDescriptors.put(TaskTarget.Gold_Money, new TaskDescriptor(TaskType.Misc, "redstone", "mine", "2", 1));
        TaskDescriptors.put(TaskTarget.GarbageCans, new TaskDescriptor(TaskType.Misc, "w1", "w1", null, null));

        // World 2
        TaskDescriptors.put(TaskTarget.Crimsonite, new TaskDescriptor(TaskType.Mining, "lush", "w2", "2", 1));
        TaskDescriptors.put(TaskTarget.Verdelith, new TaskDescriptor(TaskType.Mining, "veil", "w2", "2", 1));
        TaskDescriptors.put(TaskTarget.Azuregem, new TaskDescriptor(TaskType.Mining, "infernal", "w2", "2", 1));
        TaskDescriptors.put(TaskTarget.Aurorium, new TaskDescriptor(TaskType.Mining, "abyss", "w2", "2", 1));
        TaskDescriptors.put(TaskTarget.Bonsai, new TaskDescriptor(TaskType.Foraging, "w2", "w2", "3", 2));
        TaskDescriptors.put(TaskTarget.Pomegranate, new TaskDescriptor(TaskType.Foraging, "w2", "w2", "3", 2));
        TaskDescriptors.put(TaskTarget.Pine, new TaskDescriptor(TaskType.Foraging, "w2", "w2", "3", 2));
        TaskDescriptors.put(TaskTarget.Pinecone, new TaskDescriptor(TaskType.Foraging, "w2", "w2", "3", 2));
        TaskDescriptors.put(TaskTarget.Deadwood, new TaskDescriptor(TaskType.Foraging, "w2", "w2", "3", 2));
        TaskDescriptors.put(TaskTarget.Zephyr, new TaskDescriptor(TaskType.Foraging, "abyss", "w2", "3", 2));
        TaskDescriptors.put(TaskTarget.SkyBeetle, new TaskDescriptor(TaskType.Foraging, "w2", "w2", "3", 2));
        TaskDescriptors.put(TaskTarget.Panda, new TaskDescriptor(TaskType.Combat, "sky", "w2", "1", 0));
        TaskDescriptors.put(TaskTarget.Sniffer, new TaskDescriptor(TaskType.Combat, "forge", "w2", "1", 0));
        TaskDescriptors.put(TaskTarget.Lurker, new TaskDescriptor(TaskType.Combat, "lush", "w2", "1", 0));
        TaskDescriptors.put(TaskTarget.CaveCrawler, new TaskDescriptor(TaskType.Combat, "lush", "w2", "1", 5));
        TaskDescriptors.put(TaskTarget.PoisonSlime, new TaskDescriptor(TaskType.Combat, "lush", "w2", "1", 0));
        TaskDescriptors.put(TaskTarget.Spotter, new TaskDescriptor(TaskType.Combat, "veil", "w2", "1", 0));
        TaskDescriptors.put(TaskTarget.Verdemite, new TaskDescriptor(TaskType.Combat, "veil", "w2", "1", 0));
        TaskDescriptors.put(TaskTarget.Enderman, new TaskDescriptor(TaskType.Combat, "veil", "w2", "1", 0));
        TaskDescriptors.put(TaskTarget.Ghast, new TaskDescriptor(TaskType.Combat, "infernal", "w2", "1", 0));
        TaskDescriptors.put(TaskTarget.GhastSoul, new TaskDescriptor(TaskType.Combat, "infernal", "w2", "1", 0));
        TaskDescriptors.put(TaskTarget.Blaze, new TaskDescriptor(TaskType.Combat, "infernal", "w2", "1", 0));
        TaskDescriptors.put(TaskTarget.Nrub, new TaskDescriptor(TaskType.Combat, "infernal", "w2", "1", 0));
        TaskDescriptors.put(TaskTarget.InfernalImp, new TaskDescriptor(TaskType.Combat, "infernal", "w2", "1", 0));
        TaskDescriptors.put(TaskTarget.Wick, new TaskDescriptor(TaskType.Combat, "abyss", "w2", "1", 0));
        TaskDescriptors.put(TaskTarget.GlowSquid, new TaskDescriptor(TaskType.Combat, "abyss", "w2", "1", 5));
        TaskDescriptors.put(TaskTarget.Slinker, new TaskDescriptor(TaskType.Combat, "abyss", "w2", "1", 0));
        TaskDescriptors.put(TaskTarget.Rodrick, new TaskDescriptor(TaskType.Combat, "rodrick", "w2", "1", 0));
        TaskDescriptors.put(TaskTarget.SkyBeetleQueen, new TaskDescriptor(TaskType.Combat, "sky", "w2", "1", 5));
        TaskDescriptors.put(TaskTarget.W2_Elite, new TaskDescriptor(TaskType.Combat, "infernal", "w2", "1", 0));
        TaskDescriptors.put(TaskTarget.Shimmer, new TaskDescriptor(TaskType.Farming, "shimmer", "w2", "4", 3));
        TaskDescriptors.put(TaskTarget.Garlic, new TaskDescriptor(TaskType.Farming, "garlic", "w2", "4", 3));
        TaskDescriptors.put(TaskTarget.Corn, new TaskDescriptor(TaskType.Farming, "corn", "w2", "4", 3));
        TaskDescriptors.put(TaskTarget.Shy, new TaskDescriptor(TaskType.Farming, "veil", "w2", "4", 3));
        TaskDescriptors.put(TaskTarget.LavaFruit, new TaskDescriptor(TaskType.Farming, "infernal", "w2", "4", 3));
        TaskDescriptors.put(TaskTarget.Twine, new TaskDescriptor(TaskType.Farming, "abyss", "w2", "4", 3));
        TaskDescriptors.put(TaskTarget.AdvancedCrops, new TaskDescriptor(TaskType.Farming, "garlic", "w2", "4", 3));
        TaskDescriptors.put(TaskTarget.Salmon, new TaskDescriptor(TaskType.Fishing, "w2", "w2", "5", 4));
        TaskDescriptors.put(TaskTarget.Koi, new TaskDescriptor(TaskType.Fishing, "w2", "w2", "5", 4));
        TaskDescriptors.put(TaskTarget.Axolotl, new TaskDescriptor(TaskType.Fishing, "w2", "w2", "5", 4));
        TaskDescriptors.put(TaskTarget.MagmaFish, new TaskDescriptor(TaskType.Fishing, "infernal", "w2", "5", 4));
        TaskDescriptors.put(TaskTarget.MoltenJellyfish, new TaskDescriptor(TaskType.Fishing, "infernal", "w2", "5", 4));
        TaskDescriptors.put(TaskTarget.Blubber, new TaskDescriptor(TaskType.Fishing, "infernal", "w2", "5", 4));
        TaskDescriptors.put(TaskTarget.AbyssalCrab, new TaskDescriptor(TaskType.Fishing, "abyss", "w2", "5", 4));
        TaskDescriptors.put(TaskTarget.Blackjack, new TaskDescriptor(TaskType.Gaming, "21", "w2", null, null));
        TaskDescriptors.put(TaskTarget.Silver_Money, new TaskDescriptor(TaskType.Misc, "infernal", "w2", "2", 1));
        TaskDescriptors.put(TaskTarget.AbyssLamp, new TaskDescriptor(TaskType.Misc, "abyss", "w2", null, null));

        // World 3
        TaskDescriptors.put(TaskTarget.Brightstone, new TaskDescriptor(TaskType.Mining, "mines3", "w3", "2", 1));
        TaskDescriptors.put(TaskTarget.Diamond, new TaskDescriptor(TaskType.Mining, "mines3", "w3", "2", 1));
        TaskDescriptors.put(TaskTarget.Emerald, new TaskDescriptor(TaskType.Mining, "mines3", "w3", "2", 1));
        TaskDescriptors.put(TaskTarget.Gorespore, new TaskDescriptor(TaskType.Foraging, "sty", "w3", "3", 2));
        TaskDescriptors.put(TaskTarget.GoresporeSpore, new TaskDescriptor(TaskType.Foraging, "sty", "w3", "3", 2));
        TaskDescriptors.put(TaskTarget.DuneDweller, new TaskDescriptor(TaskType.Foraging, "beach", "w3", "3", 2));
        TaskDescriptors.put(TaskTarget.DuneDwellerSpore, new TaskDescriptor(TaskType.Foraging, "beach", "w3", "3", 2));
        TaskDescriptors.put(TaskTarget.HoneyShroom, new TaskDescriptor(TaskType.Foraging, "topside", "w3", "3", 2));
        TaskDescriptors.put(TaskTarget.HoneySpore, new TaskDescriptor(TaskType.Foraging, "topside", "w3", "3", 2));
        TaskDescriptors.put(TaskTarget.DreamShroom, new TaskDescriptor(TaskType.Foraging, "w3", "w3", "3", 2));
        TaskDescriptors.put(TaskTarget.DreamSpore, new TaskDescriptor(TaskType.Foraging, "w3", "w3", "3", 2));
        TaskDescriptors.put(TaskTarget.Barky, new TaskDescriptor(TaskType.Foraging, "canine", "w3", "3", 2));
        TaskDescriptors.put(TaskTarget.Pinepoodle, new TaskDescriptor(TaskType.Foraging, "canine", "w3", "3", 2));
        TaskDescriptors.put(TaskTarget.Capsnapper, new TaskDescriptor(TaskType.Foraging, "w3", "w3", "3", 2));
        TaskDescriptors.put(TaskTarget.Baconwing, new TaskDescriptor(TaskType.Combat, "sty", "w3", "1", 0));
        TaskDescriptors.put(TaskTarget.Camel, new TaskDescriptor(TaskType.Combat, "beach", "w3", "1", 0));
        TaskDescriptors.put(TaskTarget.Bee, new TaskDescriptor(TaskType.Combat, "topside", "w3", "1", 0));
        TaskDescriptors.put(TaskTarget.RoyalGuard, new TaskDescriptor(TaskType.Combat, "topside", "w3", "1", 0));
        TaskDescriptors.put(TaskTarget.Breeze, new TaskDescriptor(TaskType.Combat, "mines3", "w3", "1", 0));
        TaskDescriptors.put(TaskTarget.DireWolf, new TaskDescriptor(TaskType.Combat, "canine", "w3", "1", 0));
        TaskDescriptors.put(TaskTarget.W3_Elite, new TaskDescriptor(TaskType.Combat, "topside", "w3", "1", 0));
        TaskDescriptors.put(TaskTarget.Oinky, new TaskDescriptor(TaskType.Farming, "sty", "w3", "4", 3));
        TaskDescriptors.put(TaskTarget.Cattail, new TaskDescriptor(TaskType.Farming, "beach", "w3", "4", 3));
        TaskDescriptors.put(TaskTarget.Gloom, new TaskDescriptor(TaskType.Farming, "underside", "w3", "4", 3));
        TaskDescriptors.put(TaskTarget.CollieFlower, new TaskDescriptor(TaskType.Farming, "canine", "w3", "4", 3));
        TaskDescriptors.put(TaskTarget.GoldfishRetriever, new TaskDescriptor(TaskType.Fishing, "canine", "w3", "5", 4));
        TaskDescriptors.put(TaskTarget.Bettafly, new TaskDescriptor(TaskType.Fishing, "sty", "w3", "5", 4));
        TaskDescriptors.put(TaskTarget.Soarfish, new TaskDescriptor(TaskType.Fishing, "beach", "w3", "5", 4));
        TaskDescriptors.put(TaskTarget.Guardian, new TaskDescriptor(TaskType.Fishing, "underside", "w3", "5", 4));
        TaskDescriptors.put(TaskTarget.Matcher, new TaskDescriptor(TaskType.Gaming, "matcher", "w3", null, null));

        // World Nightmare 1
        TaskDescriptors.put(TaskTarget.Quartz, new TaskDescriptor(TaskType.Mining, "n1", "n1", "2", 1));
        TaskDescriptors.put(TaskTarget.Lapis, new TaskDescriptor(TaskType.Mining, "n1", "n1", "2", 1));
        TaskDescriptors.put(TaskTarget.NetherGold, new TaskDescriptor(TaskType.Mining, "n1", "n1", "2", 1));
        TaskDescriptors.put(TaskTarget.AncientDebris, new TaskDescriptor(TaskType.Mining, "n1", "n1", "2", 1));
        TaskDescriptors.put(TaskTarget.Crimson, new TaskDescriptor(TaskType.Foraging, "n1", "n1", "3", 2));
        TaskDescriptors.put(TaskTarget.Toquoi, new TaskDescriptor(TaskType.Foraging, "n1", "n1", "3", 2));
        TaskDescriptors.put(TaskTarget.Aqua, new TaskDescriptor(TaskType.Foraging, "n1", "n1", "3", 2));
        TaskDescriptors.put(TaskTarget.Cheruza, new TaskDescriptor(TaskType.Foraging, "n1", "n1", "3", 2));
        TaskDescriptors.put(TaskTarget.Winkle, new TaskDescriptor(TaskType.Foraging, "n1", "n1", "3", 2));
        TaskDescriptors.put(TaskTarget.Piglin, new TaskDescriptor(TaskType.Combat, "n1", "n1", "1", 1));
        TaskDescriptors.put(TaskTarget.Bamboodle, new TaskDescriptor(TaskType.Combat, "n1", "n1", "1", 5));
        TaskDescriptors.put(TaskTarget.Firefox, new TaskDescriptor(TaskType.Combat, "n1", "n1", "1", 5));
        TaskDescriptors.put(TaskTarget.Marshmallow, new TaskDescriptor(TaskType.Combat, "n1", "n1", "1", 5));
        TaskDescriptors.put(TaskTarget.WN1_Elite, new TaskDescriptor(TaskType.Combat, "n1", "n1", "1", 1));
        TaskDescriptors.put(TaskTarget.Decay, new TaskDescriptor(TaskType.Combat, "n1", "n1", "4", 3));
        TaskDescriptors.put(TaskTarget.IcebergLettuce_Or_TorchFlower, new TaskDescriptor(TaskType.Combat, "n1", "n1", "4", 3));
        TaskDescriptors.put(TaskTarget.Mandrake, new TaskDescriptor(TaskType.Combat, "n1", "n1", "4", 3));
        TaskDescriptors.put(TaskTarget.Splinterseed, new TaskDescriptor(TaskType.Combat, "n1", "n1", "4", 3));
        TaskDescriptors.put(TaskTarget.Charred, new TaskDescriptor(TaskType.Combat, "n1", "n1", "5", 4));
        TaskDescriptors.put(TaskTarget.SmokedSalmon, new TaskDescriptor(TaskType.Combat, "n1", "n1", "5", 4));
        TaskDescriptors.put(TaskTarget.Dice, new TaskDescriptor(TaskType.Combat, "n1", "n1", null, null));
        TaskDescriptors.put(TaskTarget.Shiver_Money, new TaskDescriptor(TaskType.Combat, "n1", "n1", "2", 1));
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
        TaskMap.put("ladybugs", TaskTarget.Ladybug); // ?
        TaskMap.put("scared hogs", TaskTarget.ScaredHog);
        TaskMap.put("wild boars", TaskTarget.WildBoar);
        TaskMap.put("mountain goats", TaskTarget.MountainGoat);
        TaskMap.put("hoglin", TaskTarget.Hoglin);// ?
        TaskMap.put("elite mobs in world #1", TaskTarget.W1_Elite);
        TaskMap.put("wheat", TaskTarget.Wheat);
        TaskMap.put("carrots", TaskTarget.Carrot);
        TaskMap.put("potatoes", TaskTarget.Potato);
        TaskMap.put("beetroot", TaskTarget.Beetroot);
        TaskMap.put("honeycomb", TaskTarget.Honeycomb);
        TaskMap.put("riverfish", TaskTarget.Riverfish);
        TaskMap.put("fish", TaskTarget.Riverfish);
        TaskMap.put("crabs", TaskTarget.Crab);
        TaskMap.put("hermit crabs", TaskTarget.HermitCrab);
        TaskMap.put("rps", TaskTarget.Rps);
        TaskMap.put("coinflip", TaskTarget.Coinflip);
        TaskMap.put("pixel pop", TaskTarget.Pixelpop);
        TaskMap.put("items", TaskTarget.Items);
        TaskMap.put("gold from selling items", TaskTarget.Gold_Money);
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
        TaskMap.put("enderman", TaskTarget.Enderman);
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
        TaskMap.put("elite mobs in world #2", TaskTarget.W2_Elite);
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
        TaskMap.put("blubber", TaskTarget.Blubber);
        TaskMap.put("abyssal crabs", TaskTarget.AbyssalCrab);
        TaskMap.put("21", TaskTarget.Blackjack);
        TaskMap.put("silver from selling items", TaskTarget.Silver_Money);
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
        TaskMap.put("elite mobs in world #3", TaskTarget.W3_Elite);
        TaskMap.put("oinky", TaskTarget.Oinky);
        TaskMap.put("cattails", TaskTarget.Cattail);
        TaskMap.put("gloom", TaskTarget.Gloom);
        TaskMap.put("collie-flowers", TaskTarget.CollieFlower);
        TaskMap.put("goldfish retrievers", TaskTarget.GoldfishRetriever);
        TaskMap.put("bettafly", TaskTarget.Bettafly);
        TaskMap.put("soarfish", TaskTarget.Soarfish);
        TaskMap.put("guardians", TaskTarget.Guardian);
        TaskMap.put("matcher", TaskTarget.Matcher);

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
        TaskMap.put("firefoxes", TaskTarget.Firefox); // ???????????
        TaskMap.put("marshmallows", TaskTarget.Marshmallow);
        TaskMap.put("elite mobs in nightmare #1", TaskTarget.WN1_Elite);
        TaskMap.put("decay", TaskTarget.Decay);
        TaskMap.put("torchflowers or iceberg lettuce", TaskTarget.IcebergLettuce_Or_TorchFlower);
        TaskMap.put("mandrakes", TaskTarget.Mandrake);
        TaskMap.put("splinterseed", TaskTarget.Splinterseed);
        TaskMap.put("charred", TaskTarget.Charred);
        TaskMap.put("smoked salmon", TaskTarget.SmokedSalmon);
        TaskMap.put("striders", TaskTarget.Strider);
        TaskMap.put("pairadice", TaskTarget.Dice);
        TaskMap.put("shiver from selling items", TaskTarget.Shiver_Money);
    }


    public static TaskDescriptor TryGetDescriptor(String target) {
        return TaskDescriptors.getOrDefault(TaskMap.getOrDefault(target.toLowerCase(), null), null);
    }

    public static TaskDescriptor GetDescriptor(TaskTarget target) {
        return TaskDescriptors.get(target);
    }

    public static class TaskDescriptor {
        private final TaskType type;
        private final String command;
        private final String fallbackCommand;
        private final String defaultWardrobe;
        private final Integer defaultHotBarSlot;

        public TaskDescriptor(TaskType type, String command, String fallbackCommand, String wardrobe, Integer hotBarSlotId) {
            this.type = type;
            this.command = command;
            this.fallbackCommand = fallbackCommand;
            this.defaultWardrobe = wardrobe;
            this.defaultHotBarSlot = hotBarSlotId;
        }

        public String getDefaultWardrobe(){
            return defaultWardrobe;
        }

        public Integer getDefaultHotBarSlot(){
            return defaultHotBarSlot;
        }

        public String getCommand() {
            return type != TaskType.Gaming ? "warp " + command : command;
        }

        public String getFallbackCommand() {
            return type != TaskType.Gaming ? "warp " + fallbackCommand : fallbackCommand;
        }
    }

    public enum TaskType {
        Misc,
        Gaming,
        Farming,
        Combat,
        Fishing,
        Mining,
        Foraging,
    }

    public enum TaskTarget {
        // WORLD 1
        // Mining
        Coal, Iron, Copper, Gold, Redstone,

        // Foraging
        Applewood, Apple, Palm, Coconut, Ladybug,

        // Combat
        ScaredHog, WildBoar, MountainGoat, Hoglin,
        W1_Elite,

        // Farming
        Wheat, Carrot, Potato, Beetroot, Honeycomb,

        // Fishing
        Riverfish, Crab, HermitCrab,

        // Gaming
        Rps, Coinflip, Pixelpop,

        // Misc
        Items, Gold_Money, GarbageCans,

        // WORLD 2
        // Mining
        Crimsonite, Verdelith, Azuregem, Aurorium,

        // Foraging
        Bonsai, Pomegranate, Pine, Pinecone, Deadwood, Zephyr, SkyBeetle,

        // Combat
        Panda, Sniffer, Lurker, CaveCrawler, PoisonSlime, Spotter, Verdemite, Enderman, Ghast, GhastSoul, Blaze, Nrub, InfernalImp, Wick, GlowSquid, Slinker, Rodrick, SkyBeetleQueen,
        W2_Elite,

        // Farming
        Shimmer, Garlic, Corn, Shy, LavaFruit, Twine,
        AdvancedCrops,

        // Fishing
        Salmon, Koi, Axolotl, MagmaFish, MoltenJellyfish, Blubber, AbyssalCrab,

        // Gaming
        Blackjack,

        // Misc
        Silver_Money, AbyssLamp,

        // WORLD 3
        // Mining
        Brightstone, Diamond, Emerald,

        // Foraging
        Gorespore, GoresporeSpore, DuneDweller, DuneDwellerSpore, HoneyShroom, HoneySpore, DreamShroom, DreamSpore, Barky, Pinepoodle, Capsnapper,

        // Combat
        Baconwing, Camel, Bee, RoyalGuard, Breeze, DireWolf,
        W3_Elite,

        // Farming
        Oinky, Cattail, Gloom, CollieFlower,

        // Fishing
        GoldfishRetriever, Bettafly, Soarfish, Guardian,

        // Gaming
        Matcher,

        // WORLD Nightmare 1
        // Mining
        Quartz, Lapis, NetherGold, AncientDebris,

        // Foraging
        Crimson, Toquoi, Aqua, Cheruza, Winkle,

        // Combat
        Piglin, Bamboodle, Firefox, Marshmallow,
        WN1_Elite,

        // Farming
        Decay, IcebergLettuce_Or_TorchFlower, Mandrake, Splinterseed,

        // Fishing
        Charred, SmokedSalmon, Strider,

        // Gaming
        Dice,

        // Misc
        Shiver_Money,
    }
}

