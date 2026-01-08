package com.incrementalclient.config;

import com.incrementalclient.config.internals.InsertableListOptionImpl;
import dev.isxander.yacl3.api.ListOption;

public interface InsertableListOption<T> extends ListOption<T> {

    static <T> Builder<T> createBuilder() {
        return new InsertableListOptionImpl.BuilderImpl<T>();
    }
}
