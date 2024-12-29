package com.incrementalqol;

import net.minecraft.client.gui.hud.ClientBossBar;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Task{
    private final String name;
    private final String description;
    private final String warp;
    private final int strWidth;
    private boolean completed;
    private String taskType = "";
    private String targetAmount = "";
    private String taskTarget = "";
    private String progress = "";

    private Pattern applicablePattern;
    private Pattern generalProgressPattern;

    private Map<String, Pattern> patterns = Map.of(
            "KILL", Pattern.compile("Collect (?<amount>\\d+\\,?\\d*) drops from (?<type>.*) \\((?<progress>\\d+\\,?\\d*)"),
            "HARVEST", Pattern.compile("Harvest (?<amount>\\d+\\,?\\d*) (?<type>.+) \\((?<progress>\\d+\\,?\\d*)"),
            "COLLECT", Pattern.compile("Collect (?<amount>\\d+\\,?\\d*) (?<type>.+) \\((?<progress>\\d+\\,?\\d*)")
    );

    public Task (String name, String description, String warp, int strWidth, boolean completed) {
        this.name=name;
        this.description=description;
        this.warp=warp;
        this.strWidth = strWidth;
        this.completed=completed;
        this.generalProgressPattern = Pattern.compile(this.name + " (?<progress>\\d+\\,?\\d*)");

        patterns.forEach((type, pattern) -> {
            Matcher m = pattern.matcher(description);
            if(m.find()) {
                System.out.println(">>> TASK MATCHED " + type);
                this.taskType = type;
                this.applicablePattern = pattern;
                this.progress = m.group("progress") == null ? "" : m.group("progress");
                this.targetAmount = m.group("amount") == null ? "" : m.group("amount");
                this.taskTarget = m.group("type") == null ? "" : m.group("type");
            }
            else {
                this.applicablePattern = null;
            }
        });
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
    public String getWarp() {
        return warp;
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

        Matcher m = generalProgressPattern.matcher(data);
        if (!m.find()) {
            m = applicablePattern.matcher(data);
            if (m.find()) {
                System.out.println("=== PROGRESSBAR FOR TASK " + this.name);
                this.progress = m.group("progress") == null ? "" : m.group("progress");
                this.targetAmount = m.group("amount") == null ? "" : m.group("amount");
                this.taskTarget = m.group("type") == null ? "" : m.group("type");
                matched = true;
            }
        }
        else {
            System.out.println("=== PROGRESSBAR FOR TASK " + this.name);
            this.progress = m.group("progress") == null ? "" : m.group("progress");
            matched = true;
        }
    }

    public String render() {
        return this.taskType + ": " + this.taskTarget + " " + this.progress + "/" + this.targetAmount;
    }
}
