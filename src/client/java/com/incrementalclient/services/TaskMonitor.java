package com.incrementalclient.services;

import com.incrementalclient.abstractions.ObservableBase;
import com.incrementalclient.common.data.tasks.Constraint;
import com.incrementalclient.common.data.tasks.Task;
import com.incrementalclient.common.data.tasks.TaskType;
import com.incrementalclient.common.utils.Utils;
import com.incrementalclient.interfaces.Observer;
import com.incrementalclient.internals.BossBarReader;
import com.incrementalclient.internals.ScreenCapture;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
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

    private final static Pattern bossBarPatternForTask = Pattern.compile("^(Kill |Slay |Collect |Harvest |Spear |Clean |Repair |Sell |Gain |Loot |Play |Earn |Find )");

    public TaskMonitor(BossBarReader bossBarReader, ChatHandler chatHandler, ScreenCapture screenCapture) {
        bossBarReader.subscribe(new BossBarObserver(this));
        chatHandler.subscribe(new ChatMessageObserver(this));
        screenCapture.subscribe(new ScreenObserver(this));
    }

    public List<TaskState> getTaskList(){
        return this.taskList;
    }

    private void bossBarUpdated(BossBarReader.BossBar bossBar) {
        var isTaskUpdate = bossBarPatternForTask.matcher(bossBar.text());
        if (isTaskUpdate.hasMatch()) {
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

    private record BossBarObserver(TaskMonitor taskMonitor) implements Observer<BossBarReader.BossBar> {
        @Override
        public void onEvent(BossBarReader.BossBar result) {
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
                        if (taskType.get() == TaskType.Quest) {
                            return new TaskState(taskName, null, slotId, false, false, "", "", null);
                        } else {
                            String description = blocks.size() > 1 ? blocks.get(1) : "";
                            for (Pattern pattern : taskType.get().getPatterns()) {
                                var taskMatch = tryMatchTask(pattern, description);
                                if (taskMatch != null) {
                                    return new TaskState(taskName, taskMatch.task, slotId, isTicketTask(blocks.getFirst()), isSocialiteTask(blocks.getFirst()), taskMatch.amount, taskMatch.progress, pattern);
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }

        private static TaskMatch tryMatchTask(Pattern pattern, String description) {
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

                var task = Task.tryGetTask(taskTarget, constraints);
                return new TaskMatch(task, progress, targetAmount);
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
            var currentItem = stack.getItem();
            return currentItem.getName().getString().contains("Book");
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
            Pattern p = Pattern.compile("^≡ƒöÑ?\\s*(.+?)\\s*(?:≡ƒöÑ|EASY|MEDIUM|HARD)?$");
            Matcher m = p.matcher(taskName);

            return m.matches() ? m.group(1) : taskName;
        }

        private record TaskMatch(Task task, String progress, String amount) {
        }
    }

    public static final class TaskState {
        private final String name;
        private final Task task;
        private final int slotId;
        private final boolean isTicket;
        private final boolean isSocialite;
        private final String required;
        private final Pattern taskPattern;
        private String current;
        private boolean isCompleted;

        TaskState(String name, Task task, int slotId, boolean isTicket, boolean isSocialite, String required, String current, Pattern taskPattern) {
            this.name = name;
            this.task = task;
            this.slotId = slotId;
            this.isCompleted = required.equals(current);
            this.isTicket = isTicket;
            this.isSocialite = isSocialite;
            this.required = required;
            this.current = current;
            this.taskPattern = taskPattern;
        }

        private UpdateState tryUpdate(String text) {
            var taskMatch = TaskStateFactory.tryMatchTask(taskPattern, text);

            if (taskMatch == null) {
                return UpdateState.NotMatched;
            }
            if (!taskMatch.progress.equals(current)) {
                current = taskMatch.progress;
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

        public boolean isComplete() {
            return isCompleted;
        }

        public int getSlotId() {
            return slotId;
        }

        public String getName() {
            return name;
        }

        public enum UpdateState {
            NotMatched,
            UnChanged,
            Changed
        }
    }
}
