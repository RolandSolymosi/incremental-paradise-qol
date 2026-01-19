package com.incrementalclient.hud;

import com.google.common.base.Suppliers;
import com.incrementalclient.abstractions.HudElement;
import com.incrementalclient.abstractions.TextListHudElement;
import com.incrementalclient.common.data.skills.SkillCategory;
import com.incrementalclient.common.utils.Vector2f;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.services.HudManager;
import com.incrementalclient.services.skillCooldowns.SkillCooldownMonitor;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SkillCooldownElement extends TextListHudElement<SkillCooldownElement.Configuration> {

    private final Configuration configuration = new Configuration();

    private final SkillCooldownMonitor skillCooldownMonitor;

    private final Supplier<List<OptionPiece>> options;

    // Mapping from a category to an associated emoji for it.
    // (A string is used instead of Character to make a default/unimplemented response of "" legal
    private static final Map<SkillCategory, String> emojiMap = Map.ofEntries(
            Map.entry(SkillCategory.Combat, "\uD83D\uDDE1"),
            Map.entry(SkillCategory.Mining, "⛏"),
            Map.entry(SkillCategory.Foraging, "\uD83E\uDE93"),
            Map.entry(SkillCategory.Farming, "\uD83C\uDF3E"), // this isnt farmtouch or farmluck emoji but both kinda suck
            Map.entry(SkillCategory.SpearFishing, "\uD83D\uDC1F"),
            Map.entry(SkillCategory.Sharpshooting, "\uD83C\uDFF9"),
            Map.entry(SkillCategory.Excavation, "\uD83E\uDD96")
    );

    public SkillCooldownElement(
            MinecraftClientAccessor uiAccessor,
            HudManager hudManager,
            SkillCooldownMonitor skillCooldownMonitor
    ) {
        super(uiAccessor, hudManager);
        this.skillCooldownMonitor = skillCooldownMonitor;
        this.anchorPoint = new Vector2f(10, 10);

        options = Suppliers.memoize(() -> List.of(
                Categories.Hud.SkillCooldown.createConfig(0,
                        Option.<Boolean>createBuilder()
                                .name(Text.of("Toggle Item Target HUD on and off"))
                                .description(OptionDescription.of(Text.of("Turn on and off the skill cooldown tracker HUD.")))
                                .binding(configuration.isHudEnabled, () -> configuration.isHudEnabled, newVal -> configuration.isHudEnabled = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build()
                ),
                Categories.Hud.SkillCooldown.createConfig(1,
                        Option.<Boolean>createBuilder()
                                .name(Text.of("Filter message"))
                                .description(OptionDescription.of(Text.of("Toggle if skill cooldown tracker should filter message or not. If the skill cooldown tracker is disabled, it won't filter messages anyway.")))
                                .binding(configuration.filterMessages, () -> configuration.filterMessages, newVal -> configuration.filterMessages = newVal)
                                .controller(BooleanControllerBuilder::create)
                                .build()
                ),
                Categories.Hud.SkillCooldown.createConfig(2,
                        Option.<Double>createBuilder()
                                .name(Text.of("Consumable HUD background opacity"))
                                .description(OptionDescription.of(Text.of("Set the opacity of the consumable HUD background.")))
                                .binding(configuration.hudBackgroundOpacity, () -> configuration.hudBackgroundOpacity, newVal -> configuration.hudBackgroundOpacity = newVal)
                                .controller(o -> DoubleSliderControllerBuilder.create(o).step(0.01).range(0.0, 1.0))
                                .build()
                )
        ));

        // Force update filterMessages value
        updateMonitor();
    }

    @Override
    protected List<Text> getTextsToRender(boolean editMode) {
        // TODO: Are there other timers this should handle too?
        //   The only one that springs to mind is Mouse Trap, which nobody really cares about.
        //   Theoretically, there's also slimefused/glowfused rings, but it's hard to tell when their timer resets.
        // Unrelated question: Why does ItemTargetTrackerElement use the collector version instead of Stream.toList()?
        // I did it here just to copy but I don't know why it was done.
        return this.skillCooldownMonitor.getCurrentlyActiveSkills().keySet().stream()
                .map(this::renderSkill)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private Text renderSkill(SkillCategory category) {
        var cooldown = this.skillCooldownMonitor.getCurrentlyActiveSkills().get(category);
        if(cooldown == null) {
            // shouldn't be possible, but just as a failsafe.
            return null;
        }

        return Text.literal(emojiMap.get(category))
                .append(cooldown.getHudTextLine());
    }

    @Override
    protected int getBackgroundOpacity() {
        return (int) (getConfiguration().hudBackgroundOpacity * 255);
    }

    @Override
    public boolean isElementEnabled() {
        return getConfiguration().isHudEnabled;
    }

    @Override
    public Vector2f getAnchorPoint() {
        return anchorPoint;
    }

    @Override
    public String getDisplayName() {
        return "Skill Cooldown Timers";
    }

    @Override
    public String getJsonSection() {
        return "skillCooldownHud";
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
    public void optionChanged() {
        super.optionChanged();
        updateMonitor();
    }

    private void updateMonitor() {
        this.skillCooldownMonitor.setFilterChat(getConfiguration().isHudEnabled && getConfiguration().filterMessages);
    }

    public static class Configuration extends HudElement.ConfigurationBase {
        @SerialEntry
        public boolean isHudEnabled = true;
        @SerialEntry
        public boolean filterMessages = true;
        @SerialEntry
        public double hudBackgroundOpacity = 0.3;
    }
}
