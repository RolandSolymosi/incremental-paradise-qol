package com.incrementalclient.abstractions;

import com.incrementalclient.interfaces.AsyncObserver;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public abstract class AsyncObservableBase<TObserver extends AsyncObserver<TResult, TResponse>, TResult, TResponse>
        extends ObservableBase<TObserver, TResult> {

    protected CompletableFuture<Void> notifyObserversAsync(TResult result, CompletableFuture<TResponse> resolution) {
        var futures = new ArrayList<CompletableFuture<Void>>();
        for (var observer : getListeners()) {
            var future = observer.onEventAsync(result, resolution);
            if (future != null) futures.add(future);
        }

        if (futures.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
}
