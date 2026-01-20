package com.incrementalclient.services.skillCooldowns;

import com.incrementalclient.common.utils.DurationEstimator;
import com.incrementalclient.common.utils.Utils;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.time.Duration;
import java.util.Optional;

public abstract class NormalSkillCooldown implements SkillCooldown {

    protected final String skillName;
    private STATE state;
    private final DurationEstimator cooldownEstimator = new DurationEstimator();

    // copy() is used here to make it immutable.
    private final Text READY_STATE_TEXT = Text.literal("Ready!").formatted(Formatting.GOLD).copy();
    private final Text UNKNOWN_COOLDOWN_TEXT = Text.literal("On cooldown...").formatted(Formatting.RED).copy();
    // Unknown state is available to subclasses specifically for InstantActiveSkillCooldown
    protected final Text UNKNOWN_STATE_TEXT = Text.literal("Unknown skill state").formatted(Formatting.RED).copy();

    /*
    Most skill cooldowns are similar: You activate, something happens, then it goes on cooldown.
    Note that there are special cases like Ricochet which are NOT under this class, hence why this is NOT
    merged with its superclass.
    This class covers the cases which aren't "extraordinarily special" like that one.
     */
    public NormalSkillCooldown(String skillName) {
        this.skillName = skillName;
        this.state = STATE.UNKNOWN;
    }

    // Some skills are active for a duration (buzzing assault, spoon bender). This signals to NormalSkillCooldown
    // that that is the case.
    // Note: Not all skills will call this function! For example Condensed Strike and Rhino Charge won't use this!
    protected void onEnterActiveDuration() {
        this.state = STATE.ACTIVE;
    }

    // For some skills, this is done when the skill is pressed (see: rhino charge, enters cd immediately)
    // For other skills, this is done after some time (see: spoon bender, only enters cd later)
    // It's up to the subclass to call this function when it's needed.
    protected void onEnterCooldown() {
        this.state = STATE.COOLDOWN;
        cooldownEstimator.start();
    }

    @Override
    public Text getHudTextLine() {
        var skillInfo = switch (this.state) {
            case READY -> READY_STATE_TEXT;
            case ACTIVE -> getActiveTextLine();
            case COOLDOWN -> getCooldownTextLine();
            default -> UNKNOWN_STATE_TEXT;
        };

        return Text.literal(this.skillName).append(": ").append(skillInfo);
    }

    protected abstract Text getActiveTextLine();

    protected Text getCooldownTextLine() {
        var remainingDuration = cooldownEstimator.getEstimatedRemainingDuration();
        if(remainingDuration == null) {
            return UNKNOWN_COOLDOWN_TEXT;
        }
        else {
            return Text.literal("On cooldown for ")
                    .append(Utils.formatDurationSeconds(remainingDuration))
                    .append(" seconds")
                    .formatted(Formatting.RED);
        }
    }

    @Override
    public void onReady() {
        this.state = STATE.READY;
        cooldownEstimator.stop();
    }

    @Override
    public void onCooldown(double cooldownTime) {
        // note that ofSeconds would require casting to long, which would cut off decimal points
        // so doing this lets us keep 3 decimal points, and I don't think we have precision past millis.
        cooldownEstimator.estimateStopsIn(Duration.ofMillis((long) (cooldownTime * 1000)));
    }

    @Override
    public Optional<Float> getCooldownFraction() {
        return switch (this.state) {
            case UNKNOWN, READY -> Optional.empty();
            case ACTIVE -> getActiveCooldownFraction();
            case COOLDOWN -> getCooldownStateCooldownFraction();
        };
    }

    // Effectively getCooldownFraction (see docs from SkillCooldown) but only for the active state of this
    public abstract Optional<Float> getActiveCooldownFraction();

    // getCooldownFraction for specifically cooldownState
    // getCooldownCooldownFraction was confusing to me
    public Optional<Float> getCooldownStateCooldownFraction() {
        return Optional.of(cooldownEstimator.getEstimatedRemainingFraction());
    }

    private enum STATE {
        UNKNOWN,
        ACTIVE,
        COOLDOWN,
        READY
    }
}
