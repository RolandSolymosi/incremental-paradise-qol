package com.incrementalclient.services.skillCooldowns;

import com.incrementalclient.services.CommandHandler;
import com.incrementalclient.services.InteractionScheduler;

/**
 * The main function of this class is to open the /skills menu and navigate through it.
 * This could theoretically have been a function inside SkillCooldownMonitor, but to reduce the complexity
 * of that class, this class was separated over.
 * This makes more sense anyway, since (due to SkillCooldownMonitor#onScreenArrived) the ONLY information
 * needed between the two classes is for this class to know when to run its interaction.
 * No shared variables, no shared functions, just runSkillsInteraction() is the only communication between the two.
 */
public class SkillCooldownInteractionManager {

    private final InteractionScheduler<Void> interactionScheduler;
    private final CommandHandler commandHandler;

    public SkillCooldownInteractionManager(
            InteractionScheduler<Void> interactionScheduler,
            CommandHandler commandHandler
    ) {
        this.interactionScheduler = interactionScheduler;
        this.commandHandler = commandHandler;
    }

    public void runSkillsInteraction() {
        // TODO
    }

}
