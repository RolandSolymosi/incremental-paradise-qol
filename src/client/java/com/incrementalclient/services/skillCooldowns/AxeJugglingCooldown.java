package com.incrementalclient.services.skillCooldowns;

import com.incrementalclient.common.utils.DurationEstimator;
import com.incrementalclient.common.utils.Utils;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.time.Duration;
import java.util.Optional;

/**
 * Notes: Axe juggling is similar to Variable Duration, with the notable exception that it DOES NOT announce
 * when the skill is over! You can tell from the audio cue, but I don't want to hijack into the audio manager
 * just for something that is very easy to have a false positive.
 */
public class AxeJugglingCooldown implements SkillCooldown {
    // will always be "Axe Juggling" but leaving it like this for convention
    private final String skillName;
    private STATE state;

    private final DurationEstimator cooldownEstimator = new DurationEstimator();

    public AxeJugglingCooldown(String skillName) {
        this.skillName = skillName;
        this.state = STATE.READY;
    }

    @Override
    public String getSkillName() {
        return skillName;
    }

    @Override
    public SkillAction onActivate() {
        // Failsafe just to be double-sure.
        cooldownEstimator.clearStart();

        this.state = STATE.ACTIVE;
        // Cooldown isn't available until you fail one, not when you activate one.
        return SkillAction.NONE;
    }

    @Override
    public SkillAction onSkillEnd(boolean worldChange) {
        // Doesn't get called for axe juggling, so ignorable.
        return SkillAction.NONE;
    }

    @Override
    public void onCooldown(double cooldownTime) {
        // this is why clearStart is used so much.
        cooldownEstimator.createStartIfNotExist();
        // doesnt accept non-integers, but rounding to millis is close enough
        cooldownEstimator.estimateStopsIn(Duration.ofMillis((long) (1000 * cooldownTime)));

        this.state = STATE.COOLDOWN;
    }

    @Override
    public void onReady() {
        cooldownEstimator.stop();
        // do not reuse this start for the next one
        cooldownEstimator.clearStart();

        this.state = STATE.READY;
    }

    @Override
    public Text getHudTextLine() {
        return switch (this.state) {
            case ACTIVE -> Text.of("Active?");
            case COOLDOWN -> Text.of(getCooldownTextLine());
            case READY -> Text.of(Text.literal("Ready!").formatted(Formatting.GOLD));
        };
    }

    private Text getCooldownTextLine() {
        var remainingDuration = cooldownEstimator.getEstimatedRemainingDuration();
        String out;
        if(remainingDuration == null) {
            out = "Cooldown...";
        }
        else {
            out = Utils.formatDurationSeconds(remainingDuration);
        }
        return Text.literal(out).formatted(Formatting.RED);
    }

    @Override
    public Optional<Float> getCooldownFraction() {
        return Optional.empty();
    }

    // Same enum as NormalSkillCooldown
    // but I don't want to make that one non-private
    // and this is a really different skill regardless
    private enum STATE {
        ACTIVE,
        COOLDOWN,
        READY
    }
}
