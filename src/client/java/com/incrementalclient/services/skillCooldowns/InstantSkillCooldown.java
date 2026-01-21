package com.incrementalclient.services.skillCooldowns;

import net.minecraft.text.Text;

import java.util.Optional;
import java.util.function.Function;

/**
 * This represents a skill that has an "instantaneous" effect.
 * Examples are Rhino Charge, Buzzing Assault, and Explosive Arrow.
 */
public class InstantSkillCooldown extends NormalSkillCooldown {

    private final SkillAction activationAction;

    // Initially, all InstantSkills were like this
    // However, feature creep + code reuse efficiency combined to make it so some things which aren't instant
    // use the Instant's code, but need a different activationAction
    // Example: Swarm Surfer behaves almost identically to InstantSkillCooldown, but it doesn't go on cooldown instantly.
    // It goes on cooldown *later* instead. When? Can't tell, there's no message for when it ends. So it's not a
    // VariableDurationNormalSkill.
    public InstantSkillCooldown(String skillName) {
        this(skillName, SkillAction.DROP_SKILL_ITEM);
    }

    private InstantSkillCooldown(String skillName, SkillAction activationAction) {
        super(skillName);
        this.activationAction = activationAction;
    }

    public static Function<String, InstantSkillCooldown> builder(SkillAction action) {
        return (String skillName) -> new InstantSkillCooldown(skillName, action);
    }

    @Override
    protected Text getActiveTextLine() {
        // Shouldn't be entering the active state as an instant skill.
        return super.UNKNOWN_STATE_TEXT;
    }

    @Override
    public Optional<Float> getActiveCooldownFraction() {
        // Shouldn't be entering the active state as an instant skill.
        return Optional.empty();
    }

    @Override
    public SkillAction onActivate() {
        // Instant skills go on cooldown immediately.
        onEnterCooldown();
        // Action when activated: activationAction
        return activationAction;
    }

    @Override
    public SkillAction onSkillEnd(boolean worldChange) {
        // Shouldn't be possible; instant skills don't announce that they're "over".
        // Still, putting a fallback in.
        onEnterCooldown();
        return SkillAction.NONE;
    }
}
