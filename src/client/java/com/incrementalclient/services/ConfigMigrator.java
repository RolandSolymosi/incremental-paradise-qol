package com.incrementalclient.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.Strictness;
import com.incrementalclient.common.data.skills.*;
import com.incrementalclient.common.data.tasks.Task;
import com.incrementalclient.common.utils.typeAdapter.OldColorAdapter;
import com.incrementalclient.common.utils.typeAdapter.OldTaskAdapter;
import com.incrementalclient.featues.AutoSkill;
import com.incrementalclient.featues.BalloonRopeHider;
import com.incrementalclient.featues.CommandAliases;
import com.incrementalclient.featues.PxpCalculation;
import com.incrementalclient.featues.Tasking.AutoSwapLoadout;
import com.incrementalclient.featues.Tasking.TaskingOverrides;
import com.incrementalclient.featues.Tasking.TicketTaskOverride;
import com.incrementalclient.featues.Tasking.WarpNextHotkey;
import com.incrementalclient.hud.ConsumableTimerElement;
import com.incrementalclient.hud.TaskTrackerElement;
import com.incrementalclient.interfaces.Configurable;
import net.fabricmc.loader.api.FabricLoader;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigMigrator {
    public static final Path oldConfigPath = FabricLoader.getInstance().getConfigDir().resolve("incremental-qol.json5");
    public static final Path aliasPath = FabricLoader.getInstance().getConfigDir().resolve("incremental_paradise_aliases.json");

    public static boolean hasOldConfig() {
        return Files.exists(oldConfigPath);
    }

    public static void convertOldConfig(Configurable<?>[] configurableServices) {
        OldConfig oldConfig = parseOldConfig();
        for (Configurable<?> conf : configurableServices) {
            if (conf instanceof BalloonRopeHider) {
                conf.copyFrom(convertBalloonRopeHider(oldConfig));
            } else if (conf instanceof AutoSwapLoadout) {
                conf.copyFrom(convertAutoSwapLoadout(oldConfig));
            } else if (conf instanceof TaskTrackerElement) {
                conf.copyFrom(convertTaskTrackerElement(oldConfig));
            } else if (conf instanceof AutoSkill) {
                conf.copyFrom(convertAutoSkill(oldConfig));
            } else if (conf instanceof PxpCalculation) {
                conf.copyFrom(convertPxpCalculation(oldConfig));
            } else if (conf instanceof PxpCalculation) {
                conf.copyFrom(convertPxpCalculation(oldConfig));
            } else if (conf instanceof TaskingOverrides) {
                conf.copyFrom(convertTaskingOverrides(oldConfig));
            } else if (conf instanceof WarpNextHotkey) {
                conf.copyFrom(convertWarpNextHotkey(oldConfig));
            } else if (conf instanceof ConsumableTimerElement) {
                conf.copyFrom(convertConsumableTimerElement(oldConfig));
            } else if (conf instanceof CommandAliases) {
                var oldAliases = parseOldAliases();
                if (oldAliases != null) {
                    conf.copyFrom(convertAliases(oldAliases));
                }
            }
        }
    }

    public static OldConfig parseOldConfig() {
        try {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Color.class, new OldColorAdapter())
                    .registerTypeAdapter(Task.class, new OldTaskAdapter())
                    .setStrictness(Strictness.LENIENT)
                    .setPrettyPrinting()
                    .create();

            String content = Files.readString(ConfigMigrator.oldConfigPath);
            JsonObject root = gson.fromJson(content, JsonObject.class);
            if (root == null) return null;

            JsonObject taskOverridesJson = root.getAsJsonObject("taskOverrides");
            taskOverridesJson.remove("Astromite");
            taskOverridesJson.remove("Astromold");

            OldConfig loadedData = gson.fromJson(root, ConfigMigrator.OldConfig.class);
            return loadedData;
        } catch (Exception e) {
            System.err.println("[Config] Failed to load config: " + e.getMessage());
        }
        return null;
    }

    public static HashMap<String, String> parseOldAliases() {
        try {
            Gson gson = new GsonBuilder()
                    .setStrictness(Strictness.LENIENT)
                    .setPrettyPrinting()
                    .create();

            String content = Files.readString(ConfigMigrator.aliasPath);
            var aliases = gson.fromJson(content, HashMap.class);
            if (aliases == null) return null;
            return aliases;
        } catch (Exception e) {
            System.err.println("[Config] Failed to load config: " + e.getMessage());
        }
        return null;
    }

    public static BalloonRopeHider.Configuration convertBalloonRopeHider(OldConfig oldConfig) {
        BalloonRopeHider.Configuration conf = new BalloonRopeHider.Configuration();
        conf.isHidden = !oldConfig.balloonRopeEnabled;
        return conf;
    }

    public static AutoSwapLoadout.Configuration convertAutoSwapLoadout(OldConfig oldConfig) {
        AutoSwapLoadout.Configuration conf = new AutoSwapLoadout.Configuration();
        conf.enableWardrobeSwap = oldConfig.autoSwapWardrobe;
        conf.enableToolSwap = oldConfig.autoSwapTools;
        conf.combatWardrobeName = oldConfig.combatWardrobeName;
        conf.meleeWeaponSlot = oldConfig.meleeWeaponSlot;
        conf.rangedWeaponSlot = oldConfig.rangedWeaponSlot;
        conf.miningWardrobeName = oldConfig.miningWardrobeName;
        conf.miningWeaponSlot = oldConfig.miningWeaponSlot;
        conf.foragingWardrobeName = oldConfig.foragingWardrobeName;
        conf.foragingWeaponSlot = oldConfig.foragingWeaponSlot;
        conf.farmingWardrobeName = oldConfig.farmingWardrobeName;
        conf.farmingWeaponSlot = oldConfig.farmingWeaponSlot;
        conf.fishingWardrobeName = oldConfig.fishingWardrobeName;
        conf.combatFishingWardrobeName = oldConfig.combatFishingWardrobeName;
        conf.fishingWeaponSlot = oldConfig.fishingWeaponSlot;
        return conf;
    }

    public static TaskTrackerElement.Configuration convertTaskTrackerElement(OldConfig oldConfig) {
        TaskTrackerElement.Configuration conf = new TaskTrackerElement.Configuration();
        conf.enabled = oldConfig.isHudEnabled;
        // NOTE: Consider removing these 3 if the hud is redesigned
        conf.deltaX = oldConfig.hudPosX;
        conf.deltaY = oldConfig.hudPosY;
        conf.scale = (float) oldConfig.hudScale;
        conf.taskHudBackgroundOpacity = oldConfig.hudBackgroundOpacity;
        // TODO: Add the hide on boss fight when feature is readded
        conf.textColor = oldConfig.textColor.getRGB() & 0xFFFFFF;
        conf.worldColor = oldConfig.worldColor.getRGB() & 0xFFFFFF;
        conf.taskColor = oldConfig.taskColor.getRGB() & 0xFFFFFF;
        conf.socialiteColor = oldConfig.socialiteColor.getRGB() & 0xFFFFFF;
        conf.progressColor = oldConfig.progressColor.getRGB() & 0xFFFFFF;
        conf.targetColor = oldConfig.targetColor.getRGB() & 0xFFFFFF;
        conf.completeColor = oldConfig.completeColor.getRGB() & 0xFFFFFF;
        conf.ticketColor = oldConfig.ticketColor.getRGB() & 0xFFFFFF;
        return conf;
    }

    public static ConsumableTimerElement.Configuration convertConsumableTimerElement(OldConfig oldConfig) {
        ConsumableTimerElement.Configuration conf = new ConsumableTimerElement.Configuration();
        conf.isConsumableHudEnabled = oldConfig.isConsumableHudEnabled;
        // NOTE: Consider removing these 3 if the hud is redesigned
        conf.deltaX = oldConfig.consumableHudPosX;
        conf.deltaY = oldConfig.consumableHudPosY;
        conf.scale = (float) oldConfig.consumableHudScale;
        conf.consumableHudBackgroundOpacity = oldConfig.consumableHudBackgroundOpacity;
        // TODO: Add the hide on boss fight when feature is readded
        conf.consumableTimerColor = oldConfig.consumableTimerColor.getRGB() & 0xFFFFFF;
        conf.consumableTimeColor = oldConfig.consumableTimeColor.getRGB() & 0xFFFFFF;
        return conf;
    }

    public static AutoSkill.Configuration convertAutoSkill(OldConfig oldConfig) {
        AutoSkill.Configuration conf = new AutoSkill.Configuration();
        conf.enabled = oldConfig.autoSkillLeveling;
        conf.normalCombat = convertSkillLevelList(oldConfig.normalCombatSkills);
        conf.normalMining = convertSkillLevelList(oldConfig.normalMiningSkills);
        conf.normalForaging = convertSkillLevelList(oldConfig.normalForagingSkills);
        conf.normalFarming = convertSkillLevelList(oldConfig.normalFarmingSkills);
        conf.normalSpearFishing = convertSkillLevelList(oldConfig.normalSpearFishingSkills);
        conf.normalSharpshooting = convertSkillLevelList(oldConfig.normalSharpshootingSkills);
        conf.normalExcavation = convertSkillLevelList(oldConfig.normalExcavationSkills);
        conf.nightmareCombat = convertSkillLevelList(oldConfig.nightmareCombatSkills);
        conf.nightmareMining = convertSkillLevelList(oldConfig.nightmareMiningSkills);
        conf.nightmareForaging = convertSkillLevelList(oldConfig.nightmareForagingSkills);
        conf.nightmareFarming = convertSkillLevelList(oldConfig.nightmareFarmingSkills);
        conf.nightmareSpearFishing = convertSkillLevelList(oldConfig.nightmareSpearFishingSkills);
        conf.nightmareSharpshooting = convertSkillLevelList(oldConfig.nightmareSharpshootingSkills);
        return conf;
    }

    private static <T extends Skill> List<AutoSkill.Configuration.SkillLevel<T>> convertSkillLevelList(List<T> skillLevelList) {
        List<AutoSkill.Configuration.SkillLevel<T>> result = new java.util.ArrayList<>();
        if (skillLevelList.size() == 0) return result;
        Map<T, Integer> skillLevel = new java.util.HashMap();
        T previousSkill = null;
        for (T skill : skillLevelList) {
            skillLevel.merge(skill, 1, Integer::sum);
            if (previousSkill != skill)
                if (previousSkill != null) {
                    AutoSkill.Configuration.SkillLevel newSkillLevel = new AutoSkill.Configuration.SkillLevel();
                    newSkillLevel.skill = previousSkill;
                    newSkillLevel.level = skillLevel.get(previousSkill);
                    result.add(newSkillLevel);
                }
            previousSkill = skill;
        }
        AutoSkill.Configuration.SkillLevel newSkillLevel = new AutoSkill.Configuration.SkillLevel();
        newSkillLevel.skill = previousSkill;
        newSkillLevel.level = skillLevel.get(previousSkill);
        result.add(newSkillLevel);
        return result;
    }

    public static PxpCalculation.Configuration convertPxpCalculation(OldConfig oldConfig) {
        PxpCalculation.Configuration conf = new PxpCalculation.Configuration();
        conf.legendaryPxpValue = oldConfig.legendaryPxpValue;
        conf.mythicPxpValue = oldConfig.mythicPxpValue;
        return conf;
    }

    public static TaskingOverrides.Configuration convertTaskingOverrides(OldConfig oldConfig) {
        TaskingOverrides.Configuration conf = new TaskingOverrides.Configuration();

        // See if majority of tasks were skipped or not skipped
        var oldTaskOverrides = oldConfig.taskOverrides;
        int skipCount = 0;
        for (Task task : oldTaskOverrides.keySet()) {
            if (oldTaskOverrides.get(task).skip_ticket_task)
                skipCount += 1;
        }

        boolean defaultSkip = skipCount >= oldTaskOverrides.size() / 2;

        for (var overrideEntry : oldTaskOverrides.entrySet()) {
            TaskingOverrides.Configuration.Override convertedOverride = convertOldOverride(overrideEntry, defaultSkip);
            if (convertedOverride != null) {
                conf.overrides.add(convertedOverride);
            }
        }

        return conf;
    }

    public static TaskingOverrides.Configuration.Override convertOldOverride(Map.Entry<Task, OldConfig.Overrides> oldOverride, boolean defaultSkip) {
        OldConfig.Overrides overrides = oldOverride.getValue();
        if (overrides.warp.isEmpty() && overrides.pet.isEmpty() && overrides.wardrobe.isEmpty() && overrides.skip_ticket_task == defaultSkip) {
            return null;
        }
        var newOverride = new TaskingOverrides.Configuration.Override();
        newOverride.task = oldOverride.getKey();
        newOverride.warp = overrides.warp;
        newOverride.pet = overrides.pet;
        newOverride.wardrobe = overrides.wardrobe;
        newOverride.skipTicket = overrides.skip_ticket_task == defaultSkip ? TicketTaskOverride.Default : TicketTaskOverride.booleanToEnum(overrides.skip_ticket_task);

        return newOverride;
    }

    public static WarpNextHotkey.Configuration convertWarpNextHotkey(OldConfig oldConfig) {
        WarpNextHotkey.Configuration conf = new WarpNextHotkey.Configuration();
        conf.autoLevelUp = oldConfig.autoLevelUp;
        conf.warpOnAutoLevelUp = oldConfig.warpOnAutoLevelUp;
        return conf;
    }

    public static CommandAliases.Configuration convertAliases(HashMap<String, String> oldAliases) {
        CommandAliases.Configuration conf = new CommandAliases.Configuration();
        conf.command = oldAliases;
        return conf;
    }

    public static class OldConfig {
        public static class Overrides {
            public String warp;
            public String wardrobe;
            public String pet;
            public Boolean skip_ticket_task;
        }

        private boolean loggingEnabled;
        private boolean balloonRopeEnabled;
        private int interactionTimeout = 5;
        private boolean interactionIsPriority = true;
        private boolean isHudEnabled = true;
        private boolean isHudDisabledDuringBossFight;
        private double hudBackgroundOpacity = 0.3;
        private int hudPosX;
        private int hudPosY;
        private double hudScale = 1.0;
        private boolean isSortedByType;
        private boolean autoSwapWardrobe;
        private boolean autoSwapTools;
        private boolean autoLevelUp = true;
        private boolean warpOnAutoLevelUp = false;
        private Color textColor = new Color(0xfcfcfc);
        private Color worldColor = new Color(0x555555);
        private Color taskColor = new Color(0xffaa00);
        private Color socialiteColor = new Color(0x55ffff);
        private Color progressColor = new Color(0x5555ff);
        private Color targetColor = new Color(0xff5555);
        private Color completeColor = new Color(0x00aa00);
        private Color ticketColor = new Color(0x8845d1);
        private boolean isConsumableHudEnabled = true;
        private double consumableHudBackgroundOpacity = 0.3;
        private int consumableHudPosX = 10;
        private int consumableHudPosY = 10;
        private double consumableHudScale = 1.0;
        private Color consumableTimerColor = new Color(0xffaa00);
        private Color consumableTimeColor = new Color(0x55ff55);
        private int legendaryPxpValue = 75;
        private int mythicPxpValue = 500;
        private String combatWardrobeName = "1";
        private int meleeWeaponSlot = 0;
        private int rangedWeaponSlot = 5;
        private String miningWardrobeName = "2";
        private int miningWeaponSlot = 1;
        private String foragingWardrobeName = "3";
        private int foragingWeaponSlot = 2;
        private String farmingWardrobeName = "4";
        private int farmingWeaponSlot = 3;
        private String fishingWardrobeName = "5";
        private String combatFishingWardrobeName = "6";
        private int fishingWeaponSlot = 4;
        private boolean autoSkillLeveling;
        public List<NormalCombatSkill> normalCombatSkills = new ArrayList<>();
        public List<NormalMiningSkill> normalMiningSkills = new ArrayList<>();
        public List<NormalForagingSkill> normalForagingSkills = new ArrayList<>();
        public List<NormalFarmingSkill> normalFarmingSkills = new ArrayList<>();
        public List<NormalSpearFishingSkill> normalSpearFishingSkills = new ArrayList<>();
        public List<NormalSharpshootingSkill> normalSharpshootingSkills = new ArrayList<>();
        public List<NormalExcavationSkill> normalExcavationSkills = new ArrayList<>();
        public List<NightmareCombatSkill> nightmareCombatSkills = new ArrayList<>();
        public List<NightmareMiningSkill> nightmareMiningSkills = new ArrayList<>();
        public List<NightmareForagingSkill> nightmareForagingSkills = new ArrayList<>();
        public List<NightmareFarmingSkill> nightmareFarmingSkills = new ArrayList<>();
        public List<NightmareSpearFishingSkill> nightmareSpearFishingSkills = new ArrayList<>();
        public List<NightmareSharpshootingSkill> nightmareSharpshootingSkills = new ArrayList<>();
        public HashMap<Task, Overrides> taskOverrides = new HashMap<>();
    }
}