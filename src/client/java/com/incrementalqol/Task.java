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

    List<Pattern> misc = List.of(
            Pattern.compile("Clean (?<amount>\\d+) (?<type>.+) \\(?(?<progress>\\d+)"),
            Pattern.compile("Repair (?<amount>\\d+) (?<type>.+) in .+\\((?<progress>\\d)"),
            Pattern.compile("Sell (?<amount>\\d+\\,?\\d*) (?<type>.+)"),
            Pattern.compile("Gain (?<amount>\\d+\\,?\\d*) (?<type>.+) from selling.+ \\(?(?<progress>\\d+\\,?\\d*)")
    );

    List<Pattern> games = List.of(
            Pattern.compile("Play (?<amount>\\d+) (?<opt>.+) of (?<type>.+) \\(?(?<progress>\\d+)"),
            Pattern.compile("Earn (?<amount>\\d+) (?<opt>.+) in (?<type>.+) \\(?(?<progress>\\d+)"),
            Pattern.compile("Earn (?<amount>\\d+)\\s+(?<opt>.+) playing (?<type>.+) \\(?(?<progress>\\d+)")
    );

    List<Pattern> forage = List.of(
            Pattern.compile("Collect (?<amount>\\d+\\,?\\d*) (?<type>.+) \\(?(?<progress>\\d+\\,?\\d*)")
    );

    List<Pattern> combat = List.of(
            Pattern.compile("Slay (?:the) (?<type>.+) \\((?<progress>\\d+)\\/(?<amount>\\d+)"),
            Pattern.compile("Collect (?<amount>\\d+\\,?\\d*) drops from (?<type>.+) \\(?(?<progress>\\d+\\,?\\d*)")
    );

    List<Pattern> fish = List.of(
            Pattern.compile("Spear (?<type>.+) without missing (?<amount>\\d+).+\\((?<progress>\\d+)"),
            Pattern.compile("Collect (?<amount>\\d+\\,?\\d*) (?<type>.+) \\(?(?<progress>\\d+\\,?\\d*)")
    );

    List<Pattern> mine = List.of(
            Pattern.compile("Collect (?<amount>\\d+\\,?\\d*) (?<type>.+) \\(?(?<progress>\\d+\\,?\\d*)")
    );

    List<Pattern> farm = List.of(
            Pattern.compile("Harvest (?<amount>\\d+\\,?\\d*) (?<type>.+) \\(?(?<progress>\\d+\\,?\\d*)")
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

        switch(this.taskType) {
            case "Misc": {
                for (Pattern p : misc) {
                    Matcher m = p.matcher(description);

                    if (m.find()) {
                        try {
                            this.taskTarget = m.group("type") == null ? "" : m.group("type");
                            this.progress = m.group("progress") == null ? "" : m.group("progress");
                            this.targetAmount = m.group("amount") == null ? "" : m.group("amount");
                        } catch (IllegalArgumentException e) {
                            this.taskTarget = "";
                            this.progress = "";
                            this.targetAmount = "";
                        }
                        this.applicablePattern = Pattern.compile(p.pattern().
                                replace("(?<type>.+)", "(?<type>" + taskTarget + ")"));
                        break;
                    }
                }
            } break;
            case "Gaming": {
                for (Pattern p : games) {
                    Matcher m = p.matcher(description);

                    if (m.find()) {
                        String opt = "";
                        try {
                            this.progress = m.group("progress") == null ? "" : m.group("progress");
                            this.targetAmount = m.group("amount") == null ? "" : m.group("amount");
                            this.taskTarget = m.group("type") == null ? "" : m.group("type");
                            opt = m.group("opt") == null ? "" : m.group("opt");
                        } catch (IllegalArgumentException e) {
                            this.taskTarget = "";
                            this.targetAmount = "";
                            this.progress = "";
                        }
                        this.applicablePattern = Pattern.compile(p.pattern().
                                replace("(?<type>.+)", "(?<type>" + taskTarget + ")"));
                        this.taskTarget += " " + opt;
                        break;
                    }
                }
            } break;
            case "Combat": {
                for (Pattern p : combat) {
                    Matcher m = p.matcher(description);

                    if (m.find()) {
                        try {
                            this.progress = m.group("progress") == null ? "" : m.group("progress");
                            this.taskTarget = m.group("type") == null ? "" : m.group("type");
                            this.targetAmount = m.group("amount") == null ? "" : m.group("amount");
                        } catch (IllegalArgumentException e) {
                            this.taskTarget = "";
                            this.progress = "";
                            this.targetAmount = "";
                        }
                        this.applicablePattern = Pattern.compile(p.pattern().
                                replace("(?<type>.+)", "(?<type>" + taskTarget + ")"));
                        break;
                    }
                }
            } break;
            case "Farming": {
                for (Pattern p : farm) {
                    Matcher m = p.matcher(description);
                    if (m.find()) {
                        try {
                            this.taskTarget = m.group("type") == null ? "" : m.group("type");
                            this.progress = m.group("progress") == null ? "" : m.group("progress");
                            this.targetAmount = m.group("amount") == null ? "" : m.group("amount");
                        } catch (IllegalArgumentException e) {
                            this.taskTarget = "";
                            this.progress = "";
                            this.targetAmount = "";
                        }
                        this.applicablePattern = Pattern.compile(p.pattern().
                                replace("(?<type>.+)", "(?<type>" + taskTarget + ")"));
                        break;
                    }
                }
            } break;
            case "Mining": {
                for (Pattern p : mine) {
                    Matcher m = p.matcher(description);

                    if (m.find()) {
                        try {
                            this.taskTarget = m.group("type") == null ? "" : m.group("type");
                            this.progress = m.group("progress") == null ? "" : m.group("progress");
                            this.targetAmount = m.group("amount") == null ? "" : m.group("amount");
                        } catch (IllegalArgumentException e) {
                            this.taskTarget = "";
                            this.progress = "";
                            this.targetAmount = "";
                        }
                        this.applicablePattern = Pattern.compile(p.pattern().
                                replace("(?<type>.+)", "(?<type>" + taskTarget + ")"));
                        break;
                    }
                }
            } break;
            case "Fishing": {
                for (Pattern p : fish) {
                    Matcher m = p.matcher(description);

                    if (m.find()) {
                        try {
                            this.taskTarget = m.group("type") == null ? "" : m.group("type");
                            this.progress = m.group("progress") == null ? "" : m.group("progress");
                            this.targetAmount = m.group("amount") == null ? "" : m.group("amount");
                        } catch (IllegalArgumentException e) {
                            this.taskTarget = "";
                            this.progress = "";
                            this.targetAmount = "";
                        }
                        this.applicablePattern = Pattern.compile(p.pattern().
                                replace("(?<type>.+)", "(?<type>" + taskTarget + ")"));
                        break;
                    }
                }
            } break;
            case "Foraging": {
                for (Pattern p : forage) {
                    Matcher m = p.matcher(description);

                    if (m.find()) {
                        try {
                            this.taskTarget = m.group("type") == null ? "" : m.group("type");
                            this.progress = m.group("progress") == null ? "" : m.group("progress");
                            this.targetAmount = m.group("amount") == null ? "" : m.group("amount");
                        } catch (IllegalArgumentException e) {
                            this.taskTarget = "";
                            this.progress = "";
                            this.targetAmount = "";
                        }
                        this.applicablePattern = Pattern.compile(p.pattern().
                                replace("(?<type>.+)", "(?<type>" + taskTarget + ")"));
                        break;
                    }
                }
            } break;
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
            List<String> infernal = List.of("Ghasts", "Ghast Souls", "Blazes", "Nrubs", "Infernal Imps", "Azuregem", "Bubbler", "Magmafish"
                    , "Molten Jellyfish", "Lavafruit");
            List<String> abyss = List.of("Wicks", "Glow Squids", "Slinkers", "Aurorium", "Twine", "Zephyr", "Abyssal Crabs", "Lampposts");

            if (lush.contains(this.normalizedTaskTarget())) return "§2lush";
            else if(veil.contains(normalizedTaskTarget())) return "§3veil";
            else if(infernal.contains(normalizedTaskTarget())) return "§4infernal";
            else if(abyss.contains(normalizedTaskTarget())) return "§5abyss";
            else return "";
        }
    }

    private String getLocation(boolean completed) {
        return (completed) ?
                "§2[w" + world + (getSubLocation().isEmpty() ? "" : "-" + getSubLocation()).replaceAll("\\d", "2") + "]" :
                "[§8w" + world + "§f" + (getSubLocation().isEmpty() ? "" : "-§d" + getSubLocation()) + "§f]";
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
        else if (taskTarget.contains("colored Riverfish")) {
            return taskTarget.replace(" colored Riverfish", "").replace(" using a Fishing Spear", "");
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
        if (completed) return
                getLocation(true) + " " +
                        this.taskType + ": §2" + ((isShiny) ? "(*)" : "") + normalizedTaskTarget() + ((isShiny) ? "(*)" : "") +
                        " (" + this.progress + "/" + this.targetAmount + ")§f";
        else return
                getLocation(false) + " " +
                        this.taskType + ": §6§n" + ((isShiny) ? "(*)" : "") + normalizedTaskTarget() +((isShiny) ? "(*)" : "") +
                        "§r (§9" + this.progress + "§f/§c" + this.targetAmount + "§f)";
    }
}
