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

    private Pattern applicablePattern;
    private Pattern generalProgressPattern;

    private Map<String, Pattern> patterns = Map.of(
            "KILL", Pattern.compile("Collect (?<amount>\\d+\\,?\\d*) drops from (?<type>.+) \\(?(?<progress>\\d+\\,?\\d*)"),
            "HARVEST", Pattern.compile("Harvest (?<amount>\\d+\\,?\\d*) (?<type>.+) \\(?(?<progress>\\d+\\,?\\d*)"),
            "COLLECT", Pattern.compile("Collect (?<amount>\\d+\\,?\\d*) (?<type>.+) \\(?(?<progress>\\d+\\,?\\d*)"),
            "REPAIR", Pattern.compile("Repair (?<amount>\\d+) Lampposts in the Abyss \\(?(?<progress>\\d+)"),
            "EARN", Pattern.compile("Gain (?<amount>\\d+\\,?\\d*) (?<type>.+) from selling.+ \\(?(?<progress>\\d+\\,?\\d*)")
    );

    public Task (String name, String description, String warp, int strWidth, boolean completed) {
        this.name=name;
        this.description=description;
        this.warp=warp;
        this.strWidth = strWidth;
        this.completed=completed;
        this.generalProgressPattern = Pattern.compile(this.name + " \\(?(?<progress>\\d+\\,?\\d*)");

        for(Map.Entry<String, Pattern> entry : patterns.entrySet()) {
            String type= entry.getKey();
            Pattern pattern = entry.getValue();

            Matcher m = pattern.matcher(description);
            if(m.find()) {
                System.out.println(">>> TASK MATCHED " + type);
                this.taskType = type;
                this.progress = m.group("progress") == null ? "" : m.group("progress");
                this.targetAmount = m.group("amount") == null ? "" : m.group("amount");
                this.taskTarget = m.group("type") == null ? "" : m.group("type");
                this.applicablePattern = Pattern.compile(pattern.pattern().replace("(?<type>.+)", "(?<type>" + taskTarget + ")"));

                this.taskTarget = normalizeTaskTarget(taskTarget);
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
        Logger.getGlobal().info(">> TASK TARGET IS " + taskTarget);
        if (taskTarget.equals("")) return "§b--§f";
        List<String> w1 = List.of(
                "Ladybugs", "Honeycombs", "Apples", "Large Apples", "Mountain Goats", "Scared Hogs", "Wild Hogs", "Riverfish", "Coal", "Iron",
                "Copper", "Gold", "Redstone", "Wheat", "Carrots", "Beetroot", "Potatoes", "Hoglin"
        );
        List<String> w2 = List.of("Salmon", "Koi", "Axolotl", "Bonsai", "Pine", "Pinecones", "Large Pinecones",
                "Pomegranates", "Large Pomegranates", "Rodrick", "Sky Beetle Queen");

        List<String> lush = List.of("Poison Slimes", "Cave Crawlers", "Lurkers", "Ancient Lurkers", "Crimsonite");
        List<String> veil = List.of("Spotters", "Verdemites", "Endermen", "Verdelith", "Shy");
        List<String> infernal = List.of("Ghasts", "Ghast Souls", "Blazes", "Nrubs", "Infernal Imps", "Azuregem", "Blubbers", "Magmafish"
        , "Molten Jellyfish", "Lavafruit");
        List<String> abyss = List.of("Wicks", "Glow Squids", "Slinkers", "Aurorium", "Twine", "Zephyr", "Abyssal Crabs");


        if(w1.contains(taskTarget)) return (completed) ? "§2[w1]" : "[§bw1§f]";
        else if(w2.contains(taskTarget)) return (completed) ? "§2[w2]" : "[§bw2§f]";
        else if(lush.contains(taskTarget)) return (completed) ? "§2[w2-lush]" : "[§bw2§f-§5lush§f]";
        else if(veil.contains(taskTarget)) return (completed) ? "§2[w2-veil]" : "[§bw2§f-§5veil§f]";
        else if(infernal.contains(taskTarget)) return (completed) ? "§2[w2-infernal]" : "[§bw2§f-§5infernal§f]";
        else if(abyss.contains(taskTarget)) return (completed) ? "§2[abyss]" : "[§bw2§f-§5abyss§f]";
        else return (completed) ? "§2[--]" : "[§b--§f]";

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
        Logger.getAnonymousLogger().info("=== [" + taskType + "] " + name + " (" + description + ")" + " target: " + taskTarget + " -- general: " + this.generalProgressPattern + " -- applicable: " + applicablePattern);

//        Logger.getAnonymousLogger().info("++WILL TRY TO MATCH GENERAL" + data + " AGAINST " + this.generalProgressPattern);
//        Logger.getAnonymousLogger().info("++WILL TRY TO MATCH APPLICABLE" + data + " AGAINST " + this.applicablePattern);
        Matcher m = generalProgressPattern.matcher(data);
        if (!m.find()) {
            m = applicablePattern.matcher(data);
            if (m.find()) {
                this.progress = m.group("progress") == null ? "" : m.group("progress");
                this.targetAmount = m.group("amount") == null ? "" : m.group("amount");
                this.taskTarget = m.group("type") == null ? "" : m.group("type");
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

    private String normalizeTaskTarget(String target) {
        if (target.isEmpty()) return target;
        else if (target.contains("Shiny Ores")) {
            isShiny = taskTarget.contains("Shiny Ores");
            return target.replace(" from Shiny Ores", "");
        }
        else if (target.contains("using a Fishing Spear")) {
            return target.replace("using a Fishing Spear", "");
        }
        else return target;
    }

    public String render(boolean completed) {
        if (completed) return this.getWarp(completed) + this.taskType + ": §6§2" + this.taskTarget + " " + this.progress + "/" + this.targetAmount + "§f";
        else return this.getWarp(completed) + this.taskType + ": §6§n" + this.taskTarget + "§r §9" + this.progress + "§f/§c" + this.targetAmount + "§f";
    }
}
