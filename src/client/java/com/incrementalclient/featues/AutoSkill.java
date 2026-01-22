package com.incrementalclient.featues;

import com.google.common.base.Suppliers;
import com.incrementalclient.abstractions.ListenableBase;
import com.incrementalclient.common.data.World;
import com.incrementalclient.common.data.skills.*;
import com.incrementalclient.common.utils.Utils;
import com.incrementalclient.config.InsertableListOption;
import com.incrementalclient.config.controllers.ComplexTypeController;
import com.incrementalclient.config.controllers.KeyBindController;
import com.incrementalclient.interfaces.ComplexConfigurable;
import com.incrementalclient.interfaces.Listener;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.services.*;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.*;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoSkill extends ListenableBase<Listener> implements ComplexConfigurable<AutoSkill.Configuration> {

    private static final Pattern regex = Pattern.compile("^(?<Type>[a-zA-Z ]+) LEVEL UP!$");

    private final AutoSkill.Configuration configuration = new Configuration();

    private final Supplier<List<OptionPiece>> options = Suppliers.memoize(this::createScreen);

    private final KeyBindMonitor.KeyBindListener keyBindListener;
    private final WorldMonitor worldMonitor;
    private final InteractionScheduler<SkillLevelingContext> interactionScheduler;
    private final MinecraftClientAccessor minecraftClientAccessor;
    private final InteractionScheduler.Builder<SkillLevelingContext, Void> autoSkillLevelUpTask;

    public AutoSkill(
            KeyBindMonitor keyBindMonitor,
            CommandHandler commandHandler,
            ChatHandler chatHandler,
            WorldMonitor worldMonitor,
            InteractionScheduler<SkillLevelingContext> interactionScheduler,
            MinecraftClientAccessor minecraftClientAccessor
    ) {
        keyBindListener = new KeyBindMonitor.KeyBindListener(keyBindMonitor, new KeyBinding(
                "Auto Skill Enforce",
                InputUtil.Type.KEYSYM,
                configuration.keybind,
                "Incremental QOL"
        ), this::fullLevelReCheck);
        this.worldMonitor = worldMonitor;
        this.interactionScheduler = interactionScheduler;
        this.minecraftClientAccessor = minecraftClientAccessor;
        this.autoSkillLevelUpTask = new InteractionScheduler.Builder<SkillLevelingContext, Void>("AutoSkillLeveling", interactionScheduler, minecraftClientAccessor)
                .priority(0)
                .timeout(20)
                .retries(3)
                .startWith(() -> commandHandler.send("skills"))
                .step(
                        (screen, ctx) -> screen.title().getString().contains("Skills"),
                        ctx -> {
                            var customName = Objects.requireNonNull(ctx.screen().contents().get(25).getCustomName()).getString();
                            var slotId = Utils.getSkillSlotId(customName, ctx.context().category);
                            if (slotId == 0) {
                                ctx.complete();
                            }
                            ctx.click(slotId);
                            return false;
                        }
                )
                .step(
                        (screen, ctx) -> screen.title().getString().contains(ctx.category.getName()),
                        ctx -> {
                            ctx.click(ctx.context().category == SkillCategory.Sharpshooting ? 21 : 22);
                            return false;
                        }
                )
                .step(
                        (screen, ctx) -> screen.title().getString().contains(ctx.category.getName() + " Shop"),
                        this::skillLeveling
                );

        chatHandler.subscribe((e) -> chatMessageArrived(e.message()));
        worldMonitor.subscribe((e) -> {
            if (e.from().getRealm() != e.to().getRealm() && configuration.realmChangeTrigger) {
                fullLevelReCheck();
            }
        });
    }

    private List<OptionPiece> createScreen() {
        return List.of(
                Categories.SkillLeveling.General.createConfig(0,
                        Option.<Boolean>createBuilder()
                                .name(Text.literal("Is Enabled"))
                                .binding(
                                        configuration.enabled,
                                        () -> configuration.enabled,
                                        v -> configuration.enabled = v
                                )
                                .controller(BooleanControllerBuilder::create)
                                .build()),
                Categories.SkillLeveling.General.createConfig(100,
                        Option.<Boolean>createBuilder()
                                .name(Text.literal("Trigger all skill on realm change (Nightmare <-> Normal), including login."))
                                .binding(
                                        configuration.realmChangeTrigger,
                                        () -> configuration.realmChangeTrigger,
                                        v -> configuration.realmChangeTrigger = v
                                )
                                .controller(BooleanControllerBuilder::create)
                                .build()),
                Categories.SkillLeveling.General.createConfig(200,
                        Option.<Integer>createBuilder()
                                .name(Text.literal("Force all skill to level up"))
                                .binding(
                                        configuration.keybind,
                                        () -> configuration.keybind,
                                        v -> configuration.keybind = v
                                )
                                .controller((option) -> () -> new KeyBindController(option))
                                .build()),
                Categories.SkillLeveling.createConfig(200,
                        createSkillOption(
                                "Normal Combat",
                                () -> configuration.normalCombat,
                                (v) -> configuration.normalCombat = v,
                                () -> {
                                    var newSkill = new Configuration.SkillLevel<NormalCombatSkill>();
                                    newSkill.skill = NormalCombatSkill.SweepingStrike;
                                    return newSkill;
                                }
                        )),
                Categories.SkillLeveling.createConfig(300,
                        createSkillOption(
                                "Normal Mining",
                                () -> configuration.normalMining,
                                (v) -> configuration.normalMining = v,
                                () -> {
                                    var newSkill = new Configuration.SkillLevel<NormalMiningSkill>();
                                    newSkill.skill = NormalMiningSkill.Ricochet;
                                    return newSkill;
                                }
                        )),
                Categories.SkillLeveling.createConfig(400,
                        createSkillOption(
                                "Normal Foraging",
                                () -> configuration.normalForaging,
                                (v) -> configuration.normalForaging = v,
                                () -> {
                                    var newSkill = new Configuration.SkillLevel<NormalForagingSkill>();
                                    newSkill.skill = NormalForagingSkill.Timberstrike;
                                    return newSkill;
                                }
                        )),
                Categories.SkillLeveling.createConfig(500,
                        createSkillOption(
                                "Normal Farming",
                                () -> configuration.normalFarming,
                                (v) -> configuration.normalFarming = v,
                                () -> {
                                    var newSkill = new Configuration.SkillLevel<NormalFarmingSkill>();
                                    newSkill.skill = NormalFarmingSkill.Harvester;
                                    return newSkill;
                                }
                        )),
                Categories.SkillLeveling.createConfig(600,
                        createSkillOption(
                                "Normal Fishing",
                                () -> configuration.normalSpearFishing,
                                (v) -> configuration.normalSpearFishing = v,
                                () -> {
                                    var newSkill = new Configuration.SkillLevel<NormalSpearFishingSkill>();
                                    newSkill.skill = NormalSpearFishingSkill.SpoonBender;
                                    return newSkill;
                                }
                        )),
                Categories.SkillLeveling.createConfig(700,
                        createSkillOption(
                                "Normal Sharpshooting",
                                () -> configuration.normalSharpshooting,
                                (v) -> configuration.normalSharpshooting = v,
                                () -> {
                                    var newSkill = new Configuration.SkillLevel<NormalSharpshootingSkill>();
                                    newSkill.skill = NormalSharpshootingSkill.ExplosiveArrow;
                                    return newSkill;
                                }
                        )),
                Categories.SkillLeveling.createConfig(800,
                        createSkillOption(
                                "Normal Excavating",
                                () -> configuration.normalExcavation,
                                (v) -> configuration.normalExcavation = v,
                                () -> {
                                    var newSkill = new Configuration.SkillLevel<NormalExcavationSkill>();
                                    newSkill.skill = NormalExcavationSkill.SeismicResonance;
                                    return newSkill;
                                }
                        )),
                Categories.SkillLeveling.createConfig(900,
                        createSkillOption(
                                "Nightmare Combat",
                                () -> configuration.nightmareCombat,
                                (v) -> configuration.nightmareCombat = v,
                                () -> {
                                    var newSkill = new Configuration.SkillLevel<NightmareCombatSkill>();
                                    newSkill.skill = NightmareCombatSkill.DevilsGambit;
                                    return newSkill;
                                }
                        )),
                Categories.SkillLeveling.createConfig(1000,
                        createSkillOption(
                                "Nightmare Mining",
                                () -> configuration.nightmareMining,
                                (v) -> configuration.nightmareMining = v,
                                () -> {
                                    var newSkill = new Configuration.SkillLevel<NightmareMiningSkill>();
                                    newSkill.skill = NightmareMiningSkill.Shatterpoint;
                                    return newSkill;
                                }
                        )),
                Categories.SkillLeveling.createConfig(1100,
                        createSkillOption(
                                "Nightmare Foraging",
                                () -> configuration.nightmareForaging,
                                (v) -> configuration.nightmareForaging = v,
                                () -> {
                                    var newSkill = new Configuration.SkillLevel<NightmareForagingSkill>();
                                    newSkill.skill = NightmareForagingSkill.AxeJuggling;
                                    return newSkill;
                                }
                        )),
                Categories.SkillLeveling.createConfig(1200,
                        createSkillOption(
                                "Nightmare Farming",
                                () -> configuration.nightmareFarming,
                                (v) -> configuration.nightmareFarming = v,
                                () -> {
                                    var newSkill = new Configuration.SkillLevel<NightmareFarmingSkill>();
                                    newSkill.skill = NightmareFarmingSkill.Landscaper;
                                    return newSkill;
                                }
                        )),
                Categories.SkillLeveling.createConfig(1300,
                        createSkillOption(
                                "Nightmare Fishing",
                                () -> configuration.nightmareSpearFishing,
                                (v) -> configuration.nightmareSpearFishing = v,
                                () -> {
                                    var newSkill = new Configuration.SkillLevel<NightmareSpearFishingSkill>();
                                    newSkill.skill = NightmareSpearFishingSkill.FishSenses;
                                    return newSkill;
                                }
                        )),
                Categories.SkillLeveling.createConfig(1400,
                        createSkillOption(
                                "Nightmare Sharpshooting",
                                () -> configuration.nightmareSharpshooting,
                                (v) -> configuration.nightmareSharpshooting = v,
                                () -> {
                                    var newSkill = new Configuration.SkillLevel<NightmareSharpshootingSkill>();
                                    newSkill.skill = NightmareSharpshootingSkill.PeaShooter;
                                    return newSkill;
                                }
                        ))
        );
    }

    private <T extends Enum<T> & Skill> ListOption<Configuration.SkillLevel<T>> createSkillOption(
            String name,
            Supplier<List<Configuration.SkillLevel<T>>> bindingGetter,
            Consumer<List<Configuration.SkillLevel<T>>> bindingSetter,
            Supplier<Configuration.SkillLevel<T>> instanceCreator
    ) {
        return InsertableListOption.<Configuration.SkillLevel<T>>createBuilder()
                .name(Text.literal(name + " Skill leveling"))
                .binding(
                        bindingGetter.get(),
                        bindingGetter,
                        bindingSetter
                )
                .description(OptionDescription.of(Text.of("The configurable skill leveling order for " + name + ".")))
                .insertEntriesAtEnd(true)
                .customController(o -> ComplexTypeController.create(o, minecraftClientAccessor)
                        .textProvider(opt -> {
                            String skillName = opt.skill != null ? opt.skill.getName() : "None";
                            return Text.of(skillName + " to level " + opt.level);
                        })
                        .screenFactory(opt -> YetAnotherConfigLib.createBuilder()
                                .title(Text.of("Edit " + name + " settings"))
                                .category(ConfigCategory.createBuilder()
                                        .name(Text.of("Skill level settings"))
                                        .option(Option.<T>createBuilder()
                                                .name(Text.of("Skill"))
                                                .binding(opt.skill, () -> opt.skill, val -> opt.skill = val)
                                                .controller(t -> EnumDropdownControllerBuilder.create(t)
                                                        .formatValue(v -> Text.of(v.getName()))
                                                )
                                                .build())
                                        .option(Option.<Integer>createBuilder()
                                                .name(Text.of("Level to reach"))
                                                .binding(opt.level, () -> opt.level, val -> opt.level = val)
                                                .controller(c -> IntegerFieldControllerBuilder.create(c).max(100).min(0))
                                                .build())
                                        .build())
                                .save(this::notifyListeners))
                        .build()
                )
                .initial(instanceCreator)
                .collapsed(true)
                .build();
    }

    private boolean skillLeveling(InteractionScheduler.InteractionContext<SkillLevelingContext> ctx) {
        var skillLevelUps = skillNameListInOrderForSkill(ctx.context());
        if (skillLevelUps != null && skillLevelUps.size() > ctx.context().index) {
            var actualSkill = skillLevelUps.get(ctx.context().index);
            if (!ctx.context().slotIdCache.containsKey(actualSkill.skill)) {
                short slotId = 0;
                for (var slot : ctx.screen().contents()) {
                    var customName = slot.get(DataComponentTypes.CUSTOM_NAME);
                    if (customName != null && customName.getString().equals(actualSkill.skill.getName())) {
                        ctx.context().slotIdCache.put(actualSkill.skill, slotId);
                        break;
                    }
                    slotId++;
                }
            }

            if (ctx.context().slotIdCache.containsKey(actualSkill.skill)) {
                short slotId = ctx.context().slotIdCache.get(actualSkill.skill);
                var lore = ctx.screen().contents().get(slotId).get(DataComponentTypes.LORE);
                var actualLevel = Integer.parseInt(String.valueOf(lore.lines().get(1).getString().substring(7).split("/")[0]));
                if (actualLevel >= actualSkill.level) {
                    ctx.context().index++;
                    return skillLeveling(ctx);
                } else {
                    if (lore.lines().getLast().getString().contains("Can't afford")) {
                        ctx.complete();
                        return false;
                    }
                    if (lore.lines().getLast().getString().contains("MAX LEVEL")) {
                        ctx.context().index++;
                        return skillLeveling(ctx);
                    } else {
                        ctx.click(slotId);
                        return true;
                    }
                }
            } else {
                ctx.context().index++;
                return skillLeveling(ctx);
            }
        } else {
            return false;
        }
    }

    private void chatMessageArrived(Text text) {
        if (text.getString().contains("LEVEL UP!")) {
            Matcher matcher = regex.matcher(text.getString());

            if (matcher.find()) {
                var type = SkillCategory.findByName(matcher.group("Type"));
                if (type.isEmpty()) {
                    return;
                }
                var realm = worldMonitor.currentWorld().getRealm();
                startLeveling(autoSkillLevelUpTask.build(new SkillLevelingContext(type.get(), realm), type.get().name()));
            }
        }
    }

    private List<? extends Configuration.SkillLevel<?>> skillNameListInOrderForSkill(SkillLevelingContext ctx) {
        if (ctx.realm == World.Realm.Nightmare) {
            return switch (ctx.category) {
                case SkillCategory.Combat -> configuration.nightmareCombat;
                case SkillCategory.Mining -> configuration.nightmareMining;
                case SkillCategory.Foraging -> configuration.nightmareForaging;
                case SkillCategory.Farming -> configuration.nightmareFarming;
                case SkillCategory.SpearFishing -> configuration.nightmareSpearFishing;
                case SkillCategory.Sharpshooting -> configuration.nightmareSharpshooting;
                case Excavation -> null;
            };
        } else {
            return switch (ctx.category) {
                case SkillCategory.Combat -> configuration.normalCombat;
                case SkillCategory.Mining -> configuration.normalMining;
                case SkillCategory.Foraging -> configuration.normalForaging;
                case SkillCategory.Farming -> configuration.normalFarming;
                case SkillCategory.SpearFishing -> configuration.normalSpearFishing;
                case SkillCategory.Sharpshooting -> configuration.normalSharpshooting;
                case SkillCategory.Excavation -> configuration.normalExcavation;
            };
        }
    }

    private void fullLevelReCheck() {
        var realm = worldMonitor.currentWorld().getRealm();
        if (realm == World.Realm.Nightmare || realm == World.Realm.Normal) {
            var combatTask = autoSkillLevelUpTask.build(new SkillLevelingContext(SkillCategory.Combat, realm), SkillCategory.Combat.name());
            combatTask.getFuture().thenRun(() -> {
                var miningTask = autoSkillLevelUpTask.build(new SkillLevelingContext(SkillCategory.Mining, realm), SkillCategory.Mining.name());
                miningTask.getFuture().thenRun(() -> {
                    var foragingTask = autoSkillLevelUpTask.build(new SkillLevelingContext(SkillCategory.Foraging, realm), SkillCategory.Foraging.name());
                    foragingTask.getFuture().thenRun(() -> {
                        var farmingTask = autoSkillLevelUpTask.build(new SkillLevelingContext(SkillCategory.Farming, realm), SkillCategory.Farming.name());
                        farmingTask.getFuture().thenRun(() -> {
                            var fishingTask = autoSkillLevelUpTask.build(new SkillLevelingContext(SkillCategory.SpearFishing, realm), SkillCategory.SpearFishing.name());
                            fishingTask.getFuture().thenRun(() -> {
                                var sharpshootingTask = autoSkillLevelUpTask.build(new SkillLevelingContext(SkillCategory.Sharpshooting, realm), SkillCategory.Sharpshooting.name());
                                sharpshootingTask.getFuture().thenRun(() -> {

                                });
                                interactionScheduler.submit(sharpshootingTask);
                            });
                            interactionScheduler.submit(fishingTask);
                        });
                        interactionScheduler.submit(farmingTask);
                    });
                    interactionScheduler.submit(foragingTask);
                });
                interactionScheduler.submit(miningTask);
            });
            startLeveling(combatTask);
        }
    }

    private void startLeveling(InteractionScheduler.InteractionTask<SkillLevelingContext, Void> task) {
        if (configuration.enabled) {
            interactionScheduler.submit(task);
        }
    }

    @Override
    public String getJsonSection() {
        return "autoSkill";
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public List<OptionPiece> getOption() {
        return options.get();
    }

    @Override
    public void optionChanged() {
        keyBindListener.updateKeyBind(configuration.keybind);
    }

    public static class Configuration {
        @SerialEntry
        public int keybind = GLFW.GLFW_KEY_UP;
        @SerialEntry
        public boolean enabled = false;
        @SerialEntry
        public boolean realmChangeTrigger = false;
        @SerialEntry
        public List<SkillLevel<NormalCombatSkill>> normalCombat = new java.util.ArrayList<>();
        @SerialEntry
        public List<SkillLevel<NormalMiningSkill>> normalMining = new java.util.ArrayList<>();
        @SerialEntry
        public List<SkillLevel<NormalForagingSkill>> normalForaging = new java.util.ArrayList<>();
        @SerialEntry
        public List<SkillLevel<NormalFarmingSkill>> normalFarming = new java.util.ArrayList<>();
        @SerialEntry
        public List<SkillLevel<NormalSpearFishingSkill>> normalSpearFishing = new java.util.ArrayList<>();
        @SerialEntry
        public List<SkillLevel<NormalSharpshootingSkill>> normalSharpshooting = new java.util.ArrayList<>();
        @SerialEntry
        public List<SkillLevel<NormalExcavationSkill>> normalExcavation = new java.util.ArrayList<>();

        @SerialEntry
        public List<SkillLevel<NightmareCombatSkill>> nightmareCombat = new java.util.ArrayList<>();
        @SerialEntry
        public List<SkillLevel<NightmareMiningSkill>> nightmareMining = new java.util.ArrayList<>();
        @SerialEntry
        public List<SkillLevel<NightmareForagingSkill>> nightmareForaging = new java.util.ArrayList<>();
        @SerialEntry
        public List<SkillLevel<NightmareFarmingSkill>> nightmareFarming = new java.util.ArrayList<>();
        @SerialEntry
        public List<SkillLevel<NightmareSpearFishingSkill>> nightmareSpearFishing = new java.util.ArrayList<>();
        @SerialEntry
        public List<SkillLevel<NightmareSharpshootingSkill>> nightmareSharpshooting = new java.util.ArrayList<>();

        public static class SkillLevel<T extends Skill> {
            @SerialEntry
            public T skill = null;
            @SerialEntry
            public int level = 0;
        }
    }

    public static class SkillLevelingContext {
        private final SkillCategory category;
        private final World.Realm realm;
        private final HashMap<Skill, Short> slotIdCache = new HashMap<>();
        private int index = 0;

        public SkillLevelingContext(SkillCategory category, World.Realm realm) {
            this.category = category;
            this.realm = realm;
        }
    }
}
