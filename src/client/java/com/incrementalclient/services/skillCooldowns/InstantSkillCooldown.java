package com.incrementalclient.services.skillCooldowns;

import net.minecraft.text.Text;

import java.util.Optional;

/**
 * This represents a skill that has an "instantaneous" effect.
 * Examples are Rhino Charge, Buzzing Assault, and Explosive Arrow.
 */
public class InstantSkillCooldown extends NormalSkillCooldown {
    public InstantSkillCooldown(String skillName) {
        super(skillName);
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
    public void onActivate() {
        // Instant skills go on cooldown immediately.
        onEnterCooldown();
    }

    @Override
    public void onSkillEnd(boolean worldChange) {
        // Shouldn't be possible; instant skills don't announce that they're "over".
        // Still, putting a fallback in.
        onEnterCooldown();
    }
}
