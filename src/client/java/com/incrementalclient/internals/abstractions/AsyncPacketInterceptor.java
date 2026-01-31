package com.incrementalclient.internals.abstractions;

import com.incrementalclient.abstractions.AsyncObservableBase;
import com.incrementalclient.interfaces.AsyncObserver;
import com.incrementalclient.interfaces.Listener;
import com.incrementalclient.internals.events.EndClientTickListenable;
import net.minecraft.network.packet.Packet;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public abstract class AsyncPacketInterceptor<TPacket extends Packet<?>, TResponse, TKey>
        extends AsyncObservableBase<AsyncObserver<TPacket, TResponse>, TPacket, TResponse> implements Listener {

    protected record PendingRequest<TResponse>(CompletableFuture<TResponse> future, long expiryTick) {}

    private final Map<TKey, PendingRequest<TResponse>> pendingResponses = new ConcurrentHashMap<>();
    private long currentTick = 0;

    public AsyncPacketInterceptor(EndClientTickListenable tickListenable) {
        tickListenable.subscribe(this);
    }

    @Override
    public void onEvent() {
        currentTick++;
        pendingResponses.entrySet().removeIf(entry -> {
            var request = entry.getValue();
            if (currentTick > request.expiryTick) {
                request.future.completeExceptionally(
                        new TimeoutException("Network sync timeout for key: " + entry.getKey())
                );
                return true;
            }
            return false;
        });
    }

    public CompletableFuture<TResponse> intercept(TPacket packet, Consumer<TPacket> resumeAction) {
        var responseFuture = new CompletableFuture<TResponse>();

        return notifyObserversAsync(packet, responseFuture).thenCompose(v -> {
            var key = getTrackingKey(packet);

            var expiry = currentTick + getTimeoutTicks(packet);
            var oldFuture = pendingResponses.put(key, new PendingRequest<>(responseFuture, expiry));
            if (oldFuture != null && !oldFuture.future.isDone()) {
                oldFuture.future.completeExceptionally(new RuntimeException("Eclipsed by newer packet"));
            }

            try {
                resumeAction.accept(packet);
            } catch (Exception e) {
                pendingResponses.remove(key);
                responseFuture.completeExceptionally(e);
            }

            return responseFuture;
        });
    }

    protected void onResponseReceived(TKey key, TResponse response) {
        var pending = pendingResponses.remove(key);
        if (pending != null) {
            pending.future.complete(response);
        }
    }

    protected abstract TKey getTrackingKey(TPacket packet);
    protected abstract int getTimeoutTicks(TPacket packet);
}