package com.incrementalclient.services;

import com.incrementalclient.abstractions.ObservableBase;
import com.incrementalclient.common.data.tasks.Constraint;
import com.incrementalclient.common.data.tasks.Task;
import com.incrementalclient.common.data.tasks.TaskType;
import com.incrementalclient.common.utils.Utils;
import com.incrementalclient.interfaces.Observer;
import com.incrementalclient.internals.BossBarObservable;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.internals.ScreenCapture;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaskMonitor extends ObservableBase<Observer<List<TaskMonitor.TaskState>>, List<TaskMonitor.TaskState>> {
    private final List<TaskState> taskList = new CopyOnWriteArrayList<>();

    private final InteractionScheduler<Void> interactionScheduler;
    private final InteractionScheduler.Builder<Void, Void> refreshTaskBuilder;

    public TaskMonitor(
            BossBarObservable bossBarObservable,
            ChatHandler chatHandler,
            CommandHandler commandHandler,
            ScreenCapture screenCapture,
            InteractionScheduler<Void> interactionScheduler,
            MinecraftClientAccessor minecraftClientAccessor,
            WorldMonitor worldMonitor
    ) {
        this.interactionScheduler = interactionScheduler;
        this.refreshTaskBuilder = new InteractionScheduler.Builder<Void, Void>("TaskRefresh", interactionScheduler, minecraftClientAccessor)
                .priority(100)
                .timeout(20)
                .retries(2)
                .startWith(() -> commandHandler.send("tasks"))
                .step(
                        (screen, ctx) -> screen.title().getString().contains("Tasks"),
                        ctx -> false
                );

        bossBarObservable.subscribe(new BossBarObserver(this));
        chatHandler.subscribe(new ChatMessageObserver(this));
        screenCapture.subscribe(new ScreenObserver(this));
        worldMonitor.subscribe(new Observer.DefaultObserver<>(w -> {
            if (w.from().getRealm() != w.to().getRealm()){
                interactionScheduler.submit(refreshTaskBuilder.build(null));
            }
        }));
    }

    public List<TaskState> getTaskList() {
        return taskList;
    }

    private void bossBarUpdated(BossBarObservable.BossBar bossBar) {
        for (var task : taskList) {
            var updateState = task.tryUpdate(bossBar.text());
            if (updateState != TaskState.UpdateState.NotMatched) {
                if (updateState == TaskState.UpdateState.Changed) {
                    notifyObservers(taskList);
                }
                break;
            }
        }
    }

    private void chatMessageArrived(Text text) {
        if (text.getString().contains("Completed task")) {

            String pattern = "(?<=^Completed task\\s).*";

            Pattern regex = Pattern.compile(pattern);
            Matcher matcher = regex.matcher(text.getString());

            if (matcher.find()) {
                String result = matcher.group();
                for (TaskState task : taskList) {
                    if (result.equals(task.getName())) {
                        if (task.completeIfOngoing()) {
                            notifyObservers(taskList);
                        }
                        break;
                    }
                }
            }
        }
        else if (text.getString().contains("You are now Prestige ") ||
                text.getString().contains("You are now Ascension ") ||
                text.getString().contains("You are now Nightmare Prestige ") ||
                text.getString().contains("You are now Transcendence ") ||
                text.getString().contains("You started the Tr") ||
                text.getString().contains("You completed Tr") ||
                text.getString().contains("Trial abandoned")){
            interactionScheduler.submit(refreshTaskBuilder.build(null));
        }

    }

    private void screenInfoArrived(ScreenCapture.Screen screen) {
        if (screen.title().getString().equals("Tasks") && !screen.contents().isEmpty()) {
            var array = new ArrayList<TaskState>();
            for (int i = 0; i < screen.contents().size(); i++) {
                var stack = screen.contents().get(i);
                var taskState = TaskStateFactory.create(stack, i);
                if (taskState != null) {
                    array.add(taskState);
                }
            }
            taskList.clear();
            taskList.addAll(array);
            notifyObservers(taskList);
        }
    }

    private record BossBarObserver(TaskMonitor taskMonitor) implements Observer<BossBarObservable.BossBar> {
        @Override
        public void onEvent(BossBarObservable.BossBar result) {
            taskMonitor.bossBarUpdated(result);
        }
    }

    private record ChatMessageObserver(TaskMonitor taskMonitor) implements Observer<ChatHandler.Event> {
        @Override
        public void onEvent(ChatHandler.Event result) {
            taskMonitor.chatMessageArrived(result.message());
        }
    }

    private record ScreenObserver(TaskMonitor taskMonitor) implements Observer<ScreenCapture.Screen> {
        @Override
        public void onEvent(ScreenCapture.Screen result) {
            taskMonitor.screenInfoArrived(result);
        }
    }

    private static class TaskStateFactory {
        public static TaskState create(ItemStack itemStack, int slotId) {
            var lore = itemStack.get(DataComponentTypes.LORE);
            if (lore != null) {
                var text = lore.lines();
                var blocks = Utils.parseLoreLines(text);

                if ((isTaskBook(itemStack) && blocks.getFirst().contains("Task")) || Utils.isPlayerHead(itemStack)) {
                    var taskType = extractTaskType(blocks.get(0));

                    if (taskType.isPresent()) {
                        var taskName = cleanTaskName(itemStack.getName().getString());
                        if (taskType.get() == TaskType.Quest || taskType.get() == TaskType.Tutorial) {
                            var taskState = new TaskState(taskName, "", "", taskType.get(), null, new String[0], slotId, false, false, "?", "", null);
                            if (isCompletedBook(itemStack)){
                                taskState.completeIfOngoing();
                            }
                            return taskState;
                        } else {
                            String description = blocks.size() > 1 ? blocks.get(1) : "";
                            for (Pattern pattern : taskType.get().getPatterns()) {
                                var taskMatch = tryMatchTaskByFullInfo(pattern, description);
                                if (taskMatch != null) {
                                    var taskState = new TaskState(taskName, description, taskMatch.taskTarget, taskType.get(), taskMatch.task, taskMatch.constraintParameters.toArray(new String[0]), slotId, isTicketTask(blocks.getFirst()), isSocialiteTask(blocks.getFirst()), taskMatch.amount, taskMatch.progress, pattern);
                                    if (isCompletedBook(itemStack)){
                                        taskState.completeIfOngoing();
                                    }
                                    return taskState;
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }

        private static boolean isCompletedBook(ItemStack stack){
            return stack.getItem().getName().getString().contains("Written");
        }

        private static TaskMatch tryMatchTaskByFullInfo(Pattern pattern, String description) {
            var matcher = pattern.matcher(description);
            if (matcher.find()) {
                var taskTarget = getGroupValue(matcher, "type");

                var constraints = new HashSet<Constraint>();
                var matchedConstraints = getGroupValue(matcher, "constraint");
                if (matchedConstraints != null) {
                    constraints.add(Constraint.find(matchedConstraints));
                }
                matchedConstraints = getGroupValue(matcher, "constraint2");
                if (matchedConstraints != null) {
                    constraints.add(Constraint.find(matchedConstraints));
                }
                var progress = getGroupValue(matcher, "progress");
                var targetAmount = getGroupValue(matcher, "amount");

                var parameters = new ArrayList<String>();
                var parameter = getGroupValue(matcher, "parameter");
                if (parameter != null) {
                    parameters.add(parameter);
                }

                var task = Task.tryGetTask(taskTarget, constraints);
                return new TaskMatch(task, taskTarget, progress, targetAmount, parameters);
            }
            return null;
        }

        private static String getGroupValue(Matcher matcher, String groupName) {
            try {
                var match = matcher.group(groupName);
                return match == null || match.isEmpty() ? null : match;
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        private static boolean isTaskBook(ItemStack stack) {
            return stack.isIn(ItemTags.BOOKSHELF_BOOKS);
        }

        private static Optional<TaskType> extractTaskType(String block) {
            String type = "";

            if (block.contains("World") || block.contains("Nightmare")) {
                var descriptorPattern = Pattern.compile("(?<world>World|Nightmare) #(?<number>\\d+)\\s*(?<type>.+?)\\s*Task");
                var m = descriptorPattern.matcher(block);
                if (m.find()) {
                    type = m.group("type");
                }
            } else {
                var questPattern = Pattern.compile("(?<type>.+) Task");
                var m = questPattern.matcher(block);
                if (m.find()) {
                    type = m.group("type");
                }
            }

            return TaskType.findByName(type);
        }

        private static boolean isSocialiteTask(String block) {
            return block.contains("Socialite Spotlight");
        }

        private static boolean isTicketTask(String block) {
            return block.contains("Ticket Task");
        }

        private static String cleanTaskName(String taskName) {
            Pattern p = Pattern.compile("^\uD83D\uDD25?\\s*(.+?)\\s*(?:\uD83D\uDD25|EASY|MEDIUM|HARD)?$");
            Matcher m = p.matcher(taskName);

            return m.matches() ? m.group(1) : taskName;
        }

        private record TaskMatch(Task task, String taskTarget, String progress, String amount,
                                 List<String> constraintParameters) {
        }
    }

    public static final class TaskState {
        private final String name;
        // TODO: Remove after region can be got from Tasks/TheirTargets
        private final String description;
        private final String[] constraintParameters;
        private final TaskType taskType;
        private final Task task;
        private final int slotId;
        private final boolean isTicket;
        private final boolean isSocialite;
        private final String required;
        private final Pattern taskPattern;
        private final Pattern nonTrackedPattern;
        private String current;
        private boolean isCompleted;

        TaskState(String name, String description, String taskTarget, TaskType taskType, Task task, String[] constraintParameters, int slotId, boolean isTicket, boolean isSocialite, String required, String current, Pattern taskPattern) {
            this.name = name;
            this.description = description;
            this.constraintParameters = constraintParameters;
            this.taskType = task != null ? task.getDescriptor().taskType() : taskType;
            this.task = task;
            this.slotId = slotId;
            this.isCompleted = required.equals(current);
            this.isTicket = isTicket;
            this.isSocialite = isSocialite;
            this.required = required;
            this.current = current;
            this.taskPattern = taskPattern != null ? Pattern.compile(taskPattern.pattern().replace("(?<type>.+)", taskTarget)) : null;
            this.nonTrackedPattern = Pattern.compile(Pattern.quote(this.name) + " \\(?(?<progress>[0-9.,]+[kmbt]?)");
        }

        private UpdateState tryUpdate(String text) {
            var nameMatcher = nonTrackedPattern.matcher(text);
            if (nameMatcher.find()){
                var progress = nameMatcher.group("progress");
                return isChanged(progress);
            }
            else{
                if (taskPattern == null)
                    return UpdateState.NotMatched;

                var taskMatch = TaskStateFactory.tryMatchTaskByFullInfo(taskPattern, text);
                if (taskMatch == null) {
                    return UpdateState.NotMatched;
                }
                return isChanged(taskMatch.progress);
            }
        }

        private UpdateState isChanged(String progress){
            if (!isCompleted && !progress.equals(current)) {
                current = progress;
                if (current.equals(required)) {
                    isCompleted = true;
                }
                return UpdateState.Changed;
            } else {
                return UpdateState.UnChanged;
            }
        }

        private boolean completeIfOngoing() {
            if (!isCompleted) {
                current = required;
                isCompleted = true;
                return true;
            } else {
                return false;
            }
        }

        public String getCurrent() {
            return current;
        }

        public String getRequired() {
            return required;
        }

        public boolean isSocialite() {
            return isSocialite;
        }

        public boolean isTicket() {
            return isTicket;
        }

        public Task getTask() {
            return task;
        }

        public boolean isCompleted() {
            return isCompleted;
        }

        public int getSlotId() {
            return slotId;
        }

        public String getName() {
            return name;
        }

        public TaskType getTaskType() {
            return taskType;
        }

        public String getDescription() {
            return description;
        }

        public enum UpdateState {
            NotMatched,
            UnChanged,
            Changed
        }

        public String getDisplayName() {
            if (task != null) {
                return task.getDescriptor().displayName(constraintParameters);
            }
            return name;
        }
    }
}
