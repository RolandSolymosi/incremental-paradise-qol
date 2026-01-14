package com.incrementalclient.services.skillCooldowns;

import net.minecraft.text.Text;

/**
 * The basic form for all skill cooldowns as used by SkillCooldownMonitor.
 * MOST skill cooldowns fall into one of two forms:
 * - Activates instantly, then goes on cooldown (condensed strike)
 * - Activates, is available for a duration or number of activations, then goes on cooldown. (Buzzing assault, bee storm.)
 * However, there ARE some skill cooldowns which do not follow this pattern whatsoever, hence why this abstract class
 * exists instead of all skills being under the DefaultSkillCooldown class.
 * The best example of this is Ricochet, which works in a completely different manner.
 */
public abstract class SkillCooldown {
    // Note: Some more examples might be needed.
    // Note: There is no example for "ability already active", because:
    // 🔧 This ability is already active.
    // The skill's name is not in the message.

    protected final String skillName;

    public SkillCooldown(String skillName) {
        this.skillName = skillName;
    }

    // Examples:
    // 🔧 Activated Ricochet!
    // 🔧 Activated Condensed Strike!
    // 🔧 Activated Bee Storm!
    public abstract void onActivate();

    // Examples:
    // 🔧 Ricochet is over!
    public abstract void onSkillEnd();

    // Examples:
    // 🔧 Condensed Strike is on cooldown for another 16.2 seconds.
    // Note if you're wondering why this needs to exist: Weather events can make the real cooldown time different to
    //   any previous measurements we've taken (ex from reading user skills)
    public abstract void onCooldown(double cooldownTime);

    // Examples:
    // 🔧 Condensed Strike is ready to use.
    public abstract void onReady();

    // Note about this:
    // - For skills with a duration, it ends the duration (buzzing assault, bee storm)
    // - However, it does NOT reset the cooldown!
    // See the large note in SkillCooldownMonitor.onWorldChange() about whether this is a good idea or not.
    public abstract void onChangeWorld();

    // How this SkillCooldown will appear in text on the HUD.
    public abstract Text getHudTextLine();
}
