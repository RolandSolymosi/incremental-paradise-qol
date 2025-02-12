package com.incrementalqol.common.utils;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ScreenInteraction {
    public static final Logger LOGGER = LoggerFactory.getLogger("incremental-qol: ScreenInteraction");

    private final List<InteractionStep> steps;
    private final Consumer<MinecraftClient> startingAction;
    private final boolean shouldHide;

    private boolean continuousExecution = false;
    private final AtomicBoolean isActive = new AtomicBoolean();
    private CompletableFuture<Boolean> status;
    private int currentStepIndex = 0;
    private Integer actualSyncId = null;
    private Pair<Integer, List<ItemStack>> actualContent = null;
    private boolean executing = false;

    public CompletionStage<Boolean> startAsync(boolean continuousExecution) {
        var status = new CompletableFuture<Boolean>();
        if (!isActive.compareAndSet(false, true)) {
            status.complete(false);
            return status;
        }
        this.continuousExecution = continuousExecution;
        this.status = status;
        this.executeStarting(MinecraftClient.getInstance());
        return status;
    }

    public boolean stop() {
        if (isActive.get()) {
            reset(true);
            return true;

        }
        return false;
    }

    private ScreenInteraction(
            List<InteractionStep> interactionSteps,
            Consumer<MinecraftClient> startingAction,
            boolean shouldHide
    ) {
        this.steps = interactionSteps;
        this.startingAction = startingAction;
        this.shouldHide = shouldHide;

        register();
    }

    public boolean register() {
        return ScreenInteractionManager.registeredInteractions.add(this);
    }

    public boolean unregister() {
        return ScreenInteractionManager.registeredInteractions.remove(this);
    }

    private synchronized boolean listen(Packet<ClientPlayPacketListener> packet) {
        var shouldHide = false;
        if (isActive.get()) {
            if (packet instanceof OpenScreenS2CPacket screenPacket) {
                if (this.actualSyncId == null || this.actualSyncId != screenPacket.getSyncId()) {
                    if (this.getCurrentStep().screenTitleCondition.test(screenPacket.getName().getString())) {
                        actualSyncId = screenPacket.getSyncId();
                        shouldHide = this.shouldHide;
                    } else {
                        reset(false);
                    }
                }
            } else {
                InventoryS2CPacket inventoryPacket = (InventoryS2CPacket) packet;
                if (this.actualContent == null || this.actualContent.getLeft() != inventoryPacket.getSyncId()) {
                    var actual = this.actualContent == null ? -1 : this.actualContent.getLeft();
                    LOGGER.info("Read inventory, actual: " + actual + " new: " + inventoryPacket.getSyncId());
                    if (this.getCurrentStep().contentCondition.test(inventoryPacket.getContents())) {
                        this.actualContent = new Pair<>(inventoryPacket.getSyncId(), inventoryPacket.getContents());
                    } else {
                        reset(false);
                    }
                }
            }

            if (this.actualSyncId != null && this.actualContent != null) {
                if (!this.isFinished()) {
                    if (!executing){
                        if (actualSyncId.equals(actualContent.getLeft())) {
                            LOGGER.info("Execute Interaction on: " + this.actualContent.getLeft() + " as step: " + currentStepIndex);
                            executing = true;
                            var step = this.getCurrentStep();
                            next();
                            step.interaction.accept(this.actualContent.getLeft(), this.actualContent.getRight());
                        } else {
                            LOGGER.info("No match in syncID: " + actualSyncId + " vs. " + actualContent.getLeft());
                        }
                    }
                    else{
                        executing = false;
                    }
                } else {
                    status.complete(true);
                    reset(false);
                }
            }
        }
        return shouldHide;
    }

    private synchronized void next() {
        currentStepIndex++;
        LOGGER.info("NEXT: " + currentStepIndex);
    }

    private synchronized void reset(boolean forced) {
        if (!this.continuousExecution || forced) {
            isActive.set(false);
            if (status != null) {
                status.complete(false);
                status = null;
            }
        }
        currentStepIndex = 0;
        actualSyncId = null;
        actualContent = null;
        executing = false;
        LOGGER.info("RESET");
    }

    private void executeStarting(MinecraftClient client) {
        if (this.startingAction != null) {
            this.startingAction.accept(client);
        }
    }

    private InteractionStep getCurrentStep() {
        return this.steps.get(this.currentStepIndex);
    }

    private boolean isFinished() {
        return this.currentStepIndex >= this.steps.size();
    }

    public static class ScreenInteractionManager {
        private static final HashSet<ScreenInteraction> registeredInteractions = new HashSet<>();

        private static void reset() {
            for (var listener : registeredInteractions) {
                listener.reset(false);
            }
        }

        public static void OpenScreen(OpenScreenS2CPacket packet, CallbackInfo ci) {
            LOGGER.info("SyncId of OpenScreen: " + packet.getSyncId() + " with title: '" + packet.getName().getString() + "'");
            var shouldHide = false;
            for (var listener : registeredInteractions) {
                shouldHide = shouldHide || listener.listen(packet);
            }
            if (shouldHide) {
                ci.cancel();
            }
        }

        public static void InventoryPackage(InventoryS2CPacket packet) {
            LOGGER.info("SyncId of Inventory: " + packet.getSyncId());
            for (var listener : registeredInteractions) {
                listener.listen(packet);
            }
        }

        static {
            ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
                reset();
            });
            ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
                reset();
            });
        }
    }

    public static class ScreenInteractionBuilder {
        private final List<InteractionStep> configuredQueue = new ArrayList<>();
        private Consumer<MinecraftClient> startingAction;
        private boolean shouldHide = false;

        /**
         * Start the interaction with the no screen open
         *
         * @param screenNameCondition Condition to match screen name on for syncId identification
         * @param slotCondition       Condition to check content on the syncId screen of the screen name matched
         * @param interaction         The interaction to run if all rules are matched
         */
        public ScreenInteractionBuilder(
                Predicate<String> screenNameCondition,
                Predicate<List<ItemStack>> slotCondition,
                BiConsumer<Integer, List<ItemStack>> interaction
        ) {
            addInteraction(screenNameCondition, slotCondition, interaction);
        }

        public ScreenInteraction build() {
            return new ScreenInteraction(
                    configuredQueue,
                    startingAction,
                    shouldHide
            );
        }

        public ScreenInteractionBuilder setKeepScreenHidden(boolean shouldHide) {
            this.shouldHide = shouldHide;
            return this;
        }

        public ScreenInteractionBuilder setStartingAction(Consumer<MinecraftClient> startingAction) {
            this.startingAction = startingAction;
            return this;
        }

        public ScreenInteractionBuilder addInteraction(
                Predicate<String> screenNameCondition,
                Predicate<List<ItemStack>> contentCondition,
                BiConsumer<Integer, List<ItemStack>> interaction
        ) {
            this.configuredQueue.add(new InteractionStep(screenNameCondition, contentCondition, interaction));
            return this;
        }
    }

    private record InteractionStep(Predicate<String> screenTitleCondition, Predicate<List<ItemStack>> contentCondition,
                                   BiConsumer<Integer, List<ItemStack>> interaction) {
    }

    public static class WellKnownInteractions {
        private static final MinecraftClient client = MinecraftClient.getInstance();

        public static void ClickSlot(int syncId, int slotId, Button button, SlotActionType slotActionType) {
            if (client.getNetworkHandler() != null) {
                client.getNetworkHandler().sendPacket(new ClickSlotC2SPacket(
                        syncId,
                        0,
                        slotId,
                        button.getNumVal(),
                        slotActionType,
                        ItemStack.EMPTY,
                        new Int2ObjectOpenHashMap<>()
                ));
            }
        }

        public enum Button {
            Left(0),
            Right(1);

            private final int numVal;

            Button(int numVal) {
                this.numVal = numVal;
            }

            public int getNumVal() {
                return numVal;
            }
        }
    }
}
