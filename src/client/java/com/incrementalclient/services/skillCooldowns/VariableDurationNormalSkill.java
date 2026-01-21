package com.incrementalclient.services.skillCooldowns;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Optional;

/**
 * This represents a skill that has an effect over a duration, but a frequently changing duration.
 * Examples are Buzzing Assault, Bee Storm, and (kind of) Swarm Surfer.
 * (Swarm Surfer is cancelled if you crouch during it.)
 */
public class VariableDurationNormalSkill extends NormalSkillCooldown {
    // copy() used to make it immutable
    private final Text ACTIVE_TEXT_LINE = Text.literal("Active!").formatted(Formatting.GREEN).copy();

    public VariableDurationNormalSkill(String skillName) {
        super(skillName);
    }

    @Override
    protected Text getActiveTextLine() {
        return ACTIVE_TEXT_LINE;
    }

    @Override
    public Optional<Float> getActiveCooldownFraction() {
        // Variable duration - no point estimating
        return Optional.empty();
    }

    @Override
    public SkillAction onActivate() {
        // When activated: Enters the "Active" state (ie the skill is running)
        onEnterActiveDuration();

        return SkillAction.NONE;
    }

    @Override
    public SkillAction onSkillEnd(boolean worldChange) {
        // When over: Goes on cooldown.
        onEnterCooldown();

        // Refresh the cooldown
        return SkillAction.DROP_SKILL_ITEM;
    }
}
