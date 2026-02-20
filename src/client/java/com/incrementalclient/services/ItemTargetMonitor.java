package com.incrementalclient.services;

import com.incrementalclient.common.utils.NumberParser;
import com.incrementalclient.interfaces.Observer;
import com.incrementalclient.internals.events.ClientPlayConnectionObservable;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class ItemTargetMonitor implements Observer<ChatHandler.Event> {
    private final ConcurrentHashMap<String, ItemTarget> targets = new ConcurrentHashMap<>();
    private final Map<String, ItemTarget> targetView = Collections.unmodifiableMap(targets);

    private static final String startPiece = "^(?![ \\w]*\\[)";
    private static final ChatHandler.ChatFilter currentlyTrackingList = new ChatHandler.ChatFilter(Pattern.compile(startPiece + "Currently Tracking$"), false, false);
    private static final ChatHandler.ChatFilter itemList = new ChatHandler.ChatFilter(Pattern.compile(startPiece + "(?<item>.+?):\\s+(?<current>" + NumberParser.NumberPattern.pattern() + ")/(?<goal>" + NumberParser.NumberPattern.pattern() + ")$"), false, false);

    private static final ChatHandler.ChatFilter nowTracking = new ChatHandler.ChatFilter(Pattern.compile(startPiece + "🧭 Now Tracking\\s+(?<amount>" + NumberParser.NumberPattern.pattern() + ")\\s+(?<item>.+)$"), false, false);
    private static final ChatHandler.ChatFilter removedTracking = new ChatHandler.ChatFilter(Pattern.compile(startPiece + "Removed tracking for\\s+(?<item>.+)$"), false, false);

    private static final ChatHandler.ChatFilter itemTrackingProgress = new ChatHandler.ChatFilter(Pattern.compile(startPiece + "🧭 Item Tracking >>>\\s+(?<current>" + NumberParser.NumberPattern.pattern() + ")/(?<goal>" + NumberParser.NumberPattern.pattern() + ")\\s+(?<item>.+)$"), false, false);

    private boolean shouldFilterChat = true;

    public ItemTargetMonitor(
            ChatHandler chatHandler,
            CommandHandler commandHandler,
            ClientPlayConnectionObservable clientPlayConnectionObservable
    ) {
        chatHandler.subscribe(this);

        clientPlayConnectionObservable.subscribe((e)->
        {
            if (e == ClientPlayConnectionObservable.EventKind.Connect) {
                // TODO: Add delay service for dereferencing executions by X tick, with the delegate of confirmation if succeeded
                commandHandler.send("trackitem list");
            }
            else{
                targets.clear();
            }
        });
    }

    @Override
    public void onEvent(ChatHandler.Event result) {
        // If it doesn't get to the final else block - only possible if one of the filters was caught -
        // then this will remain true.
        boolean matchedFilter = true;

        var text = result.message().getString();
        if (currentlyTrackingList.getRegex().matcher(text).find()) {
            targets.clear();
        } else if (itemList.getRegex().matcher(text).find()) {
            var matcher = itemList.getRegex().matcher(text);
            if (matcher.find()) {
                var item = matcher.group("item");
                var current = matcher.group("current");
                var goal = matcher.group("goal");

                targets.put(item, new ItemTarget(result.message().getSiblings().getFirst(), item, NumberParser.parseSuffixedNumber(current), NumberParser.parseSuffixedNumber(goal)));
            }
        } else if (nowTracking.getRegex().matcher(text).find()) {
            var matcher = nowTracking.getRegex().matcher(text);
            if (matcher.find()) {
                var item = matcher.group("item");
                var amount = matcher.group("amount");
                targets.put(item, new ItemTarget(result.message().getSiblings().getLast(), item, 0, NumberParser.parseSuffixedNumber(amount)));
            }
        } else if (removedTracking.getRegex().matcher(text).find()){
            var matcher = removedTracking.getRegex().matcher(text);
            if (matcher.find()) {
                var item = matcher.group("item");
                targets.remove(item);
            }
        } else if (itemTrackingProgress.getRegex().matcher(text).find()) {
            var matcher = itemTrackingProgress.getRegex().matcher(text);
            if (matcher.find()) {
                var item = matcher.group("item");
                var current = matcher.group("current");
                var goal = matcher.group("goal");
                targets.put(item, new ItemTarget(result.message().getSiblings().getLast(), item, NumberParser.parseSuffixedNumber(current), NumberParser.parseSuffixedNumber(goal)));
            }
        } else {
            matchedFilter = false;
        }

        if(matchedFilter && this.shouldFilterChat) {
            // cancelling event means it doesn't show up on hud
            // aka it gets filted out
            result.cancel();
        }
    }

    public Map<String,ItemTarget> getItemTargets(){
        return targetView;
    }

    public void setShouldFilterChat(boolean shouldFilter) {
        this.shouldFilterChat = true;
        currentlyTrackingList.setEnabled(shouldFilter);
        itemList.setEnabled(shouldFilter);
        nowTracking.setEnabled(shouldFilter);
        removedTracking.setEnabled(shouldFilter);
        itemTrackingProgress.setEnabled(shouldFilter);
    }

    public record ItemTarget(Text DisplayText, String item, long current, long goal) {
    }
}
