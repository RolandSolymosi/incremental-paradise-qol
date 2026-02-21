package com.incrementalclient.hud.ScoreboardReplacementBar;

import com.incrementalclient.common.utils.TextUtils;
import com.incrementalclient.services.GameInfoMonitor;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class AreaText {
    public static Text getAreaText(GameInfoMonitor.PlayerProgressData progressData){
        Text areaText = progressData.getArea();

        return areaText != null ? TextUtils.textColor(areaText.getString(), Formatting.YELLOW) : null;
    }
}
