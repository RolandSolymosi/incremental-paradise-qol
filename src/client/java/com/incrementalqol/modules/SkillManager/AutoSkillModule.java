package com.incrementalqol.modules.SkillManager;

import com.incrementalqol.common.data.SkillType;
import com.incrementalqol.common.data.Skills.*;
import com.incrementalqol.common.data.World;
import com.incrementalqol.common.utils.ConfiguredLogger;
import com.incrementalqol.common.utils.ScreenInteraction;
import com.incrementalqol.common.utils.WorldChangeNotifier;
import com.incrementalqol.config.Config;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoSkillModule implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(AutoSkillModule.class);

    private static KeyBinding testKeyBind;
    private static ScreenInteraction screenInteraction = null;

    private static final Queue<SkillType> levelUpQueue = new ConcurrentLinkedQueue<>();
    private static SkillType actualSkillType = null;
    private static final AtomicBoolean ongoingLeveling = new AtomicBoolean();

    private static final Pattern regex = Pattern.compile("^(?<Type>[a-zA-Z ]+) LEVEL UP!$");

    private static CompletableFuture<Boolean> afterWorldChange(Pair<World, Boolean> input) {
        var future = new CompletableFuture<Boolean>();
        if (input.getRight()){
            reset();
            levelUpQueue.add(SkillType.Combat);
            levelUpQueue.add(SkillType.Mining);
            levelUpQueue.add(SkillType.Foraging);
            levelUpQueue.add(SkillType.Farming);
            levelUpQueue.add(SkillType.SpearFishing);
            levelUpQueue.add(SkillType.Sharpshooting);
            executeLevelUp(future);
        }
        else{
            future.complete(true);
        }

        return future;
    }

    private static void reset() {
        screenInteraction.stop();
        ongoingLeveling.set(false);
        actualSkillType = null;
    }


    @Override
    public void onInitializeClient() {
        screenInteraction = new ScreenInteraction.ScreenInteractionBuilder(
                "AutoSkillManager",
                s -> s.contains("Skills"),
                s -> true,
                (input) -> {
                    var slotId = switch (actualSkillType) {
                        case SkillType.Combat -> 21;
                        case SkillType.Mining -> 19;
                        case SkillType.Foraging -> 20;
                        case SkillType.Farming -> 23;
                        case SkillType.SpearFishing -> 24;
                        case SkillType.Sharpshooting -> 25;
                    };
                    ScreenInteraction.WellKnownInteractions.ClickSlot(input.getLeft(), slotId, ScreenInteraction.WellKnownInteractions.Button.Left, SlotActionType.PICKUP);
                    return true;
                }
        )
                .addInteraction(
                        s -> s.equals(actualSkillType.getName()),
                        s -> true,
                        (input) -> {
                            ScreenInteraction.WellKnownInteractions.ClickSlot(input.getLeft(), 22, ScreenInteraction.WellKnownInteractions.Button.Left, SlotActionType.PICKUP);
                            return true;
                        }
                )
                .addInteraction(
                        s -> s.equals(actualSkillType.getName() + " Shop"),
                        s -> true,
                        (input) -> LevelUp(input, actualSkillType)
                )
                .setStartingAction(c ->
                        c.player.networkHandler.sendChatCommand("skills")
                )
                .setKeepScreenHidden(true)
                .build();

        ClientPlayConnectionEvents.DISCONNECT.register((ClientPlayNetworkHandler handler, MinecraftClient client) -> AutoSkillModule.reset());

        // When the client world loads (e.g., changing dimensions)
        WorldChangeNotifier.Register(AutoSkillModule::afterWorldChange);

        ClientReceiveMessageEvents.GAME.register(AutoSkillModule::checkForLevelUpMessage);

        // FOR TESTING
        testKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Force Skill Level Up",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UP,
                "Incremental QOL"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(AutoSkillModule::keybindingCheck);
    }

    private static void keybindingCheck(MinecraftClient client) {
        while (testKeyBind.wasPressed()) {
            reset();
            levelUpQueue.add(SkillType.Combat);
            levelUpQueue.add(SkillType.Mining);
            levelUpQueue.add(SkillType.Foraging);
            levelUpQueue.add(SkillType.Farming);
            levelUpQueue.add(SkillType.SpearFishing);
            levelUpQueue.add(SkillType.Sharpshooting);
            executeLevelUp(new CompletableFuture<>());
        }
    }

    private static void checkForLevelUpMessage(Text message, boolean overlay) {
        if (message.getString().contains("LEVEL UP!")) {
            Matcher matcher = regex.matcher(message.getString());

            if (matcher.find()) {
                var type = SkillType.findByName(matcher.group("Type"));
                if (type.isEmpty()) {
                    return;
                }
                levelUpQueue.add(type.get());
                executeLevelUp(new CompletableFuture<>());
            }
        }
    }

    private static void executeLevelUp(CompletableFuture<Boolean> future) {
        if (!levelUpQueue.isEmpty()) {
            if (ongoingLeveling.compareAndSet(false, true)) {
                actualSkillType = levelUpQueue.peek();
                if (Config.HANDLER.instance().getAutoSwapTools()){
                    screenInteraction.startAsync(false).thenAccept(result -> {
                        actualSkillType = null;
                        levelUpQueue.poll();
                        ongoingLeveling.compareAndSet(true, false);
                        executeLevelUp(future);
                    });
                }
                else{
                    actualSkillType = null;
                    levelUpQueue.poll();
                    ongoingLeveling.compareAndSet(true, false);
                    executeLevelUp(future);
                }
            }
        }
        else{
            future.complete(true);
        }
    }

    private static boolean LevelUp(Pair<Integer, List<ItemStack>> content, SkillType toolType) {
        var skillLevelUps = SkillNameListInOrderForSkill(toolType);
        var levels = new HashMap<String, Integer>();
        var slotIdCache = new HashMap<String, Integer>();
        for (var nextSkill : skillLevelUps) {
            levels.merge(nextSkill, 1, (actual, ignore) -> actual + 1);
            var expectedLevel = levels.get(nextSkill);
            if (!slotIdCache.containsKey(nextSkill)) {
                var index = 0;
                for (var slot : content.getRight()) {
                    var customName = slot.get(DataComponentTypes.CUSTOM_NAME);
                    if (customName != null && customName.getString().equals(nextSkill)) {
                        slotIdCache.put(nextSkill, index);
                        break;
                    }
                    index++;
                }
            }
            if (!slotIdCache.containsKey(nextSkill)) {
                break;
            } else {
                var slotId = slotIdCache.get(nextSkill);
                var lore = content.getRight().get(slotId).get(DataComponentTypes.LORE);
                var level = Integer.parseInt(String.valueOf(lore.lines().get(1).getString().charAt(7)));
                if (level < expectedLevel) {
                    if (lore.lines().getLast().getString().contains("Can't afford")) {
                        ConfiguredLogger.LogInfo(LOGGER, "Can't afford: "+nextSkill+" [Level "+expectedLevel+"]");
                        return true;
                    }
                    if (lore.lines().getLast().getString().contains("MAX LEVEL")) {
                        ConfiguredLogger.LogInfo(LOGGER, "Already max level: "+nextSkill+ " (Expected: "+expectedLevel+")");
                    }
                    else{
                        ScreenInteraction.WellKnownInteractions.ClickSlot(content.getLeft(), slotId, ScreenInteraction.WellKnownInteractions.Button.Left, SlotActionType.PICKUP);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static List<String> SkillNameListInOrderForSkill(SkillType toolType) {
        if (WorldChangeNotifier.IsActualWorldNightmare()){
            return switch (toolType) {
                case SkillType.Combat ->
                        Config.HANDLER.instance().nightmareCombatSkills.stream().map(NightmareCombatSkill::getName).toList();
                case SkillType.Mining ->
                        Config.HANDLER.instance().nightmareMiningSkills.stream().map(NightmareMiningSkill::getName).toList();
                case SkillType.Foraging ->
                        Config.HANDLER.instance().nightmareForagingSkills.stream().map(NightmareForagingSkill::getName).toList();
                case SkillType.Farming ->
                        Config.HANDLER.instance().nightmareFarmingSkills.stream().map(NightmareFarmingSkill::getName).toList();
                case SkillType.SpearFishing ->
                        Config.HANDLER.instance().nightmareSpearFishingSkills.stream().map(NightmareSpearFishingSkill::getName).toList();
                case SkillType.Sharpshooting ->
                        Config.HANDLER.instance().nightmareSharpshootingSkills.stream().map(NightmareSharpshootingSkill::getName).toList();
            };
        }
        else{
            return switch (toolType) {
                case SkillType.Combat ->
                        Config.HANDLER.instance().normalCombatSkills.stream().map(NormalCombatSkill::getName).toList();
                case SkillType.Mining ->
                        Config.HANDLER.instance().normalMiningSkills.stream().map(NormalMiningSkill::getName).toList();
                case SkillType.Foraging ->
                        Config.HANDLER.instance().normalForagingSkills.stream().map(NormalForagingSkill::getName).toList();
                case SkillType.Farming ->
                        Config.HANDLER.instance().normalFarmingSkills.stream().map(NormalFarmingSkill::getName).toList();
                case SkillType.SpearFishing ->
                        Config.HANDLER.instance().normalSpearFishingSkills.stream().map(NormalSpearFishingSkill::getName).toList();
                case SkillType.Sharpshooting ->
                        Config.HANDLER.instance().normalSharpshootingSkills.stream().map(NormalSharpshootingSkill::getName).toList();
            };
        }
    }
}