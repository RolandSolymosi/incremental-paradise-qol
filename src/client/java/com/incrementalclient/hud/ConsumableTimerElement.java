package com.incrementalclient.hud;

import com.google.common.base.Suppliers;
import com.incrementalclient.abstractions.HudElement;
import com.incrementalclient.abstractions.TextListHudElement;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.common.utils.Vector2f;
import com.incrementalclient.services.ActiveConsumableMonitor;
import com.incrementalclient.services.HudManager;
import com.incrementalclient.common.utils.TextUtils;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ConsumableTimerElement extends TextListHudElement<ConsumableTimerElement.Configuration> {

    private final Configuration configuration = new Configuration();

    private final Supplier<List<OptionPiece>> options;
    private final ActiveConsumableMonitor activeConsumableMonitor;

    public ConsumableTimerElement(
            MinecraftClientAccessor uiAccessor,
            HudManager hudManager,
            ActiveConsumableMonitor activeConsumableMonitor
    ) {
        super(uiAccessor, hudManager);
        this.activeConsumableMonitor = activeConsumableMonitor;
        this.downDirection = false;
        this.leftDirection = false;
        this.defaultPosition = new Vector2f(0.99F, 0.975F);
        resetToDefaultPosition();

        options = Suppliers.memoize(() -> List.of(
                Categories.Hud.Consumable.createConfig(0,
                        Option.<Boolean>createBuilder()
                                .name(Text.of("Toggle Consumable HUD on and off"))
                                .description(OptionDescription.of(Text.of("Turn on and off the consumable timer HUD.")))
                                .binding(configuration.isConsumableHudEnabled, () -> configuration.isConsumableHudEnabled, newVal -> configuration.isConsumableHudEnabled = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build()),
                Categories.Hud.Consumable.createConfig(1,
                        Option.<Double>createBuilder()
                                .name(Text.of("Consumable HUD background opacity"))
                                .description(OptionDescription.of(Text.of("Set the opacity of the consumable HUD background.")))
                                .binding(configuration.consumableHudBackgroundOpacity, () -> configuration.consumableHudBackgroundOpacity, newVal -> configuration.consumableHudBackgroundOpacity = newVal)
                                .controller(o -> DoubleSliderControllerBuilder.create(o).step(0.01).range(0.0, 1.0))
                                .build()),
                Categories.Hud.Consumable.createConfig(2,
                        Option.<Color>createBuilder()
                                .name(Text.of("Color of the timer name"))
                                .description(OptionDescription.of(Text.of("The color of the consumable timer name.")))
                                .binding(new Color(configuration.consumableTimerColor), () -> new Color(configuration.consumableTimerColor), newVal -> configuration.consumableTimerColor = newVal.getRGB())
                                .controller(ColorControllerBuilder::create)
                                .build()),
                Categories.Hud.Consumable.createConfig(3,
                        Option.<Color>createBuilder()
                                .name(Text.of("Color of the time left"))
                                .description(OptionDescription.of(Text.of("The color of the time left text.")))
                                .binding(new Color(configuration.consumableTimeColor), () -> new Color(configuration.consumableTimeColor), newVal -> configuration.consumableTimeColor = newVal.getRGB())
                                .controller(ColorControllerBuilder::create)
                                .build())
        ));
    }

    public Text render(ActiveConsumableMonitor.ConsumableTimer consumableTimer) {
        int timerColor = configuration.consumableTimerColor;
        int timeColor = configuration.consumableTimeColor;

        String timeLeft = consumableTimer.getTimeLeftString();

        return Text.literal("")
                .append(TextUtils.textColor(consumableTimer.getBuffName() + ": ", timerColor))
                .append(TextUtils.textColor(timeLeft, timeColor));
    }

    @Override
    protected List<Text> getTextsToRender(boolean editMode) {
        return activeConsumableMonitor.getConsumableList().stream()
                .map(this::render)
                .collect(Collectors.toList());
    }

    @Override
    protected int getBackgroundOpacity() {
        return (int) (getConfiguration().consumableHudBackgroundOpacity * 255);
    }

    @Override
    public boolean isElementEnabled() {
        return getConfiguration().isConsumableHudEnabled;
    }

    @Override
    public String getDisplayName() {
        return "Consumable Timer";
    }

    @Override
    public String getJsonSection() {
        return "consumableHud";
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public List<OptionPiece> getOption() {
        return options.get();
    }


    public static class Configuration extends HudElement.ConfigurationBase {
        @SerialEntry
        public boolean isConsumableHudEnabled = true;
        @SerialEntry
        public double consumableHudBackgroundOpacity = 0.3;

        @SerialEntry
        public int consumableTimerColor = 0xffaa00;
        @SerialEntry
        public int consumableTimeColor = 0x55ff55;
    }
}

