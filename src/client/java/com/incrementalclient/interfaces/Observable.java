package com.incrementalclient.interfaces;

import com.incrementalclient.internals.events.ClientReceiveMessageEventsObservable;

import java.util.function.Consumer;

public interface Observable<TObserver extends Observer<TResult>, TResult> extends Listenable<TObserver>{

    void subscribe(Observer<TResult> observer);

    void unsubscribe(Observer<TResult> observer);

    final class DefaultObserver<T> implements Observer<T>{

        private final Consumer<T> consumer;

        public DefaultObserver(Consumer<T> consumer){
            this.consumer = consumer;
        }

        @Override
        public void onEvent(T result) {
            consumer.accept(result);
        }
    }
}
