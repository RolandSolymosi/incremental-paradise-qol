package com.incrementalclient.featues;


import com.google.common.base.Suppliers;
import com.incrementalclient.interfaces.Configurable;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.services.CommandHandler;
import com.incrementalclient.common.utils.Utils;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PxpCalculation implements Configurable<PxpCalculation.Configuration> {

    private static final int COMMON_PET_VALUE = 1;
    private static final int UNCOMMON_PET_VALUE = 2;
    private static final int RARE_PET_VALUE = 4;
    private static final int EPIC_PET_VALUE = 10;

    private static final Set<String> BLACKLISTED_PETS = Set.of(
            "Pig Stack",
            "Hog Stack",
            "Pig Mass",
            "Pig Conglomerate",
            "Rattus",
            "Vulture",
            "Infernal Imp",
            "Slinky",
            "Unicorn", // Technically tradable rn but shouldn't be so can remove later if you want
            "Queen Bee",
            "Cococrab",
            "Sky Beelte Nest",
            "Monkey",
            "Harvest Spirit",
            "Golden Rabbit"
    );

    private final Configuration configuration = new Configuration();

    private final Supplier<List<OptionPiece>> options;
    private final MinecraftClientAccessor minecraftClientAccessor;

    public PxpCalculation(
            CommandHandler commandHandler,
            MinecraftClientAccessor minecraftClientAccessor
    ) {
        this.minecraftClientAccessor = minecraftClientAccessor;

        commandHandler.register(new CommandHandler.CommandRegistration("pxpcalc", this::sumPetXpValue));

        options = Suppliers.memoize(() -> List.of(Categories.Misc.PetXp.createConfig(0,
                        Option.<Integer>createBuilder()
                                .name(Text.of("Legendary Pet Value"))
                                .description(OptionDescription.of(Text.of("The PXP value of a legendary pet")))
                                .binding( configuration.legendaryPxpValue, () -> configuration.legendaryPxpValue, newVal -> configuration.legendaryPxpValue = newVal)
                                .controller(opt -> IntegerFieldControllerBuilder.create(opt)
                                        .min(50).max(500))
                                .build()),
                Categories.Misc.PetXp.createConfig(1,
                        Option.<Integer>createBuilder()
                                .name(Text.of("Mythic Pet Value"))
                                .description(OptionDescription.of(Text.of("The PXP value of a mythic pet")))
                                .binding(configuration.mythicPxpValue, () -> configuration.mythicPxpValue, newVal -> configuration.mythicPxpValue = newVal)
                                .controller(opt -> IntegerFieldControllerBuilder.create(opt)
                                        .min(500).max(10000))
                                .build())));
    }

    public int sumPetXpValue() {
        var player =  minecraftClientAccessor.getPlayer();
        if (player.isPresent()){
            PlayerInventory inventory = player.get().getInventory();
            int totalPetXp = 0;
            for (ItemStack stack : inventory) {
                OptionalInt petXpStackValue = getPetXpStackValue(stack);
                totalPetXp += petXpStackValue.orElse(0);
            }
            return totalPetXp;
        }

        return 0;
    }

    public OptionalInt getPetXpStackValue(ItemStack itemStack) {
        if (Utils.isPlayerHead(itemStack)) {
            LoreComponent lore = itemStack.get(DataComponentTypes.LORE);
            List<Text> text = lore.lines();
            List<String> blocks = Utils.parseLoreLines(text);

            Pattern petRegex = Pattern.compile("^(Common|Uncommon|Rare|Epic|Legendary|Mythic) Pet(\uD83D\uDEAB Untradable)?");
            Matcher matcher = petRegex.matcher(blocks.get(blocks.size() - 1));

            String petName = itemStack.getName().getString().replaceAll(" Pet \\(Right Click\\)$", "");

            if (matcher.find() && matcher.group(2) == null && !BLACKLISTED_PETS.contains(petName)) {
                int value = switch (matcher.group(1)) {
                    case "Common" -> COMMON_PET_VALUE;
                    case "Uncommon" -> UNCOMMON_PET_VALUE;
                    case "Rare" -> RARE_PET_VALUE;
                    case "Epic" -> EPIC_PET_VALUE;
                    case "Legendary" -> configuration.legendaryPxpValue;
                    case "Mythic" -> configuration.mythicPxpValue;
                    default -> 0;
                };
                return OptionalInt.of(value * itemStack.getCount());
            }
        }
        return OptionalInt.empty();
    }

    @Override
    public String getJsonSection() {
        return "pxpCalculator";
    }

    @Override
    public PxpCalculation.Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public List<OptionPiece> getOption() {
        return options.get();
    }

    @Override
    public void optionChanged() {

    }

    static public class Configuration {
        @SerialEntry
        private int legendaryPxpValue = 75;
        @SerialEntry
        private int mythicPxpValue = 500;
    }
}
