package com.incrementalclient.services.skillCooldowns;

import com.incrementalclient.common.data.skills.*;
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
    // Mapping from SkillName -> SkillCooldown instance
    private final Map<String, SkillCooldown> skillCooldowns = new HashMap<>();

    // Each function is a SkillCooldown constructor
    // Mapping from SkillName -> SkillCooldown constructor for that skill
    // (It is expected that many constructors will appear many times, ex buzzing assault and beestorm)
    private final Map<String, Function<String, SkillCooldown>> skillCooldownConstructors = Map.ofEntries(
            Map.entry("Ricochet", RicochetCooldown::new),
            Map.entry("Pollinate", InstantSkillCooldown::new)
    );

    // Mapping of [Skill Name -> Skill Category]
    // Initialized at runtime (see constructor) using info from each skill.
    // Treat it as immutable after constructor - aka, as if it had Collections.unmodifiableMap().
    // TODO: Should this be in its own util class? Or maybe a SkillUtils singleton?
    private final Map<String, SkillCategory> skillCategoryMap = new HashMap<>();

    // Mapping of [Skill Category -> Currently active skill]
    // Unlike the previous map, not immutable.
    private final Map<SkillCategory, SkillCooldown> currentlyActiveSkills = new HashMap<>();

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
                    skillCategoryMap.put(skillUpg.getName(), skillUpg.getCategory());
                }
            }
        }
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

//            this.chatHandler.sendChatMessage(Text.literal("Skill's new HUD line: ").append(skill.getHudTextLine()));

            // Only worth updating the "currently active skill" after one gets used.
            // One just got used, so update the currently active skill
            SkillCategory category = skillCategoryMap.getOrDefault(skillName, null);
            if(category != null) {
                currentlyActiveSkills.put(category, skill);
            }
        } else if((matcher = skillEnded.matcher(text)).find()) {
            var skillName = matcher.group("skill");
            var skill = getSkillCooldown(skillName);
            skill.onSkillEnd();
            filterFound = true;

//            this.chatHandler.sendChatMessage(Text.literal("Skill's new HUD line: ").append(skill.getHudTextLine()));
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

//            this.chatHandler.sendChatMessage(Text.literal("Skill's new HUD line: ").append(skill.getHudTextLine()));
        } else if((matcher = skillReady.matcher(text)).find()) {
            var skillName = matcher.group("skill");
            var skill = getSkillCooldown(skillName);
            skill.onReady();
            filterFound = true;

//            this.chatHandler.sendChatMessage(Text.literal("Skill's new HUD line: ").append(skill.getHudTextLine()));
        }

        if(this.filterChat && filterFound) {
            result.cancel();
        }
    }

    public void onWorldChange(WorldMonitor.Event event) {
        // It's possible this isn't necessary,
        // since the chat already sends a message out for [Skill is over!] when you change worlds.
        this.skillCooldowns.values().forEach(SkillCooldown::onChangeWorld);
    }

    private @NotNull SkillCooldown getSkillCooldown(String skillName) {
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

    public Map<SkillCategory, SkillCooldown> getCurrentlyActiveSkills() {
        return currentlyActiveSkills;
    }

    public void setFilterChat(boolean filterChat) {
        this.filterChat = filterChat;
    }
}
