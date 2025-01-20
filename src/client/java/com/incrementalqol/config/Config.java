package com.incrementalqol.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class Config {

    private boolean hudBackground;
    private int hudPosX;
    private int hudPosY;
    private int hudSize;
    private double hudScale;
    private boolean autoGZ;
    private boolean isSortedByType;


    Config(boolean hudBackground, int hudPosX, int hudPosY, boolean autoGZ, double hudScale, int hudSize, boolean isSortedByType) {
        this.hudBackground = hudBackground;
        this.hudPosX = hudPosX;
        this.hudPosY = hudPosY;
        this.hudScale = hudScale;
        this.autoGZ = autoGZ;
        this.hudSize = hudSize;
        this.isSortedByType = isSortedByType;
    }

    public int getHudPosX() {
        return hudPosX;
    }

    public int getHudPosY() {
        return hudPosY;
    }

    public boolean getHudBackground() {
        return hudBackground;
    }

    public boolean getAutoGZ() {
        return autoGZ;
    }

    public double getHudScale() {return hudScale;}

    public int getHudSize() {return hudSize;}

    public boolean getSortedByType() {return isSortedByType;}


    public void setAutoGZ(boolean autoGZ) {
        this.autoGZ = autoGZ;
    }

    public void setHudBackground(boolean hudBackground) {
        this.hudBackground = hudBackground;
    }

    public void setHudPosX(int hudPosX) {
        this.hudPosX = hudPosX;
    }

    public void setHudPosY(int hudPosY) {
        this.hudPosY = hudPosY;
    }

    public void setHudScale(double hudScale) {this.hudScale = hudScale;}

    public void setHudSize(int hudSize) {this.hudSize = hudSize;}

    public void setSortedByType(boolean sortedByType) {this.isSortedByType = sortedByType;}

    public static final Codec<Config> CODEC = RecordCodecBuilder.create(optionsInstance -> optionsInstance.group(
            Codec.BOOL.fieldOf("hudBackground").forGetter(Config::getHudBackground),
            Codec.INT.fieldOf("hudPosX").forGetter(Config::getHudPosX),
            Codec.INT.fieldOf("hudPosY").forGetter(Config::getHudPosY),
            Codec.BOOL.fieldOf("autoGZ").forGetter(Config::getAutoGZ),
            Codec.DOUBLE.fieldOf("hudScale").forGetter(Config::getHudScale),
            Codec.INT.fieldOf("hudSize").forGetter(Config::getHudSize),
            Codec.BOOL.fieldOf("isSortedByType").forGetter(Config::getSortedByType)

            ).apply(optionsInstance, Config::new));

}
