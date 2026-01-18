package com.incrementalclient.services.skillCooldowns;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * This represents a skill that we can reasonably assume will have the same duration every time.
 * Note that this isn't always true, as skill durations can be affected by modifiers and changing worlds,
 * but that's why the duration estimator in here is an estimate instead of being exact.
 * Examples are Spoon Bender and Harvester.
 */
public class FixedDurationNormalSkill extends NormalSkillCooldown {
    // TODO: Until we start estimating the duration, this is functionally the same as VariableDurationNormalSkill.
    //   Skill duration estimation is possible with onActivate and onSkillEnd.

    public FixedDurationNormalSkill(String skillName) {
        super(skillName);
    }

    @Override
    protected Text getActiveTextLine() {
        // TODO: Estimate the remaining active duration
        return Text.literal("Active!").formatted(Formatting.GREEN).copy();
    }

    @Override
    public void onActivate() {
        // When activated: Enters the "Active" state (ie the skill is running)
        onEnterActiveDuration();
    }

    @Override
    public void onSkillEnd() {
        // When over: Goes on cooldown.
        onEnterCooldown();
    }

    @Override
    public void onChangeWorld() {

    }
}
