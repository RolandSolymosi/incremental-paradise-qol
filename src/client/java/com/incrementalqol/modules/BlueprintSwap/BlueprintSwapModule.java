package com.incrementalqol.modules.BlueprintSwap;

import com.incrementalqol.common.utils.ScreenInteraction;
import com.incrementalqol.common.data.SkillType;
import com.incrementalqol.common.utils.Utils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

import java.util.Map;

public class BlueprintSwapModule implements ClientModInitializer {

    private static Map<KeyBinding, SkillType> blueprintSwapKeyBindings;
    private static ScreenInteraction screenInteraction = null;
    private static SkillType currentSkill = null;

    public static void sendMenuOpenInteraction(MinecraftClient client) {
        var player = client.player;

        if (player == null) {
            return;
        }

        if (screenInteraction == null) {
            return;
        }

        screenInteraction.startAsync(false);
    }

    private static void keybindingCheck(MinecraftClient client) {
        blueprintSwapKeyBindings.forEach(((keyBinding, skillType) -> {
            while (keyBinding.wasPressed()) {
                assert MinecraftClient.getInstance().player != null;
                currentSkill = skillType;
                sendMenuOpenInteraction(MinecraftClient.getInstance());
            }
        }));
    }

    private void initializeKeybinding() {
        blueprintSwapKeyBindings = Map.of(
                KeyBindingHelper.registerKeyBinding(new KeyBinding("Swap Melee Blueprint", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "Incremental QOL")), SkillType.Combat,
                KeyBindingHelper.registerKeyBinding(new KeyBinding("Swap Ranged Blueprint", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "Incremental QOL")), SkillType.Sharpshooting,
                KeyBindingHelper.registerKeyBinding(new KeyBinding("Swap Pickaxe Blueprint", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "Incremental QOL")), SkillType.Mining,
                KeyBindingHelper.registerKeyBinding(new KeyBinding("Swap Axe Blueprint", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "Incremental QOL")), SkillType.Foraging,
                KeyBindingHelper.registerKeyBinding(new KeyBinding("Swap Hoe Blueprint", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "Incremental QOL")), SkillType.Farming,
                KeyBindingHelper.registerKeyBinding(new KeyBinding("Swap Fishing Blueprint", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "Incremental QOL")), SkillType.SpearFishing
        );
    }

    @Override
    public void onInitializeClient() {
        initializeKeybinding();
        screenInteraction = new ScreenInteraction.ScreenInteractionBuilder(
                "SwapBlueprint",
                s -> s.contains("Skills"),
                s -> true,
                (input) -> {
                    ScreenInteraction.WellKnownInteractions.ClickSlot(input.getLeft(), Utils.getSkillSlotId(input, currentSkill), ScreenInteraction.WellKnownInteractions.Button.Left, SlotActionType.PICKUP);
                    return true;
                }
        )
                .addInteraction(
                        s -> s.equals(currentSkill.getName()),
                        s -> true,
                        (input) -> {
                            ScreenInteraction.WellKnownInteractions.ClickSlot(input.getLeft(), (short) (currentSkill == SkillType.Sharpshooting ? 23 : 24), ScreenInteraction.WellKnownInteractions.Button.Left, SlotActionType.PICKUP);
                            return true;
                        }
                )
                .addInteraction(
                        s -> s.contains("Blueprints"),
                        s -> true,
                        (input) -> {
                            ScreenInteraction.WellKnownInteractions.ClickSlot(input.getLeft(), (short) 10, ScreenInteraction.WellKnownInteractions.Button.Left, SlotActionType.PICKUP);
                            return true;
                        }
                )
                .addInteraction(
                        s -> true,
                        s -> true,
                        (input) -> {
                            return true;
                        }
                )
                .setStartingAction(c ->
                        c.player.networkHandler.sendChatCommand("skill")
                )
                .setKeepScreenHidden(true)
                .build();

        ClientTickEvents.END_CLIENT_TICK.register(BlueprintSwapModule::keybindingCheck);
    }
}