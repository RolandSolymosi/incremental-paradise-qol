package com.incrementalqol.modules.PetXpCalculator;

import com.incrementalqol.config.Config;
import com.incrementalqol.common.utils.ScreenInteraction;
import com.incrementalqol.common.utils.Utils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PxpCalculatorModule implements ClientModInitializer {

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

    private static final ScreenInteraction screenInteraction = null;

    @Override
    public void onInitializeClient() {

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("pxpcalc").executes(context -> {
                        context.getSource().sendFeedback(Text.literal("§bCurrent pxp in inventory:§r " + sumPetXpValue()));
                        return 0;
                    })
            );
        });
    }

    public int sumPetXpValue() {
        PlayerInventory inventory = MinecraftClient.getInstance().player.getInventory();
        int totalPetXp = 0;
        for (ItemStack stack : inventory) {
            OptionalInt petXpStackValue = getPetXpStackValue(stack);
            totalPetXp += petXpStackValue.orElse(0);
        }
        return totalPetXp;
    }

    public OptionalInt getPetXpStackValue(ItemStack itemStack) {
        if (Utils.isPlayerHead(itemStack)) {
            LoreComponent lore = itemStack.get(DataComponentTypes.LORE);
            List<Text> text = lore.lines();
            List<String> blocks = Utils.parseLoreLines(text);

            Pattern petRegex = Pattern.compile("^(Common|Uncommon|Rare|Epic|Legendary|Mythic) Pet(\uD83D\uDEAB Untradable)?");
            Matcher matcher = petRegex.matcher(blocks.get(blocks.size()-1));

            String petName = itemStack.getName().getString().replaceAll(" Pet \\(Right Click\\)$", "");

            if (matcher.find() && matcher.group(2) == null && !BLACKLISTED_PETS.contains(petName)) {
                int value = switch (matcher.group(1)) {
                    case "Common" -> COMMON_PET_VALUE;
                    case "Uncommon" -> UNCOMMON_PET_VALUE;
                    case "Rare" -> RARE_PET_VALUE;
                    case "Epic" -> EPIC_PET_VALUE;
                    case "Legendary" -> Config.HANDLER.instance().getLegendaryPxpValue();
                    case "Mythic" -> Config.HANDLER.instance().getMythicPxpValue();
                    default -> 0;
                };
                return OptionalInt.of(value*itemStack.getCount());
            }
        }
            return OptionalInt.empty();
    }
}
