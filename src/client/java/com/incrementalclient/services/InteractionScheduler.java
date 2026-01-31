package com.incrementalclient.services;

import com.incrementalclient.abstractions.TaskScheduler;
import com.incrementalclient.interfaces.AsyncObserver;
import com.incrementalclient.internals.InventoryInteractionInterceptor;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.internals.ScreenCapture;
import com.incrementalclient.internals.events.ClientPlayConnectionObservable;
import com.incrementalclient.internals.events.EndClientTickListenable;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.sync.ItemStackHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class InteractionScheduler<C> extends TaskScheduler<C, InteractionScheduler.InteractionTask<C, ?>> implements AsyncObserver<ClickSlotC2SPacket, ScreenCapture.Screen> {
    private static Logger logger = LoggerFactory.getLogger(InteractionScheduler.class);
    private final MinecraftClientAccessor mcAccessor;
    private volatile int activeSyncId = 0;
    private int lastProcessedSyncId = 0;
    private Predicate<ScreenCapture.Screen> lastExpectedStep = null;
    private final Set<CompletableFuture<?>> asyncInventoryInterruptors = ConcurrentHashMap.newKeySet();

    public InteractionScheduler(
            EndClientTickListenable endClientTickListenable,
            ClientPlayConnectionObservable clientPlayConnectionObservable,
            ScreenCapture screenCapture,
            InventoryInteractionInterceptor inventoryInteractionInterceptor,
            MinecraftClientAccessor mcAccessor
    ) {
        super(endClientTickListenable, clientPlayConnectionObservable);
        this.mcAccessor = mcAccessor;

        screenCapture.registerSilencer(this::shouldSilence);
        screenCapture.subscribe(this::screenUpdated);
        inventoryInteractionInterceptor.subscribe(this);
    }

    @Override
    protected void resetAll() {
        reset();
        lastProcessedSyncId = 0;
        for (var entry : asyncInventoryInterruptors) {
            entry.complete(null);
        }
        asyncInventoryInterruptors.clear();
    }

    private void reset() {
        this.activeSyncId = 0;
        this.lastExpectedStep = null;
        if (activeTask != null) {
            activeTask.fail(null);
        }
    }

    @Override
    public void onEvent() {
        var wasCleaning = isCleaning();
        super.onEvent();
        if (wasCleaning && !isCleaning()) {
            this.lastExpectedStep = null;
        }
    }

    @Override
    public CompletableFuture<Void> onEventAsync(ClickSlotC2SPacket packet, CompletableFuture<ScreenCapture.Screen> resolution) {
        // TODO: For now only try to handle Inventory interactions as interruptors, but it also means interaction handler can't handle inventory based interactions for now (it shouldn't even be needed as inventory can be manipulated without it anytime)
        if (packet.syncId() != 0) {
            return CompletableFuture.completedFuture(null);
        }

        var prepareFuture = new CompletableFuture<Void>();

        mcAccessor.getClient().execute(() -> {
            try {
                reset();

                this.asyncInventoryInterruptors.add(resolution);

                resolution.handle((res, ex) -> {
                    this.asyncInventoryInterruptors.remove(resolution);
                    return null;
                });

                prepareFuture.complete(null);

                logger.debug("Manual packet intercepted. Current task aborted, awaiting sync...");
            } catch (Exception e) {
                prepareFuture.completeExceptionally(e);
            }
        });

        return prepareFuture;
    }

    private boolean shouldSilence(ScreenCapture.Screen screen) {
        var incomingId = screen.syncId();

        // 1. Inventory (0) is never silenced and is used to clear state
        if (incomingId == 0) return false;

        //// 2. A screen is already open
        //if (mcAccessor.getScreen().isPresent() && isHardInterrupted()) return false;

        // 3. An unexpected screen appeared based on what we are doing
        if (lastExpectedStep != null && !lastExpectedStep.test(screen)) {
            return false;
        }

        // 3. HARD FIREWALL:
        if (isCleaning() || (activeTask != null && activeTask.isInterrupted())) {
            return true;
        }

        // 4. GHOST KILLER: Filter packets logically 'older' than our last action.
        if (!isNewSyncId(incomingId, lastProcessedSyncId) || (lastExpectedStep != null && lastExpectedStep.test(screen))) {
            return true;
        }

        // 4. Standard Task Logic
        if (activeTask == null) return false;
        var step = activeTask.getCurrentStep();
        return step != null && step.screenExpectation().test(screen, activeTask.context);
    }

    private void screenUpdated(ScreenCapture.Screen screen) {
        int incomingId = screen.syncId();

        if (incomingId == 0) return;

        if (isNewSyncId(incomingId, lastProcessedSyncId)) {
            this.activeSyncId = incomingId;
            this.lastProcessedSyncId = incomingId;

            if (activeTask != null && !isCleaning()) {
                activeTask.lastSeenScreen = screen;
            }
        }
    }

    private boolean isNewSyncId(int current, int last) {
        if (last == 0) return true;
        if (current > last) return true;
        // If current is much smaller than last, it's a wrap-around (e.g., 1 vs 100)
        return (current - last) < -50;
    }

    public boolean isHardInterrupted() {
        return mcAccessor.getScreen()
                .map(screen -> screen instanceof net.minecraft.client.gui.screen.ingame.GenericContainerScreen || screen instanceof net.minecraft.client.gui.screen.ingame.InventoryScreen)
                .orElse(false) || asyncInventoryInterruptors.stream().anyMatch(f -> !f.isDone());
    }

    public int getActualSyncId() {
        return activeSyncId;
    }

    @Override
    public void submit(InteractionTask<C, ?> task) {
        task.reset();

        this.activeSyncId = 0;
        this.lastExpectedStep = null;
        super.submit(task);
    }

    @Override
    protected boolean canProcessNext() {
        return activeSyncId == 0 && !isCleaning() && !this.isHardInterrupted();
    }

    @Override
    protected boolean isFinished(InteractionTask<C, ?> task) {
        return task.getFuture().isDone() || task.currentStepIndex >= task.steps.size();
    }

    private void forceSyncId(int syncId) {
        //mcAccessor.closeScreen(this.activeSyncId);
        this.activeSyncId = syncId;
    }

    public static abstract class InteractionTask<TContext, TResult> extends QueuedTask<TContext, TResult> {
        protected final TContext context;
        protected final List<InteractionStep<TContext>> steps = new ArrayList<>();
        private final int stepDelay;
        protected int currentStepIndex = 0;
        protected Runnable startAction;
        protected ScreenCapture.Screen lastSeenScreen;

        // References needed for reset logic
        private final InteractionScheduler<?> scheduler;
        private final MinecraftClientAccessor mcAccessor;

        private int stepDelayTick;

        private int cleanupTicks = 0;

        protected InteractionTask(TContext context, int priority, int timeout, int maxRetries, int stepDelay,
                                  InteractionScheduler<?> scheduler, MinecraftClientAccessor mc) {
            super(context, priority, timeout, maxRetries);
            this.context = context;
            this.stepDelay = stepDelay > timeout
                    ? Math.max(timeout - 5, 0)
                    : stepDelay;
            this.stepDelayTick = stepDelay;
            this.scheduler = scheduler;
            this.mcAccessor = mc;
        }

        private void reset() {
            this.currentStepIndex = 0;
            this.lastSeenScreen = null;


        }

        @Override
        public void execute() {
            if (scheduler.isHardInterrupted()) {
                reset();
                this.fail(new RuntimeException("Interrupted by user GUI open"));
                return;
            }

            // 1. Initial Start Action
            if (currentStepIndex == 0 && getTimeoutTicks() == getInitialTimeoutTicks() - 1) {
                if (startAction != null) startAction.run();
                if (!steps.isEmpty()) {
                    scheduler.lastExpectedStep = screen -> steps.getFirst().screenExpectation().test(screen, this.context);
                }
            }

            if (stepDelayTick > 0) {
                stepDelayTick--;
                return;
            }

            // 2. Logic processing
            if (lastSeenScreen == null || currentStepIndex >= steps.size()) return;

            var step = steps.get(currentStepIndex);
            scheduler.lastExpectedStep = screen -> steps.getFirst().screenExpectation().test(screen, this.context);
            ;
            // 3. Verify Screen
            if (step.screenExpectation().test(lastSeenScreen, this.context)) {
                var ctx = new InteractionContext<TContext>(this.context, lastSeenScreen, this, mcAccessor);
                // 4. Check for Premature End
                if (step.prematurityCheck().test(ctx)) {
                    this.complete(null);
                    return;
                }

                // 5. Interact
                var stayOnStep = step.action().test(ctx);

                // 6. Progress State
                this.refreshTimeout();
                if (!stayOnStep) {
                    this.currentStepIndex++;
                }

                if (currentStepIndex < steps.size()) {
                    var actual = steps.get(currentStepIndex);
                    scheduler.lastExpectedStep = screen -> actual.screenExpectation().test(screen, this.context);
                } else {
                    scheduler.lastExpectedStep = null;
                }

                this.stepDelayTick = stepDelay;
                this.lastSeenScreen = null; // Clear to wait for next packet
            }
        }

        public InteractionStep<TContext> getCurrentStep() {
            if (currentStepIndex >= steps.size()) return null;
            return steps.get(currentStepIndex);
        }

        @Override
        public boolean isInterrupted() {
            // Known interruptions to fail fast, otherwise it will wait till timeout
            return mcAccessor.getPlayer().isEmpty() || mcAccessor.getPlayer().get().isDead() || scheduler.isHardInterrupted();
        }

        @Override
        public void startReset(boolean completed) {
            this.cleanupTicks = 3;
            this.lastSeenScreen = null;

            int syncId = scheduler.getActualSyncId();
            if (syncId != 0) {
                mcAccessor.getNetworkHandler().ifPresent(h ->
                        h.sendPacket(new net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket(syncId))
                );
                scheduler.forceSyncId(0);
            }

            mcAccessor.getScreen().ifPresent(screen -> {
                if (screen instanceof net.minecraft.client.gui.screen.ingame.GenericContainerScreen) {
                    //mcAccessor.setScreen(null);
                }
            });
        }

        @Override
        public boolean isResetFinished() {
            if (cleanupTicks > 0) {
                cleanupTicks--;
                return false;
            }
            return true;
        }

        public record InteractionStep<C>(
                BiPredicate<ScreenCapture.Screen, C> screenExpectation,
                Predicate<InteractionContext<C>> prematurityCheck,
                Predicate<InteractionContext<C>> action
        ) {
        }
    }

    public static class Builder<C, T> {
        private final String identifier;
        private final InteractionScheduler<?> scheduler;
        private final MinecraftClientAccessor mcAccessor;

        private Runnable startAction;
        private final List<InteractionTask.InteractionStep<C>> steps = new ArrayList<>();
        private int priority = 10;
        private int timeout = 20;
        private int retries = 3;
        private int delay;

        public Builder(String identifier, InteractionScheduler<?> scheduler, MinecraftClientAccessor mc) {
            this.identifier = identifier;
            this.scheduler = scheduler;
            this.mcAccessor = mc;
        }

        public Builder<C, T> startWith(Runnable action) {
            this.startAction = action;
            return this;
        }

        public Builder<C, T> priority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder<C, T> timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder<C, T> stepDelay(int delay) {
            this.delay = delay;
            return this;
        }

        public Builder<C, T> retries(int retries) {
            this.retries = retries;
            return this;
        }

        public Builder<C, T> step(
                BiPredicate<ScreenCapture.Screen, C> expectation,
                Predicate<InteractionContext<C>> action) {
            return step(expectation, ctx -> false, action);
        }

        public Builder<C, T> step(
                BiPredicate<ScreenCapture.Screen, C> expectation,
                Predicate<InteractionContext<C>> prematurity,
                Predicate<InteractionContext<C>> action) {
            steps.add(new InteractionTask.InteractionStep<C>(expectation, prematurity, action));
            return this;
        }

        public InteractionTask<C, T> build(C context, String identitySuffix) {
            var task = new InteractionTask<C, T>(context, priority, timeout, retries, delay, scheduler, mcAccessor) {
                @Override
                public String getIdentifier() {
                    if (identitySuffix != null) {
                        return identifier + "-" + identitySuffix;
                    }
                    return identifier;
                }
            };
            task.startAction = this.startAction;
            task.steps.addAll(this.steps);
            return task;
        }

        public InteractionTask<C, T> build(C context) {
            return build(context, null);
        }
    }

    public record InteractionContext<C>(
            C context,
            ScreenCapture.Screen screen,
            InteractionTask<?, ?> task,
            MinecraftClientAccessor mc // Added accessor here
    ) {
        public void click(int slotId) {
            click(slotId, (byte) 0, SlotActionType.PICKUP);
        }

        public void click(int slotId, byte button, SlotActionType actionType) {
            var playerOpt = mc.getPlayer();
            var networkOpt = mc.getNetworkHandler();

            if (playerOpt.isEmpty() || networkOpt.isEmpty()) return;

            var packet = new ClickSlotC2SPacket(
                    screen.syncId(),
                    screen.revision(),
                    (short) slotId,
                    button,
                    actionType,
                    new Int2ObjectOpenHashMap<>(),
                    ItemStackHash.EMPTY
            );

            networkOpt.get().sendPacket(packet);
        }

        public void complete() {
            task.complete(null);
        }
    }
}