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
    private STATE state = STATE.UNKNOWN;

    private final DurationEstimator cooldownEstimator = new DurationEstimator();

    public AxeJugglingCooldown(String skillName) {
        this.skillName = skillName;
    }

    @Override
    public void onActivate() {
        // Failsafe just to be double-sure.
        cooldownEstimator.clearStart();

        this.state = STATE.ACTIVE;
    }

    @Override
    public void onSkillEnd(boolean worldChange) {
        // Doesn't get called for axe juggling, so ignorable.
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
        var out = Text.literal(this.skillName).append(": ");
        return switch (this.state) {
            case UNKNOWN -> out.append("Unknown");
            case ACTIVE -> out.append("Active?");
            case COOLDOWN -> out.append(getCooldownTextLine());
            case READY -> out.append(Text.literal("Ready!").formatted(Formatting.GOLD));
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
        UNKNOWN,
        ACTIVE,
        COOLDOWN,
        READY
    }
}
