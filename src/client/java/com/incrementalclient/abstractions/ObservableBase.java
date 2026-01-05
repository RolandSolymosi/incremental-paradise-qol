package com.incrementalclient.abstractions;

import com.incrementalclient.interfaces.Observable;
import com.incrementalclient.interfaces.Observer;

public abstract class ObservableBase<TObserver extends Observer<TResult>, TResult> extends ListenableBase<TObserver> implements Observable<TObserver, TResult> {

    protected void notifyObservers(TResult result) {
        for (Observer<TResult> observer : getListeners()) {
            observer.onEvent(result);
        }
    }
}
