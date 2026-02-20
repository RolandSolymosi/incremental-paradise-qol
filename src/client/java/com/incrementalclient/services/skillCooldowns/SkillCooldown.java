package com.incrementalclient.services.skillCooldowns;

import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * The basic form for all skill cooldowns as used by SkillCooldownMonitor.
 * MOST skill cooldowns fall into one of two forms:
 * - Activates instantly, then goes on cooldown (condensed strike)
 * - Activates, is available for a duration or number of activations, then goes on cooldown. (Buzzing assault, bee storm.)
 * However, there ARE some skill cooldowns which do not follow this pattern whatsoever, hence why this abstract class
 * exists instead of all skills being under the DefaultSkillCooldown class.
 * The best example of this is Ricochet, which works in a completely different manner.
 */
public interface SkillCooldown {
    // Note: Some more examples might be needed.
    // Note: There is no example for "ability already active", because:
    // 🔧 This ability is already active.
    // The skill's name is not in the message.

    // Examples:
    // "Ricochet"
    // "Condensed Strike"
    // "Bee Storm"
    // etc.
    String getSkillName();

    // Examples:
    // 🔧 Activated Ricochet!
    // 🔧 Activated Condensed Strike!
    // 🔧 Activated Bee Storm!
    @NotNull SkillAction onActivate();

    // Examples:
    // 🔧 Ricochet is over!
    // The boolean is for whether the skill is ending because of a world change (warp 1, warp 2, enter bossworld, etc)
    // or because of some other reason (usually ending because the duration ended)
    @NotNull SkillAction onSkillEnd(boolean worldChange);

    // Examples:
    // 🔧 Condensed Strike is on cooldown for another 16.2 seconds.
    // Note if you're wondering why this needs to exist: Weather events can make the real cooldown time different to
    //   any previous measurements we've taken (ex from reading user skills)
    void onCooldown(double cooldownTime);

    // Examples:
    // 🔧 Condensed Strike is ready to use.
    void onReady();

    // There is ONE example of this.
    // 🔧 A use of Pea Shooter has recharged.
    default void onUseRecharged() {

    }

    // How this SkillCooldown will appear in text on the HUD.
    Text getHudTextLine();

    /**
     * @return The active cooldown fraction that this skill's related item should have. If an empty optional
     * is returned, then the cooldown override is disabled (cleared) and the default functionality is restored.
     */
    Optional<Float> getCooldownFraction();

    /**
     * The action to take after a specific function is called. The primary usecase for this is to drop a skill item
     * right as the skill ends, to forcibly refresh the cooldown so it can be looked at.
     */
    enum SkillAction {
        NONE, DROP_SKILL_ITEM;
    }
}
