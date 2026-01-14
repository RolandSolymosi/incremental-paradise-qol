package com.incrementalclient.services.skillCooldowns;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

// skillName should be constant ("Ricochet") but skillName is still used throughout the class
// to keep things consistent (esp with more SkillCoooldown classes later on)
public class RicochetCooldown extends SkillCooldown {

    private int numActive = 0;
    private int maxActive = 0;

    public RicochetCooldown(String skillName) {
        super(skillName);
    }

    @Override
    public void onActivate() {
        numActive++;
        if(maxActive < numActive) {
            maxActive = numActive;
        }
    }

    @Override
    public void onSkillEnd() {
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
    public void onChangeWorld() {

    }

    @Override
    public Text getHudTextLine() {
        return Text.literal(skillName)
                .append(": ")
                .append(String.valueOf(numActive))
                .append("/")
                .append(String.valueOf(maxActive));
    }
}
