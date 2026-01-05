package com.incrementalclient.config.components;

import dev.isxander.yacl3.api.ListOption;

public interface InsertableListOption<T> extends ListOption<T> {

    static <T> Builder<T> createBuilder() {
        return new InsertableListOptionImpl.BuilderImpl<T>();
    }
}
