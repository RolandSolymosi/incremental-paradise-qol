package com.incrementalclient.services.skillCooldowns;

import com.incrementalclient.common.data.ItemType;
import com.incrementalclient.common.data.skills.*;
import com.incrementalclient.common.utils.NumberParser;
import com.incrementalclient.interfaces.Observer;
import com.incrementalclient.internals.ItemCooldownWrapper;
import com.incrementalclient.internals.events.StartClientTickListenable;
import com.incrementalclient.services.ChatHandler;
import com.incrementalclient.services.WorldMonitor;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkillCooldownMonitor implements Observer<ChatHandler.Event> {

    private final ItemCooldownWrapper itemCooldownWrapper;
    private boolean overrideItemCooldowns = true;

    private final ChatHandler chatHandler;
    private boolean filterChat = true;

    // Mapping from Skill -> SkillCooldown instance
    private final Map<Skill, SkillCooldown> skillCooldowns = new HashMap<>();

    // Each function is a SkillCooldown constructor
    // Mapping from Skill -> SkillCooldown constructor for that skill
    // (It is expected that many constructors will appear many times, ex buzzing assault and beestorm)
    private final Map<Skill, Function<String, SkillCooldown>> skillCooldownConstructors = Map.ofEntries(
            // Combat skills
            Map.entry(NormalCombatSkill.SweepingStrike, InstantSkillCooldown::new),
            Map.entry(NormalCombatSkill.RhinoCharge, InstantSkillCooldown::new),
            Map.entry(NormalCombatSkill.BeeStorm, VariableDurationNormalSkill::new),
            // Not technically accurate, but Devil's Gambit doesn't have a message when the cooldown
            // starts, beyond the <these buffs are applied!> message.
            // The cooldown may as well be 24 seconds plus the symbol spin time, which is pretty consistent.
            Map.entry(NightmareCombatSkill.DevilsGambit, InstantSkillCooldown::new),

            // Excavation (brush) skills
            // This isn't technically an instant skill, but <Seismic Resonance is over!> is not stated in chat.
            Map.entry(NormalExcavationSkill.SeismicResonance, InstantSkillCooldown::new),
            // no nm excavation skill

            // Farming skills
            // Harvester isn't technically an instant, but <Harvester is over!> is not stated in chat.
            Map.entry(NormalFarmingSkill.Harvester, InstantSkillCooldown::new),
            Map.entry(NormalFarmingSkill.CropChomp, InstantSkillCooldown::new),
            Map.entry(NormalFarmingSkill.Pollinate, InstantSkillCooldown::new),
            // Landscaper does not make a message, so there's no information to get.
            Map.entry(NightmareFarmingSkill.Landscaper, SkillCooldownStub::new),

            // Foraging skills
            // Timberstrike's weird, since sometimes its cooldown will instantly come back.
            // However, I don't care to deal with that case - it only happens if you miss your axe.
            Map.entry(NormalForagingSkill.Timberstrike, InstantSkillCooldown::new),
            Map.entry(NormalForagingSkill.LuckyGathering, FixedDurationNormalSkill::new),
            Map.entry(NormalForagingSkill.BuzzingAssault, VariableDurationNormalSkill::new),
            Map.entry(NightmareForagingSkill.AxeJuggling, AxeJugglingCooldown::new),

            // Mining skills
            Map.entry(NormalMiningSkill.Ricochet, RicochetCooldown::new),
            Map.entry(NormalMiningSkill.CondensedStrike, InstantSkillCooldown::new),
            // Wings of Wealth is weird, since it has the <X is over!> message despite being instant,
            // AND like timberstrike it comes back instantly if it hits nothing.
            // However, nobody uses this skill, so I'm fine with just giving it an InstantSkillCooldown.
            Map.entry(NormalMiningSkill.WingsOfWealth, InstantSkillCooldown::new),
            Map.entry(NightmareMiningSkill.Shatterpoint, VariableDurationNormalSkill::new),

            // Sharpshooting skills
            Map.entry(NormalSharpshootingSkill.ExplosiveArrow, InstantSkillCooldown::new),
            // SwarmSurfer is technically a VariableDuration, but since it doesn't have <Swarm Surfer is over!>
            // it's closer to an Instant skill.
            Map.entry(NormalSharpshootingSkill.SwarmSurfer, InstantSkillCooldown::new),
            Map.entry(NightmareSharpshootingSkill.PeaShooter, PeashooterCooldown::new),

            // Spearfishing skills
            Map.entry(NormalSpearFishingSkill.SpoonBender, FixedDurationNormalSkill::new),
            Map.entry(NormalSpearFishingSkill.Wavestreak, FixedDurationNormalSkill::new),
            Map.entry(NormalSpearFishingSkill.Beenado, FixedDurationNormalSkill::new),
            Map.entry(NightmareSpearFishingSkill.FishSenses, FixedDurationNormalSkill::new)
    );

    // Mapping of [Skill Name -> Active Skill Upg]
    // Initialized at runtime (see constructor) using info from each skill.
    // Treat it as immutable after constructor - aka, as if it had Collections.unmodifiableMap().
    // TODO: Should this be in its own util class? Or maybe a SkillUtils or SkillsManager singleton?
    private final Map<String, Skill> skillNameMap = new HashMap<>();

    // Mapping of [Skill Category -> Currently active skill]
    // Unlike the previous map, not immutable.
    private final Map<SkillCategory, SkillCooldown> currentlyActiveSkills = new HashMap<>();

    // Skill cooldowns which ended this tick - don't yet know if it was because of world change or not, though.
    private final List<SkillCooldown> cooldownsEnding = new ArrayList<>();

    // Note the startPiece includes a spacebar.
    private static final String startPiece = "\uD83D\uDD27 ";
    // Note: I anticipate regex might not be the best solution here, since for two regexes we put a wildcard
    // at the start followed by two lines of text
    private static final Pattern skillActivated = Pattern.compile(startPiece + "Activated (?<skill>.+)!");
    private static final Pattern skillEnded = Pattern.compile(startPiece + "(?<skill>.+) is over!");
    private static final Pattern skillOnCooldown = Pattern.compile(startPiece + "(?<skill>.+) is on cooldown for another (?<cooldown>" + NumberParser.NumberPattern.pattern() + ") seconds.");
    private static final Pattern skillReady = Pattern.compile(startPiece + "(?<skill>.+) is ready to use.");
    private static final Pattern skillUseRecharged = Pattern.compile(startPiece + "A use of (?<skill>.+) has charged.");

    public SkillCooldownMonitor(
            ItemCooldownWrapper itemCooldownWrapper,
            ChatHandler chatHandler,
            WorldMonitor worldMonitor,
            StartClientTickListenable startClientTickListenable
    ) {
        this.itemCooldownWrapper = itemCooldownWrapper;
        this.chatHandler = chatHandler;
        chatHandler.subscribe(this::onChatMessageReceived);
        worldMonitor.subscribe(this::onWorldChange);
        startClientTickListenable.subscribe(this::onTickStart);

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
        skillNameMap.put("Devils Gambit", NightmareCombatSkill.DevilsGambit);
    }

    public void onChatMessageReceived(ChatHandler.Event result) {
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
            cooldownsEnding.add(skillCooldown);
            filterFound = true;
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
                // This could go into a util class, but there aren't many things in this game which are
                // decimal outputs. If we get another decimal output to deal with, then this should
                // go into that util class.
                chatHandler.sendChatMessage(Text.literal("Couldn't understand cooldown of " + cooldownString + " seconds."));
            }
        } else if((matcher = skillReady.matcher(text)).find()) {
            var skillName = matcher.group("skill");
            var skill = skillNameMap.getOrDefault(skillName, null);
            var skillCooldown = getSkillCooldown(skill);
            skillCooldown.onReady();
            filterFound = true;
        } else if((matcher = skillUseRecharged.matcher(text)).find()) {
            var skillName = matcher.group("skill");
            var skill = skillNameMap.getOrDefault(skillName, null);
            var skillCooldown = getSkillCooldown(skill);
            skillCooldown.onUseRecharged();
            filterFound = true;
        }

        if(this.filterChat && filterFound) {
            result.cancel();
        }
    }

    public void onWorldChange(WorldMonitor.Event event) {
        // Cooldowns ending because of a world change
        cooldownsEnding.forEach(cooldown -> cooldown.onSkillEnd(true));
        cooldownsEnding.clear();
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
        // Cooldowns ending, and NOT because of a world change
        cooldownsEnding.forEach(cooldown -> cooldown.onSkillEnd(false));
        cooldownsEnding.clear();

        // Update cooldowns now
        if(this.overrideItemCooldowns) {
            for (SkillCategory activeCategory : currentlyActiveSkills.keySet()) {
                // First, get active category's item type
                var activeItemType = ItemType.fromSkillCategory(activeCategory);
                if(activeItemType == ItemType.UNKNOWN) {
                    return;
                }

                // Next, get active category's cooldown fraction
                var activeSkillCooldown = currentlyActiveSkills.get(activeCategory);
                var activeCooldownFraction = activeSkillCooldown.getCooldownFraction();

                // Finally, override item cooldowns
                if(activeCooldownFraction.isPresent()) {
                    this.itemCooldownWrapper.setItemCooldown(activeItemType, activeCooldownFraction.get());
                }
                else {
                    this.itemCooldownWrapper.clearItemCooldown(activeItemType);
                }
            }
        }
    }

    private @NotNull SkillCooldown getSkillCooldown(Skill skill) {
        var ret = this.skillCooldowns.getOrDefault(skill, null);
        if(ret != null) {
            // skill is already in skillCooldowns map, so it's already been defined
            return ret;
        }

        SkillCooldown skillCooldown;
        if(skill == null) {
            skillCooldown = new SkillCooldownStub("Null skill");
        }
        else {
            var skillConstructor = skillCooldownConstructors.getOrDefault(skill, SkillCooldownStub::new);
            skillCooldown = skillConstructor.apply(skill.getName());
        }

        this.skillCooldowns.put(skill, skillCooldown);
        return skillCooldown;
    }

    public Map<SkillCategory, SkillCooldown> getCurrentlyActiveSkills() {
        return currentlyActiveSkills;
    }

    public void setOverrideItemCooldowns(boolean overrideItemCooldowns) {
        if(this.overrideItemCooldowns && !overrideItemCooldowns) {
            // going from true to false
            // need to override one last time to clear all
            Arrays.stream(SkillCategory.values())
                    .map(ItemType::fromSkillCategory)
                    .forEach(this.itemCooldownWrapper::clearItemCooldown);
        }

        this.overrideItemCooldowns = overrideItemCooldowns;
    }

    public void setFilterChat(boolean filterChat) {
        this.filterChat = filterChat;
    }
}
