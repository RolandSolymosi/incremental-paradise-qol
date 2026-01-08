package com.incrementalclient.services;

import com.incrementalclient.Main;
import com.incrementalclient.abstractions.HudElement;
import com.incrementalclient.abstractions.ObservableBase;
import com.incrementalclient.hud.internals.HudCustomizationScreen;
import com.incrementalclient.interfaces.Configurable;
import com.incrementalclient.interfaces.Observer;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.internals.events.HudRenderCallbackObservable;
import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.*;

public class HudManager extends ObservableBase<Observer<HudManager.Event>, HudManager.Event> implements Observer<HudRenderCallbackObservable.Event>, Configurable<HudManager.Configuration> {
    private final MinecraftClientAccessor mcClient;

    private final Configuration configuration = new Configuration();

    private final List<OptionPiece> options;

    public HudManager(
            MinecraftClientAccessor mcClient,
            HudRenderCallbackObservable hudRenderCallbackObservable) {
        this.mcClient = mcClient;

        options = List.of(new OptionPiece(
                        "HUD",
                        0,
                        "Hud General",
                        "The common configuration affect all Hud component.",
                        0,
                        Option.<Boolean>createBuilder()
                                .name(Text.of("Toggle HUD on and off"))
                                .description(OptionDescription.of(Text.of("Turn on and off the task HUD.")))
                                .binding(true, () -> configuration.isHudEnabled, newVal -> configuration.isHudEnabled = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build()),
                new OptionPiece(
                        "HUD",
                        0,
                        "Hud General",
                        "",
                        1,
                        ButtonOption.createBuilder()
                                .name(Text.of("HUD Customization"))
                                .description(OptionDescription.of(Text.of("Open the HUD customization screen to position and scale all HUD elements.")))
                                .action((t, o) -> mcClient.setScreen(Main.SERVICE_PROVIDER.getService(HudCustomizationScreen.class)))
                                .build()),
                new OptionPiece(
                        "HUD",
                        1,
                        "Vanilla HUD Elements",
                        "Hide vanilla Minecraft HUD elements to replace them with custom versions.",
                        0,
                        Option.<Boolean>createBuilder()
                                .name(Text.of("Hide vanilla scoreboard"))
                                .description(OptionDescription.of(Text.of("Hides the vanilla scoreboard sidebar.")))
                                .binding(false, () -> configuration.hideVanillaScoreboard, newVal -> configuration.hideVanillaScoreboard = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build()),
                new OptionPiece(
                        "HUD",
                        1,
                        "Vanilla HUD Elements",
                        "Hide vanilla Minecraft HUD elements to replace them with custom versions.",
                        1,
                        Option.<Boolean>createBuilder()
                                .name(Text.of("Hide vanilla hearts"))
                                .description(OptionDescription.of(Text.of("Hides the vanilla health hearts.")))
                                .binding(false, () -> configuration.hideVanillaHearts, newVal -> configuration.hideVanillaHearts = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build()),
                new OptionPiece(
                        "HUD",
                        1,
                        "Vanilla HUD Elements",
                        "Hide vanilla Minecraft HUD elements to replace them with custom versions.",
                        2,
                        Option.<Boolean>createBuilder()
                                .name(Text.of("Hide vanilla food"))
                                .description(OptionDescription.of(Text.of("Hides the vanilla hunger/food bar.")))
                                .binding(false, () -> configuration.hideVanillaFood, newVal -> configuration.hideVanillaFood = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build()),
                new OptionPiece(
                        "HUD",
                        1,
                        "Vanilla HUD Elements",
                        "Hide vanilla Minecraft HUD elements to replace them with custom versions.",
                        3,
                        Option.<Boolean>createBuilder()
                                .name(Text.of("Hide vanilla armor"))
                                .description(OptionDescription.of(Text.of("Hides the vanilla armor indicators.")))
                                .binding(false, () -> configuration.hideVanillaArmor, newVal -> configuration.hideVanillaArmor = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build()),
                new OptionPiece(
                        "HUD",
                        1,
                        "Vanilla HUD Elements",
                        "Hide vanilla Minecraft HUD elements to replace them with custom versions.",
                        4,
                        Option.<Boolean>createBuilder()
                                .name(Text.of("Hide vanilla effects"))
                                .description(OptionDescription.of(Text.of("Hides the vanilla potion effect icons.")))
                                .binding(false, () -> configuration.hideVanillaEffects, newVal -> configuration.hideVanillaEffects = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build()),
                new OptionPiece(
                        "HUD",
                        1,
                        "Vanilla HUD Elements",
                        "Hide vanilla Minecraft HUD elements to replace them with custom versions.",
                        5,
                        Option.<Boolean>createBuilder()
                                .name(Text.of("Hide vanilla overlay message"))
                                .description(OptionDescription.of(Text.of("Hides the vanilla action bar overlay (health/mana display).")))
                                .binding(false, () -> configuration.hideVanillaOverlayMessage, newVal -> configuration.hideVanillaOverlayMessage = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build()),
                new OptionPiece(
                        "HUD",
                        1,
                        "Vanilla HUD Elements",
                        "Hide vanilla Minecraft HUD elements to replace them with custom versions.",
                        6,
                        Option.<Boolean>createBuilder()
                                .name(Text.of("Hide vanilla experience bar"))
                                .description(OptionDescription.of(Text.of("Hides the vanilla experience bar.")))
                                .binding(false, () -> configuration.hideVanillaExperienceBar, newVal -> configuration.hideVanillaExperienceBar = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build()),
                new OptionPiece(
                        "HUD",
                        1,
                        "Vanilla HUD Elements",
                        "Hide vanilla Minecraft HUD elements to replace them with custom versions.",
                        7,
                        Option.<Boolean>createBuilder()
                                .name(Text.of("Hide vanilla experience level"))
                                .description(OptionDescription.of(Text.of("Hides the vanilla experience level number.")))
                                .binding(false, () -> configuration.hideVanillaExperienceLevel, newVal -> configuration.hideVanillaExperienceLevel = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build())
        );

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
            if (o1 instanceof HudRender(HudElement<?> element) && element.isInBottomBarGroup()) {
                return -1;
            }
            if (o2 instanceof HudRender(HudElement<?> element) && element.isInBottomBarGroup()) {
                return 1;
            }
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
        return options;
    }

    public static class Configuration {

        @SerialEntry
        public boolean isHudEnabled = true;
        @SerialEntry
        public boolean hideVanillaScoreboard = false;
        @SerialEntry
        public boolean hideVanillaHearts = false;
        @SerialEntry
        public boolean hideVanillaFood = false;
        @SerialEntry
        public boolean hideVanillaArmor = false;
        @SerialEntry
        public boolean hideVanillaEffects = false;
        @SerialEntry
        public boolean hideVanillaOverlayMessage = false;
        @SerialEntry
        public boolean hideVanillaExperienceBar = false;
        @SerialEntry
        public boolean hideVanillaExperienceLevel = false;

        public boolean isHudEnabled() {
            return isHudEnabled;
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

    public record HudRender(HudElement<?> element) implements Observer<Event> {

        @Override
        public void onEvent(Event event) {
            if (element.isScalable() && !element.isInBottomBarGroup()) {
                MatrixStack matrixStack = event.drawContext.getMatrices();
                matrixStack.push();
                matrixStack.scale(element.getScale(), element.getScale(), element.getScale());

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

