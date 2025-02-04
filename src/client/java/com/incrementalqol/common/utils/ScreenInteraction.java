package com.incrementalqol.common.utils;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ScreenInteraction {
    private final List<InteractionStep> steps;
    private final Consumer<MinecraftClient> startingAction;
    private boolean hasStartingAction = false;
    private final boolean shouldHide;
    private final Consumer<Screen> finalizingAction;
    private final int finalizingTickDelay;
    private final Predicate<Screen> abortCondition;
    private final int abortTickDelay;
    private int currentStepIndex = 0;
    private boolean isFinalizing = false;

    public CompletionStage<Boolean> startAsync() {
        return ScreenInteractionManager.enqueue(this);
    }

    /**
     * @deprecated Enforced interaction without thread safe queueing is not yet tested, use startAsync() instead to put interaction on the queue
     */
    @Deprecated
    public Pair<Boolean, CompletionStage<Boolean>> start() {
        var tryStart = ScreenInteractionManager.tryStart(this);
        return new Pair<>(tryStart.getLeft(), tryStart.getRight());
    }

    private ScreenInteraction(
            List<InteractionStep> interactionSteps,
            Consumer<MinecraftClient> startingAction,
            boolean shouldHide,
            Consumer<Screen> finalizingAction,
            int finalizingTickDelay,
            Predicate<Screen> abortCondition,
            int abortTickDelay
    ) {
        this.steps = interactionSteps;
        this.startingAction = startingAction;
        if (this.startingAction != null){
            this.hasStartingAction = true;
        }
        this.shouldHide = shouldHide;
        this.finalizingAction = finalizingAction;
        this.finalizingTickDelay = finalizingTickDelay;
        this.abortCondition = abortCondition;
        this.abortTickDelay = abortTickDelay;
    }

    private void next() {
        currentStepIndex++;
    }

    private void reset() {
        this.steps.forEach(i -> {
            i.initPassed = i.initCondition == null;
            i.executionDone = false;
        });
        this.currentStepIndex = 0;
        this.isFinalizing = false;
    }

    private boolean shouldWaitAtTick(Screen screen) {
        var currentStep = this.getCurrentStep();
        return currentStep.tickCondition != null && !currentStep.tickCondition.test(screen);
    }

    private boolean trySkipAtTick(Screen screen) {
        var currentStep = this.getCurrentStep();
        var shouldSkip = currentStep.tickSkipCondition == null || currentStep.tickSkipCondition.test(screen);

        if (shouldSkip) {
            next();
        }

        return shouldSkip;
    }

    private boolean isFailedAtInit(Screen screen) {
        var currentStep = this.getCurrentStep();
        return currentStep.initCondition != null && !currentStep.initCondition.test(screen);
    }

    private boolean trySkipAtInit(Screen screen) {
        var currentStep = this.getCurrentStep();
        var shouldSkip = currentStep.initSkipCondition == null || currentStep.initSkipCondition.test(screen);

        if (shouldSkip) {
            next();
        }

        return shouldSkip;
    }

    private void executeStarting(MinecraftClient client) {
        this.startingAction.accept(client);
    }

    private void executeInteraction(Screen screen) {
        var currentStep = this.getCurrentStep();
        currentStep.interaction.accept(screen);
        currentStep.executionDone = true;
    }

    private void executeFinalization(Screen screen) {
        this.finalizingAction.accept(screen);
    }

    private boolean shouldAbort(Screen screen) {
        return this.abortCondition != null && this.abortCondition.test(screen);
    }

    private InteractionStep getCurrentStep() {
        return this.steps.get(this.currentStepIndex);
    }

    private boolean isFinished() {
        return this.isFinalizing || this.currentStepIndex >= this.steps.size();
    }

    private static class ScreenInteractionManager {
        public static Screen lastInitializedScreen = null;
        private static final ConcurrentLinkedQueue<Pair<ScreenInteraction, CompletableFuture<Boolean>>> queuedInteractions = new ConcurrentLinkedQueue<>();
        private static final AtomicReference<Pair<ScreenInteraction, CompletableFuture<Boolean>>> actualInteraction = new AtomicReference<>();
        private static int abortTickCounter = 0;
        private static int finalizeTickCounter = 0;
        private static int waitTickForStuckExecution = 0;
        private static final AtomicBoolean handlingTick = new AtomicBoolean(false);

        public static void reset() {
            while (!queuedInteractions.isEmpty()) {
                var nextInteraction = queuedInteractions.poll();
                if (nextInteraction != null){
                    nextInteraction.getLeft().reset();
                    nextInteraction.getRight().complete(false);
                }
            }

            handlingTick.set(false);

            if (actualInteraction.get() != null){
                close();
            }
        }

        private static CompletableFuture<Boolean> enqueue(ScreenInteraction interaction) {
            var newInteraction = new Pair<>(interaction, new CompletableFuture<Boolean>());
            queuedInteractions.add(newInteraction);
            return newInteraction.getRight();
        }

        private static Pair<Boolean, CompletableFuture<Boolean>> tryStart(ScreenInteraction interaction) {
            var newInteraction = new Pair<>(interaction, new CompletableFuture<Boolean>());
            var successfullyStarted = true;
            if (!actualInteraction.compareAndSet(null, newInteraction)) {
                newInteraction.getRight().complete(false);
                successfullyStarted = false;

            }
            return new Pair<>(successfullyStarted, newInteraction.getRight());
        }

        private static void handleScreenInit(MinecraftClient client, Screen screen, boolean comeFromTick) {
            if (screen == null && !comeFromTick) {
                return;
            }

            // Find next non aborting interaction
            if (actualInteraction.get() == null) {
                while (!queuedInteractions.isEmpty()) {
                    var nextInteraction = queuedInteractions.poll();
                    if (nextInteraction != null) {
                        if (comeFromTick && nextInteraction.getLeft().hasStartingAction) {
                            if (actualInteraction.compareAndSet(null, nextInteraction)) {
                                actualInteraction.get().getLeft().executeStarting(client);
                            }
                            return;
                        } else {
                            if (!nextInteraction.getLeft().shouldAbort(screen) || nextInteraction.getLeft().isFailedAtInit(screen)) {
                                if (!actualInteraction.compareAndSet(null, nextInteraction)) {
                                    abort(nextInteraction);
                                    return;
                                }
                                break;
                            } else {
                                abort(nextInteraction);
                            }
                        }
                    }
                }

                // If no interaction then leave
                if (actualInteraction.get() == null) {
                    return;
                }

            }

            if (!actualInteraction.get().getLeft().isFinished()) {
                if (!actualInteraction.get().getLeft().getCurrentStep().initPassed) {
                    while (!actualInteraction.get().getLeft().isFinished()) {
                        if (actualInteraction.get().getLeft().shouldAbort(screen) || actualInteraction.get().getLeft().isFailedAtInit(screen)) {
                            abort(actualInteraction.get());
                            handleScreenInit(client, screen, comeFromTick);
                            return;
                        }

                        actualInteraction.get().getLeft().getCurrentStep().initPassed = true;
                        if (!actualInteraction.get().getLeft().trySkipAtInit(screen)) {
                            break;
                        }
                    }

                    if (actualInteraction.get().getLeft().isFinished()) {
                        complete(actualInteraction.get());
                        handleScreenInit(client, screen, comeFromTick);
                        return;
                    }
                }

                lastInitializedScreen = screen;
                abortTickCounter = 0;
            } else if (!actualInteraction.get().getLeft().isFinalizing) {
                actualInteraction.get().getLeft().isFinalizing = true;
            }

            if (actualInteraction.get().getLeft().shouldHide) {
                client.setScreen(null);
            }
        }

        private static void handleTicks(MinecraftClient client) {
            if (handlingTick.compareAndSet(false, true)) {
                if (actualInteraction.get() == null) {
                    if (!queuedInteractions.isEmpty()) {
                        handleScreenInit(client, client.currentScreen, true);
                    }
                    handlingTick.compareAndSet(true, false);
                    return;
                }

                //TODO: Maybe need to handle a special abort when screen changes from the init matched one
                //if (actualScreen != client.currentScreen){
                //    handleScreenInit(client, client.currentScreen, true);
                //    return;
                //}

                if (actualInteraction.get().getLeft().isFinalizing) {
                    actualInteraction.get().getLeft().executeFinalization(lastInitializedScreen);
                    finalizeTickCounter++;
                    if (finalizeTickCounter > actualInteraction.get().getLeft().finalizingTickDelay) {
                        close();
                        handleScreenInit(client, client.currentScreen, true);
                    }
                    handlingTick.compareAndSet(true, false);
                    return;
                }

                if (actualInteraction.get().getLeft().isFinished()){
                    handlingTick.compareAndSet(true, false);
                    return;
                }

                if (actualInteraction.get().getLeft().getCurrentStep().executionDone) {
                    waitTickForStuckExecution++;
                    if(waitTickForStuckExecution >= 3){
                        actualInteraction.get().getLeft().next();
                        waitTickForStuckExecution = 0;
                        handleScreenInit(client, lastInitializedScreen, true);
                    }
                    handlingTick.compareAndSet(true, false);
                    return;
                }

                if (!actualInteraction.get().getLeft().getCurrentStep().initPassed) {
                    handlingTick.compareAndSet(true, false);
                    return;
                }

                if (actualInteraction.get().getLeft().trySkipAtTick(lastInitializedScreen)) {
                    handleScreenInit(client, lastInitializedScreen, true);
                    handlingTick.compareAndSet(true, false);
                    return;
                }

                if (actualInteraction.get().getLeft().shouldWaitAtTick(lastInitializedScreen)) {
                    abortTickCounter++;
                    if (abortTickCounter > actualInteraction.get().getLeft().abortTickDelay) {
                        abort(actualInteraction.get());
                    }
                    handlingTick.compareAndSet(true, false);
                    return;
                }

                actualInteraction.get().getLeft().executeInteraction(lastInitializedScreen);
                handlingTick.compareAndSet(true, false);
            }
        }

        private static void complete(Pair<ScreenInteraction, CompletableFuture<Boolean>> interaction) {
            interaction.getRight().complete(true);
            interaction.getLeft().isFinalizing = true;
        }

        private static void abort(Pair<ScreenInteraction, CompletableFuture<Boolean>> interaction) {
            interaction.getRight().complete(false);
            interaction.getLeft().isFinalizing = true;
        }

        private static void close() {
            lastInitializedScreen = null;
            finalizeTickCounter = 0;
            abortTickCounter = 0;

            // TODO: These two can lead to a very unlikely race condition when the same task is put in the queue and it takes it to work on it right away before reset is done.
            var interaction = actualInteraction.get();
            actualInteraction.set(null);
            if (!interaction.getRight().isDone()){
                interaction.getRight().complete(false);
            }
            interaction.getLeft().reset();
        }

        static {
            ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> handleScreenInit(client, screen, false));
            ClientTickEvents.END_CLIENT_TICK.register(ScreenInteractionManager::handleTicks);
            ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
                reset();
            });
        }
    }

    public static class ScreenInteractionBuilder {
        private final List<InteractionStep> configuredQueue = new ArrayList<>();
        private Consumer<MinecraftClient> startingAction;
        private boolean shouldHide = false;
        private Predicate<Screen> abortCondition;
        private int abortTickDelay = 1;
        private Consumer<Screen> finalizingAction;
        private Integer finalizingTickDelay;

        /**
         * Start the interaction with the no screen open
         *
         * @param initCondition Condition to must match on screen init phase, otherwise abort the interaction
         * @param initSkipCondition Condition to skip interaction if matched on screen init phase
         * @param tickCondition Conditions to match on tick phase before the abort delay run out, otherwise abort the interaction
         * @param tickSkipCondition Condition to skip interaction if matched during tick phase
         * @param interaction The interaction to run if all rules are matched
         */
        public ScreenInteractionBuilder(
                Predicate<Screen> initCondition,
                Predicate<Screen> initSkipCondition,
                Predicate<Screen> tickCondition,
                Predicate<Screen> tickSkipCondition,
                Consumer<Screen> interaction
        ) {
            addInteraction(initCondition, initSkipCondition, tickCondition, tickSkipCondition, interaction);
        }

        /**
         * Start the interaction with the screen already open.
         *
         * @param tickCondition Condition to must match on screen init phase, otherwise abort the interaction
         * @param tickSkipCondition Condition to skip interaction if matched on screen init phase
         * @param interaction The interaction to run if all rules are matched
         */
        public ScreenInteractionBuilder(
                Predicate<Screen> tickCondition,
                Predicate<Screen> tickSkipCondition,
                Consumer<Screen> interaction
        ) {
            addInteraction(s -> true, s -> false, tickCondition, tickSkipCondition, interaction);
        }

        public ScreenInteraction build() {
            return new ScreenInteraction(
                    configuredQueue,
                    startingAction,
                    shouldHide,
                    finalizingAction != null
                            ? finalizingAction
                            : s -> {},
                    finalizingTickDelay != null
                            ? finalizingTickDelay
                            : 2,
                    abortCondition != null
                            ? abortCondition
                            : s -> false,
                    abortTickDelay
            );
        }

        public ScreenInteractionBuilder setAbortDelay(int tickCount) {
            this.abortTickDelay = tickCount;
            return this;
        }

        public ScreenInteractionBuilder setKeepScreenHidden(boolean shouldHide) {
            this.shouldHide = shouldHide;
            return this;
        }

        public ScreenInteractionBuilder setAbortCondition(Predicate<Screen> abortCondition) {
            this.abortCondition = abortCondition;
            return this;
        }

        public ScreenInteractionBuilder setFinalizingAction(Consumer<Screen> finalizingAction) {
            this.finalizingAction = finalizingAction;
            return this;
        }

        public ScreenInteractionBuilder setFinalizingActionToCloseScreen() {
            return setFinalizingAction(s -> {
                MinecraftClient.getInstance().setScreen(null);
                if (MinecraftClient.getInstance().player != null) {
                    MinecraftClient.getInstance().player.currentScreenHandler = MinecraftClient.getInstance().player.playerScreenHandler;
                }
            });
        }


        public ScreenInteractionBuilder setFinalizingWait(int tickCount) {
            this.finalizingTickDelay = tickCount;
            return this;
        }

        public ScreenInteractionBuilder setStartingAction(Consumer<MinecraftClient> startingAction) {
            this.startingAction = startingAction;
            return this;
        }

        public ScreenInteractionBuilder addInteraction(
                Predicate<Screen> initCondition,
                Predicate<Screen> initSkipCondition,
                Predicate<Screen> tickCondition,
                Predicate<Screen> tickSkipCondition,
                Consumer<Screen> interaction
        ) {
            this.configuredQueue.add(new InteractionStep(initCondition, initSkipCondition, tickCondition, tickSkipCondition, interaction));
            return this;
        }

        public ScreenInteractionBuilder addInteraction(
                Predicate<Screen> initCondition,
                Consumer<Screen> interaction
        ) {
            addInteraction(initCondition, s -> false, s -> true, s -> false, interaction);
            return this;
        }

        public ScreenInteractionBuilder addInteraction(
                Predicate<Screen> initCondition,
                Predicate<Screen> initSkipCondition,
                Consumer<Screen> interaction
        ) {
            addInteraction(initCondition, initSkipCondition, s -> true, s -> false, interaction);
            return this;
        }

        public ScreenInteractionBuilder addInteraction(
                Predicate<Screen> initCondition,
                Predicate<Screen> initSkipCondition,
                Predicate<Screen> tickCondition,
                Consumer<Screen> interaction
        ) {
            addInteraction(initCondition, initSkipCondition, tickCondition, s -> false, interaction);
            return this;
        }
    }

    private static class InteractionStep {
        private final Predicate<Screen> initCondition;
        private final Predicate<Screen> initSkipCondition;
        private final Predicate<Screen> tickCondition;
        private final Predicate<Screen> tickSkipCondition;
        private final Consumer<Screen> interaction;
        private boolean initPassed = false;
        private boolean executionDone = false;

        public InteractionStep(
                Predicate<Screen> initCondition,
                Predicate<Screen> initSkipCondition,
                Predicate<Screen> tickCondition,
                Predicate<Screen> tickSkipCondition,
                Consumer<Screen> interaction
        ) {
            this.initCondition = initCondition;
            this.initSkipCondition = initSkipCondition;
            this.tickCondition = tickCondition;
            this.tickSkipCondition = tickSkipCondition;
            this.interaction = interaction;
        }
    }
}
