package com.incrementalclient.internals;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

import java.util.Optional;

public class MinecraftClientAccessor {
    private final MinecraftClient client;

    public MinecraftClientAccessor() {
        client = MinecraftClient.getInstance();
    }

    public void setScreen(Screen screen) {
        client.setScreen(screen);
    }

    public void closeScreen(int syncId) {
        client.setScreen(null);
        if (client.getNetworkHandler() != null) {
            client.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(syncId));
        }
    }

    public Optional<Screen> getScreen() {
        return Optional.ofNullable(client.currentScreen);
    }

    public Optional<Window> getWindow() {
        return Optional.ofNullable(client.getWindow());
    }

    public Optional<TextRenderer> getTextRenderer() {
        return Optional.ofNullable(client.textRenderer);
    }

    public Optional<PlayerEntity> getPlayer() {
        return Optional.ofNullable(client.player);
    }

    public Optional<GameOptions> getOptions() {
        return Optional.ofNullable(client.options);
    }

    public Optional<ClientWorld> getWorld() {
        return Optional.ofNullable(client.world);
    }

    public Optional<ClientPlayNetworkHandler> getNetworkHandler() {
        return Optional.ofNullable(client.getNetworkHandler());
    }
}
