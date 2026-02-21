package com.incrementalclient.services;

import com.google.common.base.Suppliers;
import com.incrementalclient.Main;
import com.incrementalclient.abstractions.HudElement;
import com.incrementalclient.abstractions.ObservableBase;
import com.incrementalclient.common.utils.Vector2f;
import com.incrementalclient.hud.ScoreboardReplacementBar.ScoreboardReplacementBarElement;
import com.incrementalclient.hud.internals.HudCustomizationScreen;
import com.incrementalclient.interfaces.Configurable;
import com.incrementalclient.interfaces.Observer;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.internals.events.HudRenderCallbackObservable;
import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.NameableEnum;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

public class HudManager extends ObservableBase<Observer<HudManager.Event>, HudManager.Event> implements Observer<HudRenderCallbackObservable.Event>, Configurable<HudManager.Configuration> {
    private final MinecraftClientAccessor mcClient;

    private final Configuration configuration = new Configuration();

    private final Supplier<List<OptionPiece>> options;

    private int barHeight = 0;

    public HudManager(
            MinecraftClientAccessor mcClient,
            HudRenderCallbackObservable hudRenderCallbackObservable) {
        this.mcClient = mcClient;

        options = Suppliers.memoize(() -> List.of(
                Categories.Hud.General.createConfig(0,
                        Option.<Boolean>createBuilder()
                                .name(Text.of("Toggle HUD on and off"))
                                .description(OptionDescription.of(Text.of("Turn on and off the task HUD.")))
                                .binding(Configuration.defaultHudEnabled, () -> configuration.isHudEnabled, newVal -> configuration.isHudEnabled = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build()),
                Categories.Hud.General.createConfig(100,
                        ButtonOption.createBuilder()
                                .name(Text.of("HUD Customization"))
                                .description(OptionDescription.of(Text.of("Open the HUD customization screen to position and scale all HUD elements.")))
                                .action((t, o) -> mcClient.setScreen(Main.SERVICE_PROVIDER.getService(HudCustomizationScreen.class)))
                                .build()),
//                Categories.Hud.General.createConfig(200,
//                        Option.<Configuration.ActiveBarMode>createBuilder()
//                                .name(Text.of("Active Bar Mode"))
//                                .description(OptionDescription.of(Text.of("Bottom: Hotbar moves to bottom bar. Top: Hotbar stays in normal position. None: Hotbar stays in normal position.")))
//                                .binding(Configuration.defaultActiveBarMode,
//                                        () -> configuration.activeBarMode,
//                                        newVal -> configuration.activeBarMode = newVal)
//                                .controller(opt -> EnumControllerBuilder.create(opt)
//                                        .enumClass(Configuration.ActiveBarMode.class))
//                                .build()),
                Categories.Hud.General.createConfig(300,
                        Option.<Boolean>createBuilder()
                                .name(Text.of("Replace scoreboard with bar"))
                                .description(OptionDescription.of(Text.of("Replaces the vanilla scoreboard with a top bar")))
                                .binding(Configuration.defaultBarScoreboardReplacement, () -> configuration.barScoreboardReplacement, newVal -> configuration.barScoreboardReplacement = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build())
//                Categories.Hud.Vanilla.createConfig(100,
//                        Option.<Boolean>createBuilder()
//                                .name(Text.of("Hide vanilla hearts"))
//                                .description(OptionDescription.of(Text.of("Hides the vanilla health hearts.")))
//                                .binding(Configuration.defaultHideVanillaHearts, () -> configuration.hideVanillaHearts, newVal -> configuration.hideVanillaHearts = newVal)
//                                .controller(BooleanControllerBuilder::create)
//                                .build()),
//                Categories.Hud.Vanilla.createConfig(200,
//                        Option.<Boolean>createBuilder()
//                                .name(Text.of("Hide vanilla food"))
//                                .description(OptionDescription.of(Text.of("Hides the vanilla hunger/food bar.")))
//                                .binding(Configuration.defaultHideVanillaFood, () -> configuration.hideVanillaFood, newVal -> configuration.hideVanillaFood = newVal)
//                                .controller(BooleanControllerBuilder::create)
//                                .build()),
//                Categories.Hud.Vanilla.createConfig(300,
//                        Option.<Boolean>createBuilder()
//                                .name(Text.of("Hide vanilla armor"))
//                                .description(OptionDescription.of(Text.of("Hides the vanilla armor indicators.")))
//                                .binding(Configuration.defaultHideVanillaArmor, () -> configuration.hideVanillaArmor, newVal -> configuration.hideVanillaArmor = newVal)
//                                .controller(BooleanControllerBuilder::create)
//                                .build()),
//                Categories.Hud.Vanilla.createConfig(400,
//                        Option.<Boolean>createBuilder()
//                                .name(Text.of("Hide vanilla effects"))
//                                .description(OptionDescription.of(Text.of("Hides the vanilla potion effect icons.")))
//                                .binding(Configuration.defaultHideVanillaEffects, () -> configuration.hideVanillaEffects, newVal -> configuration.hideVanillaEffects = newVal)
//                                .controller(BooleanControllerBuilder::create)
//                                .build()),
//                Categories.Hud.Vanilla.createConfig(500,
//                        Option.<Boolean>createBuilder()
//                                .name(Text.of("Hide vanilla overlay message"))
//                                .description(OptionDescription.of(Text.of("Hides the vanilla action bar overlay (health/mana display).")))
//                                .binding(Configuration.defaultHideVanillaOverlayMessage, () -> configuration.hideVanillaOverlayMessage, newVal -> configuration.hideVanillaOverlayMessage = newVal)
//                                .controller(BooleanControllerBuilder::create)
//                                .build()),
//                Categories.Hud.Vanilla.createConfig(600,
//                        Option.<Boolean>createBuilder()
//                                .name(Text.of("Hide vanilla experience bar"))
//                                .description(OptionDescription.of(Text.of("Hides the vanilla experience bar.")))
//                                .binding(Configuration.defaultHideVanillaExperienceBar, () -> configuration.hideVanillaExperienceBar, newVal -> configuration.hideVanillaExperienceBar = newVal)
//                                .controller(BooleanControllerBuilder::create)
//                                .build()),
//                Categories.Hud.Vanilla.createConfig(700,
//                        Option.<Boolean>createBuilder()
//                                .name(Text.of("Hide vanilla experience level"))
//                                .description(OptionDescription.of(Text.of("Hides the vanilla experience level number.")))
//                                .binding(Configuration.defaultHideVanillaExperienceLevel, () -> configuration.hideVanillaExperienceLevel, newVal -> configuration.hideVanillaExperienceLevel = newVal)
//                                .controller(BooleanControllerBuilder::create)
//                                .build())
        ));

        hudRenderCallbackObservable.subscribe(this);
    }

    private boolean shouldRender() {
        var options = mcClient.getOptions();
        if (options.isEmpty() || options.get().hudHidden) {
            return false;
        }
        return configuration.isHudEnabled;
    }

    @Override
    public void onEvent(HudRenderCallbackObservable.Event result) {
        if (shouldRender()) {
            notifyObservers(new Event(result.drawContext()));
        }
    }

    @Override
    protected Comparator<Observer<HudManager.Event>> getComparator() {
        return (o1, o2) -> {
            if (o1 == o2) return 0;
            // Check if elements are bars (BottomBarElement or TopBarElement)
            boolean o1IsBar = o1 instanceof HudRender(HudElement<?> element1, HudManager hudManager) &&
                    (element1 instanceof ScoreboardReplacementBarElement);
            boolean o2IsBar = o2 instanceof HudRender(HudElement<?> element2, HudManager hudManager) &&
                    (element2 instanceof ScoreboardReplacementBarElement);

            // Bars render first (return -1), other elements render after (return 0)
            if (o1IsBar && !o2IsBar) return -1;
            if (!o1IsBar && o2IsBar) return 1;
            // Both are bars or both are not bars - same layer
            return 0;
        };
    }

    @Override
    public String getJsonSection() {
        return "hudShared";
    }

    @Override
    public HudManager.Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void optionChanged() {

    }

    @Override
    public List<Configurable.OptionPiece> getOption() {
        return options.get();
    }

    public static boolean shouldRenderBar(HudElement<?> element, Configuration config) {
        if (element instanceof ScoreboardReplacementBarElement) {
            return config.getBarScoreboardReplacement();
        }
        return element.isEnabled(); // Non-bar elements always render
    }

    public int getBarHeight() {
        return getConfiguration().getBarScoreboardReplacement() ? barHeight : 0;
    }

    public void setBarHeight(int barHeight) {
        this.barHeight = barHeight;
    }

    public static class Configuration {

        public enum ActiveBarMode implements NameableEnum {
            NONE("None"),
            BOTTOM("Bottom Bar"),
            TOP("Top Bar");

            private final String displayName;

            ActiveBarMode(String displayName) {
                this.displayName = displayName;
            }

            @Override
            public Text getDisplayName() {
                return Text.of(displayName);
            }
        }

        private static final boolean defaultHudEnabled = true;
        private static final ActiveBarMode defaultActiveBarMode = ActiveBarMode.NONE;
        private static final boolean defaultBarScoreboardReplacement = true;
        private static final boolean defaultHideVanillaScoreboard = false;
        private static final boolean defaultHideVanillaHearts = false;
        private static final boolean defaultHideVanillaFood = false;
        private static final boolean defaultHideVanillaArmor = false;
        private static final boolean defaultHideVanillaEffects = false;
        private static final boolean defaultHideVanillaOverlayMessage = false;
        private static final boolean defaultHideVanillaExperienceBar = false;
        private static final boolean defaultHideVanillaExperienceLevel = false;

        @SerialEntry
        public boolean isHudEnabled = defaultHudEnabled;
        @SerialEntry
        public ActiveBarMode activeBarMode = defaultActiveBarMode;
        @SerialEntry
        public boolean barScoreboardReplacement = defaultBarScoreboardReplacement;
        @SerialEntry
        public boolean hideVanillaScoreboard = defaultHideVanillaScoreboard;
        @SerialEntry
        public boolean hideVanillaHearts = defaultHideVanillaHearts;
        @SerialEntry
        public boolean hideVanillaFood = defaultHideVanillaFood;
        @SerialEntry
        public boolean hideVanillaArmor = defaultHideVanillaArmor;
        @SerialEntry
        public boolean hideVanillaEffects = defaultHideVanillaEffects;
        @SerialEntry
        public boolean hideVanillaOverlayMessage = defaultHideVanillaOverlayMessage;
        @SerialEntry
        public boolean hideVanillaExperienceBar = defaultHideVanillaExperienceBar;
        @SerialEntry
        public boolean hideVanillaExperienceLevel = defaultHideVanillaExperienceLevel;

        public boolean isHudEnabled() {
            return isHudEnabled;
        }

        public ActiveBarMode getActiveBarMode() {
            return activeBarMode;
        }

        public boolean getBarScoreboardReplacement() {
            return barScoreboardReplacement && isHudEnabled;
        }

        public boolean isHideVanillaArmor() {
            return hideVanillaArmor;
        }

        public boolean isHideVanillaScoreboard() {
            return hideVanillaScoreboard;
        }

        public boolean isHideVanillaHearts() {
            return hideVanillaHearts;
        }

        public boolean isHideVanillaFood() {
            return hideVanillaFood;
        }

        public boolean isHideVanillaEffects() {
            return hideVanillaEffects;
        }

        public boolean isHideVanillaOverlayMessage() {
            return hideVanillaOverlayMessage;
        }

        public boolean isHideVanillaExperienceBar() {
            return hideVanillaExperienceBar;
        }

        public boolean isHideVanillaExperienceLevel() {
            return hideVanillaExperienceLevel;
        }
    }

    public record HudRender(HudElement<?> element, HudManager hudManager) implements Observer<Event> {

        @Override
        public void onEvent(Event event) {
            // Check if element is a bar and should be rendered based on activeBarMode
            if (!shouldRenderBar(element, hudManager.getConfiguration())) {
                return; // Skip rendering if bar is not active
            }

            if (element.isScalable()) {
                MatrixStack matrixStack = event.drawContext.getMatrices();
                matrixStack.push();
                Vector2f pos = element.getTopLeftCornerPosition();
                matrixStack.translate(pos.x, pos.y, 0);
                matrixStack.scale(element.getScale(), element.getScale(), element.getScale());
                matrixStack.translate(-pos.x, -pos.y, 0);

                element.render(new HudElement.RenderSettings(event.drawContext, 1.0f, false));

                matrixStack.pop();
            } else {
                element.render(new HudElement.RenderSettings(event.drawContext, 1.0f, false));
            }
        }
    }

    public static class Event {
        private final DrawContext drawContext;

        public Event(DrawContext drawContext) {
            this.drawContext = drawContext;
        }
    }
}

