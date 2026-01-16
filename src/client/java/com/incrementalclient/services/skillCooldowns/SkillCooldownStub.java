package com.incrementalclient.services.skillCooldowns;

import com.incrementalclient.Main;
import net.minecraft.text.Text;

/**
 * This is a stub class, purely so that SkillCooldownMonitor doesn't have to handle "null" type skill cooldowns.
 */
public class SkillCooldownStub extends SkillCooldown {

    public SkillCooldownStub(String skillName) {
        super(skillName);
    }

    @Override
    public void onActivate() {

    }

    @Override
    public void onSkillEnd() {

    }

    @Override
    public void onCooldown(double cooldownTime) {

    }

    @Override
    public void onReady() {

    }

    @Override
    public void onChangeWorld() {

    }

    @Override
    public Text getHudTextLine() {
        return Text.literal(this.skillName).append(": Unimplemented");
    }
}
