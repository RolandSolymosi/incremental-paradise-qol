package com.incrementalclient.services.skillCooldowns;

import com.incrementalclient.common.data.skills.Skill;
import com.incrementalclient.common.utils.NumberParser;
import com.incrementalclient.interfaces.Observer;
import com.incrementalclient.services.ChatHandler;
import com.incrementalclient.services.WorldMonitor;
import net.minecraft.network.message.SentMessage;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkillCooldownMonitor implements Observer<ChatHandler.Event> {
    // TODO: Hud element for SkillCooldown instances. Not here, would be a different class in hud package.
    // Note the startPiece includes a spacebar.
    private static final String startPiece = "\uD83D\uDD27 ";

    private final ChatHandler chatHandler;

    // TODO: After writing "SkillCooldown" too many times I started getting confused.
    //  Maybe a better variable name would be good?
    // Mapping from SkillName -> SkillCooldown instance
    private final Map<String, SkillCooldown> skillCooldowns = new HashMap<>();

    // Each function is a SkillCooldown constructor
    // Mapping from SkillName -> SkillCooldown constructor for that skill
    // (It is expected that many constructors will appear many times, ex buzzing assault and beestorm)
    private final Map<String, Function<String, SkillCooldown>> skillCooldownConstructors = Map.ofEntries(
            Map.entry("Ricochet", RicochetCooldown::new)
    );

    // Note: I anticipate regex might not be the best solution here, since for two regexes we put a wildcard
    // at the start followed by two lines of text
    private static final Pattern skillActivated = Pattern.compile(startPiece + "Activated (?<skill>.+)!");
    private static final Pattern skillEnded = Pattern.compile(startPiece + "(?<skill>.+) is over!");
    private static final Pattern skillOnCooldown = Pattern.compile(startPiece + "(?<skill>.+) is on cooldown for another (?<cooldown>" + NumberParser.NumberPattern.pattern() + ") seconds!");
    private static final Pattern skillReady = Pattern.compile(startPiece + "(?<skill>.+) is ready to use.");

    public SkillCooldownMonitor(
            ChatHandler chatHandler,
            WorldMonitor worldMonitor
    ) {
        this.chatHandler = chatHandler;
        chatHandler.subscribe(this);
        worldMonitor.subscribe(this::onWorldChange);
    }

    @Override
    public void onEvent(ChatHandler.Event result) {
        var text = result.message().getString();
        boolean filterFound = false;

        Matcher matcher;
        // TODO Remove the ChatHandler prints. They're only here temporarily while a HUD element doesn't exist.
        //   Related note: the ChatHandler prints dont work at all, either. I don't know why, and it's not important
        //   if a HUD element is created for them.
        if((matcher = skillActivated.matcher(text)).find()) {
            var skillName = matcher.group("skill");
            var skill = getSkillCooldown(skillName);
            skill.onActivate();
            filterFound = true;

            this.chatHandler.sendChatMessage(Text.literal("Skill's new HUD line: ").append(skill.getHudTextLine()));
        } else if((matcher = skillEnded.matcher(text)).find()) {
            var skillName = matcher.group("skill");
            var skill = getSkillCooldown(skillName);
            skill.onSkillEnd();
            filterFound = true;

            this.chatHandler.sendChatMessage(Text.literal("Skill's new HUD line: ").append(skill.getHudTextLine()));
        } else if((matcher = skillOnCooldown.matcher(text)).find()) {
            var skillName = matcher.group("skill");
            var skill = getSkillCooldown(skillName);
            String cooldownString = matcher.group("cooldown");
            try {
                var cooldown = Double.parseDouble(cooldownString);
                skill.onCooldown(cooldown);
                // note that here, filterFound only applies if parseDouble works
                filterFound = true;
            } catch (NumberFormatException nfe) {
                // TODO Maybe put this into a util class?
                chatHandler.sendChatMessage(Text.literal("Couldn't understand cooldown of " + cooldownString + " seconds."));
            }

            this.chatHandler.sendChatMessage(Text.literal("Skill's new HUD line: ").append(skill.getHudTextLine()));
        } else if((matcher = skillReady.matcher(text)).find()) {
            var skillName = matcher.group("skill");
            var skill = getSkillCooldown(skillName);
            skill.onReady();
            filterFound = true;

            this.chatHandler.sendChatMessage(Text.literal("Skill's new HUD line: ").append(skill.getHudTextLine()));
        }

        if(filterFound) {
            result.cancel();
        }
    }

    public void onWorldChange(WorldMonitor.Event event) {
        // TODO: Do we want to just wipe the entire skill cooldown menu on world change
        //   or do we want to have a skillCooldown.onWorldChange() function?
        //   Current method:
        //   - Current and max cooldowns lost when world changes
        //   - Only fixed when the user uses an active skill while it's on cooldown
        //   - More adaptable to skill cooldown buffs/debuffs (ex alpha weathers temporarily changing cooldowns
        //     then going to another world won't cause lingering bugs)\
        //   - Will never show nm cooldowns in normal world, and vice versa
        //   onWorldChange() method:
        //   - Current and max cooldowns remembered when world changes
        //   - Will need to update cooldowns when entering boss world (azryn, tqb, root)
        //   - Potential confusion with Alpha (skill cooldown weathers)
        //   - If too many skill cooldowns end up on the list (ex buying new active skills), need to handle removing
        //     old ones from the list
        //   - Need to split NM skills vs w1-4 skills and have different HUD output based on current world
        this.skillCooldowns.clear();
    }

    private SkillCooldown getSkillCooldown(String skillName) {
        var ret = this.skillCooldowns.getOrDefault(skillName, null);
        if(ret != null) {
            // skill is already in skillCooldowns map, so it's already been defined
            return ret;
        }

        var skillConstructor = skillCooldownConstructors.getOrDefault(skillName, null);
        if(skillConstructor == null) {
            this.chatHandler.sendChatMessage(Text.literal("Warning: SkillCooldownStub created for skill " + skillName));
            skillConstructor = SkillCooldownStub::new;
        }

        var skillCooldown = skillConstructor.apply(skillName);
        this.skillCooldowns.put(skillName, skillCooldown);
        return skillCooldown;
    }
}
