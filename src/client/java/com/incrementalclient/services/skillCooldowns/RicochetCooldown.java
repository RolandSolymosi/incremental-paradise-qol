package com.incrementalclient.services.skillCooldowns;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.Optional;

// skillName should be constant ("Ricochet") but the SkillCooldownMonitor expects a one-arg constructor
// so to keep things consistent, it'll be getting that. (Note: Landscaping is the same form, so this could be
// reused for landscaping too, theoretically. The problem is that landscaping doesn't say when it activates, and
// nobody really cares about that one anyway.)
public class RicochetCooldown implements SkillCooldown {

    private final String skillName;

    private int numActive = 0;
    private int maxActive = 0;

    public RicochetCooldown(String skillName) {
        this.skillName = skillName;
    }

    @Override
    public void onActivate() {
        numActive++;
        if(maxActive < numActive) {
            maxActive = numActive;
        }
    }

    @Override
    public void onSkillEnd(boolean worldChange) {
        numActive--;
    }

    @Override
    public void onCooldown(double cooldownTime) {
        // Ricochet doesn't go on cooldown.
    }

    @Override
    public void onReady() {
        // "Ricochet is ready" is not a message ("Ricochet is over!" is the equivalent)
    }

    @Override
    public Text getHudTextLine() {
        return Text.literal(skillName)
                .append(": ")
                .append(String.valueOf(numActive))
                .append("/")
                .append(String.valueOf(maxActive));
    }

    @Override
    public Optional<Float> getCooldownFraction() {
        float numerator = numActive;
        float denominator = maxActive;
        return Optional.of(numerator/denominator);
    }
}
