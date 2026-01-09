package com.incrementalclient.abstractions;

import com.incrementalclient.interfaces.Listener;
import com.incrementalclient.interfaces.Observer;
import com.incrementalclient.internals.events.ClientPlayConnectionObservable;
import com.incrementalclient.internals.events.EndClientTickListenable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

public abstract class TaskScheduler<C, T extends TaskScheduler.QueuedTask<C, ?>> implements Listener {
    private final PriorityBlockingQueue<T> queue;
    private final Map<String, T> pendingTasks = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    protected T activeTask = null;
    private boolean isCleaning = false;

    public TaskScheduler(EndClientTickListenable endClientTickListenable, ClientPlayConnectionObservable clientPlayConnectionObservable) {
        var comparator = Comparator.comparingInt(T::getPriority);
        this.queue = new PriorityBlockingQueue<>(11, comparator);

        endClientTickListenable.subscribe(this);
        clientPlayConnectionObservable.subscribe(new Observer.DefaultObserver<>((e) -> {
            //noinspection Convert2MethodRef: THIS MUST BE KEPT AS AN EXPANDED METHOD BODY! otherwise the derived class resetAll() won't be called by the base.
            this.resetAllBase();
        }));
    }

    public String getActiveTaskName() {
        var task = activeTask;
        if (task == null) return "Idle";
        return isCleaning
                ? "Cleaning: " + task.getIdentifier()
                : "Running: " + task.getIdentifier();
    }

    private void resetAllBase(){
        lock.lock();
        try {
            resetAll(); // This will now correctly find the child's implementation
            this.queue.clear();
            this.pendingTasks.clear();
            this.activeTask = null;
            this.isCleaning = false;
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public void submit(T task) {
        lock.lock();
        try {
            var taskIdentifier = task.getIdentifier();
            if (pendingTasks.containsKey(taskIdentifier)) {
                var existing = pendingTasks.get(taskIdentifier);
                // Update priority of the instance already waiting in queue
                if (existing.getPriority() != task.getPriority()) {
                    queue.remove(existing);
                    existing.setPriority(task.getPriority());
                    queue.add(existing);
                }
            } else {
                pendingTasks.put(taskIdentifier, task);
                queue.add(task);
            }
        } finally {
            lock.unlock();
        }
    }

    protected boolean isCleaning() {
        return isCleaning;
    }

    @Override
    public void onEvent() {
        if (isCleaning) {
            if (activeTask != null && activeTask.isResetFinished()) {
                isCleaning = false;
                finalizeTaskEnd();
            }
            return;
        }

        if (activeTask == null) {
            if (!canProcessNext() || queue.isEmpty()) return;

            lock.lock();
            try {
                activeTask = queue.poll();
                if (activeTask != null) {
                    pendingTasks.remove(activeTask.getIdentifier());
                }
            } finally {
                lock.unlock();
            }

            if (activeTask == null) return;

            if (!activeTask.preValidate()) {
                submit(activeTask);
                activeTask = null;
                return;
            }
        }

        processActiveTask();
    }

    private void processActiveTask() {
        if (activeTask.isInterrupted()) {
            initiateTaskEnd(false);
            return;
        }

        var currentTicks = activeTask.getTimeoutTicks();
        activeTask.setTimeoutTicks(currentTicks - 1);

        if (activeTask.getTimeoutTicks() <= 0) {
            initiateTaskEnd(false);
            return;
        }

        try {
            activeTask.execute();
            if (isFinished(activeTask)) {
                initiateTaskEnd(true);
            }
        } catch (Exception e) {
            initiateTaskEnd(false);
        }
    }

    private void initiateTaskEnd(boolean success) {
        isCleaning = true;
        activeTask.startReset(success);
    }

    private void finalizeTaskEnd() {
        var task = activeTask;
        activeTask = null;

        var future = task.getFuture();

        var isFinished = isFinished(task);

        // Safety net in case the Task isn't calling its own complete
        if (isFinished && !future.isDone()) {
            task.complete(null);
        }

        var trulyCompleted = future.isDone() && !future.isCompletedExceptionally();

        if (!trulyCompleted) {
            if (task.getRetryCount() < task.getMaxRetries()) {
                task.incrementRetry();
                task.setTimeoutTicks(task.getInitialTimeoutTicks());
                submit(task);
            } else {
                var error = new RuntimeException("Task " + task.getIdentifier() + " failed.");
                task.fail(error);
            }
        }
    }

    protected abstract boolean canProcessNext();

    protected abstract boolean isFinished(T task);

    protected abstract void resetAll();

    public abstract static class QueuedTask<TContext, TResult> {
        private final TContext context;
        private final CompletableFuture<TResult> future = new CompletableFuture<>();
        private final int initialTimeout;
        private final int maxRetries;

        private int priority;
        private int timeoutTicks;
        private int retryCount = 0;

        protected QueuedTask(TContext context) {
            this(context, 10, 20, 3);
        }

        protected QueuedTask(TContext context, int priority, int timeoutTicks, int maxRetries) {
            this.context = context;
            this.priority = priority;
            this.initialTimeout = timeoutTicks;
            this.timeoutTicks = timeoutTicks;
            this.maxRetries = maxRetries;
        }

        public abstract String getIdentifier();

        public abstract void execute();

        // Hooks for optional behavior
        public boolean preValidate() {
            return true;
        }

        public boolean isInterrupted() {
            return false;
        }

        public void startReset(boolean completed) {
        }

        public boolean isResetFinished() {
            return true;
        }

        // Logic Helpers
        protected final void refreshTimeout() {
            this.timeoutTicks = initialTimeout;
        }

        public final CompletableFuture<TResult> getFuture() {
            return future;
        }

        public final void complete(TResult result) {
            future.complete(result);
        }

        public final void fail(Throwable ex) {
            future.completeExceptionally(ex);
        }

        final int getPriority() {
            return priority;
        }

        final void setPriority(int p) {
            this.priority = p;
        }

        protected final int getInitialTimeoutTicks() {
            return initialTimeout;
        }

        protected final int getTimeoutTicks() {
            return timeoutTicks;
        }

        final void setTimeoutTicks(int t) {
            this.timeoutTicks = t;
        }

        final int getRetryCount() {
            return retryCount;
        }

        final void incrementRetry() {
            this.retryCount++;
        }

        final int getMaxRetries() {
            return maxRetries;
        }
    }
}