package com.incrementalclient.hud;

import com.google.common.base.Suppliers;
import com.incrementalclient.abstractions.HudElement;
import com.incrementalclient.abstractions.TextListHudElement;
import com.incrementalclient.common.data.Region;
import com.incrementalclient.common.data.World;
import com.incrementalclient.common.data.tasks.TaskType;
import com.incrementalclient.common.data.tasks.abstractions.NormalTask;
import com.incrementalclient.common.utils.TextUtils;
import com.incrementalclient.interfaces.Configurable;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.services.GameInfoMonitor;
import com.incrementalclient.services.HudManager;
import com.incrementalclient.services.TaskMonitor;
import com.incrementalclient.common.utils.Vector2f;
import com.incrementalclient.services.WorldMonitor;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TaskTrackerElement extends TextListHudElement<TaskTrackerElement.Configuration> {

    private final WorldMonitor worldMonitor;
    private final TaskMonitor taskMonitor;
    private final GameInfoMonitor gameInfoMonitor;

    private final TaskTrackerElement.Configuration configuration = new Configuration();

    private final Supplier<List<OptionPiece>> options;

    public TaskTrackerElement(
            MinecraftClientAccessor mcAccessor,
            WorldMonitor worldMonitor,
            TaskMonitor taskMonitor,
            GameInfoMonitor gameInfoMonitor,
            HudManager hudManager
    ) {
        super(mcAccessor, hudManager);
        this.worldMonitor = worldMonitor;
        this.taskMonitor = taskMonitor;
        this.gameInfoMonitor = gameInfoMonitor;
        this.defaultPosition = new Vector2f(0.01F, 0.01777777F);
        resetToDefaultPosition();

        options = Suppliers.memoize(() -> List.of(
                Categories.Hud.Tasking.createConfig(0,
                        Option.<Boolean>createBuilder()
                                .name(Text.of("Disable Hud element during Boss Fights"))
                                .description(OptionDescription.of(Text.of("Toggle if you want this element to be disabled and hidden during boss fights")))
                                .binding(Configuration.defaultIsHudDisabledDuringBossFight, () -> configuration.isHudDisabledDuringBossFight, newVal -> configuration.isHudDisabledDuringBossFight = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build()),
                Categories.Hud.Tasking.createConfig(1,
                        Option.<Double>createBuilder()
                                .name(Text.of("Task HUD background opacity"))
                                .description(OptionDescription.of(Text.of("Set the opacity of the HUD background.")))
                                .binding(Configuration.defaultTaskHudBackgroundOpacity, () -> configuration.taskHudBackgroundOpacity, newVal -> configuration.taskHudBackgroundOpacity = newVal)
                                .controller(o -> DoubleSliderControllerBuilder.create(o).step(0.01).range(0.0, 1.0))
                                .build()),
                Categories.Hud.Tasking.createConfig(2,
                        Option.<Color>createBuilder()
                                .name(Text.of("Color of the base text"))
                                .description(OptionDescription.of(Text.of("The color of the base text.")))
                                .binding(new Color(Configuration.defaultTextColor), () -> new Color(configuration.textColor), newVal -> configuration.textColor = newVal.getRGB())
                                .controller(ColorControllerBuilder::create)
                                .build()),
                Categories.Hud.Tasking.createConfig(3,
                        Option.<Color>createBuilder()
                                .name(Text.of("Color of the world text"))
                                .description(OptionDescription.of(Text.of("The color of the world text.")))
                                .binding(new Color(Configuration.defaultWorldColor), () -> new Color(configuration.worldColor), newVal -> configuration.worldColor = newVal.getRGB())
                                .controller(ColorControllerBuilder::create)
                                .build()),
                Categories.Hud.Tasking.createConfig(4,
                        Option.<Color>createBuilder()
                                .name(Text.of("Color of the task text"))
                                .description(OptionDescription.of(Text.of("The color of the task target.")))
                                .binding(new Color(Configuration.defaultTaskColor), () -> new Color(configuration.taskColor), newVal -> configuration.taskColor = newVal.getRGB())
                                .controller(ColorControllerBuilder::create)
                                .build()),
                Categories.Hud.Tasking.createConfig(5,
                        Option.<Color>createBuilder()
                                .name(Text.of("Color of the socialite text"))
                                .description(OptionDescription.of(Text.of("The highlight color used for socialite tasks.")))
                                .binding(new Color(Configuration.defaultSocialiteColor), () -> new Color(configuration.socialiteColor), newVal -> configuration.socialiteColor = newVal.getRGB())
                                .controller(ColorControllerBuilder::create)
                                .build()),
                Categories.Hud.Tasking.createConfig(6,
                        Option.<Color>createBuilder()
                                .name(Text.of("Color of the progress text"))
                                .description(OptionDescription.of(Text.of("The color used for the current task progress number.")))
                                .binding(new Color(Configuration.defaultProgressColor), () -> new Color(configuration.progressColor), newVal -> configuration.progressColor = newVal.getRGB())
                                .controller(ColorControllerBuilder::create)
                                .build()),
                Categories.Hud.Tasking.createConfig(7,
                        Option.<Color>createBuilder()
                                .name(Text.of("Color of the target text"))
                                .description(OptionDescription.of(Text.of("The color used for the target amount required.")))
                                .binding(new Color(Configuration.defaultTargetColor), () -> new Color(configuration.targetColor), newVal -> configuration.targetColor = newVal.getRGB())
                                .controller(ColorControllerBuilder::create)
                                .build()),
                Categories.Hud.Tasking.createConfig(8,
                        Option.<Color>createBuilder()
                                .name(Text.of("Color of the completed text"))
                                .description(OptionDescription.of(Text.of("The text color used when a task is completed.")))
                                .binding(new Color(Configuration.defaultCompleteColor), () -> new Color(configuration.completeColor), newVal -> configuration.completeColor = newVal.getRGB())
                                .controller(ColorControllerBuilder::create)
                                .build()),
                Categories.Hud.Tasking.createConfig(9,
                        Option.<Color>createBuilder()
                                .name(Text.of("Color of the ticket text"))
                                .description(OptionDescription.of(Text.of("The color used to highlight ticket tasks.")))
                                .binding(new Color(Configuration.defaultTicketColor), () -> new Color(configuration.ticketColor), newVal -> configuration.ticketColor = newVal.getRGB())
                                .controller(ColorControllerBuilder::create)
                                .build())
        ));
    }

    @Override
    protected List<Text> getTextsToRender(boolean editMode) {

        if (worldMonitor.currentWorld() == World.Supermarket) {
            return miscTaskRender(gameInfoMonitor.getCurrentSnapshot().miscTask);
        }

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
    public boolean isEnabled() {
        return enabled && (worldMonitor.currentWorld() != World.BossArenas || !configuration.isHudDisabledDuringBossFight);
    }

    @Override
    public String getDisplayName() {
        return "Task Tracker";
    }

    @Override
    protected int getPlaceholderWidth() {
        return HudConstants.PLACEHOLDER_WIDTH_SMALL;
    }

    private Text taskRender(TaskMonitor.TaskState taskState) {

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
                .append(TextUtils.textColor(isQuestOrTutorial(taskState) ? taskState.getName() : taskState.getDisplayName(), taskState.isSocialite() ? socialiteColor : taskColor, true, false));


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

    private List<Text> miscTaskRender(Text miscTask) {
        String[] words = miscTask.getString().split(" ");
        int maxWidth = (int) (getScreenWidth() * 0.28);

        List<Text> lines = new ArrayList<>();
        MutableText currentLine = Text.literal("");
        int currentWidth = 0;

        for (String word : words) {
            // This block is specifically for the supermarket event task format, if another case for this comes up this
            // block should be fixed up for that, however remaining code is just to split it up so task doesn't take up
            // more than 28% of the screen
            Text wordText = word.equals("TASK")
                    ? TextUtils.textColor(word, Formatting.YELLOW, false, true)
                    : Text.literal(word);

            int wordWidth = getTextWidth(wordText);
            int spaceWidth = currentWidth == 0 ? 0 : getTextWidth(Text.literal(" "));

            if (currentWidth != 0 && currentWidth + spaceWidth + wordWidth > maxWidth) {
                lines.add(currentLine);
                currentLine = Text.literal("");
                currentWidth = 0;
                spaceWidth = 0;
            }

            if (currentWidth != 0) {
                currentLine.append(" ");
                currentWidth += spaceWidth;
            }
            currentLine.append(wordText);
            currentWidth += wordWidth;
        }

        if (currentWidth != 0 || lines.isEmpty()) {
            lines.add(currentLine);
        }

        return lines;
    }

    private boolean isQuestOrTutorial(TaskMonitor.TaskState taskState) {
        return (taskState.getTaskType() == TaskType.Quest || taskState.getTaskType() == TaskType.Tutorial);
    }

    private Text getSublocation(TaskMonitor.TaskState taskState) {
        if (taskState.getTask() != null && taskState.getTask().getDescriptor() instanceof NormalTask task) {
            int textColor = configuration.textColor;
            return switch (task.getRegion()) {
                case Region.W2_Lush ->
                        Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0x54fc54));
                case Region.W2_Veil ->
                        Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0xa800a8));
                case Region.W2_Infernal ->
                        Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0xfc5454));
                case Region.W2_Abyss ->
                        Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0x4b696d));
                case Region.W3_Sty ->
                        Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0xe9a3a2));
                case Region.W3_Beach ->
                        Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0xd6cba2));
                case Region.W3_Underside ->
                        Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0x74b0b0));
                case Region.W3_Topside ->
                        Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0xe58a08));
                case Region.W3_Canine ->
                        Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0x9c929f));
                case Region.W3_Mine ->
                        Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0xa3cbcb));
                case Region.W4_Homestead ->
                        Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0x944a00));
                case Region.W4_Sewer ->
                        Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0x944a00));
                case Region.W4_CityOutskirt ->
                        Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0x944a00));
                case Region.W4_Alpha ->
                        Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0x00a800));
                case Region.W4_Beta ->
                        Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0xa800a8));
                case Region.W4_Delta ->
                        Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0x5353f9));
                default -> Text.of("");
            };
        } else return Text.of("");
    }

    private Text getLocation(TaskMonitor.TaskState taskState) {

        int textColor = configuration.textColor;
        int worldColor = configuration.worldColor;

        String world = taskState.getTask() != null
                ? taskState.getTask().getDescriptor().getRegion().getWorld().getShortName()
                : "-";

        return Text.literal("")
                .append(TextUtils.textColor("[", textColor))
                .append(TextUtils.textColor(world, worldColor))
                .append(getSublocation(taskState))
                .append(TextUtils.textColor("]", textColor));
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
        return options.get();
    }

    public static class Configuration extends HudElement.ConfigurationBase {

        private static final boolean defaultIsHudDisabledDuringBossFight = false;
        private static final double defaultTaskHudBackgroundOpacity = 0.0;
        private static final int defaultTextColor = 0xfcfcfc;
        private static final int defaultWorldColor = 0x555555;
        private static final int defaultTaskColor = 0xffaa00;
        private static final int defaultSocialiteColor = 0x55ffff;
        private static final int defaultProgressColor = 0x5555ff;
        private static final int defaultTargetColor = 0xff5555;
        private static final int defaultCompleteColor = 0x00aa00;
        private static final int defaultTicketColor = 0x8845d1;

        @SerialEntry
        public boolean isHudDisabledDuringBossFight = defaultIsHudDisabledDuringBossFight;
        @SerialEntry
        public double taskHudBackgroundOpacity = defaultTaskHudBackgroundOpacity;
        @SerialEntry
        public int textColor = defaultTextColor;
        @SerialEntry
        public int worldColor = defaultWorldColor;
        @SerialEntry
        public int taskColor = defaultTaskColor;
        @SerialEntry
        public int socialiteColor = defaultSocialiteColor;
        @SerialEntry
        public int progressColor = defaultProgressColor;
        @SerialEntry
        public int targetColor = defaultTargetColor;
        @SerialEntry
        public int completeColor = defaultCompleteColor;
        @SerialEntry
        public int ticketColor = defaultTicketColor;
    }
}

