package com.incrementalclient.services;

import com.incrementalclient.abstractions.ObservableBase;
import com.incrementalclient.interfaces.Observer;
import com.incrementalclient.internals.events.ClientReceiveMessageEventsObservable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class ChatHandler extends ObservableBase<Observer<ChatHandler.Event>, ChatHandler.Event> implements Observer<ClientReceiveMessageEventsObservable.Event> {
    private final MinecraftClient client;
    private final Set<ChatFilter> filters = ConcurrentHashMap.newKeySet();

    public ChatHandler(ClientReceiveMessageEventsObservable clientReceiveMessageEventsObservable) {
        client = MinecraftClient.getInstance();
        clientReceiveMessageEventsObservable.subscribe(this);
    }

    public Set<ChatFilter> getFilters(){
        return filters;
    }

    public void registerFilter(ChatFilter filter){
        filters.add(filter);
    }

    public void removeFilter(ChatFilter filter){
        filters.remove(filter);
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
