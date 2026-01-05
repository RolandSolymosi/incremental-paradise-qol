package com.incrementalclient.interfaces;

public interface Observable<TObserver extends Observer<TResult>, TResult> extends Listenable<TObserver>{

    void subscribe(Observer<TResult> observer);

    void unsubscribe(Observer<TResult> observer);
}
