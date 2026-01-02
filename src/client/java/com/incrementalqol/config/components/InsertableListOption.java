package com.incrementalqol.config.components;

import dev.isxander.yacl3.api.ListOption;

public interface InsertableListOption<T> extends ListOption<T> {

    static <T> ListOption.Builder<T> createBuilder() {
        return new InsertableListOptionImpl.BuilderImpl<T>();
    }
}
