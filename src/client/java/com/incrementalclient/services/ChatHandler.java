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
    private final Set<ChatFilter> filters = ConcurrentHashMap.newKeySet();
    private final MinecraftClientAccessor minecraftClientAccessor;

    public ChatHandler(ClientReceiveMessageEventsObservable clientReceiveMessageEventsObservable, MinecraftClientAccessor minecraftClientAccessor) {
        this.minecraftClientAccessor = minecraftClientAccessor;
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
        if (minecraftClientAccessor.getPlayer().isEmpty()) return;
        minecraftClientAccessor.getClient().execute(() -> {
            minecraftClientAccessor.getPlayer().get().sendMessage(message, false);
        });
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
        // TODO: The current implementation is just the previous implementation, except done without mixins.
        //   However, in my opinion, since ClientReceiveMessageEventsObservable.Event is no longer a record,
        //   (and therefore no longer final), we might be safe to just make ChatHandler.Event equal to:
        //   public class Event extends ClientReceiveMessageEventsObservable.Event { /*constructor here*/ }
        //   Then, since that would make it a CancellableEvent, the filtering code that's done here
        //   would be done by each listener to ChatHandler.
        //   Upside: Each ChatHandler's listener could do MUCH more complicated conditions for filtering
        //   if they wanted (not just limited to regex; could now use states from previous messages)
        //   Downside: The filtering code would basically need to be rebuilt by each individual listener.

        notifyObservers(new Event(result.message(), result.overlay()));

        var message = result.message();
        for(var filter : this.getFilters()) {
            if(filter.isEnabled()) {
                // TODO: What is isFilterPlayerMessage?
                //   Do you mean "shouldFilterPlayerMessage"? aka "whether this filter should filter messages
                //   from players"?
                // If statement copied directly from old ChatHud mixin.
                if (filter.isFilterPlayerMessage() || message.getSiblings().stream().noneMatch(s -> s.getStyle().getClickEvent() instanceof ClickEvent.RunCommand(String command) && command.startsWith("/stats "))){
                    if (filter.getRegex().matcher(message.getString()).find()){
                        result.cancel();
//                        Main.LOGGER.info("Cancelling CRMEO event");
                        return;
                    }
                }
            }
        }
//        Main.LOGGER.info("NOT Cancelling CRMEO event with {} filters", this.getFilters().size());
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
