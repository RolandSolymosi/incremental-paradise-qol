package com.incrementalclient.featues;

import com.google.common.base.Suppliers;
import com.incrementalclient.config.controllers.KeyBindController;
import com.incrementalclient.interfaces.Configurable;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.internals.ScreenCapture;
import com.incrementalclient.services.CommandHandler;
import com.incrementalclient.services.InteractionScheduler;
import com.incrementalclient.services.KeyBindMonitor;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.Supplier;

public class DepositHotkey implements Configurable<DepositHotkey.Configuration> {

    private final Supplier<List<OptionPiece>> options;
    private final Configuration configuration = new Configuration();
    private final KeyBindMonitor.KeyBindListener keyBindListener;
    private final InteractionScheduler<Void> interactionScheduler;
    private final InteractionScheduler.Builder<Void, Void> depositTaskBuilder;

    public DepositHotkey(
            InteractionScheduler<Void> interactionScheduler,
            KeyBindMonitor keyBindMonitor,
            MinecraftClientAccessor mcAccessor,
            CommandHandler commandHandler
    ) {
        this.interactionScheduler = interactionScheduler;
        options = Suppliers.memoize(() -> List.of(Categories.Hotkeys.Bank.createConfig(0,
                Option.<Integer>createBuilder()
                        .name(Text.literal("Deposit all"))
                        .binding(
                                configuration.keybind,
                                () -> configuration.keybind,
                                v -> configuration.keybind = v
                        )
                        .controller((option) -> () -> new KeyBindController(option))
                        .build())));
        keyBindListener = new KeyBindMonitor.KeyBindListener(keyBindMonitor, new KeyBinding(
                "Deposit",
                InputUtil.Type.KEYSYM,
                configuration.keybind,
                "Incremental QOL"
        ), this::deposit);

        // We saw it! Now we can safely close everything and finish.
        depositTaskBuilder = new InteractionScheduler.Builder<Void, Void>("DepositHotkey", interactionScheduler, mcAccessor)
                .startWith(() -> mcAccessor.getPlayer().ifPresent(player ->
                        commandHandler.send("bank")
                ))
                .step(
                        (screen, ctx) -> screen.title().getString().contains("Bank"),
                        ctx -> {
                            ctx.click(21);
                            return false;
                        }
                )
                .step(
                        (screen, ctx) -> screen.title().getString().equals("Safe"),
                        (ctx) -> !hasItemsToDeposit(ctx.screen()),
                        ctx -> {
                            ctx.click(22);
                            return false;
                        }
                )
                .step(
                        (screen, ctx) -> screen.title().getString().contains("Safe") && !hasItemsToDeposit(screen),
                        ctx -> {
                            ctx.complete();
                            return false;
                        }
                )
                .priority(5);
    }

    private void deposit() {
        interactionScheduler.submit(depositTaskBuilder.build(null));
    }

    private boolean hasItemsToDeposit(ScreenCapture.Screen screen) {
        var stack = screen.contents().get(22);
        if (stack.isEmpty()) return false;

        var lore = stack.get(DataComponentTypes.LORE);
        if (lore == null || lore.lines().isEmpty()) return false;

        var lastLine = lore.lines().getLast().getString();
        return !lastLine.contains("You have no items to deposit");
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
        keyBindListener.updateKeyBind(configuration.keybind);
    }

    @Override
    public List<OptionPiece> getOption() {
        return options.get();
    }

    public static class Configuration {
        @SerialEntry
        public int keybind = GLFW.GLFW_KEY_B;
    }
}
