package com.incrementalclient.services;

import net.minecraft.client.MinecraftClient;

public class CommandSender {
    private final MinecraftClient client;

    public CommandSender(){
        client = MinecraftClient.getInstance();
    }

    public boolean send(String command){
        if (client.player != null){
            return client.player.networkHandler.sendCommand(command);
        }
        return false;
    }
}
