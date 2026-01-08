package com.incrementalclient.hud;

import com.incrementalclient.abstractions.HudElement;
import com.incrementalclient.abstractions.TextListHudElement;
import com.incrementalclient.common.data.World;
import com.incrementalclient.common.data.tasks.Constraint;
import com.incrementalclient.common.data.tasks.TaskType;
import com.incrementalclient.common.utils.TextUtils;
import com.incrementalclient.interfaces.Configurable;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.services.HudManager;
import com.incrementalclient.services.TaskMonitor;
import com.incrementalclient.common.utils.Vector2f;
import com.incrementalclient.services.WorldMonitor;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TaskTrackerElement extends TextListHudElement<TaskTrackerElement.Configuration> {
    private final WorldMonitor worldMonitor;
    private final TaskMonitor taskMonitor;

    private final TaskTrackerElement.Configuration configuration = new Configuration();

    private final List<Configurable.OptionPiece> options;

    public TaskTrackerElement(
            MinecraftClientAccessor mcAccessor,
            WorldMonitor worldMonitor,
            TaskMonitor taskMonitor,
            HudManager hudManager
    ) {
        super(mcAccessor, hudManager);
        this.worldMonitor = worldMonitor;
        this.taskMonitor = taskMonitor;
        this.anchorPoint = new Vector2f(10, 10);

        options = List.of(
                new Configurable.OptionPiece(
                        "HUD",
                        60,
                        "Task HUD configuration",
                        "Allow you to set opacity of the Task HUD background.",
                        0,
                        Option.<Double>createBuilder()
                                .name(Text.of("Task HUD background opacity"))
                                .description(OptionDescription.of(Text.of("Set the opacity of the HUD background.")))
                                .binding(0.3, () -> configuration.taskHudBackgroundOpacity, newVal -> configuration.taskHudBackgroundOpacity = newVal)
                                .controller(o -> DoubleSliderControllerBuilder.create(o).step(0.01).range(0.0, 1.0))
                                .build()),
                new Configurable.OptionPiece(
                        "HUD",
                        60,
                        "Task HUD Color configuration",
                        "Also allow you to set the colors of your hud. Press enter or esc to exit the color selection.",
                        0,
                        Option.<Color>createBuilder()
                                .name(Text.of("Color of the base text"))
                                .description(OptionDescription.of(Text.of("The color of the base text.")))
                                .binding(new Color(0xfcfcfc), () -> new Color(configuration.textColor), newVal -> configuration.textColor = newVal.getRGB())
                                .controller(ColorControllerBuilder::create)
                                .build()),
                new Configurable.OptionPiece(
                        "HUD",
                        60,
                        "Task HUD Color configuration",
                        "",
                        1,
                        Option.<Color>createBuilder()
                                .name(Text.of("Color of the world text"))
                                .description(OptionDescription.of(Text.of("The color of the world text.")))
                                .binding(new Color(0x555555), () -> new Color(configuration.worldColor), newVal -> configuration.worldColor = newVal.getRGB())
                                .controller(ColorControllerBuilder::create)
                                .build()),
                new Configurable.OptionPiece(
                        "HUD",
                        60,
                        "Task HUD Color configuration",
                        "",
                        2,
                        Option.<Color>createBuilder()
                                .name(Text.of("Color of the task text"))
                                .description(OptionDescription.of(Text.of("The color of the task target.")))
                                .binding(new Color(0xffaa00), () -> new Color(configuration.taskColor), newVal -> configuration.taskColor = newVal.getRGB())
                                .controller(ColorControllerBuilder::create)
                                .build()),
                new Configurable.OptionPiece(
                        "HUD",
                        60,
                        "Task HUD Color configuration",
                        "",
                        3,
                        Option.<Color>createBuilder()
                                .name(Text.of("Color of the socialite text"))
                                .description(OptionDescription.of(Text.of("The highlight color used for socialite tasks.")))
                                .binding(new Color(0x55ffff), () -> new Color(configuration.socialiteColor), newVal -> configuration.socialiteColor = newVal.getRGB())
                                .controller(ColorControllerBuilder::create)
                                .build()),
                new Configurable.OptionPiece(
                        "HUD",
                        60,
                        "Task HUD Color configuration",
                        "",
                        4,
                        Option.<Color>createBuilder()
                                .name(Text.of("Color of the progress text"))
                                .description(OptionDescription.of(Text.of("The color used for the current task progress number.")))
                                .binding(new Color(0x5555ff), () -> new Color(configuration.progressColor), newVal -> configuration.progressColor = newVal.getRGB())
                                .controller(ColorControllerBuilder::create)
                                .build()),
                new Configurable.OptionPiece(
                        "HUD",
                        60,
                        "Task HUD Color configuration",
                        "",
                        5,
                        Option.<Color>createBuilder()
                                .name(Text.of("Color of the target text"))
                                .description(OptionDescription.of(Text.of("The color used for the target amount required.")))
                                .binding(new Color(0xff5555), () -> new Color(configuration.targetColor), newVal -> configuration.targetColor = newVal.getRGB())
                                .controller(ColorControllerBuilder::create)
                                .build()),
                new Configurable.OptionPiece(
                        "HUD",
                        60,
                        "Task HUD Color configuration",
                        "",
                        6,
                        Option.<Color>createBuilder()
                                .name(Text.of("Color of the completed text"))
                                .description(OptionDescription.of(Text.of("The text color used when a task is completed.")))
                                .binding(new Color(0x00aa00), () -> new Color(configuration.completeColor), newVal -> configuration.completeColor = newVal.getRGB())
                                .controller(ColorControllerBuilder::create)
                                .build()),
                new Configurable.OptionPiece(
                        "HUD",
                        60,
                        "Task HUD Color configuration",
                        "",
                        6,
                        Option.<Color>createBuilder()
                                .name(Text.of("Color of the ticket text"))
                                .description(OptionDescription.of(Text.of("The color used to highlight ticket tasks.")))
                                .binding(new Color(0x8845d1), () -> new Color(configuration.ticketColor), newVal -> configuration.ticketColor = newVal.getRGB())
                                .controller(ColorControllerBuilder::create)
                                .build())
        );
    }

    @Override
    protected List<Text> getTextsToRender(boolean editMode) {
        var taskList = taskMonitor.getTaskList();

        if (taskList.isEmpty()) {
            return List.of();
        }

        // Sort if needed
        // TODO: Move sorted by to the TaskMonitor, or an inbetween service
        //if (configuration.isSortedByType) {
        //    taskList.sort(Comparator.comparing(t -> t.getTask().getDescriptor().taskType().name()));
        //}

        return taskList.stream()
                .map(this::taskRender)
                .collect(Collectors.toList());
    }

    @Override
    protected int getBackgroundOpacity() {
        return (int) (configuration.taskHudBackgroundOpacity * 255);
    }

    @Override
    public boolean isElementEnabled() {
        return worldMonitor.currentWorld() != World.BossArenas;
    }

    @Override
    public String getDisplayName() {
        return "Task Tracker";
    }

    @Override
    protected int getPlaceholderWidth() {
        return HudConstants.PLACEHOLDER_WIDTH_SMALL;
    }

    @Override
    public Vector2f getAnchorPoint() {
        return anchorPoint;
    }

    public Text taskRender(TaskMonitor.TaskState taskState) {

        int textColor = configuration.textColor;
        int taskColor = configuration.taskColor;
        int socialiteColor = configuration.socialiteColor;
        int progressColor = configuration.progressColor;
        int targetColor = configuration.targetColor;
        int completeColor = configuration.completeColor;
        int ticketColor = configuration.ticketColor;

        MutableText displayText = Text.literal("")
                .append(getLocation(taskState))
                .append(TextUtils.textColor(" " + taskState.getTaskType().name() + ": ", textColor))
                .append(TextUtils.textColorUnderline(isQuestOrTutorial(taskState) ? taskState.getName() : normalizedTaskTarget(taskState), taskState.isSocialite() ? socialiteColor : taskColor));


        if (!isQuestOrTutorial(taskState)) {
            displayText
                    .append(TextUtils.textColor(" (", textColor))
                    .append(TextUtils.textColor(taskState.isCompleted() ? taskState.getRequired() : taskState.getCurrent(), progressColor))
                    .append(TextUtils.textColor("/", textColor))
                    .append(TextUtils.textColor(taskState.getRequired(), targetColor))
                    .append(TextUtils.textColor(")", textColor));
        }

        if (taskState.isTicket()) {
            displayText = TextUtils.mutableRecolor(displayText, ticketColor);
        }

        if (taskState.isCompleted()) {
            displayText = TextUtils.mutableRecolor(displayText, completeColor);
        }

        return displayText;
    }

    private boolean isQuestOrTutorial(TaskMonitor.TaskState taskState) {
        return (taskState.getTaskType() == TaskType.Quest || taskState.getTaskType() == TaskType.Tutorial);
    }

    private Text getSublocation(TaskMonitor.TaskState taskStaten) {

        int textColor = configuration.textColor;

        // TODO: These should be replaced to be using the TaskTargets, also probably make it static data and change data structur
        if (lush.contains(taskStaten.getDescription()))
            return Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor("Lush", 0x54fc54));
        else if (veil.contains(taskStaten.getDescription()))
            return Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor("Veil", 0xa800a8));
        else if (infernal.contains(taskStaten.getDescription()))
            return Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor("Infernal", 0xfc5454));
        else if (abyss.contains(taskStaten.getDescription()))
            return Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor("Abyss", 0x4b696d));

        else if (sty.contains(taskStaten.getDescription()))
            return Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor("Sty", 0xe9a3a2));
        else if (beach.contains(taskStaten.getDescription()))
            return Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor("Beach", 0xd6cba2));
        else if (underside.contains(taskStaten.getDescription()))
            return Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor("Underside", 0x74b0b0));
        else if (topside.contains(taskStaten.getDescription()))
            return Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor("Topside", 0xe58a08));
        else if (canine.contains(taskStaten.getDescription()))
            return Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor("Canine", 0x9c929f));
        else if (mines.contains(taskStaten.getDescription()))
            return Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor("Mines", 0xa3cbcb));

        else if (homestead.contains(taskStaten.getDescription()))
            return Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor("Homestead", 0x944a00));
        else if (alpha.contains(taskStaten.getDescription()))
            return Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor("Alpha", 0x00a800));
        else if (beta.contains(taskStaten.getDescription()))
            return Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor("Beta", 0xa800a8));

        else return Text.of("");
    }

    private static final List<String> lush = Stream.of("Poison Slimes", "Cave Crawlers", "Lurkers", "Ancient Lurkers", "Crimsonite").map(String::toLowerCase).toList();
    private static final List<String> veil = Stream.of("Spotters", "Verdemites", "Endermen", "Verdelith", "Shy").map(String::toLowerCase).toList();
    private static final List<String> infernal = Stream.of("Ghasts", "Ghast Souls", "Blazes", "Nrubs", "Infernal Imps", "Azuregem", "Bubbler", "Magmafish", "Molten Jellyfish", "Lavafruit").map(String::toLowerCase).toList();
    private static final List<String> abyss = Stream.of("Wicks", "Glow Squids", "Slinkers", "Aurorium", "Twine", "Zephyr", "Abyssal Crabs", "Lampposts").map(String::toLowerCase).toList();

    private static final List<String> sty = Stream.of("Gorespore", "Gorespore Spores", "Baconwings", "Oinky", "Bettafly").map(String::toLowerCase).toList();
    private static final List<String> beach = Stream.of("Dune Dweller", "Dune Dweller Spores", "Camels", "Cattails", "Soarfish").map(String::toLowerCase).toList();
    private static final List<String> underside = Stream.of("Gloom", "Guardians").map(String::toLowerCase).toList();
    private static final List<String> topside = Stream.of("Honey Shrooms", "Honey Shroom Spores", "Bees", "Royal Guards").map(String::toLowerCase).toList();
    private static final List<String> canine = Stream.of("Barky", "Pinepoodles", "Dire Wolves", "Collie-Flowers", "Goldfish Retrievers").map(String::toLowerCase).toList();
    private static final List<String> mines = Stream.of("Brightstone", "Diamonds", "Emeralds", "Breezes").map(String::toLowerCase).toList();

    private static final List<String> homestead = Stream.of("Cheddore", "Blue Cheese", "Nesting Wood", "Passionfruit", "Bats", "Rats", "Rattus", "Grasshoppers", "Astromold & Astromites", "Magmold", "Cats", "Catfish", "Sewer Chests").map(String::toLowerCase).toList();
    private static final List<String> alpha = Stream.of("Cryoflora", "Chillfruit", "Sulphoroot", "Jackfruit", "Pyrospire", "Scorchberries", "Thorn Beetles", "Worms", "Frogs", "Algae", "Boom Shrooms", "Piranhas").map(String::toLowerCase).toList();
    private static final List<String> beta = Stream.of("Glowdust", "Slimecrust", "Voidshard", "Snipers", "Angry Miners", "Ravagers").map(String::toLowerCase).toList();

    private Text getLocation(TaskMonitor.TaskState taskState) {

        int textColor = configuration.textColor;
        int worldColor = configuration.worldColor;

        String world = taskState.getTask() != null
                ? taskState.getTask().getDescriptor().warps().getFirst().getWorld().getName()
                : "-";

        return Text.literal("")
                .append(TextUtils.textColor("[", textColor))
                .append(TextUtils.textColor(world, worldColor))
                .append(getSublocation(taskState))
                .append(TextUtils.textColor("]", textColor));
    }

    private String normalizedTaskTarget(TaskMonitor.TaskState taskState) {
        if (taskState.getTask() == null) {
            return "";
        }

        if (taskState.getTask().getDescriptor().constraints().contains(Constraint.Large)) {
            return "Large " + taskState.getTask().getDescriptor().names().getFirst();
        }

        if (taskState.getTask().getDescriptor().constraints().contains(Constraint.Shiny)) {
            return "Shiny " + taskState.getTask().getDescriptor().names().getFirst();
        }

        if (taskState.getTask().getDescriptor().constraints().contains(Constraint.Elite)) {
            return "Elite " + taskState.getTask().getDescriptor().names().getFirst();
        }

        if (taskState.getTask().getDescriptor().constraints().contains(Constraint.Consecutive)) {
            return "2 Riverfish in a row without missing";
        }

        if (taskState.getTask().getDescriptor().constraints().contains(Constraint.Colored)) {
            // TODO: Separate colors
            return "TODO colored fish";
        }

        if (!taskState.getTask().getDescriptor().constraints().isEmpty()) {
            return taskState.getTask().name() + " with " + taskState.getTask().getDescriptor().constraints().getFirst().getName();
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

        return taskState.getTask().getDescriptor().names().getFirst();
    }

    @Override
    public String getJsonSection() {
        return "taskTrackerHud";
    }

    @Override
    public TaskTrackerElement.Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public List<Configurable.OptionPiece> getOption() {
        return options;
    }


    public static class Configuration extends HudElement.ConfigurationBase {

        @SerialEntry
        public double taskHudBackgroundOpacity = 0.3;

        @SerialEntry
        public int textColor = 0xfcfcfc;
        @SerialEntry
        public int worldColor = 0x555555;
        @SerialEntry
        public int taskColor = 0xffaa00;
        @SerialEntry
        public int socialiteColor = 0x55ffff;
        @SerialEntry
        public int progressColor = 0x5555ff;
        @SerialEntry
        public int targetColor = 0xff5555;
        @SerialEntry
        public int completeColor = 0x00aa00;
        @SerialEntry
        public int ticketColor = 0x8845d1;
    }
}

