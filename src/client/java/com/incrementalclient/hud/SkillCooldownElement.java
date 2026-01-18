package com.incrementalclient.hud;

import com.google.common.base.Suppliers;
import com.incrementalclient.abstractions.HudElement;
import com.incrementalclient.abstractions.TextListHudElement;
import com.incrementalclient.common.data.skills.SkillCategory;
import com.incrementalclient.common.utils.Vector2f;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.services.HudManager;
import com.incrementalclient.services.skillCooldowns.SkillCooldownMonitor;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SkillCooldownElement extends TextListHudElement<SkillCooldownElement.Configuration> {

    // TODO: This is not tested.

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
                // TODO: This needs everything that ItemTargetTrackerElement got.
        ));

        // Force update filterMessages value
        this.skillCooldownMonitor.setFilterChat(getConfiguration().filterMessages);
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
        return super.getOption();
    }

    @Override
    public void optionChanged() {
        super.optionChanged();
        this.skillCooldownMonitor.setFilterChat(getConfiguration().filterMessages);
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
