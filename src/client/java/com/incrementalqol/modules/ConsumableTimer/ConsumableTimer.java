package com.incrementalqol.modules.ConsumableTimer;

import com.incrementalqol.common.utils.TextUtils;
import com.incrementalqol.config.Config;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class ConsumableTimer {
    private final String buffName;
    private final long expirationTimeMillis; // When the timer expires (System.currentTimeMillis())
    private int strWidth;

    public ConsumableTimer(String buffName, long expirationTimeMillis) {
        this.buffName = buffName;
        this.expirationTimeMillis = expirationTimeMillis;
        this.strWidth = 0;
    }

    public String getBuffName() {
        return buffName;
    }

    public long getExpirationTimeMillis() {
        return expirationTimeMillis;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= expirationTimeMillis;
    }

    public String getTimeLeftString() {
        long now = System.currentTimeMillis();
        long timeLeftMillis = expirationTimeMillis - now;
        
        if (timeLeftMillis <= 0) {
            return "Expired";
        }
        
        long totalSeconds = timeLeftMillis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        
        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%ds", seconds);
        }
    }

    public int getStrWidth() {
        return strWidth;
    }

    public Text render() {
        var config = Config.HANDLER.instance();
        int timerColor = config.getConsumableTimerColor().getRGB();
        int timeColor = config.getConsumableTimeColor().getRGB();

        String timeLeft = getTimeLeftString();
        MutableText displayText = Text.literal("")
                .append(TextUtils.textColor(buffName + ": ", timerColor))
                .append(TextUtils.textColor(timeLeft, timeColor));

        calculateDisplayLength(displayText);
        return displayText;
    }

    private void calculateDisplayLength(Text input) {
        this.strWidth = input.getString().length();
    }
}
