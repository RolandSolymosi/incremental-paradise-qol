package com.incrementalclient.services.skillCooldowns;

import com.incrementalclient.common.utils.DurationEstimator;
import com.incrementalclient.common.utils.Utils;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Optional;

public class PeashooterCooldown implements SkillCooldown {
    private final String skillName;

    private int numActive = 0;
    private int maxActive = 1;

    private final DurationEstimator cooldownEstimator = new DurationEstimator();

    public PeashooterCooldown(String skillName) {
        this.skillName = skillName;
    }

    @Override
    public String getSkillName() {
        return skillName;
    }

    @Override
    public @NotNull SkillAction onActivate() {
        numActive++;
        if(maxActive < numActive) {
            maxActive = numActive;
        }

        // Dropping the item could force-spawn another (can't tell if it would), so don't try
        return SkillAction.NONE;
    }

    @Override
    public @NotNull SkillAction onSkillEnd(boolean worldChange) {
        // funny enough won't be called on a world change
        numActive--;

        // Dropping the item could force-spawn another (can't tell if) so don't try
        return SkillAction.NONE;
    }

    @Override
    public void onCooldown(double cooldownTime) {
        cooldownEstimator.createStartIfNotExist();
        cooldownEstimator.estimateStopsIn(Duration.ofMillis((long) (1000 * cooldownTime)));
    }

    @Override
    public void onReady() {
        cooldownEstimator.stop();
        cooldownEstimator.clearStart();
    }

    @Override
    public Text getHudTextLine() {
        if(getNumActive() == 0) {
            return Text.literal(Utils.formatDurationSeconds(cooldownEstimator.getEstimatedRemainingDuration()))
                    .formatted(Formatting.GOLD);
        }
        else {
            return Text.literal(String.valueOf(numActive))
                    .append("/")
                    .append(String.valueOf(maxActive));
        }
    }

    @Override
    public Optional<Float> getCooldownFraction() {
        if(getNumActive() == 0) {
            return Optional.of(cooldownEstimator.getEstimatedRemainingFraction());
        }
        else {
            float numerator = getNumActive();
            float denominator = maxActive;
            return Optional.of(numerator / denominator);
        }
    }

    @Override
    public void onUseRecharged() {
        SkillCooldown.super.onUseRecharged();
    }

    private int getNumActive() {
        // something weird happened once and i dont care to fix it
        if(numActive < 0) {
            numActive = 0;
        }
        return numActive;
    }
}
