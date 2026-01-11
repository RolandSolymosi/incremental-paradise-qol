package com.incrementalclient.hud;

import com.incrementalclient.abstractions.HudElement;
import com.incrementalclient.abstractions.TextListHudElement;
import com.incrementalclient.common.data.Region;
import com.incrementalclient.common.data.World;
import com.incrementalclient.common.data.tasks.TaskType;
import com.incrementalclient.common.data.tasks.abstractions.NormalTask;
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
                Categories.Tasking.Hud.createConfig(0,
                        Option.<Double>createBuilder()
                                .name(Text.of("Task HUD background opacity"))
                                .description(OptionDescription.of(Text.of("Set the opacity of the HUD background.")))
                                .binding(0.3, () -> configuration.taskHudBackgroundOpacity, newVal -> configuration.taskHudBackgroundOpacity = newVal)
                                .controller(o -> DoubleSliderControllerBuilder.create(o).step(0.01).range(0.0, 1.0))
                                .build()),
                Categories.Tasking.Hud.createConfig(1,
                        Option.<Color>createBuilder()
                                .name(Text.of("Color of the base text"))
                                .description(OptionDescription.of(Text.of("The color of the base text.")))
                                .binding(new Color(0xfcfcfc), () -> new Color(configuration.textColor), newVal -> configuration.textColor = newVal.getRGB())
                                .controller(ColorControllerBuilder::create)
                                .build()),
                Categories.Tasking.Hud.createConfig(2,
                        Option.<Color>createBuilder()
                                .name(Text.of("Color of the world text"))
                                .description(OptionDescription.of(Text.of("The color of the world text.")))
                                .binding(new Color(0x555555), () -> new Color(configuration.worldColor), newVal -> configuration.worldColor = newVal.getRGB())
                                .controller(ColorControllerBuilder::create)
                                .build()),
                Categories.Tasking.Hud.createConfig(3,
                        Option.<Color>createBuilder()
                                .name(Text.of("Color of the task text"))
                                .description(OptionDescription.of(Text.of("The color of the task target.")))
                                .binding(new Color(0xffaa00), () -> new Color(configuration.taskColor), newVal -> configuration.taskColor = newVal.getRGB())
                                .controller(ColorControllerBuilder::create)
                                .build()),
                Categories.Tasking.Hud.createConfig(4,
                        Option.<Color>createBuilder()
                                .name(Text.of("Color of the socialite text"))
                                .description(OptionDescription.of(Text.of("The highlight color used for socialite tasks.")))
                                .binding(new Color(0x55ffff), () -> new Color(configuration.socialiteColor), newVal -> configuration.socialiteColor = newVal.getRGB())
                                .controller(ColorControllerBuilder::create)
                                .build()),
                Categories.Tasking.Hud.createConfig(5,
                        Option.<Color>createBuilder()
                                .name(Text.of("Color of the progress text"))
                                .description(OptionDescription.of(Text.of("The color used for the current task progress number.")))
                                .binding(new Color(0x5555ff), () -> new Color(configuration.progressColor), newVal -> configuration.progressColor = newVal.getRGB())
                                .controller(ColorControllerBuilder::create)
                                .build()),
                Categories.Tasking.Hud.createConfig(6,
                        Option.<Color>createBuilder()
                                .name(Text.of("Color of the target text"))
                                .description(OptionDescription.of(Text.of("The color used for the target amount required.")))
                                .binding(new Color(0xff5555), () -> new Color(configuration.targetColor), newVal -> configuration.targetColor = newVal.getRGB())
                                .controller(ColorControllerBuilder::create)
                                .build()),
                Categories.Tasking.Hud.createConfig(7,
                        Option.<Color>createBuilder()
                                .name(Text.of("Color of the completed text"))
                                .description(OptionDescription.of(Text.of("The text color used when a task is completed.")))
                                .binding(new Color(0x00aa00), () -> new Color(configuration.completeColor), newVal -> configuration.completeColor = newVal.getRGB())
                                .controller(ColorControllerBuilder::create)
                                .build()),
                Categories.Tasking.Hud.createConfig(8,
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
                .append(TextUtils.textColorUnderline(isQuestOrTutorial(taskState) ? taskState.getName() : taskState.getDisplayName(), taskState.isSocialite() ? socialiteColor : taskColor));


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

    private Text getSublocation(TaskMonitor.TaskState taskState) {
        if (taskState.getTask() != null && taskState.getTask().getDescriptor() instanceof NormalTask task) {
            int textColor = configuration.textColor;
            return switch (task.getRegion()) {
                case Region.W2_Lush -> Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0x54fc54));
                case Region.W2_Veil -> Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0xa800a8));
                case Region.W2_Infernal -> Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0xfc5454));
                case Region.W2_Abyss -> Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0x4b696d));
                case Region.W3_Sty -> Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0xe9a3a2));
                case Region.W3_Beach -> Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0xd6cba2));
                case Region.W3_Underside -> Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0x74b0b0));
                case Region.W3_Topside -> Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0xe58a08));
                case Region.W3_Canine -> Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0x9c929f));
                case Region.W3_Mine -> Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0xa3cbcb));
                case Region.W4_Homestead -> Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0x944a00));
                case Region.W4_Alpha -> Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0x00a800));
                case Region.W4_Beta -> Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0xa800a8));
                case Region.W4_Delta -> Text.literal("").append(TextUtils.textColor("-", textColor)).append(TextUtils.textColor(task.getRegion().getName(), 0x54fc54)); //TODO: Need color for Delta;
                default -> Text.of("");
            };
        }

        else return Text.of("");
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

