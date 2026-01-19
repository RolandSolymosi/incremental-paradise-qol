package com.incrementalclient.services.skillCooldowns;

import com.incrementalclient.common.data.skills.*;
import com.incrementalclient.common.utils.NumberParser;
import com.incrementalclient.interfaces.Observer;
import com.incrementalclient.internals.events.EndClientTickListenable;
import com.incrementalclient.internals.events.StartClientTickListenable;
import com.incrementalclient.services.ChatHandler;
import com.incrementalclient.services.WorldMonitor;
import net.minecraft.network.message.SentMessage;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkillCooldownMonitor implements Observer<ChatHandler.Event> {
    // Note the startPiece includes a spacebar.
    private static final String startPiece = "\uD83D\uDD27 ";

    private final ChatHandler chatHandler;
    private boolean filterChat = true;

    // TODO: After writing "SkillCooldown" too many times I started getting confused.
    //  Maybe a better variable name would be good?
    // Mapping from Skill -> SkillCooldown instance
    private final Map<Skill, SkillCooldown> skillCooldowns = new HashMap<>();

    // Each function is a SkillCooldown constructor
    // Mapping from Skill -> SkillCooldown constructor for that skill
    // (It is expected that many constructors will appear many times, ex buzzing assault and beestorm)
    private final Map<Skill, Function<String, SkillCooldown>> skillCooldownConstructors = Map.ofEntries(
            // TODO finish
            Map.entry(NormalMiningSkill.Ricochet, RicochetCooldown::new),
            Map.entry(NormalFarmingSkill.Pollinate, InstantSkillCooldown::new),
            Map.entry(NormalForagingSkill.BuzzingAssault, VariableDurationNormalSkill::new),
            Map.entry(NormalSpearFishingSkill.SpoonBender, FixedDurationNormalSkill::new)
    );

    // Mapping of [Skill Name -> Active Skill Upg]
    // Initialized at runtime (see constructor) using info from each skill.
    // Treat it as immutable after constructor - aka, as if it had Collections.unmodifiableMap().
    // TODO: Should this be in its own util class? Or maybe a SkillUtils or SkillsManager singleton?
    private final Map<String, Skill> skillNameMap = new HashMap<>();

    // Mapping of [Skill Category -> Currently active skill]
    // Unlike the previous map, not immutable.
    private final Map<SkillCategory, SkillCooldown> currentlyActiveSkills = new HashMap<>();

    // Note: I anticipate regex might not be the best solution here, since for two regexes we put a wildcard
    // at the start followed by two lines of text
    private static final Pattern skillActivated = Pattern.compile(startPiece + "Activated (?<skill>.+)!");
    private static final Pattern skillEnded = Pattern.compile(startPiece + "(?<skill>.+) is over!");
    private static final Pattern skillOnCooldown = Pattern.compile(startPiece + "(?<skill>.+) is on cooldown for another (?<cooldown>" + NumberParser.NumberPattern.pattern() + ") seconds.");
    private static final Pattern skillReady = Pattern.compile(startPiece + "(?<skill>.+) is ready to use.");

    private int test = 0;

    public SkillCooldownMonitor(
            ChatHandler chatHandler,
            WorldMonitor worldMonitor,
            StartClientTickListenable startClientTickListenable
    ) {
        this.chatHandler = chatHandler;
        chatHandler.subscribe(this);
        worldMonitor.subscribe(this::onWorldChange);
        startClientTickListenable.subscribe(this::onTickStart);

        // TODO: Should this go into a Util class instead of here?
        //   Okay, I tried, and it wasn't working because I was having issues with generics and "var" wasn't working.
        //   Leaving it here for now.
        var allSkills = List.of(
                NormalCombatSkill.class, NightmareCombatSkill.class,
                NormalFarmingSkill.class, NightmareFarmingSkill.class,
                NormalForagingSkill.class, NightmareForagingSkill.class,
                NormalMiningSkill.class, NightmareMiningSkill.class,
                NormalSharpshootingSkill.class, NightmareSharpshootingSkill.class,
                NormalSpearFishingSkill.class, NightmareSpearFishingSkill.class,
                NormalExcavationSkill.class
        );
        for(var skill : allSkills) {
            for(var skillUpg : skill.getEnumConstants()) {
                if(skillUpg.isActiveUpgrade()) {
                    skillNameMap.put(skillUpg.getName(), skillUpg);
                }
            }
        }
    }

    @Override
    public void onEvent(ChatHandler.Event result) {
        var text = result.message().getString();
        boolean filterFound = false;

        Matcher matcher;
        if((matcher = skillActivated.matcher(text)).find()) {
            var skillName = matcher.group("skill");
            var skill = skillNameMap.getOrDefault(skillName, null);
            var skillCooldown = getSkillCooldown(skill);
            skillCooldown.onActivate();
            filterFound = true;

            // Only worth updating the "currently active skill" after one gets used.
            // One just got used, so update the currently active skill
            if(skill != null) {
                SkillCategory category = skill.getCategory();
                currentlyActiveSkills.put(category, skillCooldown);
            }
        } else if((matcher = skillEnded.matcher(text)).find()) {
            var skillName = matcher.group("skill");
            var skill = skillNameMap.getOrDefault(skillName, null);
            var skillCooldown = getSkillCooldown(skill);
            skillCooldown.onSkillEnd();
            filterFound = true;

            test |= 1;
        } else if((matcher = skillOnCooldown.matcher(text)).find()) {
            var skillName = matcher.group("skill");
            var skill = skillNameMap.getOrDefault(skillName, null);
            var skillCooldown = getSkillCooldown(skill);
            String cooldownString = matcher.group("cooldown");
            try {
                var cooldown = Double.parseDouble(cooldownString);
                skillCooldown.onCooldown(cooldown);
                // note that here, filterFound only applies if parseDouble works
                filterFound = true;
            } catch (NumberFormatException nfe) {
                // TODO Maybe put this into a util class?
                chatHandler.sendChatMessage(Text.literal("Couldn't understand cooldown of " + cooldownString + " seconds."));
            }
        } else if((matcher = skillReady.matcher(text)).find()) {
            var skillName = matcher.group("skill");
            var skill = skillNameMap.getOrDefault(skillName, null);
            var skillCooldown = getSkillCooldown(skill);
            skillCooldown.onReady();
            filterFound = true;
        }

        if(this.filterChat && filterFound) {
//            result.cancel();
        }
    }

    public void onWorldChange(WorldMonitor.Event event) {
        // It's possible this isn't necessary,
        // since the chat already sends a message out for [Skill is over!] when you change worlds.
        this.skillCooldowns.values().forEach(SkillCooldown::onChangeWorld);

        test |= 2;
    }

    public void onTickStart() {
        /*
        Order of operations is:
        - Chat event, aka onEvent(ChatHandler.Event)
        - End client tick
        - Client change world event (onWorldChange)
        - Start of next client tick
        Therefore, if we want to detect the difference between "skill is over due to time" and "skill is over due to
        world change", we MUST put the check at onTickStart! onTickEnd WILL NOT WORK for this situation!
         */
        // This method has to be done because <Skill is over!> message is sent BEFORE a world change is detected.
        if(test != 0) {
            this.chatHandler.sendChatMessage(Text.of("test variable was " + test));
            test = 0;
        }
    }

    private @NotNull SkillCooldown getSkillCooldown(Skill skill) {
        var ret = this.skillCooldowns.getOrDefault(skill, null);
        if(ret != null) {
            // skill is already in skillCooldowns map, so it's already been defined
            return ret;
        }

        var skillConstructor = skillCooldownConstructors.getOrDefault(skill, null);
        if(skillConstructor == null) {
            skillConstructor = SkillCooldownStub::new;
        }

        var skillCooldown = skillConstructor.apply(skill.getName());
        this.skillCooldowns.put(skill, skillCooldown);
        return skillCooldown;
    }

    public Map<SkillCategory, SkillCooldown> getCurrentlyActiveSkills() {
        return currentlyActiveSkills;
    }

    public void setFilterChat(boolean filterChat) {
        this.filterChat = filterChat;
    }
}
