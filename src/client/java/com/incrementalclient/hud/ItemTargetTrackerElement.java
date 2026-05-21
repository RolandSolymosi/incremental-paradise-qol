package com.incrementalclient.hud;

import com.google.common.base.Suppliers;
import com.incrementalclient.abstractions.HudElement;
import com.incrementalclient.abstractions.TextListHudElement;
import com.incrementalclient.common.data.World;
import com.incrementalclient.common.utils.NumberParser;
import com.incrementalclient.common.utils.TextUtils;
import com.incrementalclient.common.utils.Vector2f;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.internals.events.ClientPlayConnectionObservable;
import com.incrementalclient.services.*;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ItemTargetTrackerElement extends TextListHudElement<ItemTargetTrackerElement.Configuration> {

    private final Configuration configuration = new Configuration();

    private final WorldMonitor worldMonitor;
    private final Supplier<List<OptionPiece>> options;
    private final ItemTargetMonitor itemTargetMonitor;

    public ItemTargetTrackerElement(
            MinecraftClientAccessor uiAccessor,
            WorldMonitor worldMonitor,
            HudManager hudManager,
            ItemTargetMonitor itemTargetMonitor
    ) {
        super(uiAccessor, hudManager);
        this.worldMonitor = worldMonitor;
        this.itemTargetMonitor = itemTargetMonitor;
        this.leftDirection = false;
        this.defaultPosition = new Vector2f(0.99F, 0.017777F);
        resetToDefaultPosition();

        options = Suppliers.memoize(() -> List.of(
                Categories.Hud.ItemTarget.createConfig(1,
                        Option.<Boolean>createBuilder()
                                .name(Text.of("Filter message"))
                                .description(OptionDescription.of(Text.of("Toggle if item tracker should filter message or not. If item tracker is disabled it won't filter message anyway.")))
                                .binding(Configuration.defaultFilterMessages, () -> configuration.filterMessages, newVal -> configuration.filterMessages = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build()),
                Categories.Hud.ItemTarget.createConfig(2,
                        Option.<Boolean>createBuilder()
                                .name(Text.of("Disable Hud element during Boss Fights"))
                                .description(OptionDescription.of(Text.of("Toggle if you want this element to be disabled and hidden during boss fights")))
                                .binding(Configuration.defaultIsHudDisabledDuringBossFight, () -> configuration.isHudDisabledDuringBossFight, newVal -> configuration.isHudDisabledDuringBossFight = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build()),
                Categories.Hud.ItemTarget.createConfig(4,
                        Option.<Double>createBuilder()
                                .name(Text.of("Item Target HUD background opacity"))
                                .description(OptionDescription.of(Text.of("Set the opacity of the Item Target HUD background.")))
                                .binding(Configuration.defaultHudBackgroundOpacity, () -> configuration.hudBackgroundOpacity, newVal -> configuration.hudBackgroundOpacity = newVal)
                                .controller(o -> DoubleSliderControllerBuilder.create(o).step(0.01).range(0.0, 1.0))
                                .build())
        ));
    }

    public Text render(ItemTargetMonitor.ItemTarget item) {

        Text text = Text.literal("")
                .append(item.DisplayText())
                .append(": ")
                .append(TextUtils.textColor(NumberParser.formatSuffixedNumber(item.current()) + "/" + NumberParser.formatSuffixedNumber(item.goal()), 0x00FFFF));

        if (item.current() >= item.goal()) {
            text = TextUtils.mutableRecolor(text, 0x00aa00);
        }

        return text;
    }

    @Override
    protected List<Text> getTextsToRender(boolean editMode) {
        return itemTargetMonitor.getItemTargets().values().stream()
                .map(this::render)
                .collect(Collectors.toList());
    }

    @Override
    protected int getBackgroundOpacity() {
        return (int) (getConfiguration().hudBackgroundOpacity * 255);
    }

    @Override
    public boolean isEnabled() {
        return enabled && (worldMonitor.currentWorld() != World.BossArenas || !configuration.isHudDisabledDuringBossFight);
    }

    @Override
    public String getDisplayName() {
        return "Item Tracker";
    }

    @Override
    public String getJsonSection() {
        return "itemTrackerHud";
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public List<OptionPiece> getOption() {
        return options.get();
    }

    @Override
    public void optionChanged(){
        super.optionChanged();
        itemTargetMonitor.shouldFilterChat(configuration.filterMessages);
    }


    public static class Configuration extends ConfigurationBase {

        private static final boolean defaultFilterMessages = true;
        private static final boolean defaultIsHudDisabledDuringBossFight = false;
        private static final double defaultHudBackgroundOpacity = 0.0;

        @SerialEntry
        public boolean filterMessages = defaultFilterMessages;
        @SerialEntry
        public boolean isHudDisabledDuringBossFight = defaultIsHudDisabledDuringBossFight;
        @SerialEntry
        public double hudBackgroundOpacity = defaultHudBackgroundOpacity;
    }
}

