package com.incrementalqol.common.utils;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class ScreenInteraction {
    public static final Logger LOGGER = LoggerFactory.getLogger(ScreenInteraction.class);

    private final String name;
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
        register();
        this.continuousExecution = continuousExecution;
        this.status = status;
        this.executeStarting(MinecraftClient.getInstance());
        return status;
    }

    public boolean stop() {
        if (isActive.get()) {
            reset(false, true);
            return true;
        }
        unregister();
        return false;
    }

    private ScreenInteraction(
            String name,
            List<InteractionStep> interactionSteps,
            Consumer<MinecraftClient> startingAction,
            boolean shouldHide
    ) {
        this.name = name;
        this.steps = interactionSteps;
        this.startingAction = startingAction;
        this.shouldHide = shouldHide;
    }

    public boolean register() {
        return ScreenInteractionManager.registeredInteractions.add(this);
    }

    public boolean unregister() {
        return ScreenInteractionManager.registeredInteractions.remove(this);
    }

    private synchronized boolean listen(Packet<ClientPlayPacketListener> packet) {
        var shouldHide = false;
        try {
            if (isActive.get()) {
                if (packet instanceof OpenScreenS2CPacket screenPacket) {
                    if (this.actualSyncId == null || this.actualSyncId != screenPacket.getSyncId()) {
                        ConfiguredLogger.LogInfo(LOGGER, "[" + name + "]: Read open screen, actual: " + actualSyncId + " new: " + screenPacket.getSyncId());
                        if (this.getCurrentStep().screenTitleCondition.test(screenPacket.getName().getString())) {
                            actualSyncId = screenPacket.getSyncId();
                            shouldHide = this.shouldHide;
                        } else {
                            reset(false, false);
                        }
                    }
                } else {
                    InventoryS2CPacket inventoryPacket = (InventoryS2CPacket) packet;
                    if (this.actualContent == null || this.actualContent.getLeft() != inventoryPacket.getSyncId()) {
                        var actual = this.actualContent == null ? -1 : this.actualContent.getLeft();
                        ConfiguredLogger.LogInfo(LOGGER, "[" + name + "]: Read inventory, actual: " + actual + " new: " + inventoryPacket.getSyncId());
                        if (this.getCurrentStep().contentCondition.test(inventoryPacket.getContents())) {
                            this.actualContent = new Pair<>(inventoryPacket.getSyncId(), inventoryPacket.getContents());
                        } else {
                            reset(false, false);
                        }
                    }
                }

                if (this.actualSyncId != null && this.actualContent != null) {
                    if (!this.isFinished()) {
                        if (!executing) {
                            if (actualSyncId.equals(actualContent.getLeft()) && steps.stream().noneMatch(s -> s.alreadyConsumedSyncId == actualContent.getLeft())) {
                                ConfiguredLogger.LogInfo(LOGGER, "[" + name + "]: Execute Interaction on: " + this.actualContent.getLeft() + " as step: " + currentStepIndex);
                                executing = true;
                                var step = this.getCurrentStep();
                                if (step.interaction.apply(new Pair<>(this.actualContent.getLeft(), this.actualContent.getRight()))) {
                                    step.alreadyConsumedSyncId = this.actualContent.getLeft();
                                    ConfiguredLogger.LogInfo(LOGGER, "[" + name + "]: Consumed: " + this.actualContent.getLeft() + " as step: " + currentStepIndex);
                                    next();
                                }
                            } else if (actualSyncId.equals(actualContent.getLeft())) {
                                ConfiguredLogger.LogInfo(LOGGER, "[" + name + "]: Already consumed syncID: " + actualContent.getLeft());
                            } else {
                                ConfiguredLogger.LogInfo(LOGGER, "[" + name + "]: No match in syncID: " + actualSyncId + " vs. " + actualContent.getLeft());
                            }
                        } else {
                            executing = false;
                        }
                    } else {
                        reset(true, false);
                    }
                }
            }
        } catch (Exception e) {
            reset(false, true);
        }
        return shouldHide;
    }

    private synchronized void next() {
        currentStepIndex++;
        ConfiguredLogger.LogInfo(LOGGER, "[" + name + "]: NEXT: " + currentStepIndex);
    }

    private synchronized void reset(boolean resultStatus, boolean forced) {
        if (shouldHide && steps.stream().anyMatch(s ->  s.alreadyConsumedSyncId != -1)) {
            // TODO: Move this to the concurrent interaction handler
            // Close window to reset handler
            MinecraftClient.getInstance().getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(actualContent.getLeft()));

            // Enforce update of the special slot of the potion
            MinecraftClient.getInstance().getNetworkHandler().sendPacket(new ClickSlotC2SPacket(
                    0,
                    0,
                    45,
                    0,
                    SlotActionType.PICKUP,
                    ItemStack.EMPTY,
                    new Int2ObjectOpenHashMap<>()
            ));
        }
        currentStepIndex = 0;
        actualSyncId = null;
        actualContent = null;
        executing = false;
        steps.forEach(s -> s.alreadyConsumedSyncId = -1);
        if (!this.continuousExecution || forced) {
            unregister();
            isActive.set(false);
            if (status != null) {
                var actualStatus = status;
                status = null;
                actualStatus.complete(resultStatus);
            }
        }
        ConfiguredLogger.LogInfo(LOGGER, "[" + name + "]: RESET with status: " + resultStatus);
    }

    private void executeStarting(MinecraftClient client) {
        if (this.startingAction != null) {
            try {
                this.startingAction.accept(client);
            } catch (Exception e) {
                reset(false, true);
            }
        }
    }

    private InteractionStep getCurrentStep() {
        return this.steps.get(this.currentStepIndex);
    }

    private boolean isFinished() {
        return this.currentStepIndex >= this.steps.size();
    }

    public static class ScreenInteractionManager {
        private static final Set<ScreenInteraction> registeredInteractions = ConcurrentHashMap.newKeySet();
        private static AtomicBoolean hadInteraction = new AtomicBoolean();

        private static void reset() {
            for (var listener : registeredInteractions) {
                listener.reset(false, false);
            }
            hadInteraction.set(false);
        }

        public static boolean anyActiveInteractionOngoing() {
            return !registeredInteractions.stream().filter(i -> i.shouldHide && i.isActive.get()).findAny().isEmpty();
        }

        public static void OpenScreen(OpenScreenS2CPacket packet, CallbackInfo ci) {
            ConfiguredLogger.LogInfo(LOGGER, "[SHARED]: SyncId of OpenScreen: " + packet.getSyncId() + " with title: '" + packet.getName().getString() + "'");
            var shouldHide = false;
            MinecraftClient.getInstance().execute(ScreenInteractionManager::ProtectInteraction);
            for (var listener : registeredInteractions) {
                shouldHide = shouldHide || listener.listen(packet);
                hadInteraction.compareAndSet(false, true);
            }
            if (shouldHide) {
                ci.cancel();
            }
        }

        public static void InventoryPackage(InventoryS2CPacket packet, CallbackInfo ci) {
            ConfiguredLogger.LogInfo(LOGGER, "[SHARED]: SyncId of Inventory: " + packet.getSyncId());// + " revision: "+packet.getRevision());
            MinecraftClient.getInstance().execute(ScreenInteractionManager::ProtectInteraction);
            for (var listener : registeredInteractions) {
                listener.listen(packet);
                hadInteraction.compareAndSet(false, true);
            }
        }

        public static void SlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo ci) {
            //ConfiguredLogger.LogInfo(LOGGER, "[SHARED]: SyncId of SlotUpdate: " + packet.getSyncId() + " slotId: "+packet.getSlot() + " revision: "+packet.getRevision());
        }

        public static void SetInventory(SetPlayerInventoryS2CPacket packet, CallbackInfo ci) {
            //ConfiguredLogger.LogInfo(LOGGER, "[SHARED]: Set Inventory slotId: "+packet.slot());
        }

        public static void ScreenPropertyUpdate(ScreenHandlerPropertyUpdateS2CPacket packet, CallbackInfo ci) {
            //ConfiguredLogger.LogInfo(LOGGER, "[SHARED]: SyncId of ScreenPropertyUpdate: " + packet.getSyncId() + " propertyId: "+packet.getPropertyId());
        }

        public static void EntityStatus(EntityStatusS2CPacket packet, CallbackInfo ci) {
            //ConfiguredLogger.LogInfo(LOGGER, "[SHARED] EntityStatusUpdate: " + packet.getStatus());
        }

        public static void Respawn(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
            //ConfiguredLogger.LogInfo(LOGGER, "[SHARED] Respawn: " + packet.commonPlayerSpawnInfo().dimension().getValue().toShortTranslationKey());
        }

        public static void GameStateChange(GameStateChangeS2CPacket packet, CallbackInfo ci) {
            //ConfiguredLogger.LogInfo(LOGGER, "[SHARED] GameStateChange: " + packet.getValue());
        }

        private static void ProtectInteraction() {
            //var client = MinecraftClient.getInstance();
            //if (ScreenInteraction.ScreenInteractionManager.anyActiveInteractionOngoing() && client.currentScreen != null) {
            //    MinecraftClient.getInstance().setScreen(null); // Re-hide screen if necessary
            //}
        }

        static {
            ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
                reset();
                registeredInteractions.clear();
            });
            ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
                reset();
            });
            ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
                if (screen instanceof InventoryScreen) {
                    if (client.player != null){
                        if (hadInteraction.get()){
                            hadInteraction.set(false);
                            ConfiguredLogger.LogInfo(LOGGER, "[SHARED]: Inventory Opened, reset handler to inventory.");
                            client.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(0));
                        }
                    }
                }
            });

            ClientTickEvents.END_CLIENT_TICK.register(client -> {

            });
        }
    }

    public static class ScreenInteractionBuilder {
        private final List<InteractionStep> configuredQueue = new ArrayList<>();
        private final String name;
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
                String name,
                Predicate<String> screenNameCondition,
                Predicate<List<ItemStack>> slotCondition,
                Function<Pair<Integer, List<ItemStack>>, Boolean> interaction
        ) {
            this.name = name;
            addInteraction(screenNameCondition, slotCondition, interaction);
        }

        public ScreenInteraction build() {
            return new ScreenInteraction(
                    name,
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
                Function<Pair<Integer, List<ItemStack>>, Boolean> interaction
        ) {
            this.configuredQueue.add(new InteractionStep(screenNameCondition, contentCondition, interaction));
            return this;
        }
    }

    private static final class InteractionStep {
        private final Predicate<String> screenTitleCondition;
        private final Predicate<List<ItemStack>> contentCondition;
        private final Function<Pair<Integer, List<ItemStack>>, Boolean> interaction;
        private int alreadyConsumedSyncId = -1;

        private InteractionStep(Predicate<String> screenTitleCondition, Predicate<List<ItemStack>> contentCondition,
                                Function<Pair<Integer, List<ItemStack>>, Boolean> interaction) {
            this.screenTitleCondition = screenTitleCondition;
            this.contentCondition = contentCondition;
            this.interaction = interaction;
        }

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
