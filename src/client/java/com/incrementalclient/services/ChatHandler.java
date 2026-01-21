package com.incrementalclient.services;

import com.incrementalclient.Main;
import com.incrementalclient.abstractions.ObservableBase;
import com.incrementalclient.interfaces.Observer;
import com.incrementalclient.internals.MinecraftClientAccessor;
import com.incrementalclient.internals.events.ClientReceiveMessageEventsObservable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class ChatHandler extends ObservableBase<Observer<ChatHandler.Event>, ChatHandler.Event> implements Observer<ClientReceiveMessageEventsObservable.Event> {
    private final MinecraftClientAccessor minecraftClientAccessor;

    public ChatHandler(ClientReceiveMessageEventsObservable clientReceiveMessageEventsObservable, MinecraftClientAccessor minecraftClientAccessor) {
        this.minecraftClientAccessor = minecraftClientAccessor;
        clientReceiveMessageEventsObservable.subscribe(this);
    }

    public void sendChatMessage(Text message) {
        if (message == null || message.getString().isEmpty()) return;
        if (minecraftClientAccessor.getPlayer().isEmpty()) return;
        minecraftClientAccessor.getClient().execute(() -> {
            minecraftClientAccessor.getPlayer().get().sendMessage(message, false);
        });
    }

    public void sendChatMessage(String message) {
        this.sendChatMessage(Text.of(message));
    }

    public void sendOverlayMessage(Text message) {
        if (message == null || message.getString().isEmpty()) return;
        if (minecraftClientAccessor.getPlayer().isEmpty()) return;
        minecraftClientAccessor.getClient().execute(() -> {
            minecraftClientAccessor.getPlayer().get().sendMessage(message, true);
        });
    }

    @Override
    public void onEvent(ClientReceiveMessageEventsObservable.Event result) {
        var event = new Event(result.message(), result.overlay());
        notifyObservers(event);
        if(event.isCancelled()) {
            result.cancel();
        }
    }

    public static class Event extends ClientReceiveMessageEventsObservable.Event {
        public Event(Text message, boolean isOverlay) {
            super(message, isOverlay);
        }
    }

    // TODO: Remove? This was used for an old idea (involving mixins), but the new method is more generally applicable.
    //   This ONLY works with regex, new system is regex + others.
    public static final class ChatFilter{
        private final Pattern regex;
        private boolean enabled;
        private final boolean filterPlayerMessage;

        public static final Pattern UserMessagePattern = Pattern.compile("^.*?\\[\\d+(?:┅\\d+)?\\].*?:");

        public ChatFilter(Pattern regex, boolean enabled, boolean filterPlayerMessage){
            this.regex = regex;
            this.enabled = enabled;
            this.filterPlayerMessage = filterPlayerMessage;
        }

        public Pattern getRegex() {
            return regex;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isFilterPlayerMessage() {
            return filterPlayerMessage;
        }
    }
}
