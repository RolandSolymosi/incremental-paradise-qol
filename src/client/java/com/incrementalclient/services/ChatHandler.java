package com.incrementalclient.services;

import com.incrementalclient.abstractions.ObservableBase;
import com.incrementalclient.interfaces.Observer;
import com.incrementalclient.internals.events.ClientReceiveMessageEventsObservable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ChatHandler extends ObservableBase<Observer<ChatHandler.Event>, ChatHandler.Event> implements Observer<ClientReceiveMessageEventsObservable.Event> {
    private final MinecraftClient client;

    public ChatHandler(ClientReceiveMessageEventsObservable clientReceiveMessageEventsObservable) {
        client = MinecraftClient.getInstance();
        clientReceiveMessageEventsObservable.subscribe(this);
    }

    public void sendChatMessage(Text message) {
        if (message == null || message.getString().isEmpty()) return;
        if (client.player == null) return;
        client.player.sendMessage(message, false);
    }

    public void sendOverlayMessage(Text message) {
        if (message == null || message.getString().isEmpty()) return;
        if (client.player == null) return;
        client.player.sendMessage(message, true);
    }

    @Override
    public void onEvent(ClientReceiveMessageEventsObservable.Event result) {
        notifyObservers(new Event(result.message(), result.overlay()));
    }

    public record Event(Text message, boolean isOverlay) {
    }
}
