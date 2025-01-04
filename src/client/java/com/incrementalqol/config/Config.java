package com.incrementalqol.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class Config {

    private boolean hudBackground;
    private int hudPosX;
    private int hudPosY;
    private boolean autoGZ;


    Config(boolean hudBackground, int hudPosX, int hudPosY, boolean autoGZ) {
        this.hudBackground = hudBackground;
        this.hudPosX = hudPosX;
        this.hudPosY = hudPosY;
        this.autoGZ = autoGZ;
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

    public static final Codec<Config> CODEC = RecordCodecBuilder.create(optionsInstance -> optionsInstance.group(
            Codec.BOOL.fieldOf("hudBackground").forGetter(Config::getHudBackground),
            Codec.INT.fieldOf("hudPosX").forGetter(Config::getHudPosX),
            Codec.INT.fieldOf("hudPosY").forGetter(Config::getHudPosY),
            Codec.BOOL.fieldOf("autoGZ").forGetter(Config::getAutoGZ)
            ).apply(optionsInstance, Config::new));

}
