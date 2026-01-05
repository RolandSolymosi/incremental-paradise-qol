package com.incrementalclient.services;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ChatHandler {
    private final MinecraftClient client;

    public ChatHandler() {
        client =  MinecraftClient.getInstance();
    }

    public void sendChatMessage(Text message) {
        if (message == null || message.getString().isEmpty()) return;
        if (client.player == null) return;
        client.player.sendMessage(message, false);
    }
}
