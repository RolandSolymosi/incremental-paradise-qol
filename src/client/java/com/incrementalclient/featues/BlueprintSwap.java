package com.incrementalclient.featues;

import com.google.common.base.Suppliers;
import com.incrementalclient.common.data.skills.SkillCategory;
import com.incrementalclient.common.utils.Utils;
import com.incrementalclient.config.controllers.KeyBindController;
import com.incrementalclient.interfaces.Configurable;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.services.CommandHandler;
import com.incrementalclient.services.InteractionScheduler;
import com.incrementalclient.services.KeyBindMonitor;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class BlueprintSwap implements Configurable<BlueprintSwap.Configuration> {

    private final Supplier<List<OptionPiece>> options;
    private final Configuration configuration = new Configuration();
    private final KeyBindMonitor.KeyBindListener meleeKeyBindListener;
    private final KeyBindMonitor.KeyBindListener rangedKeyBindListener;
    private final KeyBindMonitor.KeyBindListener pickAxeKeyBindListener;
    private final KeyBindMonitor.KeyBindListener axeKeyBindListener;
    private final KeyBindMonitor.KeyBindListener hoeKeyBindListener;
    private final KeyBindMonitor.KeyBindListener spearKeyBindListener;
    private final InteractionScheduler<SkillCategory> interactionScheduler;
    private final InteractionScheduler.Builder<SkillCategory, Void> swapBlueprintTaskBuilder;
    private final AtomicReference<InteractionScheduler.InteractionTask<SkillCategory, ?>> ongoingSwap = new AtomicReference<>();

    public BlueprintSwap(
            InteractionScheduler<SkillCategory> interactionScheduler,
            KeyBindMonitor keyBindMonitor,
            MinecraftClientAccessor mcAccessor,
            CommandHandler commandHandler
    ) {
        this.interactionScheduler = interactionScheduler;
        options = Suppliers.memoize(() -> List.of(
                Categories.Hotkeys.BlueprintSwap.createConfig(0,
                        Option.<Integer>createBuilder()
                                .name(Text.literal("Swap Melee Blueprint"))
                                .binding(
                                        configuration.meleeSwapHotkey,
                                        () -> configuration.meleeSwapHotkey,
                                        v -> configuration.meleeSwapHotkey = v
                                )
                                .controller((option) -> () -> new KeyBindController(option))
                                .build()),
                Categories.Hotkeys.BlueprintSwap.createConfig(1,
                        Option.<Integer>createBuilder()
                                .name(Text.literal("Swap Ranged Blueprint"))
                                .binding(
                                        configuration.rangedSwapHotkey,
                                        () -> configuration.rangedSwapHotkey,
                                        v -> configuration.rangedSwapHotkey = v
                                )
                                .controller((option) -> () -> new KeyBindController(option))
                                .build()),
                Categories.Hotkeys.BlueprintSwap.createConfig(2,
                        Option.<Integer>createBuilder()
                                .name(Text.literal("Swap Pickaxe Blueprint"))
                                .binding(
                                        configuration.pickaxeSwapHotkey,
                                        () -> configuration.pickaxeSwapHotkey,
                                        v -> configuration.pickaxeSwapHotkey = v
                                )
                                .controller((option) -> () -> new KeyBindController(option))
                                .build()),
                Categories.Hotkeys.BlueprintSwap.createConfig(3,
                        Option.<Integer>createBuilder()
                                .name(Text.literal("Swap Axe Blueprint"))
                                .binding(
                                        configuration.axeSwapHotkey,
                                        () -> configuration.axeSwapHotkey,
                                        v -> configuration.axeSwapHotkey = v
                                )
                                .controller((option) -> () -> new KeyBindController(option))
                                .build()),
                Categories.Hotkeys.BlueprintSwap.createConfig(4,
                        Option.<Integer>createBuilder()
                                .name(Text.literal("Swap Pickaxe Blueprint"))
                                .binding(
                                        configuration.hoeSwapHotkey,
                                        () -> configuration.hoeSwapHotkey,
                                        v -> configuration.hoeSwapHotkey = v
                                )
                                .controller((option) -> () -> new KeyBindController(option))
                                .build()),
                Categories.Hotkeys.BlueprintSwap.createConfig(5,
                        Option.<Integer>createBuilder()
                                .name(Text.literal("Swap Fishing Spear Blueprint"))
                                .binding(
                                        configuration.spearSwapHotkey,
                                        () -> configuration.spearSwapHotkey,
                                        v -> configuration.spearSwapHotkey = v
                                )
                                .controller((option) -> () -> new KeyBindController(option))
                                .build())
        ));
        meleeKeyBindListener = new KeyBindMonitor.KeyBindListener(keyBindMonitor, new KeyBinding(
                "Swap Melee Blueprint",
                InputUtil.Type.KEYSYM,
                configuration.meleeSwapHotkey,
                "Incremental QOL"
        ), this::swapMelee);
        rangedKeyBindListener = new KeyBindMonitor.KeyBindListener(keyBindMonitor, new KeyBinding(
                "Swap Ranged Blueprint",
                InputUtil.Type.KEYSYM,
                configuration.rangedSwapHotkey,
                "Incremental QOL"
        ), this::swapRanged);
        pickAxeKeyBindListener = new KeyBindMonitor.KeyBindListener(keyBindMonitor, new KeyBinding(
                "Swap Pickaxe Blueprint",
                InputUtil.Type.KEYSYM,
                configuration.pickaxeSwapHotkey,
                "Incremental QOL"
        ), this::swapPickAxe);
        axeKeyBindListener = new KeyBindMonitor.KeyBindListener(keyBindMonitor, new KeyBinding(
                "Swap Axe Blueprint",
                InputUtil.Type.KEYSYM,
                configuration.axeSwapHotkey,
                "Incremental QOL"
        ), this::swapAxe);
        hoeKeyBindListener = new KeyBindMonitor.KeyBindListener(keyBindMonitor, new KeyBinding(
                "Swap Hoe Blueprint",
                InputUtil.Type.KEYSYM,
                configuration.hoeSwapHotkey,
                "Incremental QOL"
        ), this::swapHoe);
        spearKeyBindListener = new KeyBindMonitor.KeyBindListener(keyBindMonitor, new KeyBinding(
                "Swap Fishing Spear Blueprint",
                InputUtil.Type.KEYSYM,
                configuration.spearSwapHotkey,
                "Incremental QOL"
        ), this::swapSpear);

        // We saw it! Now we can safely close everything and finish.
        swapBlueprintTaskBuilder = new InteractionScheduler.Builder<SkillCategory, Void>("SwapBlueprint", interactionScheduler, mcAccessor)
                .priority(5)
                .retries(3)
                .startWith(() -> commandHandler.send("skill"))
                // Step 1: Open the Skills Menu
                .step(
                        (screen, ctx) -> screen.title().getString().contains("Skills"),
                        // Catch-all for the final state
                        ctx -> {
                            var customName = Objects.requireNonNull(ctx.screen().contents().get(25).getCustomName()).getString();
                            ctx.click(Utils.getSkillSlotId(customName, ctx.context()));
                            return false;
                        }
                )
                // Step 2: Select the specific Skill
                .step(
                        (screen, ctx) -> screen.title().getString().equals(ctx.getName()),
                        ctx -> {
                            short slotId = (short) (ctx.context() == SkillCategory.Sharpshooting ? 23 : 24);
                            ctx.click(slotId);
                            return false;
                        }
                )
                // Step 3: Open Blueprints
                .step(
                        (screen, ctx) -> screen.title().getString().contains("Blueprints"),
                        ctx -> {
                            ctx.click(10);
                            return false;
                        }
                )
                // Step 4: Finalize and Complete
                .step(
                        (screen, ctx) -> true, // Catch-all for the final state
                        ctx -> {
                            ctx.complete();
                            return false;
                        }
                );
    }

    private void swapMelee() {
        swap(SkillCategory.Combat);
    }

    private void swapRanged() {
        swap(SkillCategory.Sharpshooting);
    }

    private void swapPickAxe() {
        swap(SkillCategory.Mining);
    }

    private void swapAxe() {
        swap(SkillCategory.Foraging);
    }

    private void swapHoe() {
        swap(SkillCategory.Farming);
    }

    private void swapSpear() {
        swap(SkillCategory.SpearFishing);
    }

    private void swap(SkillCategory skillType) {
        ongoingSwap.set(null);
        var swapStarted = ongoingSwap.compareAndSet(null, swapBlueprintTaskBuilder.build(skillType));
        if (swapStarted) {
            ongoingSwap.get().getFuture().thenRun(() -> ongoingSwap.set(null));
            interactionScheduler.submit(ongoingSwap.get());
        }
    }

    @Override
    public String getJsonSection() {
        return "depositHotkey";
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void optionChanged() {
        meleeKeyBindListener.updateKeyBind(configuration.meleeSwapHotkey);
        rangedKeyBindListener.updateKeyBind(configuration.rangedSwapHotkey);
        pickAxeKeyBindListener.updateKeyBind(configuration.pickaxeSwapHotkey);
        axeKeyBindListener.updateKeyBind(configuration.axeSwapHotkey);
        hoeKeyBindListener.updateKeyBind(configuration.hoeSwapHotkey);
        spearKeyBindListener.updateKeyBind(configuration.spearSwapHotkey);
    }

    @Override
    public List<OptionPiece> getOption() {
        return options.get();
    }

    public static class Configuration {
        @SerialEntry
        public int meleeSwapHotkey = GLFW.GLFW_KEY_UNKNOWN;
        @SerialEntry
        public int rangedSwapHotkey = GLFW.GLFW_KEY_UNKNOWN;
        @SerialEntry
        public int pickaxeSwapHotkey = GLFW.GLFW_KEY_UNKNOWN;
        @SerialEntry
        public int axeSwapHotkey = GLFW.GLFW_KEY_UNKNOWN;
        @SerialEntry
        public int hoeSwapHotkey = GLFW.GLFW_KEY_UNKNOWN;
        @SerialEntry
        public int spearSwapHotkey = GLFW.GLFW_KEY_UNKNOWN;
    }
}
