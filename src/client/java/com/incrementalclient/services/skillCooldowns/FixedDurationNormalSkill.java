package com.incrementalclient.services.skillCooldowns;

import com.incrementalclient.common.utils.DurationEstimator;
import com.incrementalclient.common.utils.Utils;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * This represents a skill that we can reasonably assume will have the same duration every time.
 * Note that this isn't always true, as skill durations can be affected by modifiers and changing worlds,
 * but that's why the duration estimator in here is an estimate instead of being exact.
 * Examples are Spoon Bender and Harvester.
 */
public class FixedDurationNormalSkill extends NormalSkillCooldown {
    private DurationEstimator durationEstimator = new DurationEstimator();

    public FixedDurationNormalSkill(String skillName) {
        super(skillName);
    }

    @Override
    protected Text getActiveTextLine() {
        var remainingDuration = durationEstimator.getEstimatedRemainingDuration();
        if(remainingDuration == null) {
            return Text.literal("Active!").formatted(Formatting.GREEN).copy();
        }
        else {
            return Text.literal("Active for " + Utils.formatDurationSeconds(remainingDuration) + " seconds")
                    .formatted(Formatting.GREEN);
        }
    }

    @Override
    public void onActivate() {
        // When activated: Enters the "Active" state (ie the skill is running)
        onEnterActiveDuration();
        durationEstimator.start();
    }

    @Override
    public void onSkillEnd(boolean worldChange) {
        // When over: Goes on cooldown.
        onEnterCooldown();
        if(worldChange) {
            // start-end measurement now has a "corrupted end"
            // so don't bother
            durationEstimator.clearStart();
        }
        else {
            // start-end measurement is accurate
            durationEstimator.stop();
        }
    }
}
