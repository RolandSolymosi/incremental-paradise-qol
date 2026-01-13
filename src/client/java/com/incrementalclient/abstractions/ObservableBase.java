package com.incrementalclient.abstractions;

import com.incrementalclient.interfaces.Observable;
import com.incrementalclient.interfaces.Observer;

public abstract class ObservableBase<TObserver extends Observer<TResult>, TResult> extends ListenableBase<TObserver> implements Observable<TObserver, TResult> {

    protected void notifyObservers(TResult result) {
        for (var observer : getListeners()) {
            // TODO: Should this check for if result instanceof CancellableEvent?
            //   The idea is that we could do "if result.isCancelled(), do not notify observers"
            observer.onEvent(result);
        }
    }
}
