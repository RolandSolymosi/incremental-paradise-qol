package com.incrementalclient.hud.ScoreboardReplacementBar;

import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.services.GameInfoMonitor;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import com.incrementalclient.common.utils.TextUtils;

public class PlayerNameText {

    public static Text getPlayerNameText(GameInfoMonitor.PlayerProgressData progressData, MinecraftClientAccessor mcAccessor) {
        var player = mcAccessor.getPlayer();

        // Get player name and rank
        String nameString = player.get().getName().getString();
        String rankString = progressData.getRank().getString();

        Text nameText;
        if (rankString != null && !rankString.isEmpty()) {
            Formatting rankColor = getRankColor(rankString);
            nameText =
                    Text.literal("")
                    .append(TextUtils.textColor("[", Formatting.GRAY))
                    .append(TextUtils.textColor(rankString, rankColor, false, true))
                    .append(TextUtils.textColor("] ", Formatting.GRAY))
                    .append(TextUtils.textColor(nameString, Formatting.GRAY));
        } else {
            nameText = TextUtils.textColor(nameString, Formatting.GRAY);
        }

        return nameText;
    }

    private static Formatting getRankColor(String rankName) {
        String rankLower = rankName.toLowerCase();
        return switch (rankLower) {
            case "explorer" -> Formatting.YELLOW;
            case "navigator" -> Formatting.GREEN;
            case "adventurer" -> Formatting.AQUA;
            case "voyager" -> Formatting.LIGHT_PURPLE;
            case "outlander" -> Formatting.DARK_PURPLE;
            case "trailblazer" -> Formatting.GOLD;
            case "qa" -> Formatting.BLUE;
            case "mod" -> Formatting.DARK_GREEN;
            default -> Formatting.WHITE;
        };
    }
}
