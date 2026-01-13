package com.incrementalclient.common.data.targets;

import com.incrementalclient.common.data.Region;
import com.incrementalclient.common.data.World;
import com.incrementalclient.common.data.targets.abstractions.BlockTarget;
import com.incrementalclient.common.data.targets.abstractions.EntityTarget;
import com.incrementalclient.common.data.targets.abstractions.ITarget;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.stream.Collectors;

public enum Target {
    Fossil_Sand(new BlockTarget("sandfossil", "Sand Fossil", List.of(Region.W1_CoalMine, Region.W1_IronMine, Region.W1_CopperMine, Region.W1_GoldMine, Region.W1_RedstoneMine, Region.W2_Lush, Region.W2_Veil, Region.W2_Infernal, Region.W2_Abyss, Region.W3_Underside, Region.W4_CityOutskirt, Region.W4_Alpha), t -> true, Blocks.SUSPICIOUS_SAND)),
    Fossil_Gravel(new BlockTarget("gravelfossil", "Gravel Fossil", List.of(Region.W4_Delta), t -> true, Blocks.SUSPICIOUS_GRAVEL)),

    // --- World 1 ---
    W1_Coal(new BlockTarget("coalore", "Coal", List.of(Region.W1_CoalMine), t -> true, Blocks.DEEPSLATE_COAL_ORE)),
    W1_Iron(new BlockTarget("ironore", "Iron", List.of(Region.W1_IronMine), t -> true, Blocks.DEEPSLATE_IRON_ORE)),
    W1_Copper(new BlockTarget("copperore", "Copper", List.of(Region.W1_CopperMine), t -> true, Blocks.DEEPSLATE_COPPER_ORE)),
    W1_Gold(new BlockTarget("goldore", "Gold", List.of(Region.W1_GoldMine), t -> true, Blocks.DEEPSLATE_GOLD_ORE)),
    W1_Redstone(new BlockTarget("redstoneore", "Redstone", List.of(Region.W1_RedstoneMine), t -> true, Blocks.DEEPSLATE_REDSTONE_ORE)),
    W1_AppleTree(new BlockTarget("applewood", "Apple Tree", List.of(Region.W1_Overworld), t -> true, Blocks.JUNGLE_WOOD, Blocks.SPRUCE_PLANKS, Blocks.JUNGLE_PLANKS, Blocks.BROWN_MUSHROOM_BLOCK)),
    W1_PalmTree(new BlockTarget("palm", "Palm Tree", List.of(Region.W1_Crab), t -> true, Blocks.JUNGLE_WOOD, Blocks.SPRUCE_SLAB, Blocks.BROWN_MUSHROOM_BLOCK)),
    W1_Wheat(new BlockTarget("wheat", "Wheat", List.of(Region.W1_Overworld), t -> true, Blocks.WHEAT)),
    W1_Carrot(new BlockTarget("carrot", "Carrot", List.of(Region.W1_Overworld), t -> true, Blocks.CARROTS)),
    W1_Potato(new BlockTarget("potato", "Potato", List.of(Region.W1_Overworld), t -> true, Blocks.POTATOES)),
    W1_Beetroot(new BlockTarget("beetroot", "Beetroot", List.of(Region.W1_Overworld), t -> true, Blocks.BEETROOTS)),
    W1_Honeycomb(new BlockTarget("honeycomb", "Honeycomb", List.of(Region.W1_Overworld), t -> t.get(Properties.HONEY_LEVEL) > 0, Blocks.BEE_NEST)),
    W1_Ladybug(new EntityTarget("ladybug", "Ladybug", List.of(Region.W1_Overworld), t -> true, EntityType.ARMOR_STAND)),
    W1_ScaredHog(new EntityTarget("scaredhog", "Scared Hog", List.of(Region.W1_Overworld), t -> true, EntityType.PIG)),
    W1_WildBoar(new EntityTarget("wildboar", "Wild Boar", List.of(Region.W1_Overworld), t -> true, EntityType.HOGLIN)),
    W1_Goat(new EntityTarget("mountaingoat", "Mountain Goat", List.of(Region.W1_Overworld), t -> true, EntityType.GOAT)),
    W1_Riverfish(new EntityTarget("riverfish", "River Fish", List.of(Region.W1_Overworld), t -> true, EntityType.TROPICAL_FISH)),
    W1_Crab(new EntityTarget("crab", "Crab", List.of(Region.W1_Overworld), t -> true, EntityType.PIG)),
    W1_HermitCrab(new EntityTarget("hermitcrab", "Hermit Crab", List.of(Region.W1_Overworld), t -> true, EntityType.PIG)),
    W1_Hoglin(new EntityTarget("hoglin", "Hoglin", List.of(Region.W1_Overworld), t -> true, EntityType.HOGLIN)),
    W1_GarbageCan(new EntityTarget("garbagecan", "Garbage Can", List.of(Region.W1_Overworld), t -> true, EntityType.ARMOR_STAND)),

    // --- World 2 ---
    W2_Crimsonite(new BlockTarget("crimsonite", "Crimsonite", List.of(Region.W2_Lush), t -> true, Blocks.RED_CONCRETE, Blocks.RED_WOOL, Blocks.RED_TERRACOTTA, Blocks.CRIMSON_PLANKS, Blocks.PURPLE_TERRACOTTA)),
    W2_Verdelith(new BlockTarget("verdelith", "Verdelith", List.of(Region.W2_Veil), t -> true, Blocks.GREEN_CONCRETE, Blocks.GREEN_TERRACOTTA, Blocks.GREEN_WOOL)),
    W2_Azuregem(new BlockTarget("azuregem", "Azuregem", List.of(Region.W2_Infernal), t -> true, Blocks.PRISMARINE, Blocks.LIGHT_BLUE_TERRACOTTA)),
    W2_Aurorium(new BlockTarget("aurorium", "Aurorium", List.of(Region.W2_Abyss), t -> true, Blocks.YELLOW_TERRACOTTA, Blocks.ORANGE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE, Blocks.YELLOW_CONCRETE_POWDER, Blocks.SPONGE)),
    W2_SkyBeetle(new EntityTarget("skybeetle", "Sky Beetle", List.of(Region.W2_Overworld), t -> true, EntityType.ARMOR_STAND)),
    W2_Panda(new EntityTarget("panda", "Panda", List.of(Region.W2_Overworld), t -> true, EntityType.PANDA)),
    W2_Sniffer(new EntityTarget("sniffer", "Sniffer", List.of(Region.W2_Overworld), t -> true, EntityType.SNIFFER)),
    W2_Lurker(new EntityTarget("lurker", "Lurker", List.of(Region.W2_Lush), t -> true, EntityType.ARMOR_STAND)), // JSON used candle identifier, mapped to stand for custom model logic
    W2_AncientLurker(new EntityTarget("ancientlurker", "Ancient Lurker", List.of(Region.W2_Lush), t -> true, EntityType.ARMOR_STAND)),
    W2_CaveCrawler(new EntityTarget("cavecrawler", "Cave Crawler", List.of(Region.W2_Lush), t -> true, EntityType.CAVE_SPIDER)),
    W2_PoisonSlime(new EntityTarget("poisonslime", "Poison Slime", List.of(Region.W2_Lush), t -> true, EntityType.SLIME)),
    W2_Spotter(new EntityTarget("spotter", "Spotter", List.of(Region.W2_Veil), t -> true, EntityType.PHANTOM)),
    W2_Verdemite(new EntityTarget("verdemite", "Verdemite", List.of(Region.W2_Veil), t -> true, EntityType.ENDERMITE)),
    W2_Enderman(new EntityTarget("enderman", "Enderman", List.of(Region.W2_Veil), t -> true, EntityType.ENDERMAN)),
    W2_Ghast(new EntityTarget("ghast", "Ghast", List.of(Region.W2_Infernal), t -> true, EntityType.GHAST)),
    W2_GhastSoul(new EntityTarget("ghastsoul", "Ghast Soul", List.of(Region.W2_Infernal), t -> true, EntityType.PLAYER)),
    W2_Blaze(new EntityTarget("blaze", "Blaze", List.of(Region.W2_Infernal), t -> true, EntityType.BLAZE)),
    W2_Nrub(new EntityTarget("nrub", "Nrub", List.of(Region.W2_Infernal), t -> true, EntityType.BLAZE)),
    W2_InfernalImp(new EntityTarget("infernalimp", "Infernal Imp", List.of(Region.W2_Infernal), t -> true, EntityType.PLAYER)),
    W2_Wick(new EntityTarget("wick", "Wick", List.of(Region.W2_Abyss), t -> true, EntityType.ARMOR_STAND)),
    W2_GlowSquid(new EntityTarget("glowsquid", "Glow Squid", List.of(Region.W2_Abyss), t -> true, EntityType.GLOW_SQUID)),
    W2_Slinker(new EntityTarget("slinker", "Slinker", List.of(Region.W2_Abyss), t -> true, EntityType.PIG)),
    W2_Warden(new EntityTarget("warden", "Warden", List.of(Region.W2_Abyss), t -> true, EntityType.WARDEN)),
    W2_Lavafruit(new EntityTarget("lavafruit", "Lavafruit", List.of(Region.W2_Infernal), t -> true, EntityType.MAGMA_CUBE)),
    W2_Twine(new EntityTarget("twine", "Twine", List.of(Region.W2_Veil), t -> true, EntityType.ARMOR_STAND)),
    W2_Salmon(new EntityTarget("salmon", "Salmon", List.of(Region.W2_Overworld), t -> true, EntityType.SALMON)),
    W2_Koi(new EntityTarget("koi", "Koi", List.of(Region.W2_Overworld), t -> true, EntityType.TROPICAL_FISH)),
    W2_Axolotl(new EntityTarget("axolotl", "Axolotl", List.of(Region.W2_Overworld), t -> true, EntityType.AXOLOTL)),
    W2_Magmafish(new EntityTarget("magmafish", "Magmafish", List.of(Region.W2_Infernal), t -> true, EntityType.TROPICAL_FISH)),
    W2_MoltenJellyfish(new EntityTarget("moltenjellyfish", "Molten Jellyfish", List.of(Region.W2_Infernal), t -> true, EntityType.ARMOR_STAND)),
    W2_Bubbler(new EntityTarget("bubbler", "Bubbler", List.of(Region.W2_Infernal), t -> true, EntityType.ARMOR_STAND)),

    // --- World 3 ---
    W3_Brightstone(new BlockTarget("brightstone", "Brightstone", List.of(Region.W3_Mine), t -> true, Blocks.SEA_LANTERN)),
    W3_Diamond(new BlockTarget("diamond", "Diamond", List.of(Region.W3_Mine), t -> true, Blocks.DEEPSLATE_DIAMOND_ORE)),
    W3_Emerald(new BlockTarget("emerald", "Emerald", List.of(Region.W3_Mine), t -> true, Blocks.DEEPSLATE_EMERALD_ORE)),
    W3_Catsnapper(new EntityTarget("catsnapper", "Catsnapper", List.of(Region.W3_Overworld), t -> true, EntityType.ARMOR_STAND)),
    W3_Baconwing(new EntityTarget("baconwing", "Baconwing", List.of(Region.W3_Sty), t -> true, EntityType.PLAYER)),
    W3_Camel(new EntityTarget("camel", "Camel", List.of(Region.W3_Beach), t -> true, EntityType.CAMEL)),
    W3_Djinn(new EntityTarget("djinn", "Djinn", List.of(Region.W3_Beach), t -> true, EntityType.SKELETON)),
    W3_Bee(new EntityTarget("bee", "Bee", List.of(Region.W3_Topside), t -> true, EntityType.BEE)),
    W3_RoyalGuard(new EntityTarget("royalguard", "Royal Guard", List.of(Region.W3_Topside), t -> true, EntityType.PLAYER)),
    W3_Breeze(new EntityTarget("breeze", "Breeze", List.of(Region.W3_Mine), t -> true, EntityType.BREEZE)),
    W3_DireWolf(new EntityTarget("direwolf", "Dire Wolf", List.of(Region.W3_Canine), t -> true, EntityType.WOLF)),
    W3_Goldfish(new EntityTarget("goldfish", "Goldfish Retriever", List.of(Region.W3_Canine), t -> true, EntityType.TROPICAL_FISH)),
    W3_Bettafly(new EntityTarget("bettafly", "Bettafly", List.of(Region.W3_Sty), t -> true, EntityType.SALMON)),
    W3_Soarfish(new EntityTarget("soarfish", "Soarfish", List.of(Region.W3_Beach), t -> true, EntityType.SALMON)),
    W3_LegendaryBettafly(new EntityTarget("legbettafly", "Legendary Bettafly", List.of(Region.W3_Sty), t -> true, EntityType.SALMON)),
    W3_LegendarySoarfish(new EntityTarget("legsoarfish", "Legendary Soarfish", List.of(Region.W3_Beach), t -> true, EntityType.SALMON)),

    // --- World 4 ---
    W4_Cheddore(new BlockTarget("cheddore", "Cheddore", List.of(Region.W4_CityOutskirt), t -> true, Blocks.RAW_GOLD_BLOCK)),
    W4_BlueCheese(new BlockTarget("bluecheese", "Blue Cheese", List.of(Region.W4_CityOutskirt), t -> true, Blocks.WARPED_WART_BLOCK)),
    W4_Glowdust(new BlockTarget("glowdust", "Glowdust", List.of(Region.W4_Beta), t -> true, Blocks.YELLOW_CONCRETE, Blocks.RAW_GOLD_BLOCK, Blocks.YELLOW_TERRACOTTA, Blocks.YELLOW_CONCRETE_POWDER, Blocks.YELLOW_WOOL)),
    W4_Slimecrust(new BlockTarget("slimecrust", "Slimecrust", List.of(Region.W4_Beta), t -> true, Blocks.LIME_TERRACOTTA, Blocks.GREEN_WOOL, Blocks.LIME_CONCRETE, Blocks.LIME_WOOL, Blocks.EMERALD_BLOCK)),
    W4_Voidshard(new BlockTarget("voidshard", "Voidshard", List.of(Region.W4_Beta), t -> true, Blocks.BLUE_TERRACOTTA, Blocks.PURPLE_WOOL, Blocks.PURPLE_CONCRETE, Blocks.PURPLE_CONCRETE_POWDER)),
    W4_Petrafin(new BlockTarget("petrafin", "Petrafin", List.of(Region.W4_Delta), t -> true, Blocks.DEAD_BRAIN_CORAL_BLOCK, Blocks.ORANGE_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.ORANGE_CONCRETE)),
    W4_Bat(new EntityTarget("bat", "Bat", List.of(Region.W4_Homestead), t -> true, EntityType.BAT)),
    W4_Rat(new EntityTarget("rat", "Rat", List.of(Region.W4_Sewer), t -> true, EntityType.SILVERFISH)),
    W4_Frog(new EntityTarget("frog", "Frog", List.of(Region.W4_Alpha), t -> true, EntityType.FROG)),
    W4_Sniper(new EntityTarget("sniper", "Sniper", List.of(Region.W4_Beta), t -> true, EntityType.STRAY)),
    W4_AngryMiner(new EntityTarget("angryminer", "Angry Miner", List.of(Region.W4_Beta), t -> true, EntityType.PLAYER)),
    W4_Ravager(new EntityTarget("ravager", "Ravager", List.of(Region.W4_Beta), t -> true, EntityType.RAVAGER)),
    W4_Pearl(new EntityTarget("pearl", "Pearl", List.of(Region.W4_Delta), t -> true, EntityType.INTERACTION)),
    W4_Catfish(new EntityTarget("catfish", "Catfish", List.of(Region.W4_Sewer), t -> true, EntityType.TROPICAL_FISH)),
    W4_Piranha(new EntityTarget("piranha", "Piranha", List.of(Region.W4_Alpha), t -> true, EntityType.TROPICAL_FISH)),
    W4_Clownfish(new EntityTarget("clownfish", "Clownfish", List.of(Region.W4_Delta), t -> true, EntityType.TROPICAL_FISH)),
    W4_Cichlid(new EntityTarget("cichlid", "Cichlid", List.of(Region.W4_Delta), t -> true, EntityType.TROPICAL_FISH)),
    W4_Parrotfish(new EntityTarget("parrotfish", "Parrotfish", List.of(Region.W4_Delta), t -> true, EntityType.TROPICAL_FISH)),
    W4_RedEmperor(new EntityTarget("redemperor", "Red Emperor", List.of(Region.W4_Delta), t -> true, EntityType.TROPICAL_FISH)),
    W4_Cat(new EntityTarget("cat", "Cat", List.of(Region.W4_Sewer), t -> true, EntityType.CAT)),
    W4_Pufferfish(new EntityTarget("pufferfish", "Pufferfish", List.of(Region.W4_Delta), t -> true, EntityType.PUFFERFISH)),
    W4_SewerChest(new BlockTarget("sewerchest", "Sewer Chest", List.of(Region.W4_Sewer), t -> true, Blocks.CHEST)),
    W4_TurtleEgg(new EntityTarget("turtleegg", "Turtle Egg", List.of(Region.W4_Delta), t -> true, EntityType.INTERACTION)),

    // --- Nightmare 1 ---
    N1_Quartz(new BlockTarget("quartzore", "Quartz", List.of(Region.WN1_QuartzMine), t -> true, Blocks.NETHER_QUARTZ_ORE)),
    N1_Lapis(new BlockTarget("lapisore", "Lapis", List.of(Region.WN1_LapisMine), t -> true, Blocks.DEEPSLATE_LAPIS_ORE)),
    N1_NetherGold(new BlockTarget("nethergoldore", "Nether Gold", List.of(Region.WN1_NetherGoldMine), t -> true, Blocks.NETHER_GOLD_ORE)),
    N1_AncientDebris(new BlockTarget("ancientdebris", "Ancient Debris", List.of(Region.WN1_AncientDebrisMine), t -> true, Blocks.ANCIENT_DEBRIS)),
    N1_Piglin(new EntityTarget("piglin", "Piglin", List.of(Region.WN1_Overworld), t -> true, EntityType.PIGLIN)),
    N1_Bamboodle(new EntityTarget("bamboodle", "Bamboodle", List.of(Region.WN1_Overworld), t -> true, EntityType.PANDA)),
    N1_Firefox(new EntityTarget("firefox", "Firefox", List.of(Region.WN1_Overworld), t -> true, EntityType.FOX)),
    N1_Marshmallow(new EntityTarget("marshmallow", "Marshmallow", List.of(Region.WN1_Overworld), t -> true, EntityType.GHAST)),
    N1_Mandrake(new EntityTarget("mandrake", "Mandrake", List.of(Region.WN1_Overworld), t -> true, EntityType.PIG)),
    N1_Splinterseed(new EntityTarget("splinterseed", "Splinterseed", List.of(Region.WN1_Overworld), t -> true, EntityType.INTERACTION)),
    N1_Charred(new EntityTarget("charred", "Charred", List.of(Region.WN1_Overworld), t -> true, EntityType.TROPICAL_FISH)),
    N1_SmokedSalmon(new EntityTarget("smokedsalmon", "Smoked Salmon", List.of(Region.WN1_Overworld), t -> true, EntityType.SALMON)),
    N1_Strider(new EntityTarget("strider", "Strider", List.of(Region.WN1_Overworld), t -> true, EntityType.STRIDER));
    ;

    private static final Map<String, ITarget> BY_NAME = Arrays
            .stream(Target.values())
            .map(e -> e.target)
            .collect(Collectors.toUnmodifiableMap(ITarget::name, e -> e));

    private static final Map<net.minecraft.block.Block, List<BlockTarget>> BY_TYPE_BLOCK = Collections.unmodifiableMap(Arrays
            .stream(Target.values()).map(e -> e.target)
            .filter(e -> e instanceof BlockTarget)
            .map(e -> (BlockTarget) e)
            .flatMap(bt -> bt.getBlocks().stream().map(b -> Map.entry(b, bt)))
            .collect(Collectors.groupingBy(Map.Entry::getKey,
                    Collectors.mapping(Map.Entry::getValue, Collectors.toList()))));

    private static final Map<EntityType<?>, List<EntityTarget>> BY_TYPE_ENTITY = Collections.unmodifiableMap(Arrays
            .stream(Target.values()).map(e -> e.target)
            .filter(e -> e instanceof EntityTarget)
            .map(e -> (EntityTarget) e)
            .collect(Collectors.groupingBy(
                    EntityTarget::getType,
                    Collectors.toUnmodifiableList()
            )));

    private final ITarget target;

    Target(ITarget Target) {
        this.target = Target;
    }

    public static ITarget byName(String name) {
        return BY_NAME.get(name);
    }

    public static List<BlockTarget> find(net.minecraft.world.World world, BlockPos pos) {
        var block = world.getBlockState(pos);
        if (block != null) {
            var possibleTargets = BY_TYPE_BLOCK.get(block.getBlock());
            if (possibleTargets != null) {
                return possibleTargets.stream().filter(x -> x.matches(world, pos)).toList();
            }
        }
        return List.of();
    }

    public static Optional<BlockTarget> find(World world, Block block) {
        var targets = BY_TYPE_BLOCK.getOrDefault(block, null);
        if (targets != null) {
            return targets.stream().filter(p -> p.matches(world, block)).findFirst();
        }
        return Optional.empty();
    }

    public static List<EntityTarget> find(net.minecraft.entity.Entity entity) {
        var possibleTargets = BY_TYPE_ENTITY.get(entity.getType());
        if (possibleTargets != null) {
            return possibleTargets.stream().filter(x -> x.matches(entity)).toList();
        }
        return List.of();
    }
}