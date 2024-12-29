package com.incrementalqol;

import net.minecraft.client.gui.hud.ClientBossBar;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Task{
    private final String name;
    private final String description;
    private final String warp;
    private final int strWidth;
    private boolean completed;
    private boolean isShiny = false;
    private String taskType = "";
    private String targetAmount = "";
    private String taskTarget = "";
    private String progress = "";
    private String world;
    private String subLocation;

    private Pattern applicablePattern;
    private Pattern generalProgressPattern;

    private Map<String, Pattern> patterns = Map.of(
            "KILL", Pattern.compile("Collect (?<amount>\\d+\\,?\\d*) drops from (?<type>.+) \\(?(?<progress>\\d+\\,?\\d*)"),
            "HARVEST", Pattern.compile("Harvest (?<amount>\\d+\\,?\\d*) (?<type>.+) \\(?(?<progress>\\d+\\,?\\d*)"),
            "COLLECT", Pattern.compile("Collect (?<amount>\\d+\\,?\\d*) (?<type>.+) \\(?(?<progress>\\d+\\,?\\d*)"),
            "REPAIR", Pattern.compile("Repair (?<amount>\\d+) Lampposts in the Abyss \\(?(?<progress>\\d+)"),
            "EARN", Pattern.compile("Gain (?<amount>\\d+\\,?\\d*) (?<type>.+) from selling.+ \\(?(?<progress>\\d+\\,?\\d*)"),
            "PLAY", Pattern.compile("Play (?<amount>\\d+) games of (?<type>.+) \\(?(?<progress>\\d+)"),
            "SCORE_PIXEL", Pattern.compile("Earn (?<amount>\\d+) score in (?<type>.+) \\(?(?<progress>\\d+)"),
            "SPEAR_CONSECUTIVE", Pattern.compile("Spear 2 fish in a row without missing (?<amount>\\d+) times \\(?(?<progress>\\d+)")
    );

    public Task (String name, String description, String warp, int strWidth, boolean completed, String world, String type) {
        this.name=name;
        this.description=description;
        this.warp=warp;
        this.strWidth = strWidth;
        this.completed=completed;
        this.generalProgressPattern = Pattern.compile(this.name + " \\(?(?<progress>\\d+\\,?\\d*)");
        this.world = world;
        this.taskType = type.trim();

        for(Map.Entry<String, Pattern> entry : patterns.entrySet()) {
            Pattern pattern = entry.getValue();

            Matcher m = pattern.matcher(description);
            if(m.find()) {
                this.progress = m.group("progress") == null ? "" : m.group("progress");
                this.targetAmount = m.group("amount") == null ? "" : m.group("amount");
                try {
                    this.taskTarget = m.group("type") == null ? "" : m.group("type");
                } catch (IllegalArgumentException e) {
                    taskTarget = "";
                }
                this.applicablePattern = Pattern.compile(pattern.pattern().replace("(?<type>.+)", "(?<type>" + taskTarget + ")"));
                break;
            }
            else {
                this.applicablePattern = null;
            }
        }
    }

    public boolean isCompleted() {
        return completed;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getWarp(boolean completed) {
        return warp;
    }

    private String getSubLocation() {
        if (this.world.equals("1")) return "";
        else {
            List<String> lush = List.of("Poison Slimes", "Cave Crawlers", "Lurkers", "Ancient Lurkers", "Crimsonite");
            List<String> veil = List.of("Spotters", "Verdemites", "Endermen", "Verdelith", "Shy");
            List<String> infernal = List.of("Ghasts", "Ghast Souls", "Blazes", "Nrubs", "Infernal Imps", "Azuregem", "Blubbers", "Magmafish"
                    , "Molten Jellyfish", "Lavafruit");
            List<String> abyss = List.of("Wicks", "Glow Squids", "Slinkers", "Aurorium", "Twine", "Zephyr", "Abyssal Crabs");

            if (lush.contains(this.normalizedTaskTarget())) return "lush";
            else if(veil.contains(normalizedTaskTarget())) return "veil";
            else if(infernal.contains(normalizedTaskTarget())) return "infernal";
            else if(abyss.contains(normalizedTaskTarget())) return "abyss";
            else return "";
        }
    }

    private String getLocation(boolean completed) {
        return (completed) ? "§2[w" + world + getSubLocation() + "]" : "[§bw" + world + "§f-§d" + getSubLocation() + "§f]";
    }
    public int getStrWidth() {
        return strWidth;
    }

    public void setCompleted() {
        this.completed = true;
    }

    public void bossBarForTask(ClientBossBar bar) {
        boolean matched = false;
        String data = bar.getName().getString();
        if (this.applicablePattern == null) return;
//        Logger.getAnonymousLogger().info("=== [" + taskType + "] " + name + " (" + description + ")" + " target: " + taskTarget + " -- general: " + this.generalProgressPattern + " -- applicable: " + applicablePattern);
//
//        Logger.getAnonymousLogger().info("++WILL TRY TO MATCH GENERAL" + data + " AGAINST " + this.generalProgressPattern);
//        Logger.getAnonymousLogger().info("++WILL TRY TO MATCH APPLICABLE" + data + " AGAINST " + this.applicablePattern);
        Matcher m = generalProgressPattern.matcher(data);
        if (!m.find()) {
            m = applicablePattern.matcher(data);
            if (m.find()) {
                this.progress = m.group("progress") == null ? "" : m.group("progress");
                this.targetAmount = m.group("amount") == null ? "" : m.group("amount");
                try {
                    this.taskTarget = m.group("type") == null ? "" : m.group("type");
                } catch (IllegalArgumentException e) {
                    taskTarget = "";
                }
                if (!taskTarget.isEmpty()) {

                }
                matched = true;
            }
        }
        else {
            this.progress = m.group("progress") == null ? "" : m.group("progress");
            matched = true;
        }
    }

    private String normalizedTaskTarget() {
        if (taskTarget.isEmpty()) return taskTarget;
        else if (taskTarget.contains("Shiny Ores")) {
            isShiny = taskTarget.contains("Shiny Ores");
            return taskTarget.replace(" from Shiny Ores", "");
        }
        else if (taskTarget.contains("using a Fishing Spear")) {
            return taskTarget.replace(" using a Fishing Spear", "");
        }
        else if (taskTarget.contains("drops from")) {
            return taskTarget.replace(" drops from", "");
        }
        else return taskTarget;
    }

    public String render(boolean completed) {
        if (completed) return getLocation(true) + " " + this.taskType + ": §2" + normalizedTaskTarget() + " (" + this.progress + "/" + this.targetAmount + "§f)";
        else return getLocation(false) + " " + this.taskType + ": §6§n" + normalizedTaskTarget() + "§r (§9" + this.progress + "§f/§c" + this.targetAmount + "§f)";
    }
}
