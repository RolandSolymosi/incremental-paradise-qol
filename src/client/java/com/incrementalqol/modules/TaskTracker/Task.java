package com.incrementalqol.modules.TaskTracker;

import com.incrementalqol.common.data.TaskCollection;
import com.incrementalqol.common.data.ToolType;
import com.incrementalqol.common.utils.ConfiguredLogger;
import com.incrementalqol.config.Config;
import net.minecraft.client.gui.hud.ClientBossBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.incrementalqol.common.data.TaskCollection.TryGetTaskTarget;

public class Task {
    public static final Logger LOGGER = LoggerFactory.getLogger(Task.class);

    public final TaskCollection.TaskDescriptor descriptor;
    private final String name;
    private final String description;
    private String warp;
    private int strWidth;
    private final String number;
    private boolean completed;
    private boolean isShiny = false;
    private boolean isLarge = false;
    private String color = null;
    private String constraint = null;
    private boolean isTicket = false;
    private boolean isSocialite = false;
    private String taskType = "";
    private String targetAmount = "";
    private String taskTarget = "";
    private String progress = "";
    private String world;
    private String subLocation;
    private final ToolType requiredTool;

    private Pattern applicablePattern;
    private Pattern generalProgressPattern;

    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("§.");


    private static final List<Pattern> MISC_PATTERNS = List.of(
            Pattern.compile("Clean (?<amount>[0-9.,]+[km]?) (?<type>.+) \\(?(?<progress>[0-9.,]+[km]?)"),
            Pattern.compile("Repair (?<amount>[0-9.,]+[km]?) (?<type>.+) in [^(]+\\(?(?<progress>[0-9.,]+[km]?)"),
            Pattern.compile("Sell (?<amount>[0-9.,]+[km]?) (?<type>.+) \\(?(?<progress>[0-9.,]+[km]?)"),
            Pattern.compile("Gain (?<amount>[0-9.,]+[km]?) (?<type>.+) \\(?(?<progress>[0-9.,]+[km]?)"),
            Pattern.compile("Loot (?<amount>[0-9.,]+[km]?) (?<type>.+) \\(?(?<progress>[0-9.,]+[km]?)")
    );

    private static final List<Pattern> GAMES_PATTERNS = List.of(
            Pattern.compile("Play (?<amount>[0-9.,]+[km]?) (?<constraint>.+) of (?<type>.+) \\(?(?<progress>[0-9.,]+[km]?)"),
            Pattern.compile("Earn (?<amount>[0-9.,]+[km]?) (?<constraint>.+) in (?<type>.+) \\(?(?<progress>[0-9.,]+[km]?)"),
            Pattern.compile("Earn (?<amount>[0-9.,]+[km]?)\\s+(?<constraint>.+) playing (?<type>.+) \\(?(?<progress>[0-9.,]+[km]?)"),
            Pattern.compile("Find (?<amount>[0-9.,]+[km]?) (?<constraint>.+) while playing (?<type>.+) \\(?(?<progress>[0-9.,]+[km]?)")
    );

    private static final List<Pattern> FORAGING_PATTERNS = List.of(
            Pattern.compile("Collect (?<amount>[0-9.,]+[km]?) (?<large>Large )?(?<type>.+) with (?<constraint>.+) \\(?(?<progress>[0-9.,]+[km]?)"),
            Pattern.compile("Collect (?<amount>[0-9.,]+[km]?) (?<large>Large )?(?<type>.+) \\(?(?<progress>[0-9.,]+[km]?)")
    );

    private static final List<Pattern> COMBAT_PATTERNS = List.of(
            Pattern.compile("Kill (?:the )?(?<type>.+) \\(?(?<progress>[0-9.,]+[km]?)/(?<amount>[0-9.,]+[km]?)"),
            Pattern.compile("Slay (?:the )?(?<type>.+) \\(?(?<progress>[0-9.,]+[km]?)/(?<amount>[0-9.,]+[km]?)"),
            Pattern.compile("Collect (?<amount>[0-9.,]+[km]?) drops from (?<type>.+) with (?<constraint>.+) \\(?(?<progress>[0-9.,]+[km]?)"),
            Pattern.compile("Collect (?<amount>[0-9.,]+[km]?) drops from (?<type>.+) \\(?(?<progress>[0-9.,]+[km]?)")
    );

    private static final List<Pattern> FISHING_PATTERNS = List.of(
            Pattern.compile("Spear [0-9.,]+ (?<type>.+) (?<constraint>in a row without missing) (?<amount>[0-9.,]+[km]?).+\\((?<progress>[0-9.,]+[km]?)"),
            Pattern.compile("Collect (?<amount>[0-9.,]+[km]?) (?<color>.+) (?<constraint>colored) (?<type>.+) using a Fishing Spear \\(?(?<progress>[0-9.,]+[km]?)"),
            Pattern.compile("Collect (?<amount>[0-9.,]+[km]?) (?<type>.+) using a Fishing Spear \\(?(?<progress>[0-9.,]+[km]?)"),
            Pattern.compile("Collect (?<amount>[0-9.,]+[km]?) drops from (?<type>.+) with (?<constraint>.+) \\(?(?<progress>[0-9.,]+[km]?)"),
            Pattern.compile("Collect (?<amount>[0-9.,]+[km]?) drops from (?<type>.+) \\(?(?<progress>[0-9.,]+[km]?)"),
            Pattern.compile("Collect (?<amount>[0-9.,]+[km]?) (?<type>.+) \\(?(?<progress>[0-9.,]+[km]?)")
    );

    private static final List<Pattern> MINING_PATTERNS = List.of(
            Pattern.compile("Collect (?<amount>[0-9.,]+[km]?) (?<type>.+) from (?<shiny>Shiny) Ores \\(?(?<progress>[0-9.,]+[km]?)"),
            Pattern.compile("Collect (?<amount>[0-9.,]+[km]?) (?<shiny>Shiny) (?<type>.+) \\(?(?<progress>[0-9.,]+[km]?)"),
            Pattern.compile("Collect (?<amount>[0-9.,]+[km]?) (?<type>.+) with (?<constraint>.+) \\(?(?<progress>[0-9.,]+[km]?)"),
            Pattern.compile("Collect (?<amount>[0-9.,]+[km]?) (?<type>.+) \\(?(?<progress>[0-9.,]+[km]?)")
    );

    private static final List<Pattern> FARMING_PATTERNS = List.of(
            Pattern.compile("Harvest (?<amount>[0-9.,]+[km]?) (?<type>.+) with (?<constraint>.+) \\(?(?<progress>[0-9.,]+[km]?)"),
            Pattern.compile("Harvest (?<amount>[0-9.,]+[km]?) (?<type>.+) \\(?(?<progress>[0-9.,]+[km]?)")
    );

    //private static final Pattern PATTERN = Pattern.compile("^\\w+ (?:the|\\d+) (?<shiny>Shiny)?(?<type>[^(]+?)(?: with (?<constraint>[^(]+))? \\((?<progress>[0-9.,]+[km]?)/(?<amount>[0-9.,]+[km]?)\\)$");

    public Task(String name, String description, String warp, int strWidth, boolean completed, String world, String number, String type, boolean isTicket, boolean isSocialite, ToolType requiredTool) {
        this.name = name;
        this.description = description;
        this.warp = warp;
        this.strWidth = strWidth;
        this.completed = completed;
        this.generalProgressPattern = Pattern.compile(Pattern.quote(this.name) + " \\(?(?<progress>[0-9.,]+[km]?)");
        this.world = world;
        this.number = number;
        this.taskType = type.trim();
        this.isTicket = isTicket;
        this.isSocialite = isSocialite;
        this.requiredTool = requiredTool;

        determineTaskAttributes();
        this.descriptor = TaskCollection.TryGetDescriptor(this.taskTarget);
        if (this.descriptor == null) {
            ConfiguredLogger.LogError(LOGGER, "UNIDENTIFIED TASK: " + this.description);
        }
    }

    private void determineTaskAttributes() {
        List<Pattern> patterns = getPatternsForType();
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(description);
            if (matcher.find()) {
                extractAttributes(matcher);
                this.applicablePattern = Pattern.compile(pattern.pattern().replace("(?<type>.+)", "(?<type>" + taskTarget + ")"));
                break;
            }
        }
    }

    private List<Pattern> getPatternsForType() {
        return switch (this.taskType) {
            case "Misc" -> MISC_PATTERNS;
            case "Gaming" -> GAMES_PATTERNS;
            case "Farming" -> FARMING_PATTERNS;
            case "Combat" -> COMBAT_PATTERNS;
            case "Fishing" -> FISHING_PATTERNS;
            case "Mining" -> MINING_PATTERNS;
            case "Foraging" -> FORAGING_PATTERNS;
            //case "Quest" -> QUESTING_PATTERNS;

            default -> List.of();
        };
    }

    private void extractAttributes(Matcher matcher) {
        this.taskTarget = getGroupValue(matcher, "type"); //drops from Abyssal Crabs
        this.progress = getGroupValue(matcher, "progress");
        this.targetAmount = getGroupValue(matcher, "amount");
        this.isShiny = !getGroupValue(matcher, "shiny").equals("");
        this.isLarge = !getGroupValue(matcher, "large").equals("");
        this.color = getGroupValue(matcher, "color");
        this.constraint = getGroupValue(matcher, "constraint");
    }

    private String getGroupValue(Matcher matcher, String groupName) {
        try {
            return matcher.group(groupName) == null ? "" : matcher.group(groupName);
        } catch (IllegalArgumentException e) {
            return "";
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

    public String getWarp() {
        String result = Config.HANDLER.instance().getTaskOverrideWarp(TryGetTaskTarget(this.taskTarget));
        if (!Objects.equals(result, "") && !Objects.equals(this.taskType, "Gaming")) {
            return "warp " + result;
        } else if (this.descriptor != null) {
            return this.descriptor.getCommand();
        }
        return null;
    }

    public String getOverrideWarp() {
        return "warp " + Config.HANDLER.instance().getTaskOverrideWarp(TryGetTaskTarget(this.taskTarget));
    }

    public Optional<String> getWardrobe() {
        String overrideWardrobe = Config.HANDLER.instance().getTaskOverrideWardrobe(TryGetTaskTarget(this.taskTarget));
        if (this.descriptor.getDefaultWardrobe() != null || !Objects.equals(overrideWardrobe, "")) {
            return Optional.of(!Objects.equals(overrideWardrobe, "") ? overrideWardrobe : Config.HANDLER.instance().getWardrobeNameToDefault(this.descriptor.getDefaultWardrobe()));
        } else {
            return Optional.empty();
        }

    }

    public String getPet() {
        return Config.HANDLER.instance().getTaskOverridePet(TryGetTaskTarget(this.taskTarget));
    }

    public String getFallbackWarp(int index) {
        if (this.descriptor != null) {
            return this.descriptor.getFallbackCommand(index);
        }
        return null;
    }

    public String getTaskType() {
        return taskType;
    }

    public boolean getCompletedStatus() {
        return this.progress.equals(this.targetAmount);
    }

    private String getSubLocation() {
        if (this.world.equals("1")) return "";
        else {
            // TODO: These should be replaced to be using the TaskTargets
            List<String> lush = List.of("Poison Slimes", "Cave Crawlers", "Lurkers", "Ancient Lurkers", "Crimsonite");
            List<String> veil = List.of("Spotters", "Verdemites", "Endermen", "Verdelith", "Shy");
            List<String> infernal = List.of("Ghasts", "Ghast Souls", "Blazes", "Nrubs", "Infernal Imps", "Azuregem", "Bubbler", "Magmafish"
                    , "Molten Jellyfish", "Lavafruit");
            List<String> abyss = List.of("Wicks", "Glow Squids", "Slinkers", "Aurorium", "Twine", "Zephyr", "Abyssal Crabs", "Lampposts");
            List<String> homestead = List.of("Cheddore", "Blue Cheese", "Nesting Wood", "Passionfruit", "Bats", "Rats", "Rattus", "Grasshoppers", "Astromold & Astromites", "Magmold", "Cats", "Catfish", "Sewer Chests");
            List<String> alpha = List.of("Cryoflora", "Chillfruit", "Sulphoroot", "Jackfruit", "Pyrospire", "Scorchberries", "Thorn Beetles", "Worms", "Frogs", "Algae", "Boom Shrooms", "Piranhas");
            List<String> beta = List.of("Glowdust", "Slimecrust", "Voidshard", "Snipers", "Angry Miners", "Ravagers");

            if (lush.contains(this.taskTarget)) return "§2Lush" ;
            else if (veil.contains(this.taskTarget)) return "§3Veil" ;
            else if (infernal.contains(this.taskTarget)) return "§4Infernal" ;
            else if (abyss.contains(this.taskTarget)) return "§5Abyss" ;
            else if (homestead.contains(this.taskTarget)) return "§eHomestead" ; // TODO: Replace with the RGB text &#944a00 and use the fuel colour
            else if (alpha.contains(this.taskTarget)) return "§2Alpha" ; // &#00a800
            else if (beta.contains(this.taskTarget)) return "§5Beta" ; // &#a800a8
            else return "";
        }
    }

    private String getLocation(boolean completed) {

        return switch (this.world) {
            case "World" -> (completed) ?
                    "§2[w" + number + (getSubLocation().isEmpty() ? "" : "-" + getSubLocation()).replaceAll("§[0-9a-fA-F]", "§2") + "]" :
                    "[§8w" + number + "§f" + (getSubLocation().isEmpty() ? "" : "-§d" + getSubLocation()) + "§f]";
            case "Nightmare" -> (completed) ?
                    "§2[nm" + number + (getSubLocation().isEmpty() ? "" : "-" + getSubLocation()).replaceAll("§[0-9a-fA-F]", "§2") + "]" :
                    "[§8nm" + number + "§f" + (getSubLocation().isEmpty() ? "" : "-§d" + getSubLocation()) + "§f]";
            default -> "";
        };
    }

    public ToolType getRequiredTool() {
        return requiredTool;
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


        Pattern warpPattern = Pattern.compile("/warp\\s+[^)]+");
        Matcher warpMatcher = warpPattern.matcher(data);
        if (warpMatcher.find()) {
            this.warp = warpMatcher.group().replace("/", "");
        }


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
        } else {
            this.progress = m.group("progress") == null ? "" : m.group("progress");
            matched = true;
        }
    }

    private String normalizedTaskTarget() {
        if (taskTarget.isEmpty()) {
            return taskTarget;
        }

        if (this.isLarge) {
            return "Large " + taskTarget;
        }

        if (this.taskTarget.equals("fish")) {
            return "2 Riverfish in a row without missing";
        }

        if (!this.color.equals("")) {
            return this.color + " colored " + taskTarget;
        }

        if (!this.constraint.equals("")) {
            return taskTarget + " with " + this.constraint;
        }

        //if (taskTarget.contains("Shiny Ores")) {
        //    isShiny = true; // We already know it contains "Shiny Ores"
        //    return taskTarget.replace(" from Shiny Ores", "");
        //}

        //if (taskTarget.contains("colored Riverfish") || taskTarget.contains("using a Fishing Spear")) {
        //    return taskTarget.replace(" colored Riverfish", " Riverfish")
        //            .replace(" using a Fishing Spear", "")
        //            .replace(" drops from", "");
        //}
//
        //if (taskTarget.contains("drops from")) {
        //    return taskTarget.replace(" drops from", "");
        //}

        return taskTarget;
    }

    public String render(boolean completed) {
        String renderedString;


        if (taskType.equals("Quest") || taskType.equals("Tutorial")) {
            if (completed) {
                renderedString = "§2[" + world + "] " + this.taskType + ": §2" + name + "§f";
            } else {
                renderedString = "[§8" + world + "§f] " + this.taskType + ": §6§n" + name + "§f";
            }
        } else {
            if (completed) {
                renderedString = getLocation(true) + " " +
                        this.taskType + ": §2" + (isShiny ? "Shiny " : "") + normalizedTaskTarget() +
                        " (" + this.progress + "/" + this.targetAmount + ")§f";
            } else {
                renderedString = getLocation(false) + " " +
                        this.taskType + ": " + (isSocialite ? "§b" : "§6") + "§n" + (isShiny ? "Shiny " : "") + normalizedTaskTarget() +
                        "§r (§9" + this.progress + "§f/§c" + this.targetAmount + "§f)";
            }
        }


        calculateDisplayLength(renderedString);

        if (isTicket && !completed) {
            return renderedString.replaceAll("§[0-9a-fA-F]", "§5");
        }
        return renderedString;
    }

    private void calculateDisplayLength(String input) {
        // Remove color codes and return the length of the cleaned string
        String cleanedInput = COLOR_CODE_PATTERN.matcher(input).replaceAll("");
        this.strWidth = cleanedInput.length();
    }
}