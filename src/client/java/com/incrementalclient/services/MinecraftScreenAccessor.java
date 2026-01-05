package com.incrementalclient.services;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import java.util.Optional;

public class MinecraftScreenAccessor {
    private final MinecraftClient client;

    public MinecraftScreenAccessor(){
        client = MinecraftClient.getInstance();
    }

    public void setScreen(Screen screen){
        client.setScreen(screen);
    }

    public Optional<Screen> getScreen(){
        return Optional.ofNullable(client.currentScreen);
    }
}
