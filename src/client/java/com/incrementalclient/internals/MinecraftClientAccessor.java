package com.incrementalclient.internals;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Optional;

public class MinecraftClientAccessor {
    private final MinecraftClient client;

    public MinecraftClientAccessor(){
        client = MinecraftClient.getInstance();
    }

    public void setScreen(Screen screen){
        client.setScreen(screen);
    }

    public Optional<Screen> getScreen(){
        return Optional.ofNullable(client.currentScreen);
    }

    public Optional<Window> getWindow() { return Optional.ofNullable(client.getWindow()); }

    public Optional<TextRenderer> getTextRenderer() { return Optional.ofNullable(client.textRenderer); }

    public Optional<PlayerEntity> getPlayer() { return Optional.ofNullable(client.player); }

    public Optional<GameOptions> getOptions() { return Optional.ofNullable(client.options); }

    public Optional<ClientWorld> getWorld() { return Optional.ofNullable(client.world); }
}
