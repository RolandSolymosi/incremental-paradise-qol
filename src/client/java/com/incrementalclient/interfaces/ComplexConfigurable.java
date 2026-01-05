package com.incrementalclient.interfaces;

import dev.isxander.yacl3.api.Option;

public interface ComplexConfigurable<TConfiguration, TOption extends Option<?>> extends Configurable<TConfiguration, TOption>, Listenable<Listener> {
}