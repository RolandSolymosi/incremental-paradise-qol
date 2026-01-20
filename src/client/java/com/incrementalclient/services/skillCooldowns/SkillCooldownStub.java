package com.incrementalclient.services.skillCooldowns;

import com.incrementalclient.Main;
import net.minecraft.text.Text;

import java.util.Optional;

/**
 * This is a stub class, purely so that SkillCooldownMonitor doesn't have to handle "null" type skill cooldowns.
 */
public class SkillCooldownStub implements SkillCooldown {

    private final String skillName;

    public SkillCooldownStub(String skillName) {
        this.skillName = skillName;
    }

    @Override
    public String getSkillName() {
        return skillName;
    }

    @Override
    public void onActivate() {

    }

    @Override
    public void onSkillEnd(boolean worldChange) {

    }

    @Override
    public void onCooldown(double cooldownTime) {

    }

    @Override
    public void onReady() {

    }

    @Override
    public Text getHudTextLine() {
        return Text.of("Unimplemented");
    }

    @Override
    public Optional<Float> getCooldownFraction() {
        return Optional.empty();
    }
}
