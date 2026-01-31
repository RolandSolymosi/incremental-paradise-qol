package com.incrementalclient.interfaces;

import java.util.concurrent.CompletableFuture;

public interface AsyncObserver<TResult, TResponse> extends Observer<TResult> {
    CompletableFuture<Void> onEventAsync(TResult result, CompletableFuture<TResponse> resolution);

    @Override
    default void onEvent(TResult result) {
        onEventAsync(result, new CompletableFuture<>());
    }
}
