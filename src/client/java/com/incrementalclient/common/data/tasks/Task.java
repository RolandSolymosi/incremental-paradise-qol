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
    W1_Coal(new NormalTask(List.of("coal", "coal ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Coal), List.of(Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    W1_ShinyCoal(new NormalTask(List.of("coal", "coal ore"), Constraint.Shiny, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Coal), List.of(Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    W1_Iron(new NormalTask(List.of("coal", "coal ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Iron), List.of(Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    W1_ShinyIron(new NormalTask(List.of("coal", "coal ore"), Constraint.Shiny, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Iron), List.of(Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    W1_Copper(new NormalTask(List.of("coal", "coal ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Copper), List.of(Warp.W1_Copper, Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    W1_ShinyCopper(new NormalTask(List.of("coal", "coal ore"), Constraint.Shiny, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Copper), List.of(Warp.W1_Copper, Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    W1_Gold(new NormalTask(List.of("coal", "coal ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Gold), List.of(Warp.W1_Gold, Warp.W1_Copper, Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    W1_ShinyGold(new NormalTask(List.of("coal", "coal ore"), Constraint.Shiny, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Gold), List.of(Warp.W1_Gold, Warp.W1_Copper, Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    W1_Redstone(new NormalTask(List.of("coal", "coal ore"), null, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Redstone), List.of(Warp.W1_RedStone, Warp.W1_Gold, Warp.W1_Copper, Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),
    W1_ShinyRedstone(new NormalTask(List.of("coal", "coal ore"), Constraint.Shiny, TaskType.Mining, DefaultWardrobe.Mining, Tool.Pickaxe, List.of(Target.W1_Redstone), List.of(Warp.W1_RedStone, Warp.W1_Gold, Warp.W1_Copper, Warp.W1_Iron, Warp.W1_Coal, Warp.W1_Mines, Warp.W1_Spawn))),

    W1_Applewood(new NormalTask(List.of("applewood"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W1_AppleTree), List.of(Warp.W1_Spawn))),
    W1_Apple(new NormalTask(List.of("apple"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W1_AppleTree), List.of(Warp.W1_Spawn))),
    W1_LargeApple(new NormalTask(List.of("apples"), Constraint.Large, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W1_AppleTree), List.of(Warp.W1_Spawn))),
    W1_Palm(new NormalTask(List.of("palm"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W1_PalmTree), List.of(Warp.W1_Crab, Warp.W1_Spawn))),
    W1_Coconut(new NormalTask(List.of("coconuts"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W1_PalmTree), List.of(Warp.W1_Crab, Warp.W1_Spawn))),
    W1_LargeCoconut(new NormalTask(List.of("coconuts"), Constraint.Large, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W1_PalmTree), List.of(Warp.W1_Crab, Warp.W1_Spawn))),
    W1_Ladybug(new NormalTask(List.of("ladybugs"), null, TaskType.Foraging, DefaultWardrobe.Foraging, Tool.Axe, List.of(Target.W1_Ladybug), List.of(Warp.W1_Beetroot, Warp.W1_Spawn))),

    W1_ScaredHog(new NormalTask(List.of("scared hogs"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W1_ScaredHog), List.of(Warp.W1_Spawn))),
    W1_Goat(new NormalTask(List.of("mountain goats"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W1_Goat), List.of(Warp.W1_Spawn))),
    W1_WildHog(new NormalTask(List.of("wild boars"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W1_WildBoar), List.of(Warp.W1_Hoglin, Warp.W1_Spawn))),
    W1_Hoglin(new NormalTask(List.of("hoglin"), null, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W1_Hoglin), List.of(Warp.W1_Hoglin, Warp.W1_Spawn))),
    W1_Elite(new NormalTask(List.of("elite mobs in world #1"), Constraint.Elite, TaskType.Combat, DefaultWardrobe.Combat, Tool.Melee, List.of(Target.W1_ScaredHog, Target.W1_Goat, Target.W1_WildBoar), List.of(Warp.W1_Spawn))),

    W1_Wheat(new NormalTask(List.of("wheat"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Wheat), List.of(Warp.W1_Wheat, Warp.W1_Spawn))),
    W1_Carrot(new NormalTask(List.of("carrots"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Carrot), List.of(Warp.W1_Carrot, Warp.W1_Spawn))),
    W1_Potato(new NormalTask(List.of("potatoes"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Potato), List.of(Warp.W1_Potato, Warp.W1_Spawn))),
    W1_Beetroot(new NormalTask(List.of("beetroot"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Beetroot), List.of(Warp.W1_Beetroot, Warp.W1_Spawn))),
    W1_HoneyComb(new NormalTask(List.of("honeycomb"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Honeycomb), List.of(Warp.W1_Spawn))),
    W1_Crops(new NormalTask(List.of("crops"), null, TaskType.Farming, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Wheat, Target.W1_Carrot, Target.W1_Potato, Target.W1_Beetroot), List.of(Warp.W1_Carrot, Warp.W1_Spawn))),

    W1_Riverfish(new NormalTask(List.of("riverfish"), null, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W1_Riverfish), List.of(Warp.W1_Carrot, Warp.W1_Spawn))),
    W1_ColoredRiverfish(new NormalTask(List.of("riverfish"), Constraint.Color, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W1_Riverfish), List.of(Warp.W1_Carrot, Warp.W1_Spawn))),
    W1_ConsecutiveFish(new NormalTask(List.of("fish"), Constraint.Consecutive, TaskType.Fishing, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W1_Riverfish), List.of(Warp.W1_Carrot, Warp.W1_Spawn))),
    W1_Crab(new NormalTask(List.of("crabs"), null, TaskType.CombatFishing, DefaultWardrobe.CombatFishing, Tool.Spear, List.of(Target.W1_Crab), List.of(Warp.W1_Crab, Warp.W1_Spawn))),
    W1_HermitCrab(new NormalTask(List.of("hermit crabs"), null, TaskType.CombatFishing, DefaultWardrobe.CombatFishing, Tool.Spear, List.of(Target.W1_HermitCrab), List.of(Warp.W1_Crab, Warp.W1_Spawn))),

    W1_PlayCoinflip(new GamingTask(List.of("coinflip"), GameKind.Rps, null, List.of(Warp.W1_Spawn))),
    W1_PlayRps(new GamingTask(List.of("rps"), GameKind.Rps, null, List.of(Warp.W1_Spawn))),
    W1_EarnScorePixelpop(new GamingTask(List.of("pixel pop"), GameKind.Pixelpop, Constraint.Score, List.of(Warp.W1_Spawn))),
    W1_EarnTicketPixelpop(new GamingTask(List.of("rps"), GameKind.Pixelpop, Constraint.Ticket, List.of(Warp.W1_Spawn))),

    W1_SellItems(new NormalTask(List.of("items"), null, TaskType.Misc, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Carrot), List.of(Warp.W1_Carrot, Warp.W1_Spawn))),
    W1_EarnGold(new NormalTask(List.of("gold from selling items"), null, TaskType.Misc, DefaultWardrobe.Farming, Tool.Hoe, List.of(Target.W1_Carrot), List.of(Warp.W1_Carrot, Warp.W1_Spawn))),
    W1_CleanGarbage(new NormalTask(List.of("garbage cans"), null, TaskType.Misc, DefaultWardrobe.Fishing, Tool.Spear, List.of(Target.W1_GarbageCan), List.of(Warp.W1_Spawn))),
    ;

    private final ITask task;

    Task(ITask task) {
        this.task = task;
    }

    private static final Map<String, Map<Constraint, Task>> Tasks = Collections.unmodifiableMap(Arrays
            .stream(Task.values())
            .flatMap(t -> t.task.names().stream().map(n -> Map.entry(n, t)))
            .collect(Collectors.groupingBy(
                    Map.Entry::getKey,
                    Collectors.toMap(
                            e -> e.getValue().task.constraint().isPresent() ? e.getValue().task.constraint().get() : null,
                            Map.Entry::getValue
                    )
            )));

    public static Task TryGetTask(String target, String constraint) {
        var taskCollection = Tasks.getOrDefault(target.toLowerCase(), null);
        if (taskCollection != null) {
            var knownConstraint = Constraint.find(constraint);
            return taskCollection.get(knownConstraint);
        }
        return null;
    }
}

